package com.archontia.skillswap.service;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.archontia.skillswap.entity.Exchange;
import com.archontia.skillswap.entity.ExchangeStatus;
import com.archontia.skillswap.repository.ExchangeRepository;

@Service
public class SocialGraphService {

	private final ExchangeRepository exchangeRepository;

	public SocialGraphService(ExchangeRepository exchangeRepository) {
		this.exchangeRepository = exchangeRepository;
	}

	private Map<Long, Set<Long>> buildGraph() {
		Map<Long, Set<Long>> graph = new HashMap<>();

		List<Exchange> completedExchanges = exchangeRepository.findByStatusWithUsers(ExchangeStatus.COMPLETED);// .findByStatusWithUsers(ExchangeStatus.COMPLETED);

		for (Exchange exchange : completedExchanges) {
			Long user1 = exchange.getRequester().getId();
			Long user2 = exchange.getProvider().getId();

			graph.putIfAbsent(user1, new HashSet<>());
			graph.putIfAbsent(user2, new HashSet<>());

			graph.get(user1).add(user2);
			graph.get(user2).add(user1);
		}
		return graph;
	}

	@Transactional(readOnly = true)
	public int getTrustDistance(Long startUserId, Long targetUserId) {

		// Null safety check
		if (startUserId == null || targetUserId == null)
			return -1;

		// if it is the same user return 0
		if (Objects.equals(startUserId, targetUserId))
			return 0;

		Map<Long, Set<Long>> graph = buildGraph();

		// if someone is not in the graph return -1 (no connection)
		if (!graph.containsKey(startUserId) || !graph.containsKey(targetUserId))
			return -1;
		// initialize
		Queue<Long> queue = new LinkedList<>();
		Set<Long> visited = new HashSet<>();
		Map<Long, Integer> distanceMap = new HashMap<>();

		queue.add(startUserId);
		visited.add(startUserId);
		distanceMap.put(startUserId, 0);

		while (!queue.isEmpty()) {
			Long current = queue.poll();

			// if null return 0 to avoid errors
			Integer currentDistanceObj = distanceMap.get(current);
			if (currentDistanceObj == null) {
				continue;
			}

			int currentDistance = currentDistanceObj;

			if (current.equals(targetUserId))
				return currentDistance;

			for (Long neighbor : graph.getOrDefault(current, Collections.emptySet())) {
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					distanceMap.put(neighbor, currentDistance + 1); // neighbor is 1 step further
					queue.add(neighbor); // add him in queue to check his his neighbors after
				}
			}
		}
		return -1; // no path
	}

}
