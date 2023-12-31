package se.magnus.microservices.composite.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.product.*;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;

@RestController
public class ProductCompositeImpl implements ProductCompositeService {
    private static final Logger log = LoggerFactory.getLogger(ProductCompositeImpl.class);
    private final ProductCompositeIntegration integration;
    private final ServiceUtil serviceUtil;

    public ProductCompositeImpl(final ProductCompositeIntegration integration, final ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Void> createProduct(final ProductAggregate body) {
        try {

            log.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());
            List<Mono> monos = new ArrayList<>();
            Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            monos.add(integration.createProduct(product));

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    monos.add(integration.createRecommendation(recommendation));
                });
            }

            if (body.getReviews() != null) {
                body.getReviews().forEach(r -> {
                    Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    monos.add(integration.createReview(review));
                });
            }
            return Mono.zip(r -> "", monos.toArray(new Mono[0]))
                    .doOnError(ex -> log.error("createCompositeProduct failed: {}", ex.toString()))
                    .then();
        } catch (RuntimeException re) {
            log.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public Mono<ProductAggregate> getProduct(final int productId, final int delay, final int faultPercent) {
        log.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);
        return Mono.zip(values -> createProductAggregate(
                        (Product) values[0],
                        (List<Recommendation>) values[1],
                        (List<Review>) values[2],
                        serviceUtil.getServiceAddress()),
                integration.getProduct(productId, delay, faultPercent),
                integration.getRecommendations(productId).collectList(),
                integration.getReviews(productId).collectList());
    }

    @Override
    public Mono<Void> deleteProduct(final int productId) {
        try {
            log.info("Will delete a product aggregate for product.id: {}", productId);
            return Mono.zip(
                            r -> "",
                            integration.deleteProduct(productId),
                            integration.deleteRecommendations(productId),
                            integration.deleteReviews(productId))
                    .doOnError(ex -> log.warn("delete failed: {}", ex.toString()))
                    .log(log.getName(), FINE).then();

        } catch (RuntimeException re) {
            log.warn("deleteCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    private ProductAggregate createProductAggregate(final Product pro, final List<Recommendation> recs, final List<Review> revs, final String serviceAddress) {
        // 1. Setup product info
        int productId = pro.getProductId();
        String name = pro.getName();
        int weight = pro.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries =
                (recs == null) ? null : recs.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries =
                (revs == null) ? null : revs.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = pro.getServiceAddress();
        String reviewAddress = (revs != null && !revs.isEmpty()) ? revs.get(0).getServiceAddress() : "";
        String recommendationAddress = (recs != null && !recs.isEmpty()) ? recs.get(0).getServiceAddress() : "";
        var serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}