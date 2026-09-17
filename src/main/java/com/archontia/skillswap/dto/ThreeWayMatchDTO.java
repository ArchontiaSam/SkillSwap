package com.archontia.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreeWayMatchDTO {
	// User A (is the one making the request)
	private Long userAId;
	private String userAName;
    private String userAOfferedSkill;
	
	private Long userBId;
    private String userBName;
    private Double userBRating;
    private String userBOfferedSkill;
	
	private Long userCId;
    private String userCName;
    private Double userCRating;
    private String userCOfferedSkill;
}
