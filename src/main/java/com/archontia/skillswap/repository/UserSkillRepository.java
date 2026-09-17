package com.archontia.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.archontia.skillswap.entity.SkillType;
import com.archontia.skillswap.entity.UserSkill;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

	@Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId")
	List<UserSkill> findByUserId(@Param("userId") Long userId);

	@Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.type = :skillType")
	List<UserSkill> findByUserIdAndType(@Param("userId") Long userId, @Param("skillType") SkillType skillType);
	
	@Query("SELECT us FROM UserSkill us WHERE LOWER(us.skill.name) = LOWER(:skillName) AND us.type = :skillType")
	List<UserSkill> findBySkillNameIgnoreCaseAndType(@Param("skillName") String skillName, @Param("skillType") SkillType skillType);
	
	void deleteAllByUserId(Long userId);
	
	long countBySkill_Id(Long skillId);
	}