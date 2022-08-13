package com.example.tattooartistapp.user;

import com.example.tattooartistapp.apis.UsersApi;
import com.example.tattooartistapp.models.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController implements UsersApi {

    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        return UsersApi.super.createUser(userDto);
    }

    @Override
    public ResponseEntity<UserDto> deleteUser(UUID id) {
        return UsersApi.super.deleteUser(id);
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return UsersApi.super.getAllUsers();
    }

    @Override
    public ResponseEntity<UserDto> getUserById(UUID id) {
        return UsersApi.super.getUserById(id);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(UUID id, UserDto userDto) {
        return UsersApi.super.updateUser(id, userDto);
    }
}
