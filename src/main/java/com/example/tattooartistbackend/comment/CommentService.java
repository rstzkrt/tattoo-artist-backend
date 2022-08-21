package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.generated.models.CommentRequestDto;
import com.example.tattooartistbackend.generated.models.CommentResponseDto;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TattooWorkRepository tattooWorkRepository;

    public CommentResponseDto createComment(UUID tattooWorkId, CommentRequestDto commentRequestDto) {
        var tattooWork = tattooWorkRepository.findById(tattooWorkId).orElseThrow();
        var client = userRepository.findById(commentRequestDto.getPostedBy()).orElseThrow();
        if (tattooWork.getComment() != null) {
            throw new RuntimeException("The post already has a comment!");
        }
        return getCreateCommentResponse(commentRequestDto, tattooWork, client);
    }

    private CommentResponseDto getCreateCommentResponse(CommentRequestDto commentRequestDto, TattooWork tattooWork, User client) {
        var comment = commentRepository.save(Comment.fromDto(commentRequestDto, client, tattooWork));
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);
        var clientComments = client.getComments();
        clientComments.add(comment);
        client.setComments(clientComments);
        setAverageRating(client);
        return Comment.toResponseDto(comment);
    }

    public void deleteCommentById(UUID commentId) {
        if (commentRepository.existsById(commentId)) {
            setDeleteComment(commentId);
        } else {
            throw new UserNotFoundException();
        }
    }

    private void setDeleteComment(UUID commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow();
        var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
        var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow();
        var comments = client.getComments();
        comments.remove(comment);
        client.setComments(comments);
        tattooWork.setComment(null);
        tattooWorkRepository.save(tattooWork);
        commentRepository.deleteById(commentId);
        setAverageRating(client);
    }

    private void setAverageRating(User client) {
        var tattooWorks = client.getTattooWorks();
        var s = tattooWorks.stream()
                .map(tattooWork1 -> {
                    if (tattooWork1.getComment() == null) {
                        return null;
                    } else {
                        return tattooWork1.getComment().getRate();
                    }
                }).toList();

        BigDecimal total = BigDecimal.valueOf(0);

        for (BigDecimal bigDecimal : s) {
            System.out.println(bigDecimal);
            if (bigDecimal != null) {
                total = bigDecimal.add(total);
            }
        }
        client.setAverageRating(total.divide(BigDecimal.valueOf(s.size()), 2, RoundingMode.HALF_EVEN).doubleValue());
        userRepository.save(client);
    }

    public CommentResponseDto editComment(UUID commentId, CommentPatchRequestDto commentPatchRequestDto) {
        var comment = commentRepository.findById(commentId).orElseThrow();
        var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
        var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow();

        setEditComment(commentPatchRequestDto, comment, tattooWork, client);
        return Comment.toResponseDto(comment);
    }

    private void setEditComment(CommentPatchRequestDto commentPatchRequestDto, Comment comment, TattooWork tattooWork, User client) {
        var comments = client.getComments();
        comments.remove(comment);
        comment.setRate(commentPatchRequestDto.getRate());
        comment.setMessage(commentPatchRequestDto.getMessage());
        comments.add(comment);
        commentRepository.save(comment);
        client.setComments(comments);
        userRepository.save(client);
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);
        setAverageRating(client);
    }

    public CommentResponseDto getCommentById(UUID commentId) {
        return Comment.toResponseDto(commentRepository.findById(commentId).orElseThrow());
    }

    public List<CommentResponseDto> getCommentsByTattooWorkId(UUID tattooWorkId) {
        return commentRepository.findAllByTattooWork_Id(tattooWorkId)
                .stream()
                .map(Comment::toResponseDto)
                .collect(Collectors.toList());
    }
}
