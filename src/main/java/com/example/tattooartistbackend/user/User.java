package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.generated.models.ClientReqDto;
import com.example.tattooartistbackend.generated.models.Gender;
import com.example.tattooartistbackend.generated.models.Language;
import com.example.tattooartistbackend.generated.models.MadeByInfo;
import com.example.tattooartistbackend.generated.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.generated.models.UserResponseDto;
import com.example.tattooartistbackend.generated.models.UserUpdateRequestDto;
import com.example.tattooartistbackend.generated.models.WorkingDays;
import com.example.tattooartistbackend.review.Review;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWorkReport.TattooWorkReport;
import com.example.tattooartistbackend.userReport.UserReport;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @NotBlank
    @Unique
    private String uid;
    @NotBlank(message = "firstName cannot be null!")
    private String firstName;
    private String lastName;
    @Email
    @Unique
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private boolean hasArtistPage;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Language> languages;
    private Double averageRating;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String careerDescription;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "reportedUser",fetch = FetchType.EAGER)
    private List<UserReport> reportsUserBelongsTo;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "reportOwner", fetch = FetchType.EAGER)
    private List<UserReport> userReports;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "tattooWorkReportOwner", fetch = FetchType.EAGER)
    private List<TattooWorkReport> tattooWorkReports;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<TattooStyle> tattooStyles;
    @OneToOne
    private Address businessAddress;
    @Enumerated
    @ElementCollection(targetClass = WorkingDays.class, fetch = FetchType.EAGER)
    private List<WorkingDays> workingDaysList;
    @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER)
    private List<Comment> comments;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> favouriteArtists;
    @OneToMany(mappedBy = "madeBy", fetch = FetchType.EAGER)
    @ToString.Exclude
