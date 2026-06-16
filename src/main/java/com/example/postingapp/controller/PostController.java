package com.example.postingapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.security.UserDetailsImpl;
import com.example.postingapp.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        List<Post> posts = postService.findPostsByUserOrderedByCreatedAtDesc(user);

        model.addAttribute("posts", posts);

        return "posts/index";
    }
}