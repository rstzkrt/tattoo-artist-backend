package com.example.tattooartistapp.user;

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
    private String role;
    private String avatar;
    private LocalDate dateOfBirth;


    //available days enum
    //fovorite users
    //favorutire tattoo works
    //
}
