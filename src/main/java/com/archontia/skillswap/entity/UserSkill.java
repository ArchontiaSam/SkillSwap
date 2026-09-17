package com.archontia.skillswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserSkill {

	@Id // primary key is the id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "skill_id", nullable = false)
	private Skill skill;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SkillType type;

}
