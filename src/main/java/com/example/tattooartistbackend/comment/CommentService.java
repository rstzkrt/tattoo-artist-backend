package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.comment.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.comment.models.CommentRequestDto;
import com.example.tattooartistbackend.comment.models.CommentResponseDto;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TattooWorkRepository tattooWorkRepository;

    public CommentResponseDto createComment(UUID tattooWorkId, CommentRequestDto commentRequestDto) {
        //client averate reating set
        var tattooWork = tattooWorkRepository.findById(tattooWorkId).orElseThrow();
        var client = userRepository.findById(commentRequestDto.getPostedBy()).orElseThrow();
        setAverageRating(client);
        var comment = commentRepository.save(Comment.fromDto(commentRequestDto, client, tattooWork));
        var comments = client.getComments();
        comments.add(comment);
        userRepository.save(client);
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);
        client.setComments(comments);
        return Comment.toResponseDto(comment);
    }

    public void deleteCommentById(UUID commentId) {
        //client averate reating set

        if (commentRepository.existsById(commentId)) {
            var comment = commentRepository.findById(commentId).orElseThrow();
            var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
            var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow();
            var comments = client.getComments();
            comments.remove(comment);
            userRepository.save(client);
            tattooWork.setComment(null);
            tattooWorkRepository.save(tattooWork);
            commentRepository.deleteById(commentId);
            client.setComments(comments);
            setAverageRating(client);
        } else {
            throw new UserNotFoundException();
        }
    }

    private static void setAverageRating(User client) {
        var tattooWorks = client.getTattooWorks();
        var s = tattooWorks
                .stream()
                .map(tattooWork1 -> {
                    if(tattooWork1.getComment()==null){
                        return BigDecimal.valueOf(0);
                    }
                    return tattooWork1.getComment().getRate();
                }).toList();
        var total = 0;
        s.forEach(bigDecimal -> bigDecimal.add(BigDecimal.valueOf(total)));
        client.setAverageRating(Double.valueOf(total / s.size()));

    }

    public CommentResponseDto editComment(UUID commentId, CommentPatchRequestDto commentPatchRequestDto) {
        var comment = commentRepository.findById(commentId).orElseThrow();
        var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
        var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow();
        var comments = client.getComments();
        setAverageRating(client);
        comments.remove(comment);
        comment.setRate(commentPatchRequestDto.getRate());
        comment.setMessage(commentPatchRequestDto.getMessage());
        comments.add(comment);
        commentRepository.save(comment);
        client.setComments(comments);
        userRepository.save(client);
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);
        return null;
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
