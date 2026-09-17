package com.archontia.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.archontia.skillswap.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByRevieweeId(Long revieweeId);

	boolean existsByExchangeIdAndReviewerId(Long exchangeId, Long reviewerId);
}
