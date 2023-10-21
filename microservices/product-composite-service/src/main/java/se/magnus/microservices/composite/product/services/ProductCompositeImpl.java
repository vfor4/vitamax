package se.magnus.microservices.composite.product.services;

import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.product.*;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeImpl implements ProductCompositeService {

    private final ProductCompositeIntegration integration;
    private final ServiceUtil serviceUtil;

    public ProductCompositeImpl(final ProductCompositeIntegration integration, final ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ProductAggregate getProduct(final int productId) {
        Product pro = integration.getProduct(productId);
        List<Review> revs = integration.getReviews(productId);
        List<Recommendation> recs = integration.getRecommendations(productId);
        return createProductAggregate(pro, recs, revs, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(final Product pro, final List<Recommendation> recs, final List<Review> revs, final String serviceAddress) {
        // 1. Setup product info
        int productId = pro.getProductId();
        String name = pro.getName();
        int weight = pro.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries =
                (recs == null) ? null : recs.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries =
                (revs == null) ? null : revs.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = pro.getServiceAddress();
        String reviewAddress = (revs != null && !revs.isEmpty()) ? revs.get(0).getServiceAddress() : "";
        String recommendationAddress = (recs != null && !recs.isEmpty()) ? recs.get(0).getServiceAddress() : "";
        var serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}
