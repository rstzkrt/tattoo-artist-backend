package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.user.models.Currency;
import com.example.tattooartistbackend.user.models.UserDto;
import com.example.tattooartistbackend.user.models.WorkingDays;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

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
    @GeneratedValue(strategy = AUTO)
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

    @OneToMany(mappedBy = "madeBy", fetch = FetchType.LAZY)
    private List<TattooWork> tattooWorks;

    @OneToMany(fetch = FetchType.LAZY)
    private List<TattooWork> favoriteTattooWorks;

    @OneToMany(mappedBy = "postedBy", fetch = FetchType.LAZY)
    private List<Comment> comments;

    public static User fromDto(UserDto userDto, Address address,List<TattooWork> favoriteTattooWorks,List<TattooWork> tattooWorks,List<User> favouriteArtists,List<Comment> comments) {
        return User.builder()
                .id(userDto.getId())
                .avatarUrl(userDto.getAvatarUrl()==null? "defaultUrl": userDto.getAvatarUrl())
                .phoneNumber(userDto.getPhoneNumber())
                .dateOfBirth(userDto.getBirthDate())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .workingDaysList(userDto.getWorkDays())
                .hasArtistPage(userDto.getHasArtistPage() != null && userDto.getHasArtistPage())
                .businessAddress(address)
                .uid(userDto.getUid())
                .tattooWorks(tattooWorks == null ? new ArrayList<>() : tattooWorks )
                .favouriteArtists(favouriteArtists == null ? new ArrayList<>() : favouriteArtists )
                .comments(comments == null ? new ArrayList<>() : comments )
                .favoriteTattooWorks(favoriteTattooWorks == null ? new ArrayList<>() : favoriteTattooWorks )
                .build();
    }

    public UserDto toDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
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
        userDto.favoriteArtistIds(
                favouriteArtists
                        .stream()
                        .map(User::getId)
                        .toList()
        );
        userDto.favoriteTattooWorkIds(
                favoriteTattooWorks
                        .stream()
                        .map(TattooWork::getId)
                        .toList()
        );
        userDto.commentIds(
                comments
                        .stream()
                        .map(Comment::getId)
                        .toList()
        );
        userDto.tattooWorkIds(
                tattooWorks
                        .stream()
                        .map(TattooWork::getId)
                        .toList()
        );

        if (userDto.getMaxTattooWorkPriceCurrency() != null) {
            userDto.setMaxTattooWorkPriceCurrency(userDto.getMaxTattooWorkPriceCurrency());
        } else {
            userDto.setMaxTattooWorkPriceCurrency(Currency.EUR);
        }

        if (userDto.getMinTattooWorkPriceCurrency() != null) {
            userDto.setMinTattooWorkPriceCurrency(userDto.getMaxTattooWorkPriceCurrency());
        } else {
            userDto.setMinTattooWorkPriceCurrency(Currency.EUR);
        }

        if (userDto.getAverageRating() != null) {
            userDto.setAverageRating(userDto.getAverageRating());
        } else {
            userDto.setAverageRating(BigDecimal.valueOf(0));
        }

        if (userDto.getMinTattooWorkPrice() != null) {
            userDto.setMinTattooWorkPrice(userDto.getMinTattooWorkPrice());
        } else {
            userDto.setMinTattooWorkPrice(BigDecimal.valueOf(0));
        }

        if (userDto.getMaxTattooWorkPrice() != null) {
            userDto.setMaxTattooWorkPrice(userDto.getMaxTattooWorkPrice());
        } else {
            userDto.setMaxTattooWorkPrice(BigDecimal.valueOf(0));
        }
        return userDto;
    }
}
