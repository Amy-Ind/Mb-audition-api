package com.audition.controller;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import com.audition.web.AuditionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditionController.class)
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class AuditionControllerTest {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionLogger logger;

    @MockBean
    private AuditionService auditionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<AuditionPost> posts;

    @BeforeEach
    void setUp() {
        posts = Arrays.asList(
            new AuditionPost(1, 1, "Title 1", "Body 1"),
        new AuditionPost(2, 2, "Title 2", "Body 2")
        );
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetAllPosts() throws Exception {
        when(auditionService.getPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"));

        verify(auditionService).getPosts();
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetFilteredPostsByTitle() throws Exception {
        when(auditionService.getPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts").param("title", "Title 1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Title 1"));

        verify(auditionService).getPosts();
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetPostById_Valid() throws Exception {
        AuditionPost post = new AuditionPost(1, 1, "Title A", "Body A");
        when(auditionService.getPostById("1")).thenReturn(post);

        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.title").value("Title A"));

        verify(auditionService).getPostById("1");
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetPostCommentsById_Valid() throws Exception {
        List<AuditionPostComments> comments = Arrays.asList(
            new AuditionPostComments(1, 1, "Tom", " tom@gmail.com", "Comment 1"),
            new AuditionPostComments(1, 2, "Jack", " jack@gmail.com", "Comment 2")
        );

        when(auditionService.getPostCommentsById("1")).thenReturn(comments);

        mockMvc.perform(get("/posts/1/comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].body").value("Comment 1"));

        verify(auditionService).getPostCommentsById("1");
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetPostCommentsById_WhenIdIsBlank() throws Exception {
        // blank
        mockMvc.perform(get("/posts/{id}/comments", ""))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLE)
    void testGetPostById_WhenIdIsBlank() throws Exception {
        // blank
        mockMvc.perform(get("/posts/"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetPostCommentsById_Invalid() throws Exception {
        mockMvc.perform(get("/posts ").param("title", "title 1"))
            .andExpect(status().isUnauthorized());
    }
}
