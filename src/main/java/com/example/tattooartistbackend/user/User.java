package com.example.tattooartistbackend.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.user.models.UserDto;
import com.example.tattooartistbackend.user.models.WorkingDays;
import com.example.tattooartistbackend.tattooWork.TattooWork;
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
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private LocalDate dateOfBirth;//needed for creating an arist page
    private boolean hasArtistPage;
    @OneToOne
    private Address businessAddress;
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

    public static User fromDto(UserDto userDto,Address address){
        return User.builder()
                .id(userDto.getId())
                .avatarUrl(userDto.getAvatarUrl())
                .phoneNumber(userDto.getPhoneNumber())
                .dateOfBirth(userDto.getBirthDate())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .workingDaysList(userDto.getWorkDays())
                .hasArtistPage(userDto.getHasArtistPage())
                .businessAddress(address)
                .uid(userDto.getUid())
                .tattooWorks(new ArrayList<>())
                .favouriteArtists(new ArrayList<>())
                .comments(new ArrayList<>())
                .favoriteTattooWorks(new ArrayList<>())
                .build();
    }

    public UserDto toDto(){
        UserDto userDto= new UserDto();
        userDto.id(id);
        userDto.uid(uid);
        userDto.firstName(firstName);
        userDto.lastName(lastName);
        userDto.email(email);
        userDto.phoneNumber(phoneNumber);
        userDto.avatarUrl(avatarUrl);
        userDto.birthDate(dateOfBirth);
        userDto.hasArtistPage(hasArtistPage);
        userDto.workDays(workingDaysList);
        userDto.street(businessAddress.getStreet());
        userDto.state(businessAddress.getState());
        userDto.city(businessAddress.getCity());
        userDto.country(businessAddress.getCountry());
        userDto.postalCode(businessAddress.getPostalCode());
        userDto.otherInformation(businessAddress.getOtherInformation());
        return userDto;
    }
}
