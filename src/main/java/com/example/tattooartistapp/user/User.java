package com.example.tattooartistapp.user;

import java.util.List;

import com.example.tattooartistapp.comment.Comment;
import com.example.tattooartistapp.models.WorkingDays;
import com.example.tattooartistapp.tattooWork.TattooWork;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE,generator = "user_seq")
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private LocalDate dateOfBirth;//needed for creating an atrist page
    private boolean hasArtistPage;
    //...

    @Enumerated
    @ElementCollection(targetClass = WorkingDays.class)
    private List<WorkingDays> workingDaysList;

    @OneToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    private List<User> favouriteArtists;

    @OneToMany(mappedBy = "madeBy",fetch = FetchType.LAZY)
    private List<TattooWork> tattooWorks;

    @OneToMany(fetch = FetchType.LAZY)
    private List<TattooWork> favoriteTattooWorks;

    @OneToMany(mappedBy = "postedBy",fetch = FetchType.LAZY)
    private List<Comment> comments;
}
