package se.magnus.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.microservices.core.review.persistence.ReviewEntity;
import se.magnus.microservices.core.review.persistence.ReviewRepository;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;
    private final Scheduler jdbcScheduler;

    @Autowired
    public ReviewServiceImpl(final ReviewRepository repository, final ReviewMapper mapper, ServiceUtil serviceUtil, final Scheduler jdbcScheduler) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<Review> createReview(final Review body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        return Mono.fromCallable(() -> {
                    log.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
                    return mapper.entityToApi(repository.save(mapper.apiToEntity(body)));
                }).onErrorMap(DataIntegrityViolationException.class, ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId()))
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Review> getReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return Mono.fromCallable(() -> {
                    List<ReviewEntity> entityList = repository.findByProductId(productId);
                    List<Review> list = mapper.entityListToApiList(entityList);
                    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
                    log.info("review list size: {}", list.size());
                    return list;
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);

    }

    @Override
    public Mono<Void> deleteReviews(final int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        log.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        return Mono.fromRunnable(() -> repository.deleteAll(repository.findByProductId(productId)))
                .subscribeOn(jdbcScheduler).then();
    }
}
