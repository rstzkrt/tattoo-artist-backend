package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.generated.models.ReviewPatchRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewResponseDto;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewResponseDto createReview(UUID receiverId, ReviewPostRequestDto reviewPostRequestDto) {
        var receiver = userRepository.findById(receiverId).orElseThrow();
        var postedBy = userRepository.findById(reviewPostRequestDto.getPostedBy()).orElseThrow();
        var review = reviewRepository.save(Review.fromReviewResponseDto(reviewPostRequestDto, postedBy, receiver));

        if(receiver.getId()==postedBy.getId()){
            throw new RuntimeException("YOU CANNOT POT REVIEW FOR YOURSELF");
        }

        var takenReviews = receiver.getTakenReviews();
        takenReviews.add(review);
        receiver.setTakenReviews(takenReviews);
        userRepository.save(receiver);

        var givenReviews = receiver.getGivenReviews();
        givenReviews.add(review);
        postedBy.setGivenReviews(givenReviews);
        userRepository.save(postedBy);

        return review.toReviewResponseDto();
    }

    public void deleteReviewById(UUID id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public List<ReviewResponseDto> getAllReviewByUserId(UUID receiverId) {
        userRepository.findById(receiverId).orElseThrow();
        return reviewRepository.findAllByReceiver_Id(receiverId)
                .stream()
                .map(Review::toReviewResponseDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDto getReviewsById(UUID id) {
        return reviewRepository.findById(id).orElseThrow().toReviewResponseDto();
    }

    public ReviewResponseDto reviewPatchUpdate(UUID id, ReviewPatchRequestDto reviewPatchRequestDto) {
        var review = reviewRepository.findById(id).orElseThrow();
        review.setReviewType(reviewPatchRequestDto.getReviewType());
        review.setMessage(reviewPatchRequestDto.getMessage());
        return reviewRepository.save(review).toReviewResponseDto();
    }
}
