package com.example.tattooartistbackend.userReport;

import com.example.tattooartistbackend.generated.apis.UserReportsApi;
import com.example.tattooartistbackend.generated.models.UserReportPatchReqDto;
import com.example.tattooartistbackend.generated.models.UserReportPostReqDto;
import com.example.tattooartistbackend.generated.models.UserReportResDto;
import com.example.tattooartistbackend.generated.models.UserReportResPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserReportController implements UserReportsApi {

    private final UserReportService userReportService;

    /**
     * DELETE /user-reports/{id}
     * delete user report
     *
     * @param id (required)
     * @return No Content (status code 204)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> closeReport(UUID id) {
        userReportService.closeReport(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * POST /user-reports
     * create User Report
     *
     * @param userReportPostReqDto (optional)
     * @return Created (status code 201)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<UserReportResDto> createUserReport(UserReportPostReqDto userReportPostReqDto) {
        return new ResponseEntity<>(userReportService.createUserReport(userReportPostReqDto),HttpStatus.CREATED);
    }

    /**
     * GET /user-reports
     * get user reports
     *
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<UserReportResPageable> getAllUserReports(Integer page, Integer size) {
        return new ResponseEntity<>(userReportService.getAllUserReports(page, size),HttpStatus.OK);
    }

    /**
     * GET /user-reports/{id}
     * get user report by id
     *
     * @param id (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<UserReportResDto> getUserReportById(UUID id) {
        return new ResponseEntity<>(userReportService.getUserReportById(id),HttpStatus.OK);
    }

    /**
     * PATCH /user-reports/{id}
     * update user report
     *
     * @param id                    (required)
     * @param userReportPatchReqDto (optional)
     * @return OK (status code 200)
     */
    @Override
    public ResponseEntity<UserReportResDto> updateUserReport(UUID id, UserReportPatchReqDto userReportPatchReqDto) {
        return new ResponseEntity<>(userReportService.updateUserReport(id, userReportPatchReqDto),HttpStatus.OK);
    }
}
