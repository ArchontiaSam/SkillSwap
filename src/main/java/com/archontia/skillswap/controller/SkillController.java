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

import com.archontia.skillswap.dto.SkillDTO;
import com.archontia.skillswap.service.SkillService;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://localhost:4200")
public class SkillController {

	private SkillService skillService;

	public SkillController(SkillService skillService) {
		this.skillService = skillService;
	}

	@GetMapping
	public ResponseEntity<List<SkillDTO>> getAllSkills() {
		List<SkillDTO> skills = skillService.getAllSkills();
		return ResponseEntity.ok(skills);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SkillDTO> getSkillById(@PathVariable Long id) {
		SkillDTO skill = skillService.getSkillById(id);
		return ResponseEntity.ok(skill);
	}

	@PostMapping
	public ResponseEntity<SkillDTO> createdSkill(@RequestBody SkillDTO skillDTO) {
		SkillDTO createdSkill = skillService.createSkill(skillDTO);
		return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<SkillDTO> updatedSkill(@PathVariable Long id, @RequestBody SkillDTO skillDTO) {
		SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
		return ResponseEntity.ok(updatedSkill);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
		skillService.deleteSkill(id);
		return ResponseEntity.noContent().build();
	}

}
