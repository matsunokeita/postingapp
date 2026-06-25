package com.example.postingapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.PostEditForm;
import com.example.postingapp.form.PostRegisterForm;
import com.example.postingapp.security.UserDetailsImpl;
import com.example.postingapp.service.PostService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public String create(@ModelAttribute @Validated PostRegisterForm postRegisterForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("postRegisterForm", postRegisterForm);
            return "posts/register";
        }

        User user = userDetailsImpl.getUser();

        try {
            postService.createPost(postRegisterForm, user);
        } catch (RuntimeException e) {
            model.addAttribute("postRegisterForm", postRegisterForm);
            model.addAttribute("errorMessage", "ファイルのアップロードに失敗しました。");
            return "posts/register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "投稿が完了しました。");
        return "redirect:/posts";
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        List<Post> posts = postService.findPostsByUserOrderedByUpdatedAtAsc(user);
        model.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);

        if (optionalPost.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "投稿が存在しません。");
            return "redirect:/posts";
        }

        Post post = optionalPost.get();
        model.addAttribute("post", post);
        return "posts/show";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("postRegisterForm", new PostRegisterForm());
        return "posts/register";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
            return "redirect:/posts";
        }

        Post post = optionalPost.get();
        model.addAttribute("post", post);
        model.addAttribute("postEditForm", new PostEditForm(post.getTitle(), post.getContent(), null));
        return "posts/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated PostEditForm postEditForm,
            BindingResult bindingResult,
            @PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
            return "redirect:/posts";
        }

        Post post = optionalPost.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("postEditForm", postEditForm);
            return "posts/edit";
        }

        try {
            postService.updatePost(postEditForm, post);
        } catch (RuntimeException e) {
            model.addAttribute("post", post);
            model.addAttribute("postEditForm", postEditForm);
            model.addAttribute("errorMessage", "ファイルのアップロードに失敗しました。");
            return "posts/edit";
        }

        redirectAttributes.addFlashAttribute("successMessage", "投稿を編集しました。");
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
            return "redirect:/posts";
        }

        Post post = optionalPost.get();
        postService.deletePost(post);
        redirectAttributes.addFlashAttribute("successMessage", "投稿を削除しました。");
        return "redirect:/posts";
    }

    @GetMapping("/{id}/download")
    public void download(@PathVariable(name = "id") Integer id,
            HttpServletResponse response) throws IOException {
        Optional<Post> optionalPost = postService.findPostById(id);

        if (optionalPost.isEmpty() || optionalPost.get().getFileUrl() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Post post = optionalPost.get();
        String fileUrl = post.getFileUrl();
        String fileName = post.getFileName();

        java.net.URL url = new java.net.URL(fileUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        try (java.io.InputStream inputStream = connection.getInputStream();
                java.io.OutputStream outputStream = response.getOutputStream()) {
            inputStream.transferTo(outputStream);
        }
    }
}