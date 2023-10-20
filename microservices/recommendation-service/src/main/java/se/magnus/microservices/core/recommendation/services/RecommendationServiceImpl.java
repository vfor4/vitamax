package se.magnus.microservices.core.recommendation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(final int productId) {
        return List.of(new Recommendation(1, 1, "author", 2, "z", serviceUtil.getServiceAddress()));
    }
}
