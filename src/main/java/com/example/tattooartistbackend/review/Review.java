package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewResponseDto;
import com.example.tattooartistbackend.generated.models.ReviewType;
import com.example.tattooartistbackend.user.User;
import lombok.*;

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
    @ManyToOne
    private User postedBy ;
    @ManyToOne
    private User receiver;

    public ReviewResponseDto toReviewResponseDto(){
        ReviewResponseDto reviewResponseDto=new ReviewResponseDto();
        reviewResponseDto.setReviewType(reviewType);
        reviewResponseDto.setMessage(message);
        reviewResponseDto.setId(id);
        reviewResponseDto.setReceiver(receiver.getId());
        reviewResponseDto.setPostedBy(postedBy.getId());
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
