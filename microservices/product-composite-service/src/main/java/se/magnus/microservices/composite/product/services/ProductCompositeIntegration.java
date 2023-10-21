package se.magnus.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;

import java.util.List;

@Service
public class ProductCompositeIntegration implements ProductService, ReviewService, RecommendationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(final RestTemplate restTemplate,
                                       final ObjectMapper mapper,
                                       @Value("${app.product-service.host}") String proHost,
                                       @Value("${app.product-service.port}") int proPort,
                                       @Value("${app.review-service.host}") String revHost,
                                       @Value("${app.review-service.port}") int revPort,
                                       @Value("${app.recommendation-service.host}") String recHost,
                                       @Value("${app.recommendation-service.port}") int recPort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        productServiceUrl = "http://" + proHost + ":" + proPort + "/product/";
        recommendationServiceUrl = "http://" + recHost + ":" + recPort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + revHost + ":" + revPort + "/review?productId=";
    }

    @Override
    public Product getProduct(final int productId) {
        return restTemplate.getForObject(this.productServiceUrl + productId, Product.class);
    }

    @Override
    public List<Recommendation> getRecommendations(final int productId) {
        return restTemplate.exchange(this.recommendationServiceUrl + productId, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Recommendation>>() {
                }).getBody();
    }

    @Override
    public List<Review> getReviews(final int productId) {
        return restTemplate.exchange(this.reviewServiceUrl + productId, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Review>>() {
                }).getBody();

    }
}
