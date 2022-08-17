package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.*;
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
    private Double rate;
}
