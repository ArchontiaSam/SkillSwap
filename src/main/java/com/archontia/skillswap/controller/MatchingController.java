package com.archontia.skillswap.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archontia.skillswap.dto.MatchResponseDTO;
import com.archontia.skillswap.dto.ThreeWayMatchDTO;
import com.archontia.skillswap.service.MatchingService;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:4200")
public class MatchingController {

	private final MatchingService matchingService;

	public MatchingController(MatchingService matchingService) {
		this.matchingService = matchingService;
	}

	@GetMapping("/direct/{userId}")
	public List<MatchResponseDTO> getDirectMatches(@PathVariable Long userId) {
		return matchingService.getDirectMatches(userId);
	}

	@GetMapping("/indirect/{userId}")
	public ResponseEntity<List<ThreeWayMatchDTO>> getThreeWayMatches(@PathVariable Long userId) {
		List<ThreeWayMatchDTO> matches = matchingService.getThreeWayMatches(userId);
		return ResponseEntity.ok(matches);
	}
	@PostMapping("/three-way")
	public ResponseEntity<?> createThreeWayExchange(@RequestBody ThreeWayMatchDTO request) {
	    matchingService.createThreeWayExchange(request);
	   // return ResponseEntity.ok("3-Way Exchange proposed successfully!");
	    return ResponseEntity.ok().build();
	}
	
}
