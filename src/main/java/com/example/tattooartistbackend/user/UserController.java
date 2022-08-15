package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.user.apis.UsersApi;
import com.example.tattooartistbackend.user.models.UserDto;
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
    public ResponseEntity<UserDto> createArtistAccount(UUID id, UserDto userDto) {
        return ResponseEntity.ok(userService.createArtistAccount(id, userDto));
    }

    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDto> favoriteTattooArtist(UUID userId, UUID artistId) {
        return  ResponseEntity.ok(userService.favoriteTattooArtist(userId, artistId));
    }

    @Override
    public ResponseEntity<UserDto> favoriteTattooWork(UUID userId, UUID postId) {
        return  ResponseEntity.ok(userService.favoriteTattooWork(userId, postId));
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers(String firstName, String lastName) {
        return new ResponseEntity<>(userService.findAllUsers(firstName,lastName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(UUID id) {
        return userService.findUserById(id)
                .map(userDto -> new ResponseEntity<>(userDto,HttpStatus.OK))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public ResponseEntity<Void> unfavoriteTattooArtist(UUID userId, UUID artistId) {
        userService.unfavoriteTattooArtist(userId, artistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> unfavoriteTattooWork(UUID userId, UUID postId) {
        userService.unfavoriteTattooWork(userId, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(UUID id, UserDto userDto) {
        return userService.updateUser(id,userDto)
                .map(userDto1 -> new ResponseEntity<>(userDto1,HttpStatus.CREATED))
                .orElseThrow(UserNotFoundException::new);
    }
}
