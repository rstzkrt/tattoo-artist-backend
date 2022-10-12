package com.example.tattooartistbackend.review;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.generated.models.ReviewPatchRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewPostRequestDto;
import com.example.tattooartistbackend.generated.models.ReviewType;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
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

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

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
    private User tattooArtist;
    private User user;
    private User authenticatedUser;
    private Review review;

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

        tattooArtist = User.builder()
                .uid("test_uid")
                .avatarUrl("test_avatar_url")
                .phoneNumber("000000000")
                .firstName("test_first_name")
                .lastName("test_last_name")
                .email("test_email@email.com")
                .workingDaysList(new ArrayList<>())
                .hasArtistPage(true)
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
        tattooArtist = userRepository.save(tattooArtist);

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


        review =Review.builder()
                .postedBy(reporter)
                .receiver(tattooArtist)
                .reviewType(ReviewType.POSITIVE)
                .message("test_message")
                .build();
        review= reviewRepository.save(review);
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        userReportRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/reviews/users/{receiver_id} - POST - 201")
    void shouldReturnCreated_whenCreateUserReport() throws Exception {
        authenticatedUser=reporter;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var req =new ReviewPostRequestDto();
        req.setReviewType(ReviewType.POSITIVE);
        req.setMessage("test_report_description");
        req.setPostedBy(authenticatedUser.getId());
        mockMvc.perform(post("/reviews/users/{receiver_id}",tattooArtist.getId())
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("/reviews/{id} - DELETE - 204")
    void shouldReturnNoContent_whenDeleteReview() throws Exception {
        authenticatedUser=reporter;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/reviews/{id}",review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("/reviews/users/{receiver_id} - GET - 200")
    void shouldReturnOk_whenGetAllReviewByUserId() throws Exception {
        mockMvc.perform(get("/reviews/users/{receiver_id}",tattooArtist.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/reviews/{id} - PATCH - 200")
    void shouldReturnOk_whenReviewPatchUpdate() throws Exception {
        authenticatedUser=reporter;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var req= new ReviewPatchRequestDto();
        req.setMessage("new_message");
        req.setReviewType(ReviewType.NEUTRAL);
        mockMvc.perform(patch("/reviews/{id}",review.getId())
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("/reviews/{id} - GET - 200")
    void shouldReturnOk_whenGetReviewsById() throws Exception {
        authenticatedUser= user;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(get("/reviews/{id}",review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}