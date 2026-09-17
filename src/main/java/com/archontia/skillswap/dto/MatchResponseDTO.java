package com.archontia.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
	
	private Long matchedUserId;
	private String matchedUsername;
	private Double matchedUserRating;
	private Long offeredSkillId;
	private String offeredSkillName;
	private Long requestedSkillId;
	private String requestedSkillName;

}
