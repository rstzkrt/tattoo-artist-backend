package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.comment.CommentRepository;
import com.example.tattooartistbackend.exceptions.*;
import com.example.tattooartistbackend.generated.models.ClientReqDto;
import com.example.tattooartistbackend.generated.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.generated.models.TattooArtistPriceInterval;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.generated.models.UserResponseDto;
import com.example.tattooartistbackend.generated.models.UserUpdateRequestDto;
import com.example.tattooartistbackend.review.Review;
import com.example.tattooartistbackend.review.ReviewRepository;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final static Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TattooWorkRepository tattooWorkRepository;
    private final ReviewRepository reviewRepository;
    private final SecurityService securityService;
    private final CommentRepository commentRepository;

    public UserResponseDto createUser(ClientReqDto clientReqDto) {
        return userRepository.save(User.fromClientRequestDto(clientReqDto)).toUserResponseDto();
    }

    public List<UserResponseDto> findAllUsers(Integer page, Integer size, String firstName, String lastName) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllCustom(firstName, lastName, pageable)
                .getContent()
                .stream()
                .map(User::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> findUserById(String id) {
        if (UUID_REGEX_PATTERN.matcher(id).matches()) {
            return Optional.of(userRepository.findById(UUID.fromString(id))
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new));
        } else {
            return Optional.of(userRepository.findByUid(id)
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new));
        }
    }

    public Optional<UserResponseDto> updateUser(UserUpdateRequestDto userUpdateRequestDto) {
        var authenticatedUser = securityService.getUser();
//        if(authenticatedUser.isHasArtistPage()){

            return Optional.ofNullable(userRepository.findById(authenticatedUser.getId())
                    .map(user -> {
                        var givenReviews = reviewRepository.findAllByPostedBy_Id(user.getId());
                        var takenReviews = reviewRepository.findAllByReceiver_Id(user.getId());

                        Address address = updateReqToAddress(user, userUpdateRequestDto.getCity(), userUpdateRequestDto.getState(),
                                userUpdateRequestDto.getCountry(), userUpdateRequestDto.getPostalCode(), userUpdateRequestDto.getStreet(), userUpdateRequestDto.getOtherInformation());
                        User userToUpdate =
                                User.fromUserUpdateRequestDto(userUpdateRequestDto,
                                        address,
                                        user.getFavoriteTattooWorks(),
                                        user.getTattooWorks(),
                                        user.getFavouriteArtists(),
                                        user.getComments(),
                                        takenReviews,
                                        givenReviews);
                        userToUpdate.setId(user.getId());
                        userToUpdate.setUid(user.getUid());
                        userToUpdate.setHasArtistPage(user.isHasArtistPage());
                        userToUpdate.setDateOfBirth(user.getDateOfBirth());
                        userToUpdate.setAverageRating(user.getAverageRating());
                        return userRepository.save(userToUpdate);
                    })
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new));
//        }
//        else{
//            return null;
//        }

    }

    public void deleteUser() {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        user.setFavoriteTattooWorks(null);
        user.setFavouriteArtists(null);
        // like dislike
        commentRepository.deleteAll(user.getComments());
        tattooWorkRepository.deleteAll(user.getTattooWorks());
        reviewRepository.deleteAll(user.getTakenReviews());
        var tattooWorkList = tattooWorkRepository.findAllByClient_Id(user.getId());
        tattooWorkList.forEach(tattooWork -> {
            tattooWork.setClient(null);

            tattooWorkRepository.save(tattooWork);
        });
        userRepository.deleteById(user.getId());

    }

    public UserResponseDto favoriteTattooArtist(UUID artistId) {
        var authenticatedUser = securityService.getUser();
        User user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        User artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

        List<User> favouriteArtists = user.getFavouriteArtists();
        if (favouriteArtists.contains(artist)) {
            return user.toUserResponseDto();
        } else {
            favouriteArtists.add(artist);
            user.setFavouriteArtists(favouriteArtists);
            return userRepository.save(user).toUserResponseDto();
        }
    }

    public void unfavoriteTattooArtist(UUID artistId) {
        var authenticatedUser = securityService.getUser();

        User user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        User artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

        List<User> favouriteArtists = user.getFavouriteArtists();
        favouriteArtists.remove(artist);
        user.setFavouriteArtists(favouriteArtists);
        userRepository.save(user);
    }

    public void unfavoriteTattooWork(UUID postId) {
        var authenticatedUser = securityService.getUser();
        User user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        TattooWork tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);

        List<TattooWork> favoriteTattooWorks = user.getFavoriteTattooWorks();
        favoriteTattooWorks.remove(tattooWork);
        user.setFavoriteTattooWorks(favoriteTattooWorks);
        userRepository.save(user);
    }

    public UserResponseDto favoriteTattooWork(UUID postId) {
        var authenticatedUser = securityService.getUser();
        User user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        TattooWork tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        List<TattooWork> favoriteTattooWorks = user.getFavoriteTattooWorks();

        if (favoriteTattooWorks.contains(tattooWork)) {
            return user.toUserResponseDto();
        } else {
            favoriteTattooWorks.add(tattooWork);
            user.setFavoriteTattooWorks(favoriteTattooWorks);
            return userRepository.save(user).toUserResponseDto();
        }
    }

    public UserResponseDto createArtistAccount(TattooArtistAccReqDto tattooArtistAccReqDto) {
        var authenticatedUser = securityService.getUser();
        if (authenticatedUser.isHasArtistPage()) {
            throw new RuntimeException("Client already have an Artist Account");
        }
        return userRepository.findById(authenticatedUser.getId())
                .map(user -> {
                    if (LocalDate.now().getYear() - tattooArtistAccReqDto.getDateOfBirth().getYear() < 18) {
                        throw new UnderAgeException();
                    }
                    var givenReviews = reviewRepository.findAllByPostedBy_Id(user.getId());
                    var takenReviews = reviewRepository.findAllByReceiver_Id(user.getId());
                    Address address = updateAddress(tattooArtistAccReqDto);
                    User userToUpdate = getUserToUpdate(tattooArtistAccReqDto, user, givenReviews, takenReviews, address);
                    return userRepository.save(userToUpdate);
                })
                .map(User::toUserResponseDto)
                .orElseThrow(UserNotFoundException::new);
    }

    private Address updateAddress(TattooArtistAccReqDto tattooArtistAccReqDto) {
        Address address = Address.builder()
                .otherInformation(tattooArtistAccReqDto.getOtherInformation())
                .street(tattooArtistAccReqDto.getStreet())
                .city(tattooArtistAccReqDto.getCity())
                .country(tattooArtistAccReqDto.getCountry())
                .postalCode(tattooArtistAccReqDto.getPostalCode())
                .state(tattooArtistAccReqDto.getState())
                .build();
        addressRepository.save(address);
        return address;
    }

    private static User getUserToUpdate(TattooArtistAccReqDto tattooArtistAccReqDto, User user, List<Review> givenReviews, List<Review> takenReviews, Address address) {
        User userToUpdate = User.fromTattooArtistAccReqDto(
                tattooArtistAccReqDto,
                address,
                user.getTattooWorks(),
                user.getTattooWorks(),
                user.getFavouriteArtists(),
                user.getComments(),
                takenReviews,
                givenReviews);
        userToUpdate.setId(user.getId());
        userToUpdate.setUid(user.getUid());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setAvatarUrl(user.getAvatarUrl());
        return userToUpdate;
    }

    private Address updateAddress(User user, String city, String state, String country, String postalCode, String street, String otherInformation) {
        if (!user.isHasArtistPage()) {
            throw new UserArtistPageNotFoundException();
        }
        Address address = addressRepository.findById(user.getBusinessAddress().getId()).orElseThrow(AddressNotFoundException::new);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        address.setStreet(street);
        address.setOtherInformation(otherInformation);
        addressRepository.save(address);
        return address;
    }

    private Address updateReqToAddress(User user, String city, String state, String country, String postalCode, String street, String otherInformation) {
        return updateAddress(user, city, state, country, postalCode, street, otherInformation);
    }

    public void like(UUID postId) {
        var authenticatedUser = securityService.getUser();
        var tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        if (!tattooWorkRepository.existsByLikerIdsContainsAndId(authenticatedUser,postId)) {
            if (tattooWorkRepository.existsByDislikerIdsContainsAndId(authenticatedUser,postId)) {
                var tattooWorkDislikerIds = new ArrayList<>(tattooWork.getDislikerIds());
                tattooWorkDislikerIds.removeIf(user -> user.getId().equals(authenticatedUser.getId()));
                tattooWork.setDislikerIds(tattooWorkDislikerIds);
            }
            var tattooWorkLikerIds = tattooWork.getLikerIds();
            tattooWorkLikerIds.add(authenticatedUser);
            tattooWork.setLikerIds(tattooWorkLikerIds);
            tattooWorkRepository.save(tattooWork);
        } else {
            throw new AlreadyLikedException();
        }
    }

    public void dislike(UUID postId) {
        var authenticatedUser = securityService.getUser();
        var tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        if (!tattooWorkRepository.existsByDislikerIdsContainsAndId(authenticatedUser,postId)) {
            if (tattooWorkRepository.existsByLikerIdsContainsAndId(authenticatedUser,postId)) {
                var tattooWorkLikerIds = new ArrayList<>(tattooWork.getLikerIds());
                tattooWorkLikerIds.removeIf(user -> user.getId().equals(authenticatedUser.getId()));
                tattooWork.setLikerIds(tattooWorkLikerIds);
            }
            var tattooWorkDislikerIds = tattooWork.getDislikerIds();
            tattooWorkDislikerIds.add(authenticatedUser);
            tattooWork.setDislikerIds(tattooWorkDislikerIds);
            tattooWorkRepository.save(tattooWork);
        } else {
            throw new AlreadyDislikedException();
        }
    }

    public TattooArtistPriceInterval userPriceInterval(String id) {
        UserResponseDto user = null;
        if (UUID_REGEX_PATTERN.matcher(id).matches()) {
            user = userRepository.findById(UUID.fromString(id))
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new);
        } else {
            user = userRepository.findByUid(id)
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new);
        }
        var tattooWorkMAX = tattooWorkRepository.findTopByMadeBy_IdOrderByConvertedPriceValueDesc(user.getId()).orElse(null);
        var tattooWorkMIN = tattooWorkRepository.findTopByMadeBy_IdOrderByConvertedPriceValueAsc(user.getId()).orElse(null);
        return createTattooArtistPriceInterval(tattooWorkMAX, tattooWorkMIN);
    }

    public TattooArtistPriceInterval createTattooArtistPriceInterval(TattooWork tattooWorkWithMaxPrice, TattooWork tattooWorkWithMinPrice) {
        if (tattooWorkWithMaxPrice == null || tattooWorkWithMinPrice == null) {
            return null;
        } else {
            TattooArtistPriceInterval tattooArtistPriceInterval = new TattooArtistPriceInterval();
            tattooArtistPriceInterval.setMaxTattooWorkPrice(tattooWorkWithMaxPrice.getPrice());
            tattooArtistPriceInterval.setMinTattooWorkPrice(tattooWorkWithMinPrice.getPrice());
            tattooArtistPriceInterval.setMaxTattooWorkPriceCurrency(tattooWorkWithMaxPrice.getCurrency());
            tattooArtistPriceInterval.setMinTattooWorkPriceCurrency(tattooWorkWithMinPrice.getCurrency());
            return tattooArtistPriceInterval;
        }
    }

    public UserResponseDto getAuthenticatedUser() {
        var user = securityService.getUser();
        if (user == null) {
            return null;
        } else {
            return user.toUserResponseDto();
        }
    }

    public void deleteById(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        commentRepository.deleteAll(user.getComments());
        tattooWorkRepository.deleteAll(user.getTattooWorks());
        reviewRepository.deleteAll(user.getTakenReviews());
        var tattooWorkList = tattooWorkRepository.findAllByClient_Id(id);
        tattooWorkList.forEach(tattooWork -> tattooWork.setClient(null));
        userRepository.deleteById(id);
    }

    public List<TattooWorksResponseDto> getTattooWorks() {
        return tattooWorkRepository.findAllByMadeBy_Id(securityService.getUser().getId()).stream().map(TattooWork::toTattooWorksResponseDto).collect(Collectors.toList());
    }

    //TODO pagination
    public List<TattooWorksResponseDto> getFavoriteTattooWorks() {
        var user = securityService.getUser();
        var tattooWorkList = new ArrayList<>(user.getFavoriteTattooWorks());
        return tattooWorkList.stream().map(TattooWork::toTattooWorksResponseDto).toList();
    }

    public List<UserResponseDto> getFavoriteTattooArtists() {
        var user = securityService.getUser();
        var favoriteTattooArtistList = new ArrayList<>(user.getFavouriteArtists());
        return favoriteTattooArtistList.stream().map(User::toUserResponseDto).toList();
    }
}
