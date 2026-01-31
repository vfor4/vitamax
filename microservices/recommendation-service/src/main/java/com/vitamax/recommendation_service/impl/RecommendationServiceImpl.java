package com.vitamax.recommendation_service.impl;

import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.api.exception.dto.NotFoundException;
import com.vitamax.api.util.ApiUtil;
import com.vitamax.recommendation_service.mapper.RecommendationMapper;
import com.vitamax.recommendation_service.repository.RecommendationRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final ServiceUtil serviceUtil;
    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;

    @Override
    public ResponseEntity<List<Recommendation>> getRecommendations(final UUID courseId) {
        log.debug("get found recommendations for courseId={}", courseId);

        return ResponseEntity.ok(mapper.toRecommendations(repository.findByCourseId(courseId.toString()), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> createRecommendation(final RecommendationCreateCommand command) {
        log.debug("create recommendation for command={}", command);

        return ResponseEntity.created(ApiUtil.buildCreatedLocation(repository.save(mapper.toEntity(command)).getRecommendationId())).build();
    }

    @Override
    public ResponseEntity<Recommendation> updateRecommendation(final RecommendationUpdateCommand command) {
        log.debug("update recommendation for command={}", command);

        return repository.findByRecommendationId(command.recommendationId().toString())
                .map(entity -> ResponseEntity.ok(mapper.toRecommendation(repository.save(mapper.toEntity(command, entity)), serviceUtil.getServiceAddress())))
                .orElseThrow(() -> new NotFoundException("Recommendation not found by recommendationId=" + command.recommendationId()));
    }

    @Override
    public ResponseEntity<Void> deleteRecommendations(final UUID courseId) {
        log.debug("delete recommendation for courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