@JsonBackReference
    private List<TattooWork> tattooWorks;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<TattooWork> favoriteTattooWorks;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "likerIds")
    private List<TattooWork> likedTattooWorks;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "dislikerIds")
    private List<TattooWork> dislikedTattooWorks;
    @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Review> givenReviews;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Review> takenReviews;

    public static User fromClientRequestDto(ClientReqDto clientReqDto) {
        return User.builder()
                .id(null)
                .uid(clientReqDto.getUid())
                .avatarUrl(clientReqDto.getAvatarUrl() == null ? "https://www.gravatar.com/avatar/?d=mp" : clientReqDto.getAvatarUrl())
                .phoneNumber(null)
                .firstName(clientReqDto.getFirstName())
                .lastName(clientReqDto.getLastName())
                .email(clientReqDto.getEmail())
                .workingDaysList(null)
                .hasArtistPage(false)
                .businessAddress(null)
                .tattooWorks(new ArrayList<>())
                .favouriteArtists(new ArrayList<>())
                .comments(new ArrayList<>())
                .favoriteTattooWorks(new ArrayList<>())
                .givenReviews(new ArrayList<>())
                .takenReviews(new ArrayList<>())
                .userReports(null)
                .likedTattooWorks(new ArrayList<>())
                .dislikedTattooWorks(new ArrayList<>())
                .tattooStyles(new ArrayList<>())
                .languages(new ArrayList<>())
                .averageRating((double) 0)
                .build();
    }

    public static User fromTattooArtistAccReqDto(TattooArtistAccReqDto tattooArtistAccReqDto, Address address, List<TattooWork> favoriteTattooWorks, List<TattooWork> tattooWorks, List<User> favouriteArtists, List<Comment> comments, List<Review> takenReviews, List<Review> givenReviews) {
        return User.builder()
                .phoneNumber(tattooArtistAccReqDto.getPhoneNumber())
                .workingDaysList(tattooArtistAccReqDto.getWorkDays())
                .hasArtistPage(true)
                .dateOfBirth(tattooArtistAccReqDto.getDateOfBirth())
                .businessAddress(address)
                .tattooWorks(tattooWorks == null ? new ArrayList<>() : tattooWorks)
                .favouriteArtists(favouriteArtists == null ? new ArrayList<>() : favouriteArtists)
                .comments(comments == null ? new ArrayList<>() : comments)
                .favoriteTattooWorks(favoriteTattooWorks == null ? new ArrayList<>() : favoriteTattooWorks)
                .givenReviews(givenReviews == null ? new ArrayList<>() : givenReviews)
                .takenReviews(takenReviews == null ? new ArrayList<>() : takenReviews)
                .careerDescription(tattooArtistAccReqDto.getCareerDescription())
                .gender(tattooArtistAccReqDto.getGender())
                .tattooStyles(tattooArtistAccReqDto.getTattooStyles() == null ? new ArrayList<>() : tattooArtistAccReqDto.getTattooStyles())
                .languages(tattooArtistAccReqDto.getLanguages() == null ? new ArrayList<>() : tattooArtistAccReqDto.getLanguages())
                .build();
    }

    public static User fromUserUpdateRequestDto(UserUpdateRequestDto userUpdateRequestDto, Address address, List<TattooWork> favoriteTattooWorks, List<TattooWork> tattooWorks, List<User> favouriteArtists, List<Comment> comments, List<Review> takenReviews, List<Review> givenReviews) {
        return User.builder()
                .avatarUrl(userUpdateRequestDto.getAvatarUrl() == null ? "https://www.gravatar.com/avatar/?d=mp" : userUpdateRequestDto.getAvatarUrl())
                .phoneNumber(userUpdateRequestDto.getPhoneNumber())
                .firstName(userUpdateRequestDto.getFirstName())
                .lastName(userUpdateRequestDto.getLastName())
                .workingDaysList(userUpdateRequestDto.getWorkDays())
                .businessAddress(address)
                .tattooWorks(tattooWorks == null ? new ArrayList<>() : tattooWorks)
                .favouriteArtists(favouriteArtists == null ? new ArrayList<>() : favouriteArtists)
                .comments(comments == null ? new ArrayList<>() : comments)
                .favoriteTattooWorks(favoriteTattooWorks == null ? new ArrayList<>() : favoriteTattooWorks)
                .givenReviews(givenReviews == null ? new ArrayList<>() : givenReviews)
                .takenReviews(takenReviews == null ? new ArrayList<>() : takenReviews)
                .careerDescription(userUpdateRequestDto.getCareerDescription())
                .gender(userUpdateRequestDto.getGender())
                .tattooStyles(userUpdateRequestDto.getTattooStyles() == null ? new ArrayList<>() : userUpdateRequestDto.getTattooStyles())
                .languages(userUpdateRequestDto.getLanguages() == null ? new ArrayList<>() : userUpdateRequestDto.getLanguages())
                .build();
    }

    public User fromBasicPatchRequest(UserUpdateRequestDto updateRequestDto) {
        this.setLastName(updateRequestDto.getLastName());
        this.setFirstName(updateRequestDto.getFirstName());
        this.setAvatarUrl(updateRequestDto.getAvatarUrl());
        return this;
    }

    public MadeByInfo toMadeByInfoDto() {
        MadeByInfo madeByInfo = new MadeByInfo();
        madeByInfo.setId(id);
        madeByInfo.setUid(uid);
        madeByInfo.setFirstName(firstName);
        madeByInfo.setLastName(lastName);
        madeByInfo.setEmail(email);
        madeByInfo.setPhoneNumber(phoneNumber);
        madeByInfo.setAvatarUrl(avatarUrl);
        madeByInfo.setBirthDate(dateOfBirth);
        madeByInfo.setHasArtistPage(hasArtistPage);
        madeByInfo.setWorkDays(workingDaysList);
        madeByInfo.setTattooStyles(tattooStyles == null ? new ArrayList<>() : tattooStyles);
        if (averageRating != null) {
            madeByInfo.setAverageRating(BigDecimal.valueOf(averageRating));
        } else {
            madeByInfo.setAverageRating(BigDecimal.valueOf(0));
        }
        if (businessAddress != null) {
            madeByInfo.setStreet(businessAddress.getStreet());
            madeByInfo.setState(businessAddress.getState());
            madeByInfo.setCity(businessAddress.getCity());
            madeByInfo.setCountry(businessAddress.getCountry());
            madeByInfo.setPostalCode(businessAddress.getPostalCode());
            madeByInfo.setOtherInformation(businessAddress.getOtherInformation());
        } else {
            madeByInfo.setStreet(null);
            madeByInfo.setState(null);
            madeByInfo.setCity(null);
            madeByInfo.setCountry(null);
            madeByInfo.setPostalCode(null);
            madeByInfo.setOtherInformation(null);
        }
        return madeByInfo;
    }

    public UserResponseDto toUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(id);
        userResponseDto.setUid(uid);
        userResponseDto.setFirstName(firstName);
        userResponseDto.setLastName(lastName);
        userResponseDto.setEmail(email);
        userResponseDto.setPhoneNumber(phoneNumber);
        userResponseDto.setAvatarUrl(avatarUrl);
        userResponseDto.setBirthDate(dateOfBirth);
        userResponseDto.setHasArtistPage(hasArtistPage);
        userResponseDto.setWorkDays(workingDaysList);
        userResponseDto.setCareerDescription(careerDescription);
        userResponseDto.setGender(gender);
        userResponseDto.setTattooStyles(tattooStyles == null ? new ArrayList<>() : tattooStyles);
        userResponseDto.setLanguages(languages == null ? new ArrayList<>() : languages);
        userResponseDto.setTattooWorkReports(tattooWorkReports == null ? new ArrayList<>() : tattooWorkReports.stream().map(tattooWorkReport -> tattooWorkReport.getTattooWorkReportOwner().getId()).collect(Collectors.toList()));
        userResponseDto.setUserReports(userReports == null ? new ArrayList<>() : userReports.stream().map(userReport1 -> userReport1.getReportOwner().getId()).collect(Collectors.toList()));
        if (averageRating != null) {
            userResponseDto.setAverageRating(BigDecimal.valueOf(averageRating));
        } else {
            userResponseDto.setAverageRating(BigDecimal.valueOf(0));
        }
        if (businessAddress != null) {
            userResponseDto.setStreet(businessAddress.getStreet());
            userResponseDto.setState(businessAddress.getState());
            userResponseDto.setCity(businessAddress.getCity());
            userResponseDto.setCountry(businessAddress.getCountry());
            userResponseDto.setPostalCode(businessAddress.getPostalCode());
            userResponseDto.setOtherInformation(businessAddress.getOtherInformation());
        } else {
            userResponseDto.setStreet(null);
            userResponseDto.setState(null);
            userResponseDto.setCity(null);
            userResponseDto.setCountry(null);
            userResponseDto.setPostalCode(null);
            userResponseDto.setOtherInformation(null);
        }

        if (this.favouriteArtists.isEmpty()) {
            userResponseDto.setFavoriteArtistIds(new ArrayList<>());
        } else {
            userResponseDto.setFavoriteArtistIds(favouriteArtists.stream().map(User::getId).collect(Collectors.toList()));
        }
        if (this.favoriteTattooWorks.isEmpty()) {
            userResponseDto.setFavoriteTattooWorkIds(new ArrayList<>());
        } else {
            userResponseDto.setFavoriteTattooWorkIds(favoriteTattooWorks.stream().map(TattooWork::getId).collect(Collectors.toList()));
        }
        if (this.comments.isEmpty()) {
            userResponseDto.setCommentIds(new ArrayList<>());
        } else {
            userResponseDto.setCommentIds(comments.stream()
                    .map(Comment::getId)
                    .collect(Collectors.toList())
            );
        }
        if (this.tattooWorks.isEmpty()) {
            userResponseDto.setTattooWorkIds(new ArrayList<>());
        } else {
            userResponseDto.setTattooWorkIds(tattooWorks.stream().map(TattooWork::getId).collect(Collectors.toList()));
        }
        if (this.getAverageRating() != null) {
            userResponseDto.setAverageRating(userResponseDto.getAverageRating());
        } else {
            userResponseDto.setAverageRating(BigDecimal.valueOf(0));
        }
        return userResponseDto;
    }

    public String toString() {
        return "User(id=" + this.getId() +
                ", uid=" + this.getUid() +
                ", firstName=" + this.getFirstName() +
                ", lastName=" + this.getLastName() +
                ", email=" + this.getEmail() +
                ", phoneNumber=" + this.getPhoneNumber() +
                ", avatarUrl=" + this.getAvatarUrl() +
                ", dateOfBirth=" + this.getDateOfBirth() +
                ", hasArtistPage=" + this.isHasArtistPage() +
                ", averageRating=" + this.getAverageRating();
    }
}
