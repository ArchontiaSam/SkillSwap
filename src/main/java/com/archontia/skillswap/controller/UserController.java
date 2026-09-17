package com.archontia.skillswap.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archontia.skillswap.dto.UserDTO;
import com.archontia.skillswap.dto.UserSkillDTO;
import com.archontia.skillswap.service.UserService;
import com.archontia.skillswap.service.UserSkillService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	private final UserService userService;
	private final UserSkillService userSkillService;

	public UserController(UserService userService, UserSkillService userSkillService) {
	    this.userService = userService;
	    this.userSkillService = userSkillService;
	}

	@GetMapping
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		UserDTO user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
		UserDTO createdUser = userService.createUser(userDTO);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
		UserDTO updatedUser = userService.updateUser(id, userDTO);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{userId}/skills")
	public ResponseEntity<UserSkillDTO> addSkillToUser(@PathVariable Long userId, @RequestBody UserSkillDTO dto) {
	    dto.setUserId(userId);
	    return ResponseEntity.ok(userSkillService.addSkillToUser(dto));
	}

	@GetMapping("/{userId}/skills")
	public ResponseEntity<List<UserSkillDTO>> getUserSkills(@PathVariable Long userId) {
	    return ResponseEntity.ok(userSkillService.getUserSkills(userId));
	}

	@DeleteMapping("/skills/{userSkillId}")
	public ResponseEntity<Void> removeSkillFromUser(@PathVariable Long userSkillId) {
	    userSkillService.removeSkillFromUser(userSkillId);
	    return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO){
		 return ResponseEntity.ok(userService.login(userDTO));
	}

}
