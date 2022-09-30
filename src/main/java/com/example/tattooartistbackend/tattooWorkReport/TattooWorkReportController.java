package com.example.tattooartistbackend.tattooWorkReport;

import com.example.tattooartistbackend.generated.apis.TattooWorkReportsApi;
import com.example.tattooartistbackend.generated.models.TattooWorkReportPatchReqDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportPostReqDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportResDto;
import com.example.tattooartistbackend.generated.models.TattooWorkReportResPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TattooWorkReportController implements TattooWorkReportsApi {


    private final TattooWorkReportService tattooWorkReportService;

    /**
     * POST /tattoo-work-reports
     * create tattooWork Report
     *
     * @param tattooWorkReportPostReqDto (optional)
     * @return Created (status code 201)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkReportResDto> createTattooWorkReport(TattooWorkReportPostReqDto tattooWorkReportPostReqDto) {
        return new ResponseEntity<>(tattooWorkReportService.createTattooWorkReport(tattooWorkReportPostReqDto), HttpStatus.CREATED);
    }

    /**
     * GET /tattoo-work-reports
     * get tattoo work reports
     *
     * @param page (required)
     * @param size (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkReportResPageable> getAllTattooWorkReports(Integer page, Integer size) {
        return new ResponseEntity<>(tattooWorkReportService.getAllTattooWorkReports(page, size),HttpStatus.OK);
    }


    /**
     * GET /tattoo-work-reports/{id}
     * get tattoo work report by id
     *
     * @param id (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkReportResDto> getTattooWorkReportById(UUID id) {
        return new ResponseEntity<>(tattooWorkReportService.getTattooWorkReportById(id),HttpStatus.OK);
    }

    /**
     * DELETE /tattoo-work-reports/{id}
     * delete tattooWork report
     *
     * @param id (required)
     * @return No Content (status code 204)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> removeTattooWorkReport(UUID id) {
        tattooWorkReportService.removeTattooWorkReport(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * PATCH /tattoo-work-reports/{id}
     * update tattooWork report
     *
     * @param id                          (required)
     * @param tattooWorkReportPatchReqDto (optional)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkReportResDto> updateTattooWorkReport(UUID id, TattooWorkReportPatchReqDto tattooWorkReportPatchReqDto) {
        return new ResponseEntity<>(tattooWorkReportService.updateTattooWorkReport(id, tattooWorkReportPatchReqDto),HttpStatus.OK);
    }
}
