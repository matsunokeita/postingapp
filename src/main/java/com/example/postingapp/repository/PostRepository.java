package com.example.postingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;

public interface PostRepository extends JpaRepository<Post, Integer> {
    public List<Post> findByUserOrderByUpdatedAtAsc(User user);
    public List<Post> findByUserOrderByUpdatedAtDesc(User user);
    public List<Post> findByUserOrderByTitleAsc(User user);
    public List<Post> findByUserOrderByTitleDesc(User user);
    public Post findFirstByOrderByIdDesc();
}