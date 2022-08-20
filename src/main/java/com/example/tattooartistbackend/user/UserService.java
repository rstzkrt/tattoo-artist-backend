package com.example.tattooartistbackend.user;


import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;

import com.example.tattooartistbackend.comment.CommentRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.models.ClientReqDto;
import com.example.tattooartistbackend.user.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.user.models.UserResponseDto;
import com.example.tattooartistbackend.user.models.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TattooWorkRepository tattooWorkRepository;

    private final CommentRepository commentRepository;

    public UserResponseDto createUser(ClientReqDto clientReqDto) {
        //assign uid
        return userRepository.save(User.fromClientRequestDto(clientReqDto)).toUserResponseDto();
    }

    public List<UserResponseDto> findAllUsers(String firstName, String lastName) {
        return userRepository.findAllUsers(firstName, lastName)
                .stream()
                .map(User::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> findUserById(UUID id) {
        return Optional.of(userRepository.findById(id)
                .map(User::toUserResponseDto)
                .orElseThrow(UserNotFoundException::new));
    }

    public Optional<UserResponseDto> updateUser(UUID id, UserUpdateRequestDto userUpdateRequestDto) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(user -> {
                    Address address = updateReqToAddress(user, userUpdateRequestDto.getCity(), userUpdateRequestDto.getState(), userUpdateRequestDto.getCountry(), userUpdateRequestDto.getPostalCode(), userUpdateRequestDto.getStreet(), userUpdateRequestDto.getOtherInformation());
                    User userToUpdate = User.fromUserUpdateRequestDto(userUpdateRequestDto, address, user.getTattooWorks(), user.getTattooWorks(), user.getFavouriteArtists(), user.getComments());
                    userToUpdate.setId(user.getId());
                    userToUpdate.setUid(user.getUid());
                    userToUpdate.setHasArtistPage(user.isHasArtistPage());
                    userToUpdate.setDateOfBirth(user.getDateOfBirth());
                    return userRepository.save(userToUpdate);
                })
                .map(User::toUserResponseDto)
                .orElseThrow(UserNotFoundException::new));
    }

    public void deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserResponseDto favoriteTattooArtist(UUID userId, UUID artistId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

        List<User> favouriteArtists = user.getFavouriteArtists();
        if(favouriteArtists.contains(artist)) {
            return user.toUserResponseDto();
        }else {
            favouriteArtists.add(artist);
            user.setFavouriteArtists(favouriteArtists);
            return userRepository.save(user).toUserResponseDto();
        }
    }

    public void unfavoriteTattooArtist(UUID userId, UUID artistId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User artist =userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

        List<User> favouriteArtists = user.getFavouriteArtists();
        favouriteArtists.remove(artist);
        user.setFavouriteArtists(favouriteArtists);
        userRepository.save(user);
    }

    public void unfavoriteTattooWork(UUID userId, UUID postId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        TattooWork tattooWork = tattooWorkRepository.findById(postId).orElseThrow();

        List<TattooWork> favoriteTattooWorks = user.getFavoriteTattooWorks();
        favoriteTattooWorks.remove(tattooWork);
        user.setFavoriteTattooWorks(favoriteTattooWorks);
        userRepository.save(user);
    }

    public UserResponseDto favoriteTattooWork(UUID userId, UUID postId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        TattooWork tattooWork = tattooWorkRepository.findById(postId).orElseThrow();
        List<TattooWork> favoriteTattooWorks = user.getFavoriteTattooWorks();

        if(favoriteTattooWorks.contains(tattooWork)) {
            return user.toUserResponseDto();
        }else {
            favoriteTattooWorks.add(tattooWork);
            user.setFavoriteTattooWorks(favoriteTattooWorks);
            return userRepository.save(user).toUserResponseDto();
        }
    }

    public UserResponseDto createArtistAccount(UUID id, TattooArtistAccReqDto tattooArtistAccReqDto) {
        return userRepository.findById(id)
                .map(user -> {
                    if (LocalDate.now().getYear() - user.getDateOfBirth().getYear() < 18) {
                        try {
                            throw new RuntimeException("UNDER AGE!");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Address address = Address.builder()
                            .otherInformation(tattooArtistAccReqDto.getOtherInformation())
                            .street(tattooArtistAccReqDto.getStreet())
                            .city(tattooArtistAccReqDto.getCity())
                            .country(tattooArtistAccReqDto.getCountry())
                            .postalCode(tattooArtistAccReqDto.getPostalCode())
                            .state(tattooArtistAccReqDto.getState())
                            .build();

                    addressRepository.save(address);
                    User userToUpdate = User.fromTattooArtistAccReqDto(tattooArtistAccReqDto, address, user.getTattooWorks(), user.getTattooWorks(), user.getFavouriteArtists(), user.getComments());

                    userToUpdate.setId(user.getId());
                    userToUpdate.setUid(user.getUid());
                    userToUpdate.setEmail(user.getEmail());
                    userToUpdate.setFirstName(user.getFirstName());
                    userToUpdate.setLastName(user.getLastName());
                    userToUpdate.setDateOfBirth(user.getDateOfBirth());
                    userToUpdate.setAvatarUrl(user.getAvatarUrl());

                    System.out.println(user.toString());
                    return userRepository.save(userToUpdate);
                })
                .map(User::toUserResponseDto)
                .orElseThrow(UserNotFoundException::new);
    }

    private Address getAddress(User user, String city, String state, String country, String postalCode, String street, String otherInformation) {
        if(!user.isHasArtistPage()){
            return null;
        }
//        if(user.getBusinessAddress()==null){
//            try {
//                throw  new Exception("user address is null");
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
        Address address = addressRepository.findById(user.getBusinessAddress().getId()).orElseThrow();
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
        return getAddress(user, city, state, country, postalCode, street, otherInformation);
    }
    public void like(){

    }
    public void dislike(){

    }

}
