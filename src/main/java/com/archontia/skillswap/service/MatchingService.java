package com.archontia.skillswap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.dto.MatchResponseDTO;
import com.archontia.skillswap.dto.ThreeWayMatchDTO;
import com.archontia.skillswap.entity.Exchange;
import com.archontia.skillswap.entity.ExchangeStatus;
import com.archontia.skillswap.entity.Skill;
import com.archontia.skillswap.entity.SkillType;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.entity.UserSkill;
import com.archontia.skillswap.repository.ExchangeRepository;
import com.archontia.skillswap.repository.UserRepository;
import com.archontia.skillswap.repository.UserSkillRepository;

@Service
public class MatchingService {

	private final UserRepository userRepository;
	private final UserSkillRepository userSkillRepository;
	private final ExchangeRepository exchangeRepository;

	public MatchingService(UserRepository userRepository, UserSkillRepository userSkillRepository,
			ExchangeRepository exchangeRepository) {
		this.userRepository = userRepository;
		this.userSkillRepository = userSkillRepository;
		this.exchangeRepository = exchangeRepository;
	}

	public List<MatchResponseDTO> getDirectMatches(Long userId) {

		if (!userRepository.existsById(userId)) {
			throw new RuntimeException("User not found with id: " + userId);
		}

		List<UserSkill> myWantedSkills = userSkillRepository.findByUserIdAndType(userId, SkillType.WANTED);
		List<UserSkill> myOfferedSkills = userSkillRepository.findByUserIdAndType(userId, SkillType.OFFERED);

		List<MatchResponseDTO> matches = new ArrayList<>();

		for (UserSkill myWanted : myWantedSkills) {
			List<UserSkill> candidateOffered = userSkillRepository
					.findBySkillNameIgnoreCaseAndType(myWanted.getSkill().getName(), SkillType.OFFERED);

			for (UserSkill otherOffered : candidateOffered) {
				Long otherUserId = otherOffered.getUser().getId();

				// Ignore self
				if (otherUserId.equals(userId))
					continue;

				List<UserSkill> otherWantedSkills = userSkillRepository.findByUserIdAndType(otherUserId,
						SkillType.WANTED);

				for (UserSkill myOffered : myOfferedSkills) {
					for (UserSkill otherWanted : otherWantedSkills) {
						if (myOffered.getSkill().getName().equalsIgnoreCase(otherWanted.getSkill().getName())) {
							matches.add(new MatchResponseDTO(otherUserId, otherOffered.getUser().getName(), otherOffered.getUser().getRating(),
									otherOffered.getSkill().getId(), otherOffered.getSkill().getName(),
									myOffered.getSkill().getId(), myOffered.getSkill().getName()));
						}
					}
				}
			}
		}
		return matches;
	}

	public List<ThreeWayMatchDTO> getThreeWayMatches(Long userAId) {
		// when user does not exist in the repository
		if (!userRepository.existsById(userAId)) {
			throw new RuntimeException("User not found with id: " + userAId);
		}

		List<ThreeWayMatchDTO> threeWayMatches = new ArrayList<>();

		// Skills wanted and offered by userA
		List<UserSkill> userAWanted = userSkillRepository.findByUserIdAndType(userAId, SkillType.WANTED);
		List<UserSkill> userAOffered = userSkillRepository.findByUserIdAndType(userAId, SkillType.OFFERED);

		// Which user offers skill that userA wants
		for (UserSkill aWanted : userAWanted) {
			List<UserSkill> candidateBs = userSkillRepository
					.findBySkillNameIgnoreCaseAndType(aWanted.getSkill().getName(), SkillType.OFFERED);

			for (UserSkill userBOffered : candidateBs) {
				Long userBId = userBOffered.getUser().getId();
				if (userBId.equals(userAId))
					continue; // not userA

				// find out what skill userB wants
				List<UserSkill> userBWanted = userSkillRepository.findByUserIdAndType(userBId, SkillType.WANTED);

				for (UserSkill userbWanted : userBWanted) {
					// find out who (userC) offers wanted skill from whose (usersB) side
					List<UserSkill> candidateCs = userSkillRepository
							.findBySkillNameIgnoreCaseAndType(userbWanted.getSkill().getName(), SkillType.OFFERED);

					for (UserSkill userCOffered : candidateCs) {
						Long userCId = userCOffered.getUser().getId();
						if (userCId.equals(userAId) || userCId.equals(userBId))
							continue; // separate people

						// check if userC wants a skill that userA offers
						List<UserSkill> userCWanted = userSkillRepository.findByUserIdAndType(userCId,
								SkillType.WANTED);

						for (UserSkill cWanted : userCWanted) {
							for (UserSkill aOffered : userAOffered) {
								if (cWanted.getSkill().getName().equalsIgnoreCase(aOffered.getSkill().getName())) {

									// circle found
									threeWayMatches.add(new ThreeWayMatchDTO(userAId, aOffered.getUser().getName(),
											aOffered.getSkill().getName(), userBId, userBOffered.getUser().getName(),
											userBOffered.getUser().getRating(),userBOffered.getSkill().getName(), userCId,
											userCOffered.getUser().getName(),userCOffered.getUser().getRating() , userCOffered.getSkill().getName()));
								}
							}
						}
					}
				}
			}
		}
		return threeWayMatches;
	}

