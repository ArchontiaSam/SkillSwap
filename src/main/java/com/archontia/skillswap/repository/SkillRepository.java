package com.archontia.skillswap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.archontia.skillswap.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

	Optional<Skill> findByName(String name);
	
	boolean existsByName(String name);
	
	List<Skill> findAllByName(String name);
	
//  //returns all skills of a user based on skill type (offered,wanted)
//	List<Skill> findByUserIdAndType(Long UserId, SkillType type);
//		
//	//returns all users who have a particular skill name and type
//	List<Skill> findByNameIgnoreCaseAndType(String name,SkillType type);
//	
}