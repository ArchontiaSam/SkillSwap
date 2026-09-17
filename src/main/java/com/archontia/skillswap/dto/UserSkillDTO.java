package com.archontia.skillswap.dto;

import com.archontia.skillswap.entity.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillDTO {

    private Long id;
    private Long userId;
    private String skillName;
    private String category;
    private SkillType type;

    public void setName(String name) {
        this.skillName = name;
    }
}