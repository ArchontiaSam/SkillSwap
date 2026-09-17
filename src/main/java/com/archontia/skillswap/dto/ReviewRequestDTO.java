package com.archontia.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

	private Long exchangeId;
	private Long reviewerId;
	private Integer score; //1-5
	private String comment;
}
