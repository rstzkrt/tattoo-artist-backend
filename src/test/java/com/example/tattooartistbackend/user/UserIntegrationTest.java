package com.example.tattooartistbackend.user;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.ClientReqDto;
import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.Gender;
import com.example.tattooartistbackend.generated.models.Language;
import com.example.tattooartistbackend.generated.models.TattooArtistAccReqDto;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.generated.models.UserUpdateRequestDto;
import com.example.tattooartistbackend.generated.models.WorkingDays;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Autowired
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @Autowired
    private TattooWorkRepository tattooWorkRepository;

    private User user;
    private User client;
    private ClientReqDto clientReqDto;
    private TattooArtistAccReqDto tattooArtistAccReqDto;
    private User authenticatedUser;
    private UserUpdateRequestDto userUpdateRequestDto;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .state("Mazovian")
                .postalCode("0123")
                .street("Hoza")
                .country("Poland")
                .city("Warsaw")
                .otherInformation("TattooStudio")
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

        client = User.builder()
                .uid("test1_uid")
                .avatarUrl("test1_avatar_url")
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
        userRepository.save(client);

        var userDocument = UserDocument.builder()
                .id(authenticatedUser.getId())
                .hasTattooArtistAcc(authenticatedUser.isHasArtistPage())
                .fullName(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName())
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
        tattooArtistAccReqDto.setCity("Warsaw");
        tattooArtistAccReqDto.setCountry("Poland");
        tattooArtistAccReqDto.setGender(Gender.MALE);
        tattooArtistAccReqDto.setLanguages(new ArrayList<>());
        tattooArtistAccReqDto.setTattooStyles(new ArrayList<>());
        tattooArtistAccReqDto.setDateOfBirth(LocalDate.of(1999, 3, 23));
        tattooArtistAccReqDto.setState("Mazovian");
        tattooArtistAccReqDto.setPhoneNumber("013123123");
        tattooArtistAccReqDto.setCareerDescription("updated_career_description");
        tattooArtistAccReqDto.setStreet("krakowska");
        tattooArtistAccReqDto.setPostalCode("12342");
        tattooArtistAccReqDto.setOtherInformation("");
        tattooArtistAccReqDto.setWorkDays(new ArrayList<>());

        userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setAvatarUrl("update_avatar_url");
        userUpdateRequestDto.setFirstName("updated_John");
        userUpdateRequestDto.setLastName("updated_last_name");
        userUpdateRequestDto.setBirthDate(LocalDate.of(2001, 4, 23));
        userUpdateRequestDto.setCity("Warsaw");
        userUpdateRequestDto.setCountry("Poland");
        userUpdateRequestDto.setState("Mazovian");
        userUpdateRequestDto.setPhoneNumber("013123123");
        userUpdateRequestDto.setCareerDescription("updated_career_description");
        userUpdateRequestDto.setStreet("krakowska");
        userUpdateRequestDto.setOtherInformation("updated_Information");
        userUpdateRequestDto.setPostalCode("12342");
        userUpdateRequestDto.setGender(Gender.MALE);
        userUpdateRequestDto.setLanguages(List.of(Language.CZECH));
        userUpdateRequestDto.setTattooStyles(List.of(TattooStyle.PORTRAITS, TattooStyle.REALISTIC));
        userUpdateRequestDto.setWorkDays(List.of(WorkingDays.FRIDAY, WorkingDays.SUNDAY));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        userEsRepository.deleteAll();
        addressRepository.deleteAll();
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
        var res = mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tattooArtistAccReqDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        var tattooArtist = userRepository.findByUid(authenticatedUser.getUid()).orElseThrow(UserNotFoundException::new);
        res
                .andExpect(jsonPath("$.id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.uid").value(tattooArtist.getUid()))
                .andExpect(jsonPath("$.firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.email").value(tattooArtist.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(tattooArtist.getPhoneNumber()))
                .andExpect(jsonPath("$.state").value(tattooArtist.getBusinessAddress().getState()))
                .andExpect(jsonPath("$.country").value(tattooArtist.getBusinessAddress().getCountry()))
                .andExpect(jsonPath("$.postalCode").value(tattooArtist.getBusinessAddress().getPostalCode()))
                .andExpect(jsonPath("$.street").value(tattooArtist.getBusinessAddress().getStreet()))
                .andExpect(jsonPath("$.city").value(tattooArtist.getBusinessAddress().getCity()))
                .andExpect(jsonPath("$.avatarUrl").value(tattooArtist.getAvatarUrl()))
                .andExpect(jsonPath("$.otherInformation").value(tattooArtist.getBusinessAddress().getOtherInformation()));
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
        var updatedUser = userRepository.save(authenticatedUser);
        //when
        when(securityService.getUser()).thenReturn(authenticatedUser);
        //then
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        var updatedUserDocument = userEsRepository.findById(updatedUser.getId()).orElse(null);
        assertNotNull(updatedUserDocument);
        assertEquals(updatedUserDocument.isHasTattooArtistAcc(), updatedUser.isHasArtistPage());
    }

    @Test
    @DisplayName("users/me - PUT - 201")
    void updateBasic_shouldReturnOk() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(false);
        authenticatedUser.setBusinessAddress(null);
        var updatedUser = userRepository.save(authenticatedUser);
        //when
        when(securityService.getUser()).thenReturn(authenticatedUser);
        //then
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        var updatedUserDocument = userEsRepository.findById(updatedUser.getId()).orElse(null);
        assertNotNull(updatedUserDocument);
        assertEquals(updatedUserDocument.isHasTattooArtistAcc(), updatedUser.isHasArtistPage());
    }


    @Test
    @DisplayName("users/{id} - GET - 200")
    void findByUserById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/users/{id}", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(authenticatedUser.getId().toString()))
                .andExpect(jsonPath("$.uid").value(authenticatedUser.getUid()))
                .andExpect(jsonPath("$.firstName").value(authenticatedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(authenticatedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(authenticatedUser.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(authenticatedUser.getPhoneNumber()));
    }

    @Test
    @DisplayName("users/{id} - GET - 200")
    void findByUserByUId_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/users/{id}", authenticatedUser.getUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(authenticatedUser.getId().toString()))
                .andExpect(jsonPath("$.uid").value(authenticatedUser.getUid()))
                .andExpect(jsonPath("$.firstName").value(authenticatedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(authenticatedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(authenticatedUser.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(authenticatedUser.getPhoneNumber()));
    }

    @Test
    @DisplayName("users - GET - 200")
    void findAllTattooArtists_shouldReturnOk() throws Exception {
        authenticatedUser.setHasArtistPage(true);
        var tattooArtist = userRepository.save(authenticatedUser);
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("firstName", tattooArtist.getFirstName())
                        .param("lastName", tattooArtist.getLastName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooArtists[0].id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.tattooArtists[0].uid").value(tattooArtist.getUid()))
                .andExpect(jsonPath("$.tattooArtists[0].firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.tattooArtists[0].lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.tattooArtists[0].email").value(tattooArtist.getEmail()))
                .andExpect(jsonPath("$.tattooArtists[0].phoneNumber").value(tattooArtist.getPhoneNumber()));
    }

    @Test
    @DisplayName("users/me - DELETE - 204")
    void deleteUser_shouldReturnOk() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(String.valueOf(authenticatedUser.getId())));
    }

    @Test
    @DisplayName("users/{id} - DELETE - 403")
    void deleteUserById_shouldRequireAdminRights() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(false);
        var expectedErrorMessage = "Only Admin can perform this operation";
        mockMvc.perform(delete("/users/{id}", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andDo(result -> {
                    var body = objectMapper.readTree(result.getResponse().getContentAsString());
                    var actualMessage = body.get("message").asText();
                    assertTrue(actualMessage.contains(expectedErrorMessage));
                });
    }

    @Test
    @DisplayName("users/{id} - DELETE - 204")
    void deleteUserByIdAsAdmin_shouldReturnOK() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(delete("/users/{id}", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(String.valueOf(authenticatedUser.getId())));
    }

    @Test
    @DisplayName("users/search - GET - 200")
    void searchUsers_shouldReturnOK() throws Exception {
        authenticatedUser.setBusinessAddress(address);
        authenticatedUser.setGender(Gender.MALE);
        authenticatedUser.setLanguages(List.of(Language.PORTUGUESE));
        var tattooArtist = userRepository.save(authenticatedUser);
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow();
        userDocument.setLanguages(tattooArtist.getLanguages().stream().map(Language::toString).toList());
        userDocument.setGender(tattooArtist.getGender());
        userDocument.setCountry(tattooArtist.getBusinessAddress().getCountry());
        userDocument.setCity(tattooArtist.getBusinessAddress().getCity());
        userDocument.setAverageRating(tattooArtist.getAverageRating());
        var updatedUserDocument = userEsRepository.save(userDocument);
        mockMvc.perform(get("/users/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", tattooArtist.getFirstName())
                        .param("city", tattooArtist.getBusinessAddress().getCity())
                        .param("country", tattooArtist.getBusinessAddress().getCountry())
                        .param("isTattooArtist", String.valueOf(tattooArtist.isHasArtistPage()))
                        .param("averageRating", String.valueOf(tattooArtist.getAverageRating()))
                        .param("languages", String.valueOf(tattooArtist.getLanguages().get(0)))
                        .param("gender", tattooArtist.getGender().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooArtists[0].id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.tattooArtists[0].firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.tattooArtists[0].lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.tattooArtists[0].avatarUrl").value(tattooArtist.getAvatarUrl()));

    }

    @Test
    @DisplayName("users/search - GET - 200")
    void searchTattooArtists_shouldReturnOK() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setBusinessAddress(address);
        authenticatedUser.setGender(Gender.MALE);
        authenticatedUser.setLanguages(List.of(Language.PORTUGUESE));
        var tattooArtist = userRepository.save(authenticatedUser);
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow();
        userDocument.setHasTattooArtistAcc(tattooArtist.isHasArtistPage());
        userDocument.setLanguages(tattooArtist.getLanguages().stream().map(Language::toString).toList());
        userDocument.setGender(tattooArtist.getGender());
        userDocument.setCountry(tattooArtist.getBusinessAddress().getCountry());
        userDocument.setCity(tattooArtist.getBusinessAddress().getCity());
        userDocument.setAverageRating(tattooArtist.getAverageRating());
        var updatedUserDocument = userEsRepository.save(userDocument);
        //when
        mockMvc.perform(get("/users/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", tattooArtist.getFirstName())
                        .param("city", tattooArtist.getBusinessAddress().getCity())
                        .param("country", tattooArtist.getBusinessAddress().getCountry())
                        .param("isTattooArtist", "true")
                        .param("averageRating", String.valueOf(tattooArtist.getAverageRating()))
                        .param("languages", String.valueOf(tattooArtist.getLanguages().get(0)))
                        .param("gender", tattooArtist.getGender().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooArtists[0].id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.tattooArtists[0].firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.tattooArtists[0].lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.tattooArtists[0].avatarUrl").value(tattooArtist.getAvatarUrl()));
    }

    @Test
    @DisplayName("users/search - GET - 200")
    void searchTattooArtistsWithNullQuery_shouldReturnOK() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setBusinessAddress(address);
        authenticatedUser.setGender(Gender.MALE);
        authenticatedUser.setLanguages(List.of(Language.PORTUGUESE));
        var tattooArtist = userRepository.save(authenticatedUser);
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow();
        userDocument.setHasTattooArtistAcc(tattooArtist.isHasArtistPage());
        userDocument.setLanguages(tattooArtist.getLanguages().stream().map(Language::toString).toList());
        userDocument.setGender(tattooArtist.getGender());
        userDocument.setCountry(tattooArtist.getBusinessAddress().getCountry());
        userDocument.setCity(tattooArtist.getBusinessAddress().getCity());
        userDocument.setAverageRating(tattooArtist.getAverageRating());
        var updatedUserDocument = userEsRepository.save(userDocument);
        //when
        mockMvc.perform(get("/users/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooArtists[0].id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.tattooArtists[0].firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.tattooArtists[0].lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.tattooArtists[0].avatarUrl").value(tattooArtist.getAvatarUrl()));
    }

    @Test
    @DisplayName("users/search - GET - 200")
    void searchTattooArtistsWithNullQueryAndCountry_shouldReturnOK() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setBusinessAddress(address);
        authenticatedUser.setGender(Gender.MALE);
        authenticatedUser.setLanguages(List.of(Language.PORTUGUESE));
        var tattooArtist = userRepository.save(authenticatedUser);
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow();
        userDocument.setHasTattooArtistAcc(tattooArtist.isHasArtistPage());
        userDocument.setLanguages(tattooArtist.getLanguages().stream().map(Language::toString).toList());
        userDocument.setGender(tattooArtist.getGender());
        userDocument.setCountry(tattooArtist.getBusinessAddress().getCountry());
        userDocument.setCity(tattooArtist.getBusinessAddress().getCity());
        userDocument.setAverageRating(tattooArtist.getAverageRating());
        var updatedUserDocument = userEsRepository.save(userDocument);
        //when
        mockMvc.perform(get("/users/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", "")
                        .param("country", tattooArtist.getBusinessAddress().getCountry())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooArtists[0].id").value(tattooArtist.getId().toString()))
                .andExpect(jsonPath("$.tattooArtists[0].firstName").value(tattooArtist.getFirstName()))
                .andExpect(jsonPath("$.tattooArtists[0].lastName").value(tattooArtist.getLastName()))
                .andExpect(jsonPath("$.tattooArtists[0].avatarUrl").value(tattooArtist.getAvatarUrl()));
    }

    @Test
    @DisplayName("users/search - GET - 200")
    void searchTattooArtistsWithNullQueryAndWrongCountry_shouldReturnEmptyList() throws Exception {
        //given
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setBusinessAddress(address);
        authenticatedUser.setGender(Gender.MALE);
        authenticatedUser.setLanguages(List.of(Language.PORTUGUESE));
        var tattooArtist = userRepository.save(authenticatedUser);
        var userDocument = userEsRepository.findById(authenticatedUser.getId()).orElseThrow();
        userDocument.setHasTattooArtistAcc(tattooArtist.isHasArtistPage());
        userDocument.setLanguages(tattooArtist.getLanguages().stream().map(Language::toString).toList());
        userDocument.setGender(tattooArtist.getGender());
        userDocument.setCountry(tattooArtist.getBusinessAddress().getCountry());
        userDocument.setCity(tattooArtist.getBusinessAddress().getCity());
        userDocument.setAverageRating(tattooArtist.getAverageRating());
        var updatedUserDocument = userEsRepository.save(userDocument);
        //when
        mockMvc.perform(get("/users/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", "")
                        .param("country", "not_a_country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("/users/{id}/price-interval - GET - 200")
    void userPriceInterval_shouldReturnOk() throws Exception {
        var tattooWork_MIN_PRICE = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        var tattooWork_MAX_PRICE = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test1")
                .currency(Currency.EUR)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test1")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(10))
                .convertedPriceValue(BigDecimal.valueOf(50))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork_MAX_PRICE);
        tattooWorkRepository.save(tattooWork_MIN_PRICE);

        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork_MAX_PRICE, tattooWork_MIN_PRICE));
        userRepository.save(authenticatedUser);

        mockMvc.perform(get("/users/{id}/price-interval", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.maxTattooWorkPrice").value(tattooWork_MAX_PRICE.getPrice().intValue()))
                .andExpect(jsonPath("$.minTattooWorkPrice").value(tattooWork_MIN_PRICE.getPrice().intValue()))
                .andExpect(jsonPath("$.minTattooWorkPriceCurrency").value(tattooWork_MIN_PRICE.getCurrency().toString()))
                .andExpect(jsonPath("$.maxTattooWorkPriceCurrency").value(tattooWork_MAX_PRICE.getCurrency().toString()));
    }

    @Test
    @DisplayName("/users/{id}/price-interval - GET - 200")
    void userPriceIntervalWithUid_shouldReturnOk() throws Exception {
        var tattooWork_MIN_PRICE = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        var tattooWork_MAX_PRICE = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test1")
                .currency(Currency.EUR)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test1")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(10))
                .convertedPriceValue(BigDecimal.valueOf(50))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork_MAX_PRICE);
        tattooWorkRepository.save(tattooWork_MIN_PRICE);

        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork_MAX_PRICE, tattooWork_MIN_PRICE));
        userRepository.save(authenticatedUser);

        mockMvc.perform(get("/users/{id}/price-interval", authenticatedUser.getUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.maxTattooWorkPrice").value(tattooWork_MAX_PRICE.getPrice().intValue()))
                .andExpect(jsonPath("$.minTattooWorkPrice").value(tattooWork_MIN_PRICE.getPrice().intValue()))
                .andExpect(jsonPath("$.minTattooWorkPriceCurrency").value(tattooWork_MIN_PRICE.getCurrency().toString()))
                .andExpect(jsonPath("$.maxTattooWorkPriceCurrency").value(tattooWork_MAX_PRICE.getCurrency().toString()));
    }

    @Test
    @DisplayName("/users/me/tattooworks - GET - 200")
    void getMyTattooWorks_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(get("/users/me/tattooworks", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        assertFalse(userFromRepo.getTattooWorks().isEmpty());
    }


    @Test
    @DisplayName("/users/me/tattoo-works/{post_id}/likes - PUT - 200")
    void likeTattooWork_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        var updatedTattooWork = tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork));
        var updatedUser = userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(updatedUser);
        mockMvc.perform(put("/users/me/tattoo-works/{post_id}/likes", updatedTattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        var tattooWorkFromRepo = tattooWorkRepository.findById(tattooWork.getId()).orElseThrow();
        assertEquals(userFromRepo.getLikedTattooWorks().get(0).getId(), updatedTattooWork.getId());
        assertEquals(tattooWorkFromRepo.getLikerIds().get(0).getId(), userFromRepo.getId());
    }

    @Test
    @DisplayName("/users/me/tattoo-works/{post_id}/likes - DELETE - 200")
    void dislikeTattooWork_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/users/me/tattoo-works/{post_id}/likes", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        var tattooWorkFromRepo = tattooWorkRepository.findById(tattooWork.getId()).orElseThrow();
        assertEquals(userFromRepo.getDislikedTattooWorks().get(0).getId(), tattooWorkFromRepo.getId());
        assertEquals(tattooWorkFromRepo.getDislikerIds().get(0).getId(), userFromRepo.getId());
    }

    @Test
    @DisplayName("/users/me/tattoo-works/{post_id}/favorites - DELETE - 200")
    void unfavoriteTattooWork_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork));
        authenticatedUser.setFavoriteTattooWorks(List.of(tattooWork));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/users/me/tattoo-works/{post_id}/favorites", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        var tattooWorkFromRepo = tattooWorkRepository.findById(tattooWork.getId()).orElseThrow();
        assertTrue(userFromRepo.getFavoriteTattooWorks().isEmpty());
        assertTrue(tattooWorkFromRepo.getFavoriteUserList().isEmpty());
    }

    @Test
    @DisplayName("/users/me/tattoo-works/{post_id}/favorites - PUT - 200")
    void favoriteTattooWork_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setTattooWorks(List.of(tattooWork));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(put("/users/me/tattoo-works/{post_id}/favorites", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        var tattooWorkFromRepo = tattooWorkRepository.findById(tattooWork.getId()).orElseThrow();
        assertEquals(userFromRepo.getFavoriteTattooWorks().get(0).getId(), tattooWorkFromRepo.getId());
        assertEquals(tattooWorkFromRepo.getFavoriteUserList().get(0).getId(), userFromRepo.getId());
    }

    @Test
    @DisplayName("/users/me/tattoo-artists/{artist_id}/favorites - PUT - 200")
    void favoriteTattooArtist_shouldReturnOk() throws Exception {
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(put("/users/me/tattoo-artists/{artist_id}/favorites", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        assertEquals(userFromRepo.getFavouriteArtists().get(0).getId(), userFromRepo.getId());
    }

    @Test
    @DisplayName("/users/me/tattoo-artists/{artist_id}/favorites - DELETE - 204")
    void unfavoriteTattooArtist_shouldReturnOk() throws Exception {
        authenticatedUser.setFavouriteArtists(List.of(authenticatedUser));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/users/me/tattoo-artists/{artist_id}/favorites", authenticatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        var userFromRepo = userRepository.findById(authenticatedUser.getId()).orElseThrow();
        assertTrue(userFromRepo.getFavouriteArtists().isEmpty());
    }

    @Test
    @DisplayName("/users/me/favorite-tattoo-works - GET - 200")
    void getFavoriteTattooWorks_shouldReturnOk() throws Exception {
        var tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(authenticatedUser)
                .photos(new ArrayList<>())
                .price(BigDecimal.valueOf(1))
                .convertedPriceValue(BigDecimal.valueOf(5))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();
        tattooWorkRepository.save(tattooWork);
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setFavoriteTattooWorks(List.of(tattooWork));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(get("/users/me/favorite-tattoo-works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(tattooWork.getId().toString()))
                .andExpect(jsonPath("$[0].description").value(tattooWork.getDescription()))
                .andExpect(jsonPath("$[0].price").value(tattooWork.getPrice().intValue()));
    }

    @Test
    @DisplayName("/users/me/favorite-tattoo-artist - GET - 200")
    void getFavoriteTattooArtists_shouldReturnOk() throws Exception {
        authenticatedUser.setHasArtistPage(true);
        authenticatedUser.setFavouriteArtists(List.of(authenticatedUser));
        userRepository.save(authenticatedUser);
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(get("/users/me/favorite-tattoo-artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(authenticatedUser.getId().toString()))
                .andExpect(jsonPath("$[0].uid").value(authenticatedUser.getUid()))
                .andExpect(jsonPath("$[0].firstName").value(authenticatedUser.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(authenticatedUser.getLastName()))
                .andExpect(jsonPath("$[0].email").value(authenticatedUser.getEmail()))
                .andExpect(jsonPath("$[0].phoneNumber").value(authenticatedUser.getPhoneNumber()));
    }
}