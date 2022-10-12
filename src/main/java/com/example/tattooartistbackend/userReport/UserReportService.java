package com.example.tattooartistbackend.userReport;

import com.example.tattooartistbackend.exceptions.NotOwnerOfEntityException;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.UserReportPatchReqDto;
import com.example.tattooartistbackend.generated.models.UserReportPostReqDto;
import com.example.tattooartistbackend.generated.models.UserReportResDto;
import com.example.tattooartistbackend.generated.models.UserReportResPageable;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final RoleService roleService;

    public void closeReport(UUID id) {
        var authenticatedUser = securityService.getUser();
        var userReport = userReportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!userReport.getReportOwner().getId().equals(authenticatedUser.getId()) && !roleService.isAdmin(authenticatedUser.getUid())) {
            throw new NotOwnerOfEntityException("only authenticated user or admin can delete a report");
        } else {
            userReportRepository.deleteById(id);
        }
    }

    public UserReportResDto createUserReport(UserReportPostReqDto userReportPostReqDto) {
        var reportedUser = userRepository.findById(userReportPostReqDto.getReportedUserId()).orElseThrow(UserNotFoundException::new);
        var reportOwner = userRepository.findById(userReportPostReqDto.getReportOwnerId()).orElseThrow(UserNotFoundException::new);
        var authenticatedUser = securityService.getUser();
        if (!reportOwner.getId().equals(authenticatedUser.getId())) {
            throw new NotOwnerOfEntityException("only authenticated user can create a report");
        } else {
            return UserReport.fromEntityToResponseDto(userReportRepository.save(UserReport.fromPostReqDtoToEntity(userReportPostReqDto, reportedUser, reportOwner)));
        }
    }

    public UserReportResPageable getAllUserReports(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        var userReportPageable = userReportRepository.getAllPageable(pageable);
        var userReportResponsePageable = new UserReportResPageable();
        userReportResponsePageable.setUserReports(userReportPageable.getContent().stream().map(UserReport::fromEntityToResponseDto).collect(Collectors.toList()));
        userReportResponsePageable.setTotalElements((int) userReportPageable.getTotalElements());
        userReportResponsePageable.setTotalPages(userReportPageable.getTotalPages());
        return userReportResponsePageable;
    }

    public UserReportResDto getUserReportById(UUID id) {
        return UserReport.fromEntityToResponseDto(userReportRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserReportResDto updateUserReport(UUID id, UserReportPatchReqDto userReportPatchReqDto) {
        var userReport = userReportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var authenticatedUser = securityService.getUser();
        if (!userReport.getReportOwner().getId().equals(authenticatedUser.getId())) {
            throw new NotOwnerOfEntityException("only authenticated user can update a report");
        } else {
            userReport.setDescription(userReportPatchReqDto.getDescription());
            userReport.setDate(LocalDate.now());
            return UserReport.fromEntityToResponseDto(userReportRepository.save(userReport));
        }
    }
}
