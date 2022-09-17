package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.apis.UsersApi;
import com.example.tattooartistbackend.generated.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
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

    /**
     * DELETE /users/{id}
     * delete user
     *
     * @param id user id (required)
     * @return no content (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> deleteUserById(UUID id) {
         userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    //pagination ekle
    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers(Integer page,Integer size,String firstName, String lastName) {
        return new ResponseEntity<>(userService.findAllUsers(page,size,firstName, lastName), HttpStatus.OK);
    }

    /**
     * GET /users/me/favorite-tattoo-works
     *
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<List<TattooWorksResponseDto>> getFavoriteTattooWorks() {
        return new ResponseEntity<>(userService.getFavoriteTattooWorks(), HttpStatus.OK);
    }

    /**
     * GET /users/me
     *
     * @return ok (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<UserResponseDto> getAuthenticatedUser() {
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    /**
     * GET /users/me/tattooworks
     *
     * @return ok (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<List<TattooWorksResponseDto>> getTattooWorks() {
        return new ResponseEntity<>(userService.getTattooWorks(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(String id) {
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
    public ResponseEntity<TattooArtistPriceInterval> userPriceInterval(String id) {
        return ResponseEntity.ok(userService.userPriceInterval(id));
    }

    /**
     * GET /users/me/favorite-tattoo-artist
     *
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<List<UserResponseDto>> getFavoriteTattooArtists() {
        return ResponseEntity.ok(userService.getFavoriteTattooArtists());
    }
}
