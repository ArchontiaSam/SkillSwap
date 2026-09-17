package com.archontia.skillswap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.archontia.skillswap.dto.ExchangeRequestDTO;
import com.archontia.skillswap.dto.ExchangeResponseDTO;
import com.archontia.skillswap.service.ExchangeService;

@RestController
@RequestMapping("/api/exchanges")
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeController {

	private ExchangeService exchangeService;

	public ExchangeController(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	@PostMapping
	public ResponseEntity<ExchangeResponseDTO> createExchange(@RequestBody ExchangeRequestDTO dto) {
		ExchangeResponseDTO response = exchangeService.createExchange(dto);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}/complete")
	public ResponseEntity<ExchangeResponseDTO> completeExchange(@PathVariable Long id) {
		ExchangeResponseDTO response = exchangeService.completeExchange(id);
		return ResponseEntity.ok(response);

	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ExchangeResponseDTO>> getUserExchanges(@PathVariable Long userId) {
		return ResponseEntity.ok(exchangeService.getUserExchanges(userId));
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<ExchangeResponseDTO> cancelExchange(@PathVariable Long id, @RequestParam Long userId) {
		ExchangeResponseDTO response = exchangeService.cancelExchange(id, userId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExchange(@PathVariable Long id, @RequestParam Long userId) {
		exchangeService.deleteExchange(id, userId);
		return ResponseEntity.noContent().build();
	}

}
