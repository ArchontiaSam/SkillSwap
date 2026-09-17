package com.archontia.skillswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "users")  
@Getter // getters
@Setter // setters
@NoArgsConstructor // creates empty constructors
@AllArgsConstructor // creates constructor with all the attributes
public class User {

	@Id //primary key is the id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL is going to increase id by 1
	private Long id;

	@Column(nullable = false) // NOT NULL
	private String name;

	@Column(nullable = false, unique = true) // every email should be distinct & NOT NULL
	private String email;

	@Column(nullable = false)
	private String password; // NOT NULL

	@Column(nullable = false)
	private Double rating = 0.0; // initialize with 0.0 (CANNOT BE NULL)
}