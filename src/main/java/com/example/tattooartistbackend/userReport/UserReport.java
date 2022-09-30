package com.example.tattooartistbackend.userReport;

import com.example.tattooartistbackend.generated.models.UserReportPostReqDto;
import com.example.tattooartistbackend.generated.models.UserReportResDto;
import com.example.tattooartistbackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private LocalDate date;
    private String description;
    @ManyToOne
    private User reportedUser;
    @ManyToOne
    private User reportOwner;

    public static UserReport fromResponseDtoToEntity(UserReportResDto userReportResDto,User reportedUser,User reportOwner){ //TODO make non static
        return UserReport.builder()
                .description(userReportResDto.getDescription())
                .reportedUser(reportedUser)
                .date(userReportResDto.getDate())
                .reportOwner(reportOwner)
                .build();
    }

    public static UserReport fromPostReqDtoToEntity(UserReportPostReqDto userReportReqDto, User reportedUser, User reportOwner){ //TODO make non static
        return UserReport.builder()
                .description(userReportReqDto.getDescription())
                .reportedUser(reportedUser)
                .date(LocalDate.now())
                .reportOwner(reportOwner)
                .build();
    }

    public static UserReportResDto fromEntityToResponseDto(UserReport userReport){
        UserReportResDto userReportResDto= new UserReportResDto();
        userReportResDto.setId(userReport.getId());
        userReportResDto.setReportedUser(userReport.getReportedUser()==null? null:userReport.getReportedUser().toUserResponseDto());
        userReportResDto.setReportOwner(userReport.getReportOwner()==null? null: userReport.getReportOwner().toUserResponseDto());
        userReportResDto.setDescription(userReport.getDescription());
        userReportResDto.setDate(userReport.getDate());
        return userReportResDto;
    }
}
