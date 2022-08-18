package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.comment.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.comment.models.CommentRequestDto;
import com.example.tattooartistbackend.comment.models.CommentResponseDto;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public CommentResponseDto createComment(UUID tattooWorkId,CommentRequestDto commentRequestDto){
        var tattooWork =tattooWorkRepository.findById(tattooWorkId).orElseThrow();
        var client= userRepository.findById(commentRequestDto.getPostedBy()).orElseThrow();
        var comment=commentRepository.save(Comment.fromDto(commentRequestDto, client, tattooWork));
        var comments=client.getComments();
        comments.add(comment);
        client.setComments(comments);
        userRepository.save(client);
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);
        return Comment.toResponseDto(comment);
    }
    public void deleteCommentById(UUID commentId){
        if (commentRepository.existsById(commentId)) {
            var comment =commentRepository.findById(commentId).orElseThrow();
            var tattooWork =tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
            var client= userRepository.findById(comment.getPostedBy().getId()).orElseThrow();
            var comments=client.getComments();
            comments.remove(comment);
            client.setComments(comments);
            userRepository.save(client);
            tattooWork.setComment(null);
            tattooWorkRepository.save(tattooWork);
            commentRepository.deleteById(commentId);
        } else {
            throw new UserNotFoundException();
        }
    }
    public CommentResponseDto editComment(UUID commentId, CommentPatchRequestDto commentPatchRequestDto){
        var comment =commentRepository.findById(commentId).orElseThrow();
        var tattooWork =tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow();
        var client= userRepository.findById(comment.getPostedBy().getId()).orElseThrow();
        var comments=client.getComments();
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
    public CommentResponseDto getCommentById(UUID commentId){
        return Comment.toResponseDto(commentRepository.findById(commentId).orElseThrow());
    }
    public List<CommentResponseDto> getCommentsByTattooWorkId(UUID tattooWorkId){
        return commentRepository.findAllByTattooWork_Id(tattooWorkId)
                .stream()
                .map(Comment::toResponseDto)
                .collect(Collectors.toList());
    }

}
