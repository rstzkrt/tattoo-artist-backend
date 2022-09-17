package com.example.tattooartistbackend.comment;


import com.example.tattooartistbackend.generated.models.CommentRequestDto;
import com.example.tattooartistbackend.generated.models.CommentResponseDto;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @ManyToOne
    @ToString.Exclude
    private User postedBy;
    @NotBlank
    private String message;
    private LocalDate postDate;

    @ToString.Exclude
    @OneToOne//(cascade = CascadeType.REMOVE)
    private TattooWork tattooWork;

    @Max(value = 5)
    @Min(value = 1)
    private BigDecimal rate;

    public static Comment fromDto(CommentRequestDto commentRequestDto, User postedBy, TattooWork tattooWork) {
        return Comment.builder()
                .message(commentRequestDto.getMessage())
                .postDate(LocalDate.now())
                .postedBy(postedBy)
                .rate(commentRequestDto.getRate())
                .tattooWork(tattooWork)
                .build();
    }

    public static CommentResponseDto toResponseDto(Comment comment) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        if(comment!=null){
            commentResponseDto.setId(comment.getId());
            commentResponseDto.setMessage(comment.getMessage());
            commentResponseDto.setPostDate(comment.getPostDate());
            commentResponseDto.setPostedBy(comment.getPostedBy().toUserResponseDto());
            commentResponseDto.setWorkId(comment.getTattooWork().getId());
            commentResponseDto.setRate(comment.getRate());
            return commentResponseDto;
        }else {
            return null;
        }
    }
}
