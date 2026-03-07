package com.vitamax.review_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.review_service.entity.ReviewEntity;
import com.vitamax.review_service.review.ReviewRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"eureka.client.enabled=false"})
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureWebTestClient
@EnableTestBinder
class ReviewServiceApplicationTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> invalidCreateReviewRequests() {
        return Stream.of(
                Arguments.of(
                        "courseId is null",
                        """
                                {
                                  "courseId": null,
                                  "author": "John",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "courseId is missing",
                        """
                                {
                                  "author": "John",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "author is blank",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "author is whitespace",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "   ",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "subject is blank",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "subject": "",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "content is blank",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "subject": "Good",
                                  "content": ""
                                }
                                """
                )
        );
    }

    static Stream<Arguments> invalidUpdateReviewRequests() {
        return Stream.of(
                Arguments.of(
                        "reviewId is null",
                        """
                                {
                                  "reviewId": null,
                                  "author": "John",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "author is blank",
                        """
                                {
                                  "reviewId": "11111111-1111-1111-1111-111111111111",
                                  "author": "",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "subject is blank",
                        """
                                {
                                  "reviewId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "subject": "   ",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "content is blank",
                        """
                                {
                                  "reviewId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "subject": "Good",
                                  "content": ""
                                }
                                """
                ),
                Arguments.of(
                        "missing required fields",
                        """
                                {
                                  "author": "John"
                                }
                                """
                )
        );
    }

    @BeforeEach
    void beforeEach() {
        StepVerifier.create(repository.deleteAll())
                .verifyComplete();
    }

    @Test
    void getReview_success_return200() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createReview(UUID.randomUUID().toString(), courseId, "Author A", "Subject A", "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/review/{courseId}", courseId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(1, reviews.size());
                    final var review = reviews.getFirst();
                    assertEquals(courseId, review.courseId());
                    assertEquals("Author A", review.author());
                    assertEquals("Subject A", review.subject());
                    assertEquals("Content A", review.content());
                });
    }

    @Test
    void getReview_notFound_return200() {
        webTestClient.get()
                .uri("/api/v1/review/{courseId}", UUID.randomUUID().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(0);
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getReview_invalidId_return400(String courseId) {
        webTestClient.get()
                .uri("/api/v1/review/{courseId}", courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createReview_success_return201() {
        webTestClient.post()
                .uri("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "author": "Phuoc Nguyen",
                            "subject": "Subject is here",
                            "content": "Content is here"
                        }
                        """.formatted(UUID.randomUUID().toString()))
                .exchange()
                .expectStatus().isCreated();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateReviewRequests")
    void createReview_invalidRequest_return400(String description, String json) {
        webTestClient.post()
                .uri("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void updateReview_notFound_return404() {
        webTestClient.put()
                .uri("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "reviewId": "%s",
                            "author": "Phuoc Nguyen",
                            "subject": "Subject is here",
                            "content": "Content is here"
                        }
                        """.formatted(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .exchange()
                .expectStatus().isNotFound();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateReviewRequests")
    void updateReview_invalidRequest_returns400(String description, String json) {
        webTestClient.put()
                .uri("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void deleteReviews_success_return204() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createReview(UUID.randomUUID().toString(), courseId, "Author A", "Subject A", "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.delete()
                .uri("/api/v1/review/{courseId}", courseId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(repository.findByCourseId(courseId))
                .verifyComplete();
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteReviews_invalidId_returns400(String courseId) {
        webTestClient.delete()
                .uri("/api/v1/review/{courseId}", courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void reviewCreateEvent() throws JsonProcessingException {
        final var courseId = UUID.randomUUID();
        final var author = "Author A";
        final var subject = "Subject A";
        final var content = "Content A";
        final var payload = objectMapper.writeValueAsBytes(new ReviewCreateCommand(courseId, author,  subject, content));

        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "review.create"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId.toString()))
                                .expectNextMatches(review ->
                                        review.getCourseId().equals(courseId.toString()) &&
                                                review.getAuthor().equals(author) && review.getSubject().equals(subject) && review.getContent().equals(content)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void reviewUpdateEvent() throws JsonProcessingException {
        final var reviewId = UUID.randomUUID();
        final var courseId = UUID.randomUUID().toString();
        final var author = "Author A";
        final var updatedAuthor = "Updated Author A";
        final var subject = "Subject A";
        final var updatedSubject = "Updated Subject A";
        final var content = "Content A";
        final var updatedContent = "Updated Content A";
        final var payload = objectMapper.writeValueAsBytes(new ReviewUpdateCommand(reviewId, updatedAuthor,  updatedSubject, updatedContent));

        StepVerifier.create(createReview(reviewId.toString(), courseId, author, subject, content))
                .expectNextCount(1)
                .verifyComplete();

        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "review.update"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId))
                                .expectNextMatches(review ->
                                        review.getReviewId().equals(reviewId.toString()) &&
                                        review.getCourseId().equals(courseId) &&
                                        review.getAuthor().equals(updatedAuthor) &&
                                        review.getSubject().equals(updatedSubject) &&
                                        review.getContent().equals(updatedContent)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void reviewDeleteEvent() throws JsonProcessingException {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createReview(UUID.randomUUID().toString(), courseId, "Author A", "Subject A", "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        inputDestination.send(
                MessageBuilder.withPayload(objectMapper.writeValueAsBytes(courseId))
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "review.delete"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId))
                                .expectNextCount(0)
                                .verifyComplete()
                        );


    }

    @Test
    void optimisticLockError() {
        final var courseId = UUID.randomUUID().toString();

        final var conflictSave = createReview(UUID.randomUUID().toString(), courseId, "Author A", "Subject A", "Content A")
                .flatMap(saved -> repository.findById(saved.getReviewId())
                        .zipWith(repository.findById(saved.getReviewId())))
                .flatMap(tuple -> {
                    final var entity1 = tuple.getT1();
                    final var entity2 = tuple.getT2();

                    entity1.setAuthor("n1");
                    return repository.save(entity1)
                            .thenReturn(entity2);
                })
                .flatMap(entity2 -> {
                    entity2.setAuthor("n2");
                    return repository.save(entity2);
                });

        StepVerifier.create(conflictSave)
                .expectError(OptimisticLockingFailureException.class)
                .verify();

        StepVerifier.create(repository.findByCourseId(courseId).single())
                .assertNext(updatedEntity -> assertEquals("n1", updatedEntity.getAuthor()))
                .verifyComplete();
    }

    private Mono<ReviewEntity> createReview(String reviewId, String courseId, String author, String subject, String content) {
        return repository.save(new ReviewEntity(
                reviewId,
                courseId,
                author,
                subject,
                content
        ));
    }
}
