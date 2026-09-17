package com.archontia.skillswap.dto;

import java.time.LocalDateTime;

import com.archontia.skillswap.entity.ExchangeStatus;
import com.archontia.skillswap.entity.SkillType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponseDTO {

	private Long id;
	private Long requesterId;
	private String requesterName;
	private Long providerId;
	private String providerName;
	private Long offeredSkillId;
	private String offeredSkillName;
	private Long requestedSkillId;
	private String requestedSkillName;
	private ExchangeStatus status;
	private LocalDateTime createdAt;
	
}
