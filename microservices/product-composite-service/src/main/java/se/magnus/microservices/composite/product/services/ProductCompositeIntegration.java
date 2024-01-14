package se.magnus.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.microservices.composite.product.services.tracing.ObservationUtil;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import java.net.URI;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {
    private static final String PRODUCT_SERVICE_URL = "http://product";
    private static final String RECOMMENDATION_SERVICE_URL = "http://recommendation";
    private static final String REVIEW_SERVICE_URL = "http://review";
    private static final Logger log = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final StreamBridge streamBridge;
    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final Scheduler publishEventScheduler;

    @Autowired
    public ProductCompositeIntegration(
            final StreamBridge streamBridge, final WebClient webClient, final ObjectMapper mapper,
            final Scheduler publishEventScheduler, final ObservationUtil observationUtil) {
        this.streamBridge = streamBridge;
        this.webClient = webClient;
        this.mapper = mapper;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<Product> createProduct(final Product body) {
        return Mono.fromCallable(() -> {
            sendMessage("products-out-0", new Event<>(CREATE, body.getProductId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    //    @Retry(name = "product")
    @CircuitBreaker(name = "product", fallbackMethod = "fallBackGetProduct")
    @TimeLimiter(name = "product")
    @Override
    public Mono<Product> getProduct(final int productId, final int delay, final int faultPercent) {
        final URI url = UriComponentsBuilder.fromUriString(
                PRODUCT_SERVICE_URL + "/product/{productId}?delay={delay}&faultPercent={faultPercent}"
        ).build(productId, delay, faultPercent);
        log.debug("Will call the getProduct API on URL: {}", url);
        return webClient.get().uri(url)
                .retrieve().bodyToMono(Product.class)
                .onErrorMap(this::handleHttpClientException);
    }

    private Mono<Product> fallBackGetProduct(final int productId, final int delay, final int faultPercent, final CallNotPermittedException ex) {
        if (productId == 13) {
            log.debug("Product id 13 not found in fallback cache");
            throw new NotFoundException("product id 13 not found");
        }
        return Mono.just(new Product(productId, "name-cache", 10, "service-address-cache"));
    }

    @Override
    public Mono<Void> deleteProduct(final int productId) {
        return Mono.fromRunnable(() -> sendMessage("products-out-0", new Event<>(DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<Recommendation> createRecommendation(final Recommendation body) {
        return Mono.fromCallable(() -> {
            sendMessage("recommendations-out-0", new Event<>(CREATE, body.getProductId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Flux<Recommendation> getRecommendations(final int productId) {
        final String url = RECOMMENDATION_SERVICE_URL + "/recommendation?productId=" + productId;
        log.debug("Will call the getRecommendations API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class)
                .onErrorResume(ex -> Flux.empty());
    }

    @Override
    public Mono<Void> deleteRecommendations(final int productId) {
        return Mono.fromRunnable(() -> sendMessage("recommendations-out-0", new Event(DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<Review> createReview(final Review body) {
        return Mono.fromCallable(() -> {
            sendMessage("reviews-out-0", new Event(CREATE, body.getProductId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Flux<Review> getReviews(final int productId) {
        final String url = REVIEW_SERVICE_URL + "/review?productId=" + productId;
        log.debug("Will call the getReviews API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToFlux(Review.class)
                .onErrorResume(ex -> Flux.empty());
    }

    @Override
    public Mono<Void> deleteReviews(final int productId) {
        return Mono.fromRunnable(() -> sendMessage("reviews-out-0", new Event(DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    private Throwable handleHttpClientException(final Throwable ex) {
        if (!(ex instanceof final WebClientResponseException wcre)) {
            log.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }
        switch (HttpStatus.resolve(wcre.getStatusCode().value())) {
            case NOT_FOUND -> {
                return new NotFoundException(getErrorMessage(wcre));
            }
            case UNPROCESSABLE_ENTITY -> {
                return new InvalidInputException(getErrorMessage(wcre));
            }
            default -> {
                log.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                log.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
            }
        }
    }

    private String getErrorMessage(final WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (final IOException ioex) {
            return ex.getMessage();
        }
    }

    private void sendMessage(final String bindingName, final Event event) {
        log.debug("Sending a {} message to {}", event.getType(), bindingName);
        final Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
