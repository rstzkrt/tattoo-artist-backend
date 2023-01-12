package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.comment.Comment;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super(Comment.class.getSimpleName()+ " not found !");
    }
}
