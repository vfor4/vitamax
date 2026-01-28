package com.vitamax.review_service.review;

import com.vitamax.core.review.Review;
import com.vitamax.core.review.ReviewCreateCommand;
import com.vitamax.core.review.ReviewService;
import com.vitamax.core.review.ReviewUpdateCommand;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    @Override
    public ResponseEntity<List<Review>> getReviews(final UUID courseId) {
        log.debug("get found reviews for courseId={}", courseId);

        final var result = repository.findByCourseId(courseId.toString());
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toReviews(result, serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Review> createReview(final ReviewCreateCommand command) {
        log.debug("create review with command={}", command);

        return ResponseEntity.created(serviceUtil.buildCreatedLocation(repository.save(mapper.toEntity(command)).getReviewId())).build();
    }

    @Override
    public ResponseEntity<Review> updateReview(final ReviewUpdateCommand command) {
        log.debug("update review with command={}", command);

        return repository.findById(command.reviewId().toString())
                .map(entity -> ResponseEntity.ok(mapper.toReview(repository.save(mapper.toEntity(command, entity)), serviceUtil.getServiceAddress())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteReviews(final UUID courseId) {
        log.debug("delete review with courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
