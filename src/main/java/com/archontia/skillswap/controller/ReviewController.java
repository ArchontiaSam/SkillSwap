package com.archontia.skillswap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archontia.skillswap.dto.ReviewRequestDTO;
import com.archontia.skillswap.dto.ReviewResponseDTO;
import com.archontia.skillswap.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	@PostMapping
	public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO request){
		ReviewResponseDTO response = reviewService.createReview(request);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReviewResponseDTO>> getReviewsForUser(@PathVariable Long userId){
		List<ReviewResponseDTO> response = reviewService.getReviewsForUser(userId);
		return ResponseEntity.ok(response);
	}

}
