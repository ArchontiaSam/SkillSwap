package com.archontia.skillswap.dto;

import com.archontia.skillswap.entity.SkillType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDTO {

	private Long requesterId;
	private Long providerId;
	private Long offeredSkillId;
	private Long requestedSkillId;
	private SkillType type;

}
