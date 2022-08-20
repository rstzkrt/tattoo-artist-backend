package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.user.apis.UsersApi;
import com.example.tattooartistbackend.user.models.*;
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
    public ResponseEntity<UserResponseDto> createArtistAccount(UUID id, TattooArtistAccReqDto tattooArtistAccReqDto) {
        return ResponseEntity.ok(userService.createArtistAccount(id, tattooArtistAccReqDto));
    }

    @Override
    public ResponseEntity<UserResponseDto> createUser(ClientReqDto clientReqDto) {
        return new ResponseEntity<>(userService.createUser(clientReqDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * DELETE /users/{user_id}/tattoo-works/{post_id}/like
     * dislike
     *
     * @param userId user id (required)
     * @param postId artist id (required)
     * @return no content (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> dislikeTattooWork(UUID userId, UUID postId) {
        userService.dislike(userId,postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDto> favoriteTattooArtist(UUID userId, UUID artistId) {
        return  ResponseEntity.ok(userService.favoriteTattooArtist(userId, artistId));
    }

    @Override
    public ResponseEntity<UserResponseDto> favoriteTattooWork(UUID userId, UUID postId) {
        return  ResponseEntity.ok(userService.favoriteTattooWork(userId, postId));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers(String firstName, String lastName) {
        return new ResponseEntity<>(userService.findAllUsers(firstName,lastName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(UUID id) {
        return userService.findUserById(id)
                .map(userDto -> new ResponseEntity<>(userDto,HttpStatus.OK))
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * POST /users/{user_id}/tattoo-works/{post_id}/like
     * like
     *
     * @param userId user id (required)
     * @param postId post_id (required)
     * @return no content (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> likeTattooWork(UUID userId, UUID postId) {
        userService.like(userId,postId);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<UserResponseDto> updateUser(UUID id, UserUpdateRequestDto userUpdateRequestDto) {
        return userService.updateUser(id,userUpdateRequestDto)
                .map(userDto1 -> new ResponseEntity<>(userDto1,HttpStatus.CREATED))
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
