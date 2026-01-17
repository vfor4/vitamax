package com.vitamax.recommendation_service.recommendation;

import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.recommendation.RecommendationCreateCommand;
import com.vitamax.core.recommendation.RecommendationService;
import com.vitamax.core.recommendation.RecommendationUpdateCommand;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final RecommendationRepository repository;

    @Override
    public ResponseEntity<List<Recommendation>> getRecommendations(final int courseId) {
        final var result = new ArrayList<Recommendation>();

        result.add(new Recommendation(courseId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
        result.add(new Recommendation(courseId, 2, "Author 2", 1, "Content 2", serviceUtil.getServiceAddress()));
        result.add(new Recommendation(courseId, 3, "Author 2", 1, "Content 2", serviceUtil.getServiceAddress()));

        LOG.debug("get found {} recommendations for courseId={}", result.size(), courseId);

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Recommendation> createRecommendation(final RecommendationCreateCommand command) {
        LOG.debug("create recommendation for command={}", command);
        return ResponseEntity.ok(new Recommendation(command.courseId(), 1, command.author(), command.rate(), command.content(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Recommendation> updateRecommendation(final RecommendationUpdateCommand command) {
        LOG.debug("update recommendation for command={}", command);
        return ResponseEntity.ok(new Recommendation(command.courseId(), command.courseId(), command.author(), command.rate(), command.content(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> deleteRecommendation(final int courseId) {
        LOG.debug("delete recommendation for courseId={}", courseId);
        return ResponseEntity.noContent().build();
    }
}
