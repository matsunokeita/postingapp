package com.example.postingapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 特定のユーザーに紐づく投稿の一覧を作成日時が新しい順で取得する
    public List<Post> findPostsByUserOrderedByCreatedAtDesc(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }
}