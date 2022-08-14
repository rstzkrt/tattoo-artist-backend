package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
@RequiredArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @ManyToOne
    private User postedBy;
    private String message;
    private LocalDate postDate;
    @ManyToOne
    private TattooWork tattooWork;
}
