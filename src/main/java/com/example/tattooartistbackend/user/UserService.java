package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
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

    public UserDto createUser(UserDto userDto) {
        Address address = Address.builder()
                        .state(userDto.getState())
                        .postalCode(userDto.getPostalCode())
                        .country(userDto.getCountry())
                        .city(userDto.getCity())
                        .street(userDto.getStreet())
                        .otherInformation(userDto.getOtherInformation())
                        .build();
        addressRepository.save(address);
        return userRepository.save(User.fromDto(userDto, address)).toDto();
    }

    public List<UserDto> findAllUsers() {// search by name ...
        return userRepository.findAll()
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
        Address address =
                Address.builder()
                        .state(userDto.getState())
                        .postalCode(userDto.getPostalCode())
                        .country(userDto.getCountry())
                        .city(userDto.getCity())
                        .street(userDto.getStreet())
                        .otherInformation(userDto.getOtherInformation())
                        .build();
        addressRepository.save(address);
        return Optional.ofNullable(userRepository.findById(id)
                .map(user -> userRepository.save(User.fromDto(userDto, address)))
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
}
