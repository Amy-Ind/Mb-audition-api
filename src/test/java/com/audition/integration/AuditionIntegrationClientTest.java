package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPosts_Success() {
        AuditionPost[] mockPosts = {
            new AuditionPost(1, 1, "Title 1", "Body 1"),
            new AuditionPost(2, 2, "Title 2", "Body 2")
        };

        when(restTemplate.getForObject(anyString(), eq(AuditionPost[].class)))
            .thenReturn(mockPosts);

        List<AuditionPost> result = auditionIntegrationClient.getPosts();
        assertEquals(2, result.size());
        verify(restTemplate).getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class);
    }

    @Test
    void testGetPostById_Success() {
        AuditionPost post =  new AuditionPost(1, 1, "Title 1", "Body 1");
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class), eq("1")))
            .thenReturn(post);

        AuditionPost result = auditionIntegrationClient.getPostById("1");
        assertEquals(1, result.getId());
    }

    @Test
    void testGetPostById_NotFound() {
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class), eq("999")))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("999"));

        assertEquals("Cannot find a Post with id 999", exception.getMessage());
    }

    @Test
    void testGetPostCommentsByPostId_Success() {
        AuditionPostComments[] comments = {
            new AuditionPostComments(1, 1, "Tom", " tom@gmail.com", "Comment 1"),
            new AuditionPostComments(1, 2, "Jack", " jack@gmail.com", "Comment 2")
        };
        when(restTemplate.getForObject(anyString(), eq(AuditionPostComments[].class), eq("1")))
            .thenReturn(comments);

        List<AuditionPostComments> result = auditionIntegrationClient.getPostCommentsByPostId("1");
        assertEquals(2, result.size());
    }

    @Test
    void testGetPostComments_Success() {
        AuditionPostComments[] comments = {
            new AuditionPostComments(1, 1, "Tom", " tom@gmail.com", "Comment 1")
        };
        when(restTemplate.getForObject(anyString(), eq(AuditionPostComments[].class), eq("1")))
            .thenReturn(comments);

        List<AuditionPostComments> result = auditionIntegrationClient.getPostComments("1");
        assertEquals(1, result.size());
    }

    @Test
    void testGetPostCommentsByPostId_NotFound() {
        when(restTemplate.getForObject(anyString(), eq(AuditionPostComments[].class), eq("9959")))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostCommentsByPostId("9959"));

        assertTrue(exception.getMessage().contains("Cannot find comments with post id"));
    }
}