	@Transactional
	public void createThreeWayExchange(ThreeWayMatchDTO request) {
		User userA = userRepository.findById(request.getUserAId())
				.orElseThrow(() -> new RuntimeException("User A not found"));
		User userB = userRepository.findById(request.getUserBId())
				.orElseThrow(() -> new RuntimeException("User B not found"));
		User userC = userRepository.findById(request.getUserCId())
				.orElseThrow(() -> new RuntimeException("User C not found"));

		UserSkill userAOfferedSkill = userSkillRepository.findByUserIdAndType(userA.getId(), SkillType.OFFERED).stream()
				.filter(us -> us.getSkill().getName().equalsIgnoreCase(request.getUserAOfferedSkill())).findFirst()
				.orElseThrow(() -> new RuntimeException("User A offered skill not found"));

		UserSkill userBOfferedSkill = userSkillRepository.findByUserIdAndType(userB.getId(), SkillType.OFFERED).stream()
				.filter(us -> us.getSkill().getName().equalsIgnoreCase(request.getUserBOfferedSkill())).findFirst()
				.orElseThrow(() -> new RuntimeException("User B offered skill not found"));

		UserSkill userCOfferedSkill = userSkillRepository.findByUserIdAndType(userC.getId(), SkillType.OFFERED).stream()
				.filter(us -> us.getSkill().getName().equalsIgnoreCase(request.getUserCOfferedSkill())).findFirst()
				.orElseThrow(() -> new RuntimeException("User C offered skill not found"));

		boolean cycleExists = exchangeRepository.findByRequesterIdOrProviderId(userA.getId(), userA.getId()).stream()
				.anyMatch(
						e -> e.getStatus() != ExchangeStatus.CANCELLED && e.getRequester().getId().equals(userA.getId())
								&& e.getProvider().getId().equals(userB.getId())
								&& e.getOfferedSkill().getId().equals(userAOfferedSkill.getSkill().getId())
								&& e.getRequestedSkill().getId().equals(userBOfferedSkill.getSkill().getId()));

		if (cycleExists) {
			throw new RuntimeException("This 3-way exchange cycle already exists.");
		}

		Exchange exchangeAB = new Exchange();
		exchangeAB.setRequester(userA);
		exchangeAB.setProvider(userB);
		exchangeAB.setOfferedSkill(userAOfferedSkill.getSkill());
		exchangeAB.setRequestedSkill(userBOfferedSkill.getSkill());
		exchangeAB.setStatus(ExchangeStatus.PENDING);

		Exchange exchangeBC = new Exchange();
		exchangeBC.setRequester(userB);
		exchangeBC.setProvider(userC);
		exchangeBC.setOfferedSkill(userBOfferedSkill.getSkill());
		exchangeBC.setRequestedSkill(userCOfferedSkill.getSkill());
		exchangeBC.setStatus(ExchangeStatus.PENDING);

		Exchange exchangeCA = new Exchange();
		exchangeCA.setRequester(userC);
		exchangeCA.setProvider(userA);
		exchangeCA.setOfferedSkill(userCOfferedSkill.getSkill());
		exchangeCA.setRequestedSkill(userAOfferedSkill.getSkill());
		exchangeCA.setStatus(ExchangeStatus.PENDING);

		exchangeRepository.save(exchangeAB);
		exchangeRepository.save(exchangeBC);
		exchangeRepository.save(exchangeCA);
	}
}