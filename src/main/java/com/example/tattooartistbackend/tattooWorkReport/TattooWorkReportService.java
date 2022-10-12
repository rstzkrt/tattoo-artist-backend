package com.example.tattooartistbackend.tattooWorkReport;

import com.example.tattooartistbackend.exceptions.NotOwnerOfEntityException;
import com.example.tattooartistbackend.exceptions.TattooWorkNotFoundException;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.TattooWorkReportPatchReqDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportPostReqDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportResDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportResPageable;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
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
public class TattooWorkReportService {

    private final TattooWorkReportRepository tattooWorkReportRepository;
    private final TattooWorkRepository tattooWorkRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final RoleService roleService;

    public TattooWorkReportResDto createTattooWorkReport(TattooWorkReportPostReqDto tattooWorkReportPostReqDto) {
        var reportOwner = userRepository.findById(tattooWorkReportPostReqDto.getReportOwnerId()).orElseThrow(UserNotFoundException::new);
        var reportedTattooWork = tattooWorkRepository.findById(tattooWorkReportPostReqDto.getReportedTattooWorkId()).orElseThrow(TattooWorkNotFoundException::new);
        var authenticatedUser = securityService.getUser();
        if (!reportOwner.getId().equals(authenticatedUser.getId())) {
            throw new NotOwnerOfEntityException("only authenticated user can create a report");
        } else {
            return TattooWorkReport.fromEntityToResponseDto(tattooWorkReportRepository.save(TattooWorkReport.fromPostReqDtoToEntity(tattooWorkReportPostReqDto, reportedTattooWork, reportOwner)));
        }
    }

    public TattooWorkReportResPageable getAllTattooWorkReports(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        var workReportRepositoryAllPageable = tattooWorkReportRepository.getAllPageable(pageable);
        var tattooWorkReportResPageable = new TattooWorkReportResPageable();
        tattooWorkReportResPageable.setTattooWorkReports(workReportRepositoryAllPageable.getContent().stream().map(TattooWorkReport::fromEntityToResponseDto).collect(Collectors.toList()));
        tattooWorkReportResPageable.setTotalElements((int) workReportRepositoryAllPageable.getTotalElements());
        tattooWorkReportResPageable.setTotalPages(workReportRepositoryAllPageable.getTotalPages());
        return tattooWorkReportResPageable;
    }

    public TattooWorkReportResDto getTattooWorkReportById(UUID id) {
        return TattooWorkReport.fromEntityToResponseDto(tattooWorkReportRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public void removeTattooWorkReport(UUID id) {
        var authenticatedUser = securityService.getUser();
        var tattooWorkReport = tattooWorkReportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!tattooWorkReport.getTattooWorkReportOwner().getId().equals(authenticatedUser.getId()) && !roleService.isAdmin(authenticatedUser.getUid())) {
            throw new NotOwnerOfEntityException("only authenticated user can delete a report");
        } else {
            tattooWorkReportRepository.deleteById(id);
        }
    }

    public TattooWorkReportResDto updateTattooWorkReport(UUID id, TattooWorkReportPatchReqDto tattooWorkReportPatchReqDto) {
        var tattooWorkReport = tattooWorkReportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var authenticatedUser = securityService.getUser();
        if (!tattooWorkReport.getTattooWorkReportOwner().getId().equals(authenticatedUser.getId())) {
            throw new NotOwnerOfEntityException("only authenticated user can update a report");
        } else {
            tattooWorkReport.setDescription(tattooWorkReportPatchReqDto.getDescription());
            tattooWorkReport.setDate(LocalDate.now());
            return TattooWorkReport.fromEntityToResponseDto(tattooWorkReportRepository.save(tattooWorkReport));
        }
    }
}
