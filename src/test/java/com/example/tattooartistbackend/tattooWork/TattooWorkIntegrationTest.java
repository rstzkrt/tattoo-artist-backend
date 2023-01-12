package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.exceptions.TattooWorkNotFoundException;
import com.example.tattooartistbackend.generated.models.*;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkDocument;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsRepository;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsService;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TattooWorkIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SecurityService securityService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TattooWorkRepository tattooWorkRepository;
    @Autowired
    private AddressRepository addressRepository;
    @MockBean
    private RoleService roleService;
    @Autowired
    private TattooWorkEsRepository tattooWorkEsRepository;

    private User owner;
    private User client;
    private User user;
    private User authenticatedUser;
    private TattooWork tattooWork;
    private TattooWorkDocument tattooWorkDocument;

    @Autowired
    private TattooWorkEsService tattooWorkEsService;

    @BeforeEach
    void setUp() {

        var address = Address.builder()
                .state("Mazovian")
                .postalCode("0123")
                .street("Hoza")
                .country("Poland")
                .city("Warsaw")
                .otherInformation("TattooStudio")
                .build();
        addressRepository.save(address);

        owner = User.builder()
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
        owner = userRepository.save(owner);

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
        user = userRepository.save(user);

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
        client = userRepository.save(client);

        tattooWork = TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto("test")
                .currency(Currency.USD)
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description("test")
                .madeBy(owner)
                .photos(List.of("test"))
                .price(BigDecimal.valueOf(991))
                .convertedPriceValue(BigDecimal.valueOf(99))
                .tattooStyle(TattooStyle.REALISTIC)
                .build();

        tattooWork= tattooWorkRepository.save(tattooWork);
        tattooWorkDocument = tattooWorkEsRepository.save(TattooWorkDocument.fromTattooWork(tattooWork));

    }

    @AfterEach
    void tearDown() {
        tattooWorkEsRepository.deleteAll();
        tattooWorkRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    @DisplayName("/tattoo-works - POST - 201")
    void shouldReturnCreated_whenCreateTattooWork() throws Exception {
        authenticatedUser = owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        authenticatedUser.setHasArtistPage(true);
        userRepository.save(authenticatedUser);
        var tattooWorkReq = new TattooWorkPostRequestDto();
        tattooWorkReq.setPrice(BigDecimal.valueOf(99));
        tattooWorkReq.setTattooStyle(TattooStyle.REALISTIC);
        tattooWorkReq.setClientId(client.getId());
        tattooWorkReq.setCurrency(Currency.USD);
        tattooWorkReq.setPhotos(List.of("test"));
        tattooWorkReq.setCoverPhoto("test");
        tattooWorkReq.setDescription("test");
        mockMvc.perform(post("/tattoo-works")
                        .content(objectMapper.writeValueAsString(tattooWorkReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").value(tattooWorkReq.getDescription()))
                .andExpect(jsonPath("$.price").value(tattooWorkReq.getPrice()))
                .andExpect(jsonPath("$.currency").value(tattooWorkReq.getCurrency().getValue()))
                .andExpect(jsonPath("$.tattooStyle").value(tattooWorkReq.getTattooStyle().getValue()));
    }

    @Test
    @DisplayName("/tattoo-works/{id} - DELETE - 204")
    void deleteTattooWorkAsOwner_shouldReturnNoContent() throws Exception {
        authenticatedUser = owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/tattoo-works/{id}", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(TattooWorkNotFoundException.class, () -> tattooWorkRepository.findById(tattooWork.getId()).orElseThrow(TattooWorkNotFoundException::new));
    }

    @Test
    @DisplayName("/tattoo-works/{id} - DELETE - 204")
    void deleteTattooWorkAsAdmin_shouldReturnNoContent() throws Exception {
        authenticatedUser = user;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(delete("/tattoo-works/{id}", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(TattooWorkNotFoundException.class, () -> tattooWorkRepository.findById(tattooWork.getId()).orElseThrow(TattooWorkNotFoundException::new));
    }

    @Test
    @DisplayName("/tattoo-works/{id} - DELETE - 405")
    void deleteTattooWorkAsUserNotOwner_shouldReturnErrorMessage() throws Exception {
        authenticatedUser = client;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(false);
        var expectedMessage = "only the owner or Admin can delete the tattooWork!";
        mockMvc.perform(delete("/tattoo-works/{id}", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(405))
                .andExpect(result -> {
                    var body = objectMapper.readTree(result.getResponse().getContentAsString());
                    var actualMessage = body.get("message").asText();
                    assertTrue(actualMessage.contains(expectedMessage));
                });
    }

    @Test
    @DisplayName("/tattoo-works/search - GET - 200")
    void searchTattooWorks_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/tattoo-works/search")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("query", tattooWork.getDescription())
                        .param("currency", tattooWork.getCurrency().toString())
                        .param("tattooStyle", tattooWork.getTattooStyle().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.tattooWorks[0].id").value(tattooWorkDocument.getId().toString()))
                .andExpect(jsonPath("$.tattooWorks[0].description").value(tattooWorkDocument.getDescription()))
                .andExpect(jsonPath("$.tattooWorks[0].tattooStyle").value(tattooWorkDocument.getTattooStyle().toString()))
                .andExpect(jsonPath("$.tattooWorks[0].currency").value(tattooWorkDocument.getCurrency().getValue()));

    }

    @Test
    @DisplayName("/tattoo-works/{id} - GET - 201")
    void getTattooWorkById_shouldReturnOK() throws Exception {
        mockMvc.perform(get("/tattoo-works/{id}",tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(tattooWork.getId().toString()))
                .andExpect(jsonPath("$.currency").value(tattooWork.getCurrency().getValue()))
                .andExpect(jsonPath("$.description").value(tattooWork.getDescription()));
    }


    @Test
    @DisplayName("/tattoo-works/{id} - PATCH - 200")
    void patchTattooWork_whenCreateTattooWork() throws Exception {
        authenticatedUser=owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var patchReq= new TattooWorkPatchRequestDto();
        patchReq.setDescription("new_test_description");
        patchReq.setPrice(BigDecimal.valueOf(100));
        patchReq.setCoverPhoto("test_cover_photo");
        mockMvc.perform(patch("/tattoo-works/{id}",tattooWork.getId())
                        .content(objectMapper.writeValueAsString(patchReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.description").value(patchReq.getDescription()));
    }

}