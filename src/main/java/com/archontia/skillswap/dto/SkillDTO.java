package com.archontia.skillswap.dto;

import com.archontia.skillswap.entity.SkillType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {

	private Long id;
	private String category;
	private String name;
	private String description;
	private Long userId;

}
