package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.apis.UsersApi;
import com.example.tattooartistbackend.generated.models.ClientReqDto;
import com.example.tattooartistbackend.generated.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.generated.models.TattooArtistPriceInterval;
import com.example.tattooartistbackend.generated.models.UserResponseDto;
import com.example.tattooartistbackend.generated.models.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseDto> createArtistAccount(TattooArtistAccReqDto tattooArtistAccReqDto) {
        return ResponseEntity.ok(userService.createArtistAccount(tattooArtistAccReqDto));
    }

    @Override
    public ResponseEntity<UserResponseDto> createUser(ClientReqDto clientReqDto) {
        return new ResponseEntity<>(userService.createUser(clientReqDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> dislikeTattooWork(UUID postId) {
        userService.dislike(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDto> favoriteTattooArtist(UUID artistId) {
        return ResponseEntity.ok(userService.favoriteTattooArtist(artistId));
    }

    @Override
    public ResponseEntity<UserResponseDto> favoriteTattooWork(UUID postId) {
        return ResponseEntity.ok(userService.favoriteTattooWork(postId));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers(String firstName, String lastName) {
        return new ResponseEntity<>(userService.findAllUsers(firstName, lastName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(UUID id) {
        return userService.findUserById(id)
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public ResponseEntity<Void> likeTattooWork(UUID postId) {
        userService.like(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unfavoriteTattooArtist(UUID artistId) {
        userService.unfavoriteTattooArtist(artistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> unfavoriteTattooWork(UUID postId) {
        userService.unfavoriteTattooWork(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(UserUpdateRequestDto userUpdateRequestDto) {
        return userService.updateUser(userUpdateRequestDto)
                .map(userDto1 -> new ResponseEntity<>(userDto1, HttpStatus.CREATED))
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * GET /users/{id}/price-interval
     * price interval
     *
     * @param id user id (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooArtistPriceInterval> userPriceInterval(UUID id) {
        return ResponseEntity.ok(userService.userPriceInterval(id));
    }
}
