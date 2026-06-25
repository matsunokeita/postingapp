package com.example.postingapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.PostEditForm;
import com.example.postingapp.form.PostRegisterForm;
import com.example.postingapp.repository.PostRepository;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final Cloudinary cloudinary;

	public PostService(PostRepository postRepository, Cloudinary cloudinary) {
		this.postRepository = postRepository;
		this.cloudinary = cloudinary;
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

		uploadFile(postRegisterForm.getAttachedFile(), post);

		postRepository.save(post);
	}

	@Transactional
	public void updatePost(PostEditForm postEditForm, Post post) {
		post.setTitle(postEditForm.getTitle());
		post.setContent(postEditForm.getContent());

		uploadFile(postEditForm.getAttachedFile(), post);

		postRepository.save(post);
	}

	@Transactional
	public void deletePost(Post post) {
		if (post.getFileUrl() != null) {
			try {
				String publicId = extractPublicId(post.getFileUrl());
				cloudinary.uploader().destroy(publicId, Map.of());
			} catch (IOException e) {
				// 削除失敗してもDB削除は続行
			}
		}
		postRepository.delete(post);
	}

	private void uploadFile(MultipartFile file, Post post) {
		if (file == null || file.isEmpty()) {
			return;
		}

		try {
			String originalFilename = file.getOriginalFilename();
			String extension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}
			String publicId = "postingapp/" + UUID.randomUUID() + extension;

			Map<String, Object> uploadParams = new java.util.HashMap<>();
			uploadParams.put("public_id", publicId);
			uploadParams.put("resource_type", "image");

			Map uploadResult = cloudinary.uploader().upload(
					file.getBytes(),
					uploadParams);

			String secureUrl = (String) uploadResult.get("secure_url");

			post.setFileUrl(secureUrl);
			post.setFileName(originalFilename);

		} catch (IOException e) {
			throw new RuntimeException("ファイルのアップロードに失敗しました。", e);
		}
	}

	private String extractPublicId(String fileUrl) {
		String[] parts = fileUrl.split("/upload/");
		String afterUpload = parts[1];
		if (afterUpload.startsWith("v")) {
			afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
		}
		int dotIndex = afterUpload.lastIndexOf(".");
		if (dotIndex != -1) {
			afterUpload = afterUpload.substring(0, dotIndex);
		}
		return afterUpload;
	}

	public String getCloudinaryCredentials() {
		return cloudinary.config.apiKey + ":" + cloudinary.config.apiSecret;
	}
}