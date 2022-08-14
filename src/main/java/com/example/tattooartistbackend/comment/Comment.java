package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
public class Comment {

    @Id
    private UUID id;
    @ManyToOne
    private User postedBy;
    private String message;
    private LocalDate postDate;
    @ManyToOne
    private TattooWork tattooWork;
}
