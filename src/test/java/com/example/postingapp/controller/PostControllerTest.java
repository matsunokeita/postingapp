package com.example.postingapp.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.postingapp.entity.Post;
import com.example.postingapp.service.PostService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private PostService postService;

    @Autowired
    private com.example.postingapp.security.UserDetailsServiceImpl userDetailsService;

    @Test
    public void ログイン済みの場合は投稿一覧ページが正しく表示される() throws Exception {
        var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(get("/posts").with(user(userDetails)))
               .andExpect(status().isOk())
               .andExpect(view().name("posts/index"));
    }

    @Test
    public void 未ログインの場合は投稿一覧ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/posts"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void ログイン済みの場合は投稿詳細ページが正しく表示される() throws Exception {
        var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(get("/posts/1").with(user(userDetails)))
               .andExpect(status().isOk())
               .andExpect(view().name("posts/show"));
    }

    @Test
    public void 未ログインの場合は投稿詳細ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/posts/1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    public void ログイン済みの場合は投稿作成ページが正しく表示される() throws Exception {
        var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(get("/posts/register").with(user(userDetails)))
               .andExpect(status().isOk())
               .andExpect(view().name("posts/register"));
    }

    @Test
    public void 未ログインの場合は投稿作成ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/posts/register"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    @Transactional
    public void ログイン済みの場合は投稿作成後に投稿一覧ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(post("/posts/create").with(csrf()).with(user(userDetails)).param("title", "テストタイトル").param("content", "テスト内容"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts"));

        Post post = postService.findFirstPostByOrderByIdDesc();
        assertThat(post.getTitle()).isEqualTo("テストタイトル");
        assertThat(post.getContent()).isEqualTo("テスト内容");
    }

    @Test
    @Transactional
    public void 未ログインの場合は投稿を作成せずにログインページにリダイレクトする() throws Exception {
        mockMvc.perform(post("/posts/create").with(csrf()).param("title", "テストタイトル").param("content", "テスト内容"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));

        Post post = postService.findFirstPostByOrderByIdDesc();
        assertThat(post.getTitle()).isNotEqualTo("テストタイトル");
        assertThat(post.getContent()).isNotEqualTo("テスト内容");
    }
    
    @Test
    public void ログイン済みの場合は自身の投稿編集ページが正しく表示される() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(get("/posts/1/edit").with(user(userDetails)))
               .andExpect(status().isOk())
               .andExpect(view().name("posts/edit"));
    }

    @Test
    public void ログイン済みの場合は他人の投稿編集ページから投稿一覧ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("jiro.samurai@example.com");
        mockMvc.perform(get("/posts/1/edit").with(user(userDetails)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts"));
    }

    @Test
    public void 未ログインの場合は投稿編集ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/posts/1/edit"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    @Transactional
    public void ログイン済みの場合は自身の投稿更新後に投稿詳細ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(post("/posts/1/update").with(csrf()).with(user(userDetails)).param("title", "テストタイトル").param("content", "テスト内容"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/1"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isPresent();
        Post post = optionalPost.get();
        assertThat(post.getTitle()).isEqualTo("テストタイトル");
        assertThat(post.getContent()).isEqualTo("テスト内容");
    }

    @Test
    @Transactional
    public void ログイン済みの場合は他人の投稿を更新せずに投稿一覧ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("jiro.samurai@example.com");
        mockMvc.perform(post("/posts/1/update").with(csrf()).with(user(userDetails)).param("title", "テストタイトル").param("content", "テスト内容"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isPresent();
        Post post = optionalPost.get();
        assertThat(post.getTitle()).isNotEqualTo("テストタイトル");
        assertThat(post.getContent()).isNotEqualTo("テスト内容");
    }

    @Test
    @Transactional
    public void 未ログインの場合は投稿を更新せずにログインページにリダイレクトする() throws Exception {
        mockMvc.perform(post("/posts/1/update").with(csrf()).param("title", "テストタイトル").param("content", "テスト内容"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isPresent();
        Post post = optionalPost.get();
        assertThat(post.getTitle()).isNotEqualTo("テストタイトル");
        assertThat(post.getContent()).isNotEqualTo("テスト内容");
    }
    
    @Test
    @Transactional
    public void ログイン済みの場合は自身の投稿削除後に投稿一覧ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("taro.samurai@example.com");
        mockMvc.perform(post("/posts/1/delete").with(csrf()).with(csrf()).with(user(userDetails)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isEmpty();
    }

    @Test
    @Transactional
    public void ログイン済みの場合は他人の投稿を削除せずに投稿一覧ページにリダイレクトする() throws Exception {
    	var userDetails = userDetailsService.loadUserByUsername("jiro.samurai@example.com");
        mockMvc.perform(post("/posts/1/delete").with(csrf()).with(csrf()).with(user(userDetails)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isPresent();
    }

    @Test
    @Transactional
    public void 未ログインの場合は投稿を削除せずにログインページにリダイレクトする() throws Exception {
        mockMvc.perform(post("/posts/1/delete").with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));

        Optional<Post> optionalPost = postService.findPostById(1);
        assertThat(optionalPost).isPresent();
    }
}