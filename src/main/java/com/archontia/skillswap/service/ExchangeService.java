package com.archontia.skillswap.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.dto.ExchangeRequestDTO;
import com.archontia.skillswap.dto.ExchangeResponseDTO;
import com.archontia.skillswap.entity.Exchange;
import com.archontia.skillswap.entity.ExchangeStatus;
import com.archontia.skillswap.entity.Skill;
import com.archontia.skillswap.entity.SkillType;
import com.archontia.skillswap.entity.User;
import com.archontia.skillswap.entity.UserSkill;
import com.archontia.skillswap.repository.ExchangeRepository;
import com.archontia.skillswap.repository.SkillRepository;
import com.archontia.skillswap.repository.UserRepository;
import com.archontia.skillswap.repository.UserSkillRepository;

@Service
public class ExchangeService {

	private final UserRepository userRepository;
	private final SkillRepository skillRepository;
	private final ExchangeRepository exchangeRepository;
	private final UserSkillRepository userSkillRepository;

	public ExchangeService(UserRepository userRepository, SkillRepository skillRepository,
			ExchangeRepository exchangeRepository, UserSkillRepository userSkillRepository) {
		this.userRepository = userRepository;
		this.skillRepository = skillRepository;
		this.exchangeRepository = exchangeRepository;
		this.userSkillRepository = userSkillRepository;
	}

	public ExchangeResponseDTO createExchange(ExchangeRequestDTO dto) {
		if (dto.getRequesterId().equals(dto.getProviderId())) {
			throw new RuntimeException("Requester and Provider cannot be the same user!");
		}

		User provider = userRepository.findById(dto.getProviderId())
				.orElseThrow(() -> new RuntimeException("Provider not found with id: " + dto.getProviderId()));

		User requester = userRepository.findById(dto.getRequesterId())
				.orElseThrow(() -> new RuntimeException("Requester not found with id: " + dto.getRequesterId()));

		Skill offeredSkill = skillRepository.findById(dto.getOfferedSkillId())
				.orElseThrow(() -> new RuntimeException("Offered skill not found with id: " + dto.getOfferedSkillId()));

		Skill requestedSkill = skillRepository.findById(dto.getRequestedSkillId()).orElseThrow(
				() -> new RuntimeException("Requested skill not found with id: " + dto.getRequestedSkillId()));

		boolean requesterOffersIt = userSkillRepository.findByUserIdAndType(requester.getId(), SkillType.OFFERED)
				.stream().anyMatch(us -> us.getSkill().getId().equals(offeredSkill.getId()));
		if (!requesterOffersIt) {
			throw new RuntimeException("Offered skill is not currently offered by the requester!");
		}

		boolean providerOffersRequested = userSkillRepository.findByUserIdAndType(provider.getId(), SkillType.OFFERED)
				.stream().anyMatch(us -> us.getSkill().getId().equals(requestedSkill.getId()));
		if (!providerOffersRequested) {
			throw new RuntimeException("Requested skill is not currently offered by the provider!");
		}

		List<Exchange> relevant = exchangeRepository.findByRequesterIdOrProviderId(requester.getId(),
				requester.getId());


		boolean duplicatePendingExists = relevant.stream().anyMatch(
		        e -> e.getStatus() == ExchangeStatus.PENDING && ((e.getRequester().getId().equals(requester.getId())
		                && e.getProvider().getId().equals(provider.getId())
		                && e.getOfferedSkill().getId().equals(offeredSkill.getId())
		                && e.getRequestedSkill().getId().equals(requestedSkill.getId()))
		                || (e.getRequester().getId().equals(provider.getId())
		                        && e.getProvider().getId().equals(requester.getId())
		                        && e.getOfferedSkill().getId().equals(requestedSkill.getId())
		                        && e.getRequestedSkill().getId().equals(offeredSkill.getId()))));

		if (duplicatePendingExists) {
		    throw new RuntimeException("There is already a pending exchange for this match.");
		}

		Exchange exchange = new Exchange();
		exchange.setRequester(requester);
		exchange.setProvider(provider);
		exchange.setOfferedSkill(offeredSkill);
		exchange.setRequestedSkill(requestedSkill);
		exchange.setStatus(ExchangeStatus.PENDING);

		Exchange saved = exchangeRepository.save(exchange);
		return toDTO(saved);
	}

	public ExchangeResponseDTO completeExchange(Long exchangeId) {
		Exchange exchange = exchangeRepository.findById(exchangeId)
				.orElseThrow(() -> new RuntimeException("Exchange not found with id: " + exchangeId));

		if (exchange.getStatus() == ExchangeStatus.CANCELLED) {
			throw new RuntimeException("Cannot complete a cancelled exchange!");
		}
		if (exchange.getStatus() == ExchangeStatus.COMPLETED) {
			throw new RuntimeException("Exchange is already completed!");
		}

		exchange.setStatus(ExchangeStatus.COMPLETED);
		Exchange updated = exchangeRepository.save(exchange);

		removeWantedIfExists(updated.getRequester().getId(), updated.getRequestedSkill().getId());
		removeWantedIfExists(updated.getProvider().getId(), updated.getOfferedSkill().getId());

		return toDTO(updated);
	}

	@Transactional(readOnly = true)
	public List<ExchangeResponseDTO> getUserExchanges(Long userId) {
		List<Exchange> exchanges = exchangeRepository.findByRequesterIdOrProviderId(userId, userId);
		return exchanges.stream().map(this::toDTO).collect(Collectors.toList());
	}

	private void removeWantedIfExists(Long userId, Long skillId) {
		userSkillRepository.findByUserIdAndType(userId, SkillType.WANTED).stream()
				.filter(us -> us.getSkill().getId().equals(skillId)).findFirst()
				.ifPresent((UserSkill us) -> userSkillRepository.deleteById(us.getId()));
	}

	private ExchangeResponseDTO toDTO(Exchange e) {
		return new ExchangeResponseDTO(e.getId(), e.getRequester().getId(), e.getRequester().getName(),
				e.getProvider().getId(), e.getProvider().getName(), e.getOfferedSkill().getId(),
				e.getOfferedSkill().getName(), e.getRequestedSkill().getId(), e.getRequestedSkill().getName(),
				e.getStatus(), e.getCreatedAt());
	}

	public ExchangeResponseDTO cancelExchange(Long exchangeId, Long userId) {
		Exchange exchange = exchangeRepository.findById(exchangeId)
				.orElseThrow(() -> new RuntimeException("Exchange not found with id: " + exchangeId));

		if (exchange.getStatus() == ExchangeStatus.COMPLETED) {
			throw new RuntimeException("Cannot cancel a completed exchange!");
		}
		if (exchange.getStatus() == ExchangeStatus.CANCELLED) {
			throw new RuntimeException("Exchange is already cancelled!");
		}

		boolean isRequester = exchange.getRequester().getId().equals(userId);
		boolean isProvider = exchange.getProvider().getId().equals(userId);

		if (!isRequester && !isProvider) {
			throw new RuntimeException("User is not part of this exchange!");
		}

		exchange.setStatus(ExchangeStatus.CANCELLED);
		Exchange updated = exchangeRepository.save(exchange);

		return toDTO(updated);
	}
	
	public void deleteExchange(Long exchangeId, Long userId) {
		Exchange exchange = exchangeRepository.findById(exchangeId)
				.orElseThrow(() -> new RuntimeException("Exchange not found with id: " + exchangeId));

		if (exchange.getStatus() != ExchangeStatus.CANCELLED) {
			throw new RuntimeException("Only cancelled exchanges can be deleted!");
		}

		boolean isRequester = exchange.getRequester().getId().equals(userId);
		boolean isProvider = exchange.getProvider().getId().equals(userId);

		if (!isRequester && !isProvider) {
			throw new RuntimeException("User is not part of this exchange!");
		}

		exchangeRepository.deleteById(exchangeId);
	}
	
}