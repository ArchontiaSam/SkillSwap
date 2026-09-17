package com.archontia.skillswap.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
	private Long id;
	private Long exchangeId;
	private Long reviewerId;
	private Long revieweeId;
	private Integer score;
	private String comment;
	private LocalDateTime createdAt;
}