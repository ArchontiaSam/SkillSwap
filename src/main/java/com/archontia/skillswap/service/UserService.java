package com.archontia.skillswap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.dto.UserDTO;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.repository.UserRepository;
import com.archontia.skillswap.repository.UserSkillRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserSkillRepository userSkillRepository;

	public UserService(UserRepository userRepository, UserSkillRepository userSkillRepository) {
		this.userRepository = userRepository;
		this.userSkillRepository= userSkillRepository;
	}

	public List<UserDTO> getAllUsers() {

		List<User> users = userRepository.findAll();

		List<UserDTO> dtoList = new ArrayList<>();

		for (User user : users) {
			UserDTO dto = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRating(),
					user.getPassword());
			dtoList.add(dto);
		}

		return dtoList;
	}

	public UserDTO getUserById(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRating(), user.getPassword());

	}

	public UserDTO createUser(UserDTO userDTO) {
		User user = new User();
		user.setName(userDTO.getName());
		user.setEmail(userDTO.getEmail());

		// set default password
		user.setPassword(userDTO.getPassword());

		User savedUser = userRepository.save(user);

		return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRating(),
				savedUser.getPassword());
	}

	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		user.setName(userDTO.getName());
		user.setEmail(userDTO.getEmail());

		User updatedUser = userRepository.save(user);
		return new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRating(),
				updatedUser.getPassword());
	}
	@Transactional
	public void deleteUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new RuntimeException("User not found with id: " + userId);
		}
		userSkillRepository.deleteAllByUserId(userId); //allows to delete user and his skills

		userRepository.deleteById(userId);
	}

	public UserDTO login(UserDTO userDTO) {
		// locate user
		User user = userRepository.findByEmail(userDTO.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		// check if password is correct
		if (!userDTO.getPassword().equals(user.getPassword()))
			throw new RuntimeException("Invalid email or password");

		// if everything is fine return UserDTO
		return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRating());
	}

}
