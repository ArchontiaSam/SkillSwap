package com.archontia.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.archontia.skillswap.entity.Exchange;
import com.archontia.skillswap.entity.ExchangeStatus;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

	List<Exchange> findByRequesterIdOrProviderId(Long requesterId, Long providerId);

	@Query("SELECT e FROM Exchange e JOIN FETCH e.requester JOIN FETCH e.provider WHERE e.status = :status")
	List<Exchange> findByStatusWithUsers(@Param("status") ExchangeStatus status);
	
}
