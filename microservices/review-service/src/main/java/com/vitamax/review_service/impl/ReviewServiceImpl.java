package com.vitamax.review_service.impl;

import com.vitamax.api.core.review.ReviewService;
import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.review_service.mapper.ReviewMapper;
import com.vitamax.review_service.review.ReviewRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    @Override
    public Flux<Review> getReviews(final UUID courseId) {
        log.debug("get found reviews for courseId={}", courseId);

        return repository.findByCourseId(courseId.toString())
                .map(rev -> mapper.toReview(rev, serviceUtil.getServiceAddress()));
    }

    @Override
    public Mono<Void> createReview(final ReviewCreateCommand command) {
        log.debug("create review with command={}", command);

        return repository.save(mapper.toEntity(command)).then();
    }

    @Override
    public Mono<Void> updateReview(final ReviewUpdateCommand command) {
        log.debug("update review with command={}", command);

        return repository.findById(command.reviewId().toString())
                .flatMap(entity -> repository.save(mapper.toEntity(command, entity)))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> deleteReviews(final UUID courseId) {
        log.debug("delete review with courseId={}", courseId);

        return repository.deleteByCourseId(courseId.toString()).then();
    }
}
