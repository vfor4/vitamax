package com.vitamax.recommendation_service.recommendation;

import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.recommendation.RecommendationService;
import com.vitamax.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int courseId) {

        LOG.debug("get found course for courseId={}", courseId);

        var result = new ArrayList<Recommendation>();

        result.add(new Recommendation(courseId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
        result.add(new Recommendation(courseId, 2, "Author 2", 1, "Content 2", serviceUtil.getServiceAddress()));
        result.add(new Recommendation(courseId, 3, "Author 2", 1, "Content 2", serviceUtil.getServiceAddress()));
        return result;
    }
}
