package com.example.tattooartistbackend.tattooWorkReport;

import com.example.tattooartistbackend.generated.models.TattooWorkReportPostReqDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportResDto;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TattooWorkReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDate date;

    private String description;

    @ManyToOne
    private User tattooWorkReportOwner;

    @ManyToOne
    private TattooWork reportedTattooWork;

    public static TattooWorkReportResDto fromEntityToResponseDto(TattooWorkReport tattooWorkReport) {
        var tattooWorkReportResDto=new TattooWorkReportResDto();
        tattooWorkReportResDto.setReportedTattooWork(TattooWork.toTattooWorksResponseDto(tattooWorkReport.getReportedTattooWork()));
        tattooWorkReportResDto.setReportOwner(tattooWorkReport.getTattooWorkReportOwner().toUserResponseDto());
        tattooWorkReportResDto.setDescription(tattooWorkReport.getDescription());
        tattooWorkReportResDto.setDate(tattooWorkReport.getDate());
        tattooWorkReportResDto.setId(tattooWorkReport.getId());
        return tattooWorkReportResDto;
    }

    public static TattooWorkReport fromPostReqDtoToEntity(TattooWorkReportPostReqDto tattooWorkReportPostReqDto, TattooWork reportedTattooWork, User reportOwner) {
        return TattooWorkReport.builder()
                .reportedTattooWork(reportedTattooWork)
                .date(LocalDate.now())
                .description(tattooWorkReportPostReqDto.getDescription())
                .tattooWorkReportOwner(reportOwner)
                .build();
    }
}
