package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.exceptions.*;
import com.example.tattooartistbackend.generated.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.generated.models.CommentRequestDto;
import com.example.tattooartistbackend.generated.models.CommentResponseDto;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TattooWorkRepository tattooWorkRepository;
    private final SecurityService securityService;

    public CommentResponseDto createComment(UUID tattooWorkId, CommentRequestDto commentRequestDto) {
        var tattooWork = tattooWorkRepository.findById(tattooWorkId).orElseThrow(TattooWorkNotFoundException::new);
        var tattooWorkOwner = userRepository.findById(tattooWork.getMadeBy().getId()).orElseThrow();
        var client = userRepository.findById(commentRequestDto.getPostedBy()).orElseThrow(UserNotFoundException::new);
        if(tattooWork.getClient()==null){
            throw new RuntimeException("client account has deleted!");
        }
        if (tattooWork.getClient().getId() != client.getId()) {
            throw new NotOwnerOfEntityException("only the client of the tattooWork can post a comment!");
        }
        if (tattooWork.getComment() != null) {
            throw new TattooWorkCommentExistsException();
        }
        return getCreateCommentResponse(commentRequestDto, tattooWork, client,tattooWorkOwner);
    }

    private CommentResponseDto getCreateCommentResponse(CommentRequestDto commentRequestDto, TattooWork tattooWork, User client, User tattooWorkOwner) {
        var comment = commentRepository.save(Comment.fromDto(commentRequestDto, client, tattooWork));
        tattooWork.setComment(comment);
        tattooWorkRepository.save(tattooWork);

        Set<Comment> clientComments = new HashSet<>(client.getComments());
        clientComments.add(comment);
        client.setComments(clientComments.stream().toList());

        setAverageRating(tattooWorkOwner);
        return Comment.toResponseDto(comment);
    }

    public void deleteCommentById(UUID commentId) {
        var authenticatedUser = securityService.getUser();
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (commentRepository.existsByPostedBy_Id(authenticatedUser.getId())) {
            setDeleteComment(commentId);
        } else {
            throw new NotOwnerOfEntityException("only the owner can delete the comment!");
        }
    }

    private void setDeleteComment(UUID commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow(TattooWorkNotFoundException::new);
        var tattooWorkOwner = userRepository.findById(tattooWork.getMadeBy().getId()).orElseThrow();
        var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow(UserNotFoundException::new);

        var comments = client.getComments();
        comments.remove(comment);
        client.setComments(comments);
        userRepository.save(client);

        tattooWork.setComment(null);
        tattooWorkRepository.save(tattooWork);
        commentRepository.deleteById(commentId);

        setAverageRating(tattooWorkOwner);
    }

    private void setAverageRating(User tattooWorkOwner) {
        var tattooWorks = tattooWorkRepository.findAllByMadeBy_Id(tattooWorkOwner.getId());
        System.out.println("tattoo work s="+tattooWorks.size());
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
        if(s.stream().filter(Objects::nonNull).toList().size()==0){
            tattooWorkOwner.setAverageRating(BigDecimal.valueOf(0, 2).doubleValue());
        }else {
            tattooWorkOwner.setAverageRating(total.divide(BigDecimal.valueOf(s.stream().filter(Objects::nonNull).toList().size()), 2, RoundingMode.HALF_EVEN).doubleValue());
        }
        userRepository.save(tattooWorkOwner);
    }

    public CommentResponseDto editComment(UUID commentId, CommentPatchRequestDto commentPatchRequestDto) {
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        var tattooWork = tattooWorkRepository.findById(comment.getTattooWork().getId()).orElseThrow(TattooWorkNotFoundException::new);
        var client = userRepository.findById(comment.getPostedBy().getId()).orElseThrow(UserNotFoundException::new);
        var authenticatedUser= securityService.getUser();
        if (authenticatedUser.getComments().contains(comment)) {
            setDeleteComment(commentId);
        } else {
            throw new NotOwnerOfEntityException("only the owner can edit the comment!");
        }
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
        return Comment.toResponseDto(commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new));
    }

    public CommentResponseDto getCommentsByTattooWorkId(UUID tattooWorkId) {
        return Comment.toResponseDto(commentRepository.findByTattooWork_Id(tattooWorkId));
    }
}
