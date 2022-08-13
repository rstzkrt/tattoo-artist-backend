package com.example.tattooartistapp.comment;

import com.example.tattooartistapp.tattooWork.TattooWork;
import com.example.tattooartistapp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

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
