package com.vitamax.review_service.impl;

import com.vitamax.api.core.review.ReviewService;
import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.api.exception.dto.NotFoundException;
import com.vitamax.api.util.ApiUtil;
import com.vitamax.review_service.mapper.ReviewMapper;
import com.vitamax.review_service.review.ReviewRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

        return ResponseEntity.ok(mapper.toReviews(repository.findByCourseId(courseId.toString()), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> createReview(final ReviewCreateCommand command) {
        log.debug("create review with command={}", command);

        return ResponseEntity.created(ApiUtil.buildCreatedLocation(repository.save(mapper.toEntity(command)).getReviewId())).build();
    }

    @Override
    public ResponseEntity<Review> updateReview(final ReviewUpdateCommand command) {
        log.debug("update review with command={}", command);

        return repository.findById(command.reviewId().toString())
                .map(entity -> ResponseEntity.ok(mapper.toReview(repository.save(mapper.toEntity(command, entity)), serviceUtil.getServiceAddress())))
                .orElseThrow(() -> new NotFoundException("Review not found by reviewId=" + command.reviewId()));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteReviews(final UUID courseId) {
        log.debug("delete review with courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
