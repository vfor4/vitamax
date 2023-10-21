package se.magnus.microservices.core.review.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private final ServiceUtil serviceUtil;

    @Autowired
    public ReviewServiceImpl(final ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(final int productId) {
        return List.of(new Review(1, 2, "dog", "school", "nothing", serviceUtil.getServiceAddress()));
    }
}
