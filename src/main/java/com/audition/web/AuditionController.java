package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditionController {

    @Autowired
    @SuppressWarnings("PMD")
    AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.-Done
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam(value = "title", required = false) String title) {

        // TODO Add logic that filters response data based on the query param -Done
        List<AuditionPost> posts = auditionService.getPosts();
        if (title != null && !title.isEmpty()) {
            posts = posts.stream()
                .filter(post -> title.equals(post.getTitle()))
                .toList();
        }
        return posts;
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsById(@PathVariable("id") @NotBlank final String postId) {
        // TODO Add input validation - Done

        return auditionService.getPostById(postId.trim());
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/ -Done
    @RequestMapping(value = "/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPostComments> getPostCommentsById(@PathVariable("postId") @NotBlank final String postId) {

        return auditionService.getPostCommentsById(postId.trim());
    }
}
