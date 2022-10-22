package com.example.tattooartistbackend.userReport;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.generated.models.UserReportPostReqDto;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
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

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserReportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserReportRepository userReportRepository;

    @MockBean
    private SecurityService securityService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AddressRepository addressRepository;
    @MockBean
    private RoleService roleService;
    private User reporter;
    private User reportedUser;
    private User admin;
    private User authenticatedUser;

    private UserReport userReport;


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

        reporter = User.builder()
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
        reporter = userRepository.save(reporter);

        reportedUser = User.builder()
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
        reportedUser = userRepository.save(reportedUser);

        admin = User.builder()
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
        admin = userRepository.save(admin);


        userReport=UserReport.builder()
                .reportedUser(reportedUser)
                .reportOwner(reporter)
                .description("test")
                .date(LocalDate.now())
                .build();
        userReport= userReportRepository.save(userReport);
    }

    @AfterEach
    void tearDown() {
        userReportRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/user-reports - POST - 201")
    void shouldReturnCreated_whenCreateUserReport() throws Exception {
        authenticatedUser=reporter;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var req =new UserReportPostReqDto();
        req.setReportedUserId(reportedUser.getId());
        req.setDescription("test_report_description");
        req.setReportOwnerId(reporter.getId());
        mockMvc.perform(post("/user-reports")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("/user-reports/{id} - DELETE - 204")
    void shouldReturnNoContent_whenCloseReportAsAdmin() throws Exception {
        authenticatedUser=admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(delete("/user-reports/{id}",userReport.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("/user-reports/{id} - GET - 200")
    void shouldReturnOk_whenGetUserByIdAsAdmin() throws Exception {
        authenticatedUser=admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(get("/user-reports/{id}",userReport.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/user-reports - GET - 200")
    void shouldReturnOk_whenGetAlUserReportsAsAdmin() throws Exception {
        authenticatedUser=admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(get("/user-reports")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}