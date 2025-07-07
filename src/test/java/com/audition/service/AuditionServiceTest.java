package com.audition.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPosts() {
        AuditionPost post1 = new AuditionPost(1, 1, "Title 1", "Body 1");
        AuditionPost post2 = new AuditionPost(2, 2, "Title 2", "Body 2");

        when(auditionIntegrationClient.getPosts()).thenReturn(Arrays.asList(post1, post2));

        List<AuditionPost> result = auditionService.getPosts();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    void testGetPostById() {
        int userId = 2;
        int postId = 123;
        AuditionPost post = new AuditionPost(userId, postId, "Title", "Body");
        String postIdStr = Integer.toString(postId);
        when(auditionIntegrationClient.getPostById(postIdStr)).thenReturn(post);

        AuditionPost result = auditionService.getPostById(postIdStr);

        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(auditionIntegrationClient).getPostById(postIdStr);
    }

    @Test
    void testGetPostCommentsById() {
        int postId = 123;
        String postIdStr = Integer.toString(postId);
        AuditionPostComments comment1 = new AuditionPostComments(postId, 1, "Tom", " tom@gmail.com", "Comment 1");
        AuditionPostComments comment2 = new AuditionPostComments(postId, 2, "Jack", " jack@gmail.com", "Comment 2");

        when(auditionIntegrationClient.getPostCommentsByPostId(postIdStr)).thenReturn(Arrays.asList(comment1, comment2));

        List<AuditionPostComments> comments = auditionService.getPostCommentsById(postIdStr);

        assertEquals(2, comments.size());
        assertEquals(postId, comments.get(0).getPostId());
        verify(auditionIntegrationClient).getPostCommentsByPostId(postIdStr);
    }
}