package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {


    @Autowired
    @SuppressWarnings("PMD")
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts -Done
        try {
            AuditionPost[] postsArray = restTemplate.getForObject(
                "https://jsonplaceholder.typicode.com/posts",
                AuditionPost[].class
            );
            return postsArray != null
                ? Arrays.asList(postsArray)
                : Collections.emptyList();
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/ -Done
        try {
          return restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/{id}", AuditionPost.class, id);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function. -Done
                throw new SystemException(
                    "Unexpected error occurred while fetching post by id " + id + ": " + e.getMessage(),
                    e
                );
            }
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post. -Done
    public List<AuditionPostComments> getPostCommentsByPostId(final String postId) {
        try {
            AuditionPostComments[] postCommentsArray = restTemplate.getForObject(
                "https://jsonplaceholder.typicode.com/posts/{id}/comments",
                AuditionPostComments[].class,
                postId
            );
            return postCommentsArray != null
                ? Arrays.asList(postCommentsArray)
                : Collections.emptyList();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find comments with post id " + postId, "Resource Not Found",
                    404);
            } else {
                throw new SystemException(
                    "Unexpected error occurred while fetching comments by post id " + postId + ": " + e.getMessage(),
                    e
                );
            }
        }
    }
    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}. -Done
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.

    public List<AuditionPostComments> getPostComments(final String postId) {
        try {
            AuditionPostComments[] postCommentsArray = restTemplate.getForObject(
                "https://jsonplaceholder.typicode.com/posts/comments?postId={postId}",
                AuditionPostComments[].class,
                postId
            );
            return postCommentsArray != null
                ? Arrays.asList(postCommentsArray)
                : Collections.emptyList();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find comments with post id " + postId, "Resource Not Found",
                    404);
            } else {
                throw new SystemException(
                    "Unexpected error occurred while fetching comments by post id " + postId + ": " + e.getMessage(),
                    e
                );
            }
        }
    }
}
