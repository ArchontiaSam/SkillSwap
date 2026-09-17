package com.archontia.skillswap.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.dto.ReviewRequestDTO;
import com.archontia.skillswap.dto.ReviewResponseDTO;
import com.archontia.skillswap.entity.Exchange;
import com.archontia.skillswap.entity.ExchangeStatus;
import com.archontia.skillswap.entity.Review;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.repository.ExchangeRepository;
import com.archontia.skillswap.repository.ReviewRepository;
import com.archontia.skillswap.repository.UserRepository;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ExchangeRepository exchangeRepository;
	private final UserRepository userRepository;

	public ReviewService(ReviewRepository reviewRepository, ExchangeRepository exchangeRepository,
			UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.exchangeRepository = exchangeRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public ReviewResponseDTO createReview(ReviewRequestDTO request) {
		// validity check
		if (request.getScore() == null || request.getScore() < 1 || request.getScore() > 5)
			throw new RuntimeException("Score must be between 1-5");

		Exchange exchange = exchangeRepository.findById(request.getExchangeId())
				.orElseThrow(() -> new RuntimeException("Exchange not found with id: " + request.getExchangeId()));

		// check if status is completed
		if (exchange.getStatus() != ExchangeStatus.COMPLETED)
			throw new RuntimeException("Cannot review an exchange that is not COMPLETED");

		// check if reviewer was in the exchange
		Long reviewerId = request.getReviewerId();
		Long requesterId = exchange.getRequester().getId();
		Long providerId = exchange.getProvider().getId();

		if (!reviewerId.equals(requesterId) && !reviewerId.equals(providerId))
			throw new RuntimeException("User is not part of this exchange");

		// checking for duplicates
		if (reviewRepository.existsByExchangeIdAndReviewerId(request.getExchangeId(), reviewerId))
			throw new RuntimeException("You have already reviewed this exchange");

		// find reviewee
		Long revieweeId = reviewerId.equals(requesterId) ? providerId : requesterId;

		User reviewer = userRepository.findById(reviewerId)
				.orElseThrow(() -> new RuntimeException("Reviewer not found"));

		User reviewee = userRepository.findById(revieweeId)
				.orElseThrow(() -> new RuntimeException("Reviewee not found"));

		// creation and storing review
		Review review = new Review();
		review.setExchange(exchange);
		review.setReviewer(reviewer);
		review.setReviewee(reviewee);
		review.setScore(request.getScore());
		review.setComment(request.getComment());

		Review savedReview = reviewRepository.save(review);

		// calculating and storing reviewees average rating
		updateUserAverageRating(revieweeId);

		return new ReviewResponseDTO(savedReview.getId(), exchange.getId(), reviewer.getId(), reviewee.getId(),
				savedReview.getScore(), savedReview.getComment(), savedReview.getCreatedAt());
	}

	@Transactional(readOnly = true)
	public List<ReviewResponseDTO> getReviewsForUser(Long userId) {
		List<Review> reviews = reviewRepository.findByRevieweeId(userId);
		return reviews.stream()
				.map(review -> new ReviewResponseDTO(review.getId(), review.getExchange().getId(),
						review.getReviewer().getId(), review.getReviewee().getId(), review.getScore(),
						review.getComment(), review.getCreatedAt()))
				.collect(Collectors.toList());

	}

	private void updateUserAverageRating(Long userId) {
		List<Review> userReviews = reviewRepository.findByRevieweeId(userId);
		if (!userReviews.isEmpty()) {
			double avg = userReviews.stream().mapToInt(Review::getScore).average().orElse(0.0);

			User user = userRepository.findById(userId).orElseThrow();
			user.setRating(avg);
			userRepository.save(user);

		}

	}

}
