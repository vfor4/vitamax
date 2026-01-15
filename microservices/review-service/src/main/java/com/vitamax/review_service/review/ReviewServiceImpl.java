package com.vitamax.review_service.review;

import com.vitamax.core.review.Review;
import com.vitamax.core.review.ReviewCreateCommand;
import com.vitamax.core.review.ReviewService;
import com.vitamax.core.review.ReviewUpdateCommand;
import com.vitamax.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ResponseEntity<List<Review>> getReviews(int courseId) {

        List<Review> result = new ArrayList<>();
        result.add(new Review(courseId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        result.add(new Review(courseId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        result.add(new Review(courseId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/reviews response size: {}", result.size());

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Review> createReview(final ReviewCreateCommand command) {
        LOG.debug("create review with command={}", command);
        return ResponseEntity.ok(new Review(command.courseId(), 1, command.author(), command.subject(), command.content(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Review> updateReview(ReviewUpdateCommand command) {
        LOG.debug("update review with command={}", command);
        return ResponseEntity.ok(new Review(command.courseId(), command.reviewId(), command.author(), command.subject(), command.content(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> deleteReview(int courseId) {
        LOG.debug("delete review with courseId={}", courseId);
        return ResponseEntity.noContent().build();
    }
}
