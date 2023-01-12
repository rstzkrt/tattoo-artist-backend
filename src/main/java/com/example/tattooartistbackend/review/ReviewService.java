package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.exceptions.CreateReviewNotAllowdException;
import com.example.tattooartistbackend.exceptions.NotOwnerOfEntityException;
import com.example.tattooartistbackend.exceptions.ReviewNotFoundException;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.ReviewPatchRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewResponseDto;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    public ReviewResponseDto createReview(UUID receiverId, ReviewPostRequestDto reviewPostRequestDto) {
        var receiver = userRepository.findById(receiverId).orElseThrow(UserNotFoundException::new);
        var postedBy = userRepository.findById(reviewPostRequestDto.getPostedBy()).orElseThrow(UserNotFoundException::new);
        if(receiver.getId()==postedBy.getId()){
            throw new CreateReviewNotAllowdException();
        }
        var review = reviewRepository.save(Review.fromReviewResponseDto(reviewPostRequestDto, postedBy, receiver));
        return review.toReviewResponseDto();
    }

    public void deleteReviewById(UUID id) {
        var authenticatedUser= securityService.getUser();
        var review= reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        if (authenticatedUser.getId().equals(review.getPostedBy().getId())) {
            reviewRepository.deleteById(id);
        } else {
            throw new NotOwnerOfEntityException("only the owner can delete the review!");
        }
    }

    public List<ReviewResponseDto> getAllReviewByUserId(UUID receiverId) {
        userRepository.findById(receiverId).orElseThrow(UserNotFoundException::new);
        return reviewRepository.findAllByReceiver_Id(receiverId)
                .stream()
                .map(Review::toReviewResponseDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDto getReviewsById(UUID id) {
        return reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new).toReviewResponseDto();
    }

    public ReviewResponseDto reviewPatchUpdate(UUID id, ReviewPatchRequestDto reviewPatchRequestDto) {
        var authenticatedUser= securityService.getUser();
        var review = reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        if (authenticatedUser.getId().equals(review.getPostedBy().getId())) {
            review.setReviewType(reviewPatchRequestDto.getReviewType());
            review.setMessage(reviewPatchRequestDto.getMessage());
            return reviewRepository.save(review).toReviewResponseDto();
        } else {
            throw new NotOwnerOfEntityException("only the owner can edit the review!");
        }

    }
}
