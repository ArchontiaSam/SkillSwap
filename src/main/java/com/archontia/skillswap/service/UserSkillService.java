package com.archontia.skillswap.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.dto.UserSkillDTO;
import com.archontia.skillswap.entity.Skill;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.entity.UserSkill;
import com.archontia.skillswap.repository.SkillRepository;
import com.archontia.skillswap.repository.UserRepository;
import com.archontia.skillswap.repository.UserSkillRepository;

@Service
public class UserSkillService {

	private final UserSkillRepository userSkillRepository;
	private final UserRepository userRepository;
	private final SkillRepository skillRepository;

	public UserSkillService(UserSkillRepository userSkillRepository, UserRepository userRepository,
			SkillRepository skillRepository) {
		this.userSkillRepository = userSkillRepository;
		this.userRepository = userRepository;
		this.skillRepository = skillRepository;
	}

	@Transactional
	public UserSkillDTO addSkillToUser(UserSkillDTO dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

		List<Skill> skills = skillRepository.findAllByName(dto.getSkillName());
		Skill skill;
		if (skills.isEmpty()) {
			skill = new Skill();
			skill.setName(dto.getSkillName());
			skill.setCategory(dto.getCategory() != null ? dto.getCategory() : "General");
			skill.setUser(user);
			skill = skillRepository.save(skill);
		} else {
			skill = skills.get(0);
		}

		UserSkill userSkill = new UserSkill();
		userSkill.setUser(user);
		userSkill.setSkill(skill);
		userSkill.setType(dto.getType());

		UserSkill saved = userSkillRepository.save(userSkill);

		return new UserSkillDTO(saved.getId(), user.getId(), skill.getName(), skill.getCategory(), saved.getType());
	}

	@Transactional(readOnly = true)
	public List<UserSkillDTO> getUserSkills(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new RuntimeException("User not found with id: " + userId);
		}

		return userSkillRepository.findByUserId(userId).stream()
				.map(us -> new UserSkillDTO(us.getId(), us.getUser().getId(), us.getSkill().getName(),
						us.getSkill().getCategory(), us.getType()))
				.collect(Collectors.toList());
	}

	@Transactional
	public void removeSkillFromUser(Long userSkillId) {
		if (!userSkillRepository.existsById(userSkillId)) {
			throw new RuntimeException("UserSkill not found with id: " + userSkillId);
		}
		userSkillRepository.deleteById(userSkillId);
	}
}