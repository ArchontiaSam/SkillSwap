package com.archontia.skillswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String name;

	@Column
	private String description;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

}