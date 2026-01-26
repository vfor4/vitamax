package com.vitamax.recommendation_service.recommendation;

import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.recommendation.RecommendationCreateCommand;
import com.vitamax.core.recommendation.RecommendationService;
import com.vitamax.core.recommendation.RecommendationUpdateCommand;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        final var result = repository.findByCourseId(courseId.toString());
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toRecommendations(result, serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Recommendation> createRecommendation(final RecommendationCreateCommand command) {
        log.debug("create recommendation for command={}", command);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRecommendation(repository.save(mapper.toEntity(command)), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Recommendation> updateRecommendation(final RecommendationUpdateCommand command) {
        log.debug("update recommendation for command={}", command);

        if (!repository.existsByRecommendationId(command.recommendationId().toString())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toRecommendation(repository.save(mapper.toEntity(command)), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> deleteRecommendations(final UUID courseId) {
        log.debug("delete recommendation for courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
