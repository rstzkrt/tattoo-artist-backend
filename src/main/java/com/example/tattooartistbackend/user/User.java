package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.generated.models.*;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    private String uid;
    @NotBlank(message = "firstName cannot be null!")
    private String firstName;
    @NotBlank(message = "firstName cannot be null!")
    private String lastName;
    @Email
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private boolean hasArtistPage;
    private Double averageRating;
    @OneToOne
    private Address businessAddress;
    @Enumerated
    @ElementCollection(targetClass = WorkingDays.class)
    private List<WorkingDays> workingDaysList;

    @OneToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    private List<User> favouriteArtists;

    @OneToMany(mappedBy = "madeBy", fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<TattooWork> tattooWorks;

    @OneToMany(fetch = FetchType.LAZY)
    private List<TattooWork> favoriteTattooWorks;

    @OneToMany(mappedBy = "postedBy", fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<Comment> comments;
    public static User fromClientRequestDto(ClientReqDto clientReqDto) {
        return User.builder()
                .id(null)
                .avatarUrl(clientReqDto.getAvatarUrl() == null ? "defaultUrl" : clientReqDto.getAvatarUrl())
                .phoneNumber(null)
                .dateOfBirth(clientReqDto.getBirthDate())
                .firstName(clientReqDto.getFirstName())
                .lastName(clientReqDto.getLastName())
                .email(clientReqDto.getEmail())
                .workingDaysList(null)
                .hasArtistPage(false)
                .businessAddress(null)
                .uid(null)
                .tattooWorks(new ArrayList<>())
                .favouriteArtists(new ArrayList<>())
                .comments(new ArrayList<>())
                .favoriteTattooWorks(new ArrayList<>())
                .build();
    }

    public static User fromTattooArtistAccReqDto(TattooArtistAccReqDto tattooArtistAccReqDto, Address address, List<TattooWork> favoriteTattooWorks, List<TattooWork> tattooWorks, List<User> favouriteArtists, List<Comment> comments) {
        return User.builder()
                .phoneNumber(tattooArtistAccReqDto.getPhoneNumber())
                .workingDaysList(tattooArtistAccReqDto.getWorkDays())
                .hasArtistPage(true)
                .businessAddress(address)
                .tattooWorks(tattooWorks == null ? new ArrayList<>() : tattooWorks)
                .favouriteArtists(favouriteArtists == null ? new ArrayList<>() : favouriteArtists)
                .comments(comments == null ? new ArrayList<>() : comments)
                .favoriteTattooWorks(favoriteTattooWorks == null ? new ArrayList<>() : favoriteTattooWorks)
                .build();
    }

    public static User fromUserUpdateRequestDto(UserUpdateRequestDto userUpdateRequestDto, Address address, List<TattooWork> favoriteTattooWorks, List<TattooWork> tattooWorks, List<User> favouriteArtists, List<Comment> comments) {
        return User.builder()
                .avatarUrl(userUpdateRequestDto.getAvatarUrl() == null ? "defaultUrl" : userUpdateRequestDto.getAvatarUrl())
                .phoneNumber(userUpdateRequestDto.getPhoneNumber())
                .firstName(userUpdateRequestDto.getFirstName())
                .lastName(userUpdateRequestDto.getLastName())
                .email(userUpdateRequestDto.getEmail())
                .workingDaysList(userUpdateRequestDto.getWorkDays())
                .businessAddress(address)
                .tattooWorks(tattooWorks == null ? new ArrayList<>() : tattooWorks)
                .favouriteArtists(favouriteArtists == null ? new ArrayList<>() : favouriteArtists)
                .comments(comments == null ? new ArrayList<>() : comments)
                .favoriteTattooWorks(favoriteTattooWorks == null ? new ArrayList<>() : favoriteTattooWorks)
                .build();
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
        if(averageRating!=null){
            userResponseDto.setAverageRating(BigDecimal.valueOf(averageRating));
        }else{
            userResponseDto.setAverageRating(BigDecimal.valueOf(0));
        }
        if (businessAddress != null) {
            userResponseDto.setStreet(businessAddress.getStreet());
            userResponseDto.setState(businessAddress.getState());
            userResponseDto.setCity(businessAddress.getCity());
            userResponseDto.setCountry(businessAddress.getCountry());
            userResponseDto.setPostalCode(businessAddress.getPostalCode());
            userResponseDto.setOtherInformation(businessAddress.getOtherInformation());
        }else{
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
            userResponseDto.setFavoriteArtistIds(
                    favouriteArtists
                            .stream()
                            .map(User::getId)
                            .toList()
            );
        }

        if (this.favoriteTattooWorks.isEmpty()) {
            userResponseDto.setFavoriteTattooWorkIds(new ArrayList<>());
        } else {
            userResponseDto.setFavoriteTattooWorkIds(
                    favoriteTattooWorks
                            .stream()
                            .map(TattooWork::getId)
                            .toList());
        }
        if (this.comments.isEmpty()) {
            userResponseDto.setCommentIds(new ArrayList<>());
        } else {
            userResponseDto.setCommentIds(
                    comments
                            .stream()
                            .map(Comment::getId)
                            .toList()
            );
        }
        if (this.tattooWorks.isEmpty()) {
            userResponseDto.setTattooWorkIds(new ArrayList<>());
        } else {
            userResponseDto.setTattooWorkIds(
                    tattooWorks
                            .stream()
                            .map(TattooWork::getId)
                            .toList()
            );
        }
        if (userResponseDto.getAverageRating() != null) {
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
                ", averageRating=" + this.getAverageRating() +
                ", workingDaysList=" + this.getWorkingDaysList();
    }
}
