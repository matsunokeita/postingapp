package com.example.postingapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
}