package com.archontia.skillswap.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Review {

	@Id //primary key is the id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="exchange_id",nullable=false)
	private Exchange exchange;
	
	@ManyToOne
	@JoinColumn(name="reviewer_id",nullable=false)
	private User reviewer;
	
	@ManyToOne
	@JoinColumn(name="reviewee_id",nullable=false)
	private  User reviewee;
	
	@Column(nullable=false)
	private Integer score;
	
	@Column
	private String comment;

	@Column(nullable=false,updatable=false)
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
}
