package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.generated.apis.CommentsApi;
import com.example.tattooartistbackend.generated.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.generated.models.CommentRequestDto;
import com.example.tattooartistbackend.generated.models.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentsApi {
    private final CommentService commentService;

    @Override
    public ResponseEntity<CommentResponseDto> createComment(UUID tattooWorkId, CommentRequestDto commentRequestDto) {
        return new ResponseEntity<>(commentService.createComment(tattooWorkId,commentRequestDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteCommentById(UUID commentId) {
        commentService.deleteCommentById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<CommentResponseDto> editComment(UUID commentId, CommentPatchRequestDto commentPatchRequestDto) {
        return new ResponseEntity<>(commentService.editComment(commentId, commentPatchRequestDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CommentResponseDto> getCommentById(UUID commentId) {
        return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CommentResponseDto> getCommentByTattooWorkId(UUID tattooWorkId) {
        return new ResponseEntity<>(commentService.getCommentsByTattooWorkId(tattooWorkId), HttpStatus.OK);
    }
}
