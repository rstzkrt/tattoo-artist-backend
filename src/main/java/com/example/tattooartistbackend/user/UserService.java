package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.comment.CommentRepository;
import com.example.tattooartistbackend.exceptions.*;
import com.example.tattooartistbackend.generated.models.ClientReqDto;
import com.example.tattooartistbackend.generated.models.Language;
import com.example.tattooartistbackend.generated.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.generated.models.TattooArtistPriceInterval;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.generated.models.UserResponseDto;
import com.example.tattooartistbackend.generated.models.UserResponseDtoPageable;
import com.example.tattooartistbackend.generated.models.UserUpdateRequestDto;
import com.example.tattooartistbackend.review.Review;
import com.example.tattooartistbackend.review.ReviewRepository;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.tattooWork.TattooWorkService;
import com.example.tattooartistbackend.user.elasticsearch.UserDocument;
import com.example.tattooartistbackend.user.elasticsearch.UserEsRepository;
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
    private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TattooWorkRepository tattooWorkRepository;
    private final TattooWorkService tattooWorkService;
    private final ReviewRepository reviewRepository;
    private final SecurityService securityService;
    private final CommentRepository commentRepository;
    private final UserEsRepository userEsRepository;
    private final RoleService roleService;

    public UserResponseDto createUser(ClientReqDto clientReqDto) {
        var user = userRepository.save(User.fromClientRequestDto(clientReqDto)).toUserResponseDto();
        UserDocument userDocument = new UserDocument();
        userDocument.setCity(user.getCity());
        userDocument.setId(user.getId());
        userDocument.setFullName(user.getFirstName() + " " + user.getLastName());
        userDocument.setCountry(user.getCountry());
        userDocument.setHasTattooArtistAcc(false);
        userDocument.setAvatarUrl(user.getAvatarUrl());
        userEsRepository.save(userDocument);
        return user;
    }

    public UserResponseDtoPageable findAllUsers(Integer page, Integer size, String firstName, String lastName) {
        Pageable pageable = PageRequest.of(page, size);
        var list = userRepository.findAllTattooArtist(firstName, lastName, pageable)
                .getContent()
                .stream()
                .map(User::toUserResponseDto).toList();
        UserResponseDtoPageable userResponseDtoPageable = new UserResponseDtoPageable();
        userResponseDtoPageable.setTattooArtists(list);
        userResponseDtoPageable.setTotalElements((int) userRepository.findAllTattooArtist(firstName, lastName, pageable).getTotalElements());
        return userResponseDtoPageable;
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
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow(() -> new RuntimeException("userDocument not found!"));
        if (LocalDate.now().getYear() - userUpdateRequestDto.getBirthDate().getYear() < 18) {
            throw new UnderAgeException();
        }
        if (authenticatedUser.isHasArtistPage()) {
            return Optional.ofNullable(userRepository.findById(authenticatedUser.getId())
                    .map(user -> {
                        var givenReviews = reviewRepository.findAllByPostedBy_Id(user.getId());
                        var takenReviews = reviewRepository.findAllByReceiver_Id(user.getId());
                        Address address = updateReqToAddress(user, userUpdateRequestDto.getCity(), userUpdateRequestDto.getState(), userUpdateRequestDto.getCountry(), userUpdateRequestDto.getPostalCode(), userUpdateRequestDto.getStreet(), userUpdateRequestDto.getOtherInformation());
                        User userToUpdate = getUserToUpdate(userUpdateRequestDto, user, givenReviews, takenReviews, address);
                        var updatedUser = userRepository.save(userToUpdate);
                        updateUserDocument(userDocument, userToUpdate, updatedUser);
                        userEsRepository.save(userDocument);
                        return updatedUser;
                    })
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new));
        } else {
            return Optional.ofNullable(userRepository.findById(authenticatedUser.getId())
                    .map(user -> {
                        var userUpdated = userRepository.save(user.fromBasicPatchRequest(userUpdateRequestDto));
                        userDocument.setFullName(userUpdated.getFirstName() + " " + userUpdated.getLastName() );
                        userDocument.setAvatarUrl(userUpdated.getAvatarUrl());
                        userEsRepository.save(userDocument);
                        return userUpdated;
                    })
                    .map(User::toUserResponseDto)
                    .orElseThrow(UserNotFoundException::new));
        }

    }

    public void like(UUID postId) {
        var authenticatedUser = securityService.getUser();
        var tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        if (!tattooWorkRepository.existsByLikerIdsContainsAndId(authenticatedUser, postId)) {
            if (tattooWorkRepository.existsByDislikerIdsContainsAndId(authenticatedUser, postId)) {
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
        if (!tattooWorkRepository.existsByDislikerIdsContainsAndId(authenticatedUser, postId)) {
            if (tattooWorkRepository.existsByLikerIdsContainsAndId(authenticatedUser, postId)) {
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

    public UserResponseDto getAuthenticatedUser() {
        var user = securityService.getUser();
        if (user == null) {
            return null;
        } else {
            return user.toUserResponseDto();
        }
    }

    public void deleteById(UUID id) {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!roleService.isAdmin(authenticatedUser.getUid())) {
            throw new NoAdminRightsException();
        }
        //handle favorites
        user.setFavoriteTattooWorks(null);
        user.setFavouriteArtists(null);
        user.setComments(null);
        userRepository.save(user);
        //handle people favorite me
        List<User> toDOKendmiAra = userRepository.findAllByFavouriteArtistsIn(List.of(user));
        for (User beniFavUser : toDOKendmiAra) {
            var beniFavUserFavListi = beniFavUser.getFavouriteArtists();
            beniFavUserFavListi.remove(user);
            beniFavUser.setFavouriteArtists(beniFavUserFavListi);
            userRepository.save(beniFavUser);
        }
        //handle own tatoo works
        var listOfTattooWorks = user.getTattooWorks();
        for (TattooWork listOfTattooWork : listOfTattooWorks) {
            tattooWorkService.deleteTattooWork(listOfTattooWork.getId());
        }
        //handle tattoworks which we are client of
        var tattooWorkList = tattooWorkRepository.findAllByClient_Id(user.getId());
        tattooWorkList.forEach(tattooWork -> {
            tattooWork.setClient(null);
            tattooWork.setComment(null);
            tattooWorkRepository.save(tattooWork);
        });
        //handle tattoo dislikes
        var dislikedTattooWorks = tattooWorkRepository.findByDislikerIdsIn(List.of(user));
        dislikedTattooWorks.forEach(tattooWork -> {
            tattooWork.getDislikerIds().removeIf(user1 -> user1.getId() == user.getId());
            tattooWork.getLikerIds().removeIf(user1 -> user1.getId() == user.getId());
        });
        //handle tattoo likes
        var likedTattooWorks = tattooWorkRepository.findByLikerIdsIn(List.of(user));
        likedTattooWorks.forEach(tattooWork -> {
            tattooWork.getDislikerIds().removeIf(user1 -> user1.getId() == user.getId());
            tattooWork.getLikerIds().removeIf(user1 -> user1.getId() == user.getId());
        });
        //get comments by postedby id and delete
        var comment = commentRepository.findByPostedBy_Id(user.getId());
        if (comment != null) {
            commentRepository.deleteById(comment.getId());
        }
        userRepository.deleteById(user.getId());
        userEsRepository.deleteById(user.getId());
    }

    public List<TattooWorksResponseDto> getTattooWorks() {
        return tattooWorkRepository.findAllByMadeBy_Id(securityService.getUser().getId()).stream().map(TattooWork::toTattooWorksResponseDto).collect(Collectors.toList());
    }

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

    public void deleteUser() {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        //handle favorites
        user.setFavoriteTattooWorks(null);
        user.setFavouriteArtists(null);
        user.setComments(null);
        userRepository.save(user);
        //handle people favorite me
        List<User> toDOKendmiAra = userRepository.findAllByFavouriteArtistsIn(List.of(user));
        for (User beniFavUser : toDOKendmiAra) {
            var beniFavUserFavListi = beniFavUser.getFavouriteArtists();
            beniFavUserFavListi.remove(user);
            beniFavUser.setFavouriteArtists(beniFavUserFavListi);
            userRepository.save(beniFavUser);
        }
        //handle own tatoo works
        var listOfTattooWorks = user.getTattooWorks();
        for (TattooWork listOfTattooWork : listOfTattooWorks) {
            tattooWorkService.deleteTattooWork(listOfTattooWork.getId());
        }
        //handle tattoworks which we are client of
        var tattooWorkList = tattooWorkRepository.findAllByClient_Id(user.getId());
        tattooWorkList.forEach(tattooWork -> {
            tattooWork.setClient(null);
            tattooWork.setComment(null);
            tattooWorkRepository.save(tattooWork);
        });
        //handle tattoo dislikes
        var dislikedTattooWorks = tattooWorkRepository.findByDislikerIdsIn(List.of(user));
        dislikedTattooWorks.forEach(tattooWork -> {
            tattooWork.getDislikerIds().removeIf(user1 -> user1.getId() == user.getId());
            tattooWork.getLikerIds().removeIf(user1 -> user1.getId() == user.getId());
        });
        //handle tattoo likes
        var likedTattooWorks = tattooWorkRepository.findByLikerIdsIn(List.of(user));
        likedTattooWorks.forEach(tattooWork -> {
            tattooWork.getDislikerIds().removeIf(user1 -> user1.getId() == user.getId());
            tattooWork.getLikerIds().removeIf(user1 -> user1.getId() == user.getId());
        });
        //get comments by postedby id and delete
        var comment = commentRepository.findByPostedBy_Id(user.getId());
        if (comment != null) {
            commentRepository.deleteById(comment.getId());
        }
        userRepository.deleteById(user.getId());
        userEsRepository.deleteById(user.getId());
    }

    public UserResponseDto favoriteTattooArtist(UUID artistId) {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        var artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);
        var favouriteArtists = user.getFavouriteArtists();
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
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        var artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);
        var favouriteArtists = user.getFavouriteArtists();
        favouriteArtists.remove(artist);
        user.setFavouriteArtists(favouriteArtists);
        userRepository.save(user);
    }

    public void unfavoriteTattooWork(UUID postId) {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        var tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        var favoriteTattooWorks = user.getFavoriteTattooWorks();
        favoriteTattooWorks.remove(tattooWork);
        user.setFavoriteTattooWorks(favoriteTattooWorks);
        userRepository.save(user);
    }

    public UserResponseDto favoriteTattooWork(UUID postId) {
        var authenticatedUser = securityService.getUser();
        var user = userRepository.findById(authenticatedUser.getId()).orElseThrow(UserNotFoundException::new);
        var tattooWork = tattooWorkRepository.findById(postId).orElseThrow(TattooWorkNotFoundException::new);
        var favoriteTattooWorks = user.getFavoriteTattooWorks();
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
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow(() -> new RuntimeException("userDocument not found!"));
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
                    userDocument.setCity(userToUpdate.getBusinessAddress().getCity());
                    userDocument.setCountry(userToUpdate.getBusinessAddress().getCountry());
                    userDocument.setHasTattooArtistAcc(true);
                    userDocument.setLanguages(userToUpdate.getLanguages().stream().map(Language::getValue).toList());
                    userDocument.setGender(userToUpdate.getGender());
                    userEsRepository.save(userDocument);
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

    private static void updateUserDocument(UserDocument userDocument, User userToUpdate, User updatedUser) {
        userDocument.setCity(updatedUser.getBusinessAddress().getCity());
        userDocument.setId(updatedUser.getId());
        userDocument.setFullName(updatedUser.getFirstName() + " " + updatedUser.getLastName());
        userDocument.setCountry(updatedUser.getBusinessAddress().getCountry());
        userDocument.setHasTattooArtistAcc(true);
        userDocument.setAvatarUrl(updatedUser.getAvatarUrl());
        userDocument.setLanguages(userToUpdate.getLanguages().stream().map(Language::getValue).toList());
        userDocument.setGender(userToUpdate.getGender());
    }

    private static User getUserToUpdate(UserUpdateRequestDto userUpdateRequestDto, User user, List<Review> givenReviews, List<Review> takenReviews, Address address) {
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
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setUid(user.getUid());
        userToUpdate.setHasArtistPage(user.isHasArtistPage());
        userToUpdate.setDateOfBirth(user.getDateOfBirth());
        userToUpdate.setAverageRating(user.getAverageRating());
        return userToUpdate;
    }

    private TattooArtistPriceInterval createTattooArtistPriceInterval(TattooWork tattooWorkWithMaxPrice, TattooWork tattooWorkWithMinPrice) {
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
}
