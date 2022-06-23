package com.javabhakt.group.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javabhakt.group.entity.Post;
import com.javabhakt.group.entity.PostStatus;
import com.javabhakt.group.repository.PostRepository;

@RestController
@RequestMapping("/post")
public class PostController {

	@Autowired
	private PostRepository postRepository;
	
	@PostMapping("/create")
	public String createPost (@RequestBody Post post, Principal principal) {
		post.setStatus(PostStatus.PENDING);
		post.setUserName(principal.getName());
		postRepository.save(post);
		return principal.getName() + " Your post published successfully, required ADMIN/MODERATOR action now !!";
	}
	
	@GetMapping("/approvePost/{postId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String approvePost (@PathVariable int postId) {
	Post post = postRepository.findById(postId).get();
	post.setStatus(PostStatus.APPROVED);
	postRepository.save(post);
		return "Post Approved !!";
	}
	
	@GetMapping("/approveAll")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String approveAll () {
		postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.PENDING)).forEach(post -> {post.setStatus(PostStatus.APPROVED);
		postRepository.save(post);
		});
		return "Approved all the posts";
	}
	
	@GetMapping("/rejectPost/{postId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String rejectPost (@PathVariable int postId) {
	Post post = postRepository.findById(postId).get();
	post.setStatus(PostStatus.REJECTED);
	postRepository.save(post);
		return "Post Rejected !!";
	}
	
	@GetMapping("/rejectAll")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String rejectAll () {
		postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.PENDING)).forEach(post -> {post.setStatus(PostStatus.REJECTED);
		postRepository.save(post);
		});
		return "Rejected all the posts";
	}
	
	@GetMapping("/viewAll")
	public List<Post> viewAll () {
		return postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.APPROVED)).collect(Collectors.toList());
	}
	
}
