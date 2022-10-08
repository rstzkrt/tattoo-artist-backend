package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.*;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.user.elasticsearch.UserDocument;
import com.example.tattooartistbackend.user.elasticsearch.UserEsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private UserEsRepository userEsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private ClientReqDto clientReqDto;
    private TattooArtistAccReqDto tattooArtistAccReqDto;
    private User authenticatedUser;
    private UserUpdateRequestDto userUpdateRequestDto;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .state("")
                .postalCode("")
                .street("")
                .country("")
                .city("")
                .otherInformation("")
                .build();
        addressRepository.save(address);

        user = User.builder()
                .uid("test_uid")
                .avatarUrl("test_avatar_url")
                .phoneNumber("000000000")
                .firstName("test_first_name")
                .lastName("test_last_name")
                .email("test_email@email.com")
                .workingDaysList(new ArrayList<>())
                .hasArtistPage(false)
                .businessAddress(null)
                .tattooWorks(new ArrayList<>())
                .favouriteArtists(new ArrayList<>())
                .comments(new ArrayList<>())
                .favoriteTattooWorks(new ArrayList<>())
                .givenReviews(new ArrayList<>())
                .takenReviews(new ArrayList<>())
                .userReports(new ArrayList<>())
                .likedTattooWorks(new ArrayList<>())
                .dislikedTattooWorks(new ArrayList<>())
                .tattooStyles(new ArrayList<>())
                .languages(new ArrayList<>())
                .averageRating((double) 0)
                .build();
        authenticatedUser = userRepository.save(user);

        var userDocument = UserDocument.builder()
                .id(authenticatedUser.getId())
                .hasTattooArtistAcc(authenticatedUser.isHasArtistPage())
                .fullName(authenticatedUser.getFirstName() +" "+ authenticatedUser.getLastName())
                .avatarUrl(authenticatedUser.getAvatarUrl())
                .languages(authenticatedUser.getLanguages().stream().map(Language::getValue).collect(Collectors.toList()))
                .gender(authenticatedUser.getGender())
                .city(authenticatedUser.getBusinessAddress() == null ? "" : authenticatedUser.getBusinessAddress().getCity())
                .country(authenticatedUser.getBusinessAddress() == null ? "" : authenticatedUser.getBusinessAddress().getCountry())
                .averageRating(authenticatedUser.getAverageRating())
                .build();
        userEsRepository.save(userDocument);

        clientReqDto = new ClientReqDto();
        clientReqDto.setUid("test2_uid");
        clientReqDto.setFirstName("test2_uid");
        clientReqDto.setLastName("test2_avatar_url");
        clientReqDto.setEmail("test2_email@email.com");
        clientReqDto.setAvatarUrl("test2_avatar_url");

        tattooArtistAccReqDto = new TattooArtistAccReqDto();
        tattooArtistAccReqDto.setCity("");
        tattooArtistAccReqDto.setCountry("");
        tattooArtistAccReqDto.setGender(Gender.MALE);
        tattooArtistAccReqDto.setLanguages(new ArrayList<>());
        tattooArtistAccReqDto.setTattooStyles(new ArrayList<>());
        tattooArtistAccReqDto.setDateOfBirth(LocalDate.of(1999, 3, 23));
        tattooArtistAccReqDto.setState("");
        tattooArtistAccReqDto.setPhoneNumber("");
        tattooArtistAccReqDto.setCareerDescription("");
        tattooArtistAccReqDto.setStreet("");
        tattooArtistAccReqDto.setOtherInformation("");
        tattooArtistAccReqDto.setWorkDays(new ArrayList<>());

        userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setAvatarUrl("");
        userUpdateRequestDto.firstName("John");
        //..
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        userEsRepository.deleteAll();
    }

    @Test
    @DisplayName("/users - POST - 201")
    void shouldReturnCreated_whenCreateUser() throws Exception {
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientReqDto))
                .accept(MediaType.APPLICATION_JSON));
        var createdUser = userRepository.findByUid(clientReqDto.getUid()).orElseThrow(UserNotFoundException::new);
        res.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdUser.getId().toString()))
                .andExpect(jsonPath("$.uid").value(clientReqDto.getUid()))
                .andExpect(jsonPath("$.firstName").value(clientReqDto.getFirstName()))
                .andExpect(jsonPath("$.email").value(clientReqDto.getEmail()))
                .andExpect(jsonPath("$.lastName").value(clientReqDto.getLastName()))
                .andExpect(jsonPath("$.avatarUrl").value(clientReqDto.getAvatarUrl()));
    }

    @Test
    @DisplayName("/users - POST - 400")
    void shouldReturnBadRequest_whenCreateUserWithEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("users/me - GET - 200")
    void getAuthenticatedUser_shouldReturnAuthenticatedUser() throws Exception {
        when(securityService.getUser()).thenReturn(user);
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.uid").value(user.getUid()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));
    }

    @Test
    @DisplayName("users/me - PATCH - 200")
    void createTattooArtistAccount_shouldReturnOK() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tattooArtistAccReqDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authenticatedUser.getId().toString()))
                .andExpect(jsonPath("$.uid").value(authenticatedUser.getUid()))
                .andExpect(jsonPath("$.firstName").value(authenticatedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(authenticatedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(authenticatedUser.getEmail()));
    }

    @Test
    @DisplayName("users/me - PATCH - 417")
    void createTattooArtistAccount_shouldThrowUnderAgeException() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        tattooArtistAccReqDto.setDateOfBirth(LocalDate.now());
        var expectedMessage = "The user is under age. should be below 18 to create an artist page";
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tattooArtistAccReqDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(405))
                .andDo(result -> {
                    var body = objectMapper.readTree(result.getResponse().getContentAsString());
                    var actualMessage = body.get("message").asText();
                    assertTrue(actualMessage.contains(expectedMessage));
                });
    }

    @Test
    @DisplayName("users/me - PUT - 201")
    void updateTattooArtist_shouldReturnOk() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setBusinessAddress(address);
        userRepository.save(authenticatedUser);
        //when
        when(securityService.getUser()).thenReturn(authenticatedUser);
        //then
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andDo(print());
    }

}