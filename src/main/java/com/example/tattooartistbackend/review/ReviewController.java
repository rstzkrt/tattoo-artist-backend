package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.generated.apis.ReviewsApi;
import com.example.tattooartistbackend.generated.models.ReviewPatchRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewsApi {

    private final ReviewService reviewService;
    /**
     * POST /reviews/users/{receiver_id}
     * post review for tattooArtist
     *
     * @param receiverId           receiverId (required)
     * @param reviewPostRequestDto (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<ReviewResponseDto> createReview(UUID receiverId, ReviewPostRequestDto reviewPostRequestDto) {
        return new ResponseEntity<>(reviewService.createReview(receiverId, reviewPostRequestDto), HttpStatus.CREATED);
    }

    /**
     * DELETE /reviews/{id}
     * delete review
     *
     * @param id reviewId (required)
     * @return no content (status code 204)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> deleteReviewById(UUID id) {
        reviewService.deleteReviewById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * GET /reviews/users/{receiver_id}
     * get all
     *
     * @param receiverId receiverId (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewByUserId(UUID receiverId) {
        return new ResponseEntity<>(reviewService.getAllReviewByUserId(receiverId), HttpStatus.OK);

    }

    /**
     * GET /reviews/{id}
     * get revie
     *
     * @param id reviewId (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<ReviewResponseDto> getReviewsById(UUID id) {
        return new ResponseEntity<>(reviewService.getReviewsById(id), HttpStatus.OK);

    }

    /**
     * PATCH /reviews/{id}
     *
     * @param id                    reviewId (required)
     * @param reviewPatchRequestDto (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<ReviewResponseDto> reviewPatchUpdate(UUID id, ReviewPatchRequestDto reviewPatchRequestDto) {
        return new ResponseEntity<>(reviewService.reviewPatchUpdate(id, reviewPatchRequestDto), HttpStatus.OK);
    }
}
