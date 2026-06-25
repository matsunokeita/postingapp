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
		// Cloudinaryのファイルも削除
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

	// ファイルアップロード共通処理
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

			String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
			boolean isImage = originalName.endsWith(".jpg") || originalName.endsWith(".jpeg")
					|| originalName.endsWith(".png") || originalName.endsWith(".gif")
					|| originalName.endsWith(".webp");

			String resourceType = isImage ? "image" : "raw";

			Map uploadResult = cloudinary.uploader().upload(
					file.getBytes(),
					Map.of(
							"public_id", publicId,
							"resource_type", resourceType));

			String secureUrl = (String) uploadResult.get("secure_url");

			// rawファイル（PDF・Excel等）はダウンロード用URLに変換
			if (!isImage) {
				secureUrl = secureUrl.replace("/upload/", "/upload/fl_attachment/");
			}

			post.setFileUrl(secureUrl);
			post.setFileName(originalFilename);

		} catch (IOException e) {
			throw new RuntimeException("ファイルのアップロードに失敗しました。", e);
		}
	}

	// CloudinaryのURLからpublic_idを取得
	private String extractPublicId(String fileUrl) {
		// 例: https://res.cloudinary.com/xxx/image/upload/v123/postingapp/uuid
		String[] parts = fileUrl.split("/upload/");
		String afterUpload = parts[1];
		// バージョン番号(v123/)を除去
		if (afterUpload.startsWith("v")) {
			afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
		}
		// 拡張子を除去
		int dotIndex = afterUpload.lastIndexOf(".");
		if (dotIndex != -1) {
			afterUpload = afterUpload.substring(0, dotIndex);
		}
		return afterUpload;
	}
}