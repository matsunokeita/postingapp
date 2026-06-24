package com.example.postingapp.service;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.PostEditForm;
import com.example.postingapp.form.PostRegisterForm;
import com.example.postingapp.repository.PostRepository;
@Service
public class PostService {
    private final PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findPostsByUserOrderedByUpdatedAtAsc(User user) {
        return postRepository.findByUserOrderByUpdatedAtAsc(user);
    }

    public Optional<Post> findPostId(Integer id) {
        return postRepository.findById(id);
    }

    public Post findFirstPostByOrderByIdDesc() {
        return postRepository.findFirstByOrderByIdDesc();
    }

    public Optional<Post> findPostById(Integer id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void createPost(PostRegisterForm postRegisterForm, User user) {
        Post post = new Post();
        post.setTitle(postRegisterForm.getTitle());
        post.setContent(postRegisterForm.getContent());
        post.setUser(user);
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(PostEditForm postEditForm, Post post) {
        post.setTitle(postEditForm.getTitle());
        post.setContent(postEditForm.getContent());
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Post post) {
        postRepository.delete(post);
    }
}