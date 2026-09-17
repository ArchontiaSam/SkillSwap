package com.archontia.skillswap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.archontia.skillswap.service.SocialGraphService;

@RestController
@RequestMapping("/api/v1/graph")
@CrossOrigin(origins = "http://localhost:4200")
public class GraphController {

	private final SocialGraphService socialGraphService;

	public GraphController(SocialGraphService socialGraphService) {
		this.socialGraphService = socialGraphService;
	}
	
	@GetMapping("/trust-distance")
	public ResponseEntity<Integer> getTrustDistance(@RequestParam Long fromUserId,@RequestParam Long toUserId){
		
		int distance = socialGraphService.getTrustDistance(fromUserId, toUserId);
		return ResponseEntity.ok(distance);
	}
	

}
