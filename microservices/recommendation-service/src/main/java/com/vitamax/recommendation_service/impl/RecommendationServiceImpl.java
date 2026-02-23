package com.vitamax.recommendation_service.impl;

import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.recommendation_service.mapper.RecommendationMapper;
import com.vitamax.recommendation_service.repository.RecommendationRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final ServiceUtil serviceUtil;
    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;

    @Override
    public Flux<Recommendation> getRecommendations(final UUID courseId) {
        log.debug("get found recommendations for courseId={}", courseId);

        return repository.findByCourseId(courseId.toString()).map(
                rec -> mapper.toRecommendation(rec, serviceUtil.getServiceAddress()));
    }

    @Override
    public Mono<Void> createRecommendation(final RecommendationCreateCommand command) {
        log.debug("create recommendation for command={}", command);

        return repository.save(mapper.toEntity(command))
                .then();
    }

    @Override
    public Mono<Void> updateRecommendation(final RecommendationUpdateCommand command) {
        log.debug("update recommendation for command={}", command);

        return repository.findByRecommendationId(command.recommendationId().toString())
                .flatMap(entity -> repository.save(mapper.toEntity(command, entity)))
                .then();
    }

    @Override
    public Mono<Void> deleteRecommendations(final UUID courseId) {
        log.debug("delete recommendation for courseId={}", courseId);

        return repository.deleteByCourseId(courseId.toString()).then();
    }
}
