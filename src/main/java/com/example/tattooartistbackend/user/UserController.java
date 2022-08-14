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
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(UUID id) {
        return userService.findUserById(id)
                .map(userDto -> new ResponseEntity<>(userDto,HttpStatus.OK))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(UUID id, UserDto userDto) {
        return userService.updateUser(id,userDto)
                .map(userDto1 -> new ResponseEntity<>(userDto1,HttpStatus.CREATED))
                .orElseThrow(UserNotFoundException::new);
    }
}
