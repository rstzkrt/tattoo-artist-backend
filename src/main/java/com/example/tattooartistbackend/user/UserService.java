package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.comment.CommentRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.user.models.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public UserDto createUser(UserDto userDto) {//as client
        Address address = addressRepository.save(addressFromDto(userDto));
        return userRepository.save(User.fromDto(userDto, address, null, null, null, null)).toDto();
    }

    public List<UserDto> findAllUsers(String firstName, String lastName) {
        return userRepository.findAllUsers(firstName, lastName)
                .stream()
                .map(User::toDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> findUserById(UUID id) {
        return Optional.of(userRepository.findById(id)
                .map(User::toDto)
                .orElseThrow(UserNotFoundException::new));
    }

    public Optional<UserDto> updateUser(UUID id, UserDto userDto) {
        List<TattooWork> tattooWorks = userDto.getTattooWorkIds()
                .stream()
                .map(tattooWorkRepository::findById)
                .map(Optional::orElseThrow)
                .toList();
        List<TattooWork> favoriteTattooWorks = userDto.getTattooWorkIds().stream().map(tattooWorkRepository::findById).map(Optional::orElseThrow).collect(Collectors.toList());
        List<Comment> comments = userDto.getTattooWorkIds().stream().map(commentRepository::findById).map(Optional::orElseThrow).collect(Collectors.toList());
        List<User> favoriteArtists = userDto.getTattooWorkIds().stream().map(userRepository::findById).map(Optional::orElseThrow).collect(Collectors.toList());
        return Optional.ofNullable(userRepository.findById(id)
                .map(user -> {
                    Address address = addressRepository.findById(user.getBusinessAddress().getId()).orElseThrow();
                    address.setCity(userDto.getCity());
                    address.setState(userDto.getState());
                    address.setCountry(userDto.getCountry());
                    address.setPostalCode(userDto.getPostalCode());
                    address.setStreet(userDto.getStreet());
                    address.setOtherInformation(userDto.getOtherInformation());
                    addressRepository.save(address);
                    User userToUpdate = User.fromDto(userDto, address, favoriteTattooWorks, tattooWorks, favoriteArtists, comments);
                    userToUpdate.setId(id);
                    return userRepository.save(userToUpdate);
                })
                .map(User::toDto)
                .orElseThrow(UserNotFoundException::new));
    }

    public void deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserDto favoriteTattooArtist(UUID userId, UUID artistId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

        List<User> favouriteArtists = user.getFavouriteArtists();
        favouriteArtists.add(artist);
        user.setFavouriteArtists(favouriteArtists);
        return userRepository.save(user).toDto();
    }

    public void unfavoriteTattooArtist(UUID userId, UUID artistId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User artist = userRepository.findById(artistId).orElseThrow(UserNotFoundException::new);

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

    public UserDto favoriteTattooWork(UUID userId, UUID postId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        TattooWork tattooWork = tattooWorkRepository.findById(postId).orElseThrow();

        List<TattooWork> favoriteTattooWorks = user.getFavoriteTattooWorks();
        favoriteTattooWorks.add(tattooWork);
        user.setFavoriteTattooWorks(favoriteTattooWorks);
        return userRepository.save(user).toDto();
    }

    public UserDto createArtistAccount(UUID id, UserDto userDto) {//TODO
        return null;
    }

    private Address addressFromDto(UserDto userDto) {
        return Address.builder()
                .state(userDto.getState())
                .postalCode(userDto.getPostalCode())
                .country(userDto.getCountry())
                .city(userDto.getCity())
                .street(userDto.getStreet())
                .otherInformation(userDto.getOtherInformation())
                .build();
    }
}
