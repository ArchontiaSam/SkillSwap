package com.archontia.skillswap.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exchanges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Exchange {

	@Id // primary key is the id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "requester_id", nullable = false)
	private User requester;

	@ManyToOne
	@JoinColumn(name = "provider_id", nullable = false)
	private User provider;

	@ManyToOne
	@JoinColumn(name = "offered_skill_id", nullable = false)
	private Skill offeredSkill;

	@ManyToOne
	@JoinColumn(name = "requested_skill_id", nullable = false)
	private Skill requestedSkill;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExchangeStatus status;

	@Column(nullable = false, updatable = false) // date cannot change
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

}
