package com.example.postingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
    	Optional<Post> optionalPost = postService.findPostById(id);
    
    	if (optionalPost.isEmpty()) {
    		redirectAttributes.addFlashAttribute("errorMessage", "投稿が存在しません。");
    		
    		return "redirect:/posts";
    	}
    	
    	Post post = optionalPost.get();
    	model.addAttribute("post", post);
    	
    	return "posts/show";
    }
    
}