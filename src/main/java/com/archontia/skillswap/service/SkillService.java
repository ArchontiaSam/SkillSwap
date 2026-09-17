package com.archontia.skillswap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.archontia.skillswap.dto.SkillDTO;
import com.archontia.skillswap.entity.Skill;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.repository.SkillRepository;
import com.archontia.skillswap.repository.UserRepository;

@Service
public class SkillService {

	private final SkillRepository skillRepository;
	private final UserRepository userRepository;

	public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
		this.skillRepository = skillRepository;
		this.userRepository = userRepository;
	}

	public List<SkillDTO> getAllSkills() {
		List<Skill> skills = skillRepository.findAll();
		List<SkillDTO> dtoList = new ArrayList<>();
		for (Skill skill : skills) {
			Long userId = (skill.getUser() != null) ? skill.getUser().getId() : null;
			SkillDTO dto = new SkillDTO(skill.getId(), skill.getCategory(), skill.getName(),
					skill.getDescription(), userId);
			dtoList.add(dto);
		}
		return dtoList;
	}

	public SkillDTO getSkillById(Long id) {
		Skill skill = skillRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
		Long userId = (skill.getUser() != null) ? skill.getUser().getId() : null;
		return new SkillDTO(skill.getId(), skill.getCategory(), skill.getName(), skill.getDescription(), userId);
	}

	public SkillDTO createSkill(SkillDTO skillDTO) {
		if (skillDTO.getUserId() == null) {
			throw new RuntimeException("UserId cannot be null");
		}
		User user = userRepository.findById(skillDTO.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + skillDTO.getUserId()));

		Skill skill = new Skill();
		skill.setName(skillDTO.getName());
		skill.setCategory(skillDTO.getCategory());
		skill.setDescription(skillDTO.getDescription());
		skill.setUser(user);

		Skill savedSkill = skillRepository.save(skill);
		return new SkillDTO(savedSkill.getId(), savedSkill.getCategory(), savedSkill.getName(),
				savedSkill.getDescription(), savedSkill.getUser().getId());
	}

	public SkillDTO updateSkill(Long id, SkillDTO skillDTO) {
		Skill skill = skillRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));

		User updatedUser = skill.getUser();
		if (skillDTO.getUserId() != null && !skillDTO.getUserId().equals(skill.getUser().getId())) {
			updatedUser = userRepository.findById(skillDTO.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with id: " + skillDTO.getUserId()));
		}
		skill.setName(skillDTO.getName());
		skill.setCategory(skillDTO.getCategory());
		skill.setDescription(skillDTO.getDescription());
		skill.setUser(updatedUser);

		Skill updatedSkill = skillRepository.save(skill);
		return new SkillDTO(updatedSkill.getId(), updatedSkill.getCategory(), updatedSkill.getName(),
				updatedSkill.getDescription(), updatedSkill.getUser().getId());
	}

	public void deleteSkill(Long id) {
		if (!skillRepository.existsById(id)) {
			throw new RuntimeException("Skill not found with id: " + id);
		}
		skillRepository.deleteById(id);
	}
}