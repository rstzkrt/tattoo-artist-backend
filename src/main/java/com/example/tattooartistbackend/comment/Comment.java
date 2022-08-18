package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.comment.models.CommentRequestDto;
import com.example.tattooartistbackend.comment.models.CommentResponseDto;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.*;
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
public class Comment { //comments for the tatoo works but feedbacks(reviews) are for the tattoo artists

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @ManyToOne
    private User postedBy;
    private String message;
    private LocalDate postDate;
    @OneToOne
    private TattooWork tattooWork;
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
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setMessage(comment.getMessage());
        commentResponseDto.setPostDate(comment.getPostDate());
        commentResponseDto.setPostedBy(comment.getPostedBy().getId());
        commentResponseDto.setWorkId(comment.getTattooWork().getId());
        commentResponseDto.setRate(comment.getRate());
        return commentResponseDto;
    }
}
