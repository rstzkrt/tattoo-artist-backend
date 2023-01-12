package com.example.tattooartistbackend.tattooWorkReport;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.generated.models.*;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkDocument;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsRepository;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsService;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import com.example.tattooartistbackend.userReport.UserReport;
import com.example.tattooartistbackend.userReport.UserReportRepository;
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

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TattooWorkReportIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SecurityService securityService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private TattooWorkRepository tattooWorkRepository;
    @Autowired
    private TattooWorkReportRepository tattooWorkReportRepository;
    @MockBean
    private RoleService roleService;
    private TattooWork tattooWork;
    private User reporter;
    private User admin;
    private User authenticatedUser;
    private TattooWorkReport tattooWorkReport;

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

        var client = User.builder()
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
        client = userRepository.save(client);

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

        var owner = User.builder()
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
        tattooWork = tattooWorkRepository.save(tattooWork);

        tattooWorkReport = TattooWorkReport.builder()
                .reportedTattooWork(tattooWork)
                .tattooWorkReportOwner(reporter)
                .description("test")
                .date(LocalDate.now())
                .build();
        tattooWorkReport = tattooWorkReportRepository.save(tattooWorkReport);
    }

    @AfterEach
    void tearDown() {
        tattooWorkRepository.deleteAll();
        tattooWorkReportRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/tattoo-work-reports - POST - 201")
    void shouldReturnCreated_whenCreateTattooWorkReport() throws Exception {
        authenticatedUser = reporter;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var req = new TattooWorkReportPostReqDto();
        req.setReportedTattooWorkId(tattooWork.getId());
        req.setDescription("test_report_description");
        req.setReportOwnerId(reporter.getId());
        mockMvc.perform(post("/tattoo-work-reports")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("/tattoo-work-reports/{id} - DELETE - 204")
    void shouldReturnNoContent_whenCloseTattooWorkReportAsAdmin() throws Exception {
        authenticatedUser = admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(delete("/tattoo-work-reports/{id}", tattooWorkReport.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("/tattoo-work-reports/{id} - GET - 200")
    void shouldReturnOk_whenGetUserByIdAsAdmin() throws Exception {
        authenticatedUser = admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(get("/tattoo-work-reports/{id}", tattooWorkReport.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/tattoo-work-reports - GET - 200")
    void shouldReturnOk_whenGetAlTattooWorkReportsAsAdmin() throws Exception {
        authenticatedUser = admin;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(roleService.isAdmin(authenticatedUser.getUid())).thenReturn(true);
        mockMvc.perform(get("/tattoo-work-reports")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}