package com.example.tattooartistbackend.comment;

import com.example.tattooartistbackend.address.Address;
import com.example.tattooartistbackend.address.AddressRepository;
import com.example.tattooartistbackend.generated.models.CommentPatchRequestDto;
import com.example.tattooartistbackend.generated.models.CommentRequestDto;
import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentIntegrationTest {

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
    @Autowired
    private CommentRepository commentRepository;

    private Comment comment;
    private User owner;
    private User client;
    private User authenticatedUser;
    private TattooWork tattooWork;

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

        tattooWork = tattooWorkRepository.save(tattooWork);

        comment = Comment.builder()
                .message("test")
                .postDate(LocalDate.now())
                .rate(BigDecimal.valueOf(5))
                .tattooWork(tattooWork)
                .postedBy(owner)
                .build();
        comment = commentRepository.save(comment);
    }

    @AfterEach
    void tearDown() {
        tattooWorkRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("/comments/tattooworks/{tattooWork_id} - POST - 201")
    void shouldReturnCreated_whenCreateComment() throws Exception {
        authenticatedUser = client;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var req = new CommentRequestDto();
        req.setRate(BigDecimal.valueOf(5));
        req.setMessage("test_description");
        req.setPostedBy(authenticatedUser.getId());
        mockMvc.perform(post("/comments/tattooworks/{tattooWork_id}", tattooWork.getId())
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(req.getMessage()));
        var tattooWorkOwner = userRepository.findById(tattooWork.getMadeBy().getId()).orElseThrow();
        assertEquals(tattooWorkOwner.getAverageRating(), req.getRate().doubleValue());
    }

    @Test
    @DisplayName("/comments/{comment_id} - DELETE - 204")
    void shouldReturnCreated_whenDeleteCommentById() throws Exception {
        authenticatedUser = owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(delete("/comments/{comment_id}", comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("/comments/{comment_id} - PATCH - 200")
    void shouldReturnCreated_whenEditComment() throws Exception {
        authenticatedUser = owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        var commentPatchReq = new CommentPatchRequestDto();
        commentPatchReq.setMessage("updated_message");
        commentPatchReq.setRate(BigDecimal.valueOf(2));
        mockMvc.perform(patch("/comments/{comment_id}", comment.getId())
                        .content(objectMapper.writeValueAsString(commentPatchReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/comments/{comment_id} - GET - 200")
    void shouldReturnCreated_whenGetCommentById() throws Exception {
        authenticatedUser = owner;
        when(securityService.getUser()).thenReturn(authenticatedUser);
        mockMvc.perform(get("/comments/{comment_id}", comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/comments/tattooworks/{tattooWork_id} - GET - 200")
    void shouldReturnCreated_whenGetCommentByTattooWorkId() throws Exception {
        mockMvc.perform(get("/comments/tattooworks/{tattooWork_id}", tattooWork.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}