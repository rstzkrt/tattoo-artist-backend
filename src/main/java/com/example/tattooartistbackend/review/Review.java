package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewResponseDto;
import com.example.tattooartistbackend.generated.models.ReviewType;
import com.example.tattooartistbackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private ReviewType reviewType;
    private String message;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User postedBy ;
    @ManyToOne
    private User receiver;

    public ReviewResponseDto toReviewResponseDto(){
        ReviewResponseDto reviewResponseDto=new ReviewResponseDto();
        reviewResponseDto.setReviewType(reviewType);
        reviewResponseDto.setMessage(message);
        reviewResponseDto.setId(id);
        reviewResponseDto.setReceiver(receiver.getId());
        reviewResponseDto.setPostedBy(postedBy.toUserResponseDto());
        return  reviewResponseDto;
    }

    public static Review fromReviewResponseDto(ReviewPostRequestDto reviewPostRequestDto, User postedBy, User receiver){
        return Review.builder()
                .reviewType(reviewPostRequestDto.getReviewType())
                .message(reviewPostRequestDto.getMessage())
                .postedBy(postedBy)
                .receiver(receiver)
                .build();
    }
}
