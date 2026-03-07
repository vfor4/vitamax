package com.vitamax.recommendation_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.recommendation_service.entity.RecommendationEntity;
import com.vitamax.recommendation_service.repository.RecommendationRepository;
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
import org.testcontainers.containers.MongoDBContainer;
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
@Testcontainers
@AutoConfigureWebTestClient
@EnableTestBinder
class RecommendationServiceApplicationTests {
    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RecommendationRepository repository;

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> invalidCreateRecommendationRequests() {
        return Stream.of(
                Arguments.of(
                        "courseId is null",
                        """
                                {
                                  "courseId": null,
                                  "author": "John",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "author is blank",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "author is missing",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "rate is negative",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": -1,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "content is blank",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 5,
                                  "content": ""
                                }
                                """
                ),
                Arguments.of(
                        "content is missing",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 5
                                }
                                """
                ),
                Arguments.of(
                        "courseId is invalid UUID",
                        """
                                {
                                  "courseId": "not-a-uuid",
                                  "author": "John",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                )
        );
    }

    static Stream<Arguments> invalidUpdateRecommendationRequests() {
        return Stream.of(
                Arguments.of(
                        "null recommendationId",
                        """
                                {
                                  "recommendationId": null,
                                  "author": "John",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "blank author",
                        """
                                {
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "   ",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "missing author",
                        """
                                {
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "rate is negative",
                        """
                                {
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": -1,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "blank content",
                        """
                                {
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 5,
                                  "content": ""
                                }
                                """
                ),
                Arguments.of(
                        "missing content",
                        """
                                {
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 5
                                }
                                """
                ),
                Arguments.of(
                        "invalid UUID format",
                        """
                                {
                                  "recommendationId": "also-not-a-uuid",
                                  "author": "John",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "empty request body",
                        "{}"
                )
        );
    }

    @BeforeEach
    void beforeEach() {
        StepVerifier.create(repository.deleteAll())
                .verifyComplete();
    }

    @Test
    void getRecommendation_success_return200() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createRecommendation(courseId, UUID.randomUUID().toString(), "Author A", 10, "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/recommendation/{courseId}", courseId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Recommendation.class)
                .value(recommendations -> {
                    assertEquals(1, recommendations.size());
                    final var recommendation = recommendations.getFirst();
                    assertEquals(courseId, recommendation.courseId());
                    assertEquals("Author A", recommendation.author());
                    assertEquals(10, recommendation.rate());
                    assertEquals("Content A", recommendation.content());
                });
    }

    @Test
    void getRecommendation_notFound_return200() {
        webTestClient.get()
                .uri("/api/v1/recommendation/{courseId}", UUID.randomUUID().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Recommendation.class)
                .hasSize(0);
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getRecommendation_invalidId_return400(String courseId) {
        webTestClient.get()
                .uri("/api/v1/recommendation/{courseId}", courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createRecommendation_success_return201() {
        webTestClient.post()
                .uri("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "author": "Phuoc Nguyen",
                            "rate": 5,
                            "content": "Content is here"
                        }
                        """.formatted(UUID.randomUUID().toString()))
                .exchange()
                .expectStatus().isCreated();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateRecommendationRequests")
    void createRecommendation_invalidRequest_return400(String description, String json) {
        webTestClient.post()
                .uri("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void updateRecommendation_notFound_return404() {
        webTestClient.put()
                .uri("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "recommendationId": "%s",
                            "author": "Phuoc Nguyen",
                            "rate": 10,
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
    @MethodSource("invalidUpdateRecommendationRequests")
    void updateRecommendation_invalidRequest_returns400(String description, String json) {
        webTestClient.put()
                .uri("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void deleteRecommendations_success_return204() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createRecommendation(courseId, UUID.randomUUID().toString(), "Author A", 10, "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.delete()
                .uri("/api/v1/recommendation/{courseId}", courseId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(repository.findByCourseId(courseId))
                .verifyComplete();
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteRecommendations_invalidId_returns400(String courseId) {
        webTestClient.delete()
                .uri("/api/v1/recommendation/{courseId}", courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void recommendationCreateEvent_fromBinder_createsCourse() throws JsonProcessingException {
        final var courseId = UUID.randomUUID();
        final var author = "Author A";
        final var rate = 10;
        final var content = "Content A";
        final var payload = objectMapper.writeValueAsBytes(new RecommendationCreateCommand(courseId, author, rate, content));

        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "recommendation.create"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId.toString()))
                                .expectNextMatches(recommendation ->
                                        recommendation.getCourseId().equals(courseId.toString()) &&
                                        recommendation.getAuthor().equals(author) &&
                                        recommendation.getRate() == rate &&
                                        recommendation.getContent().equals(content)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void recommendationUpdateEvent() throws JsonProcessingException {
        final var courseId = UUID.randomUUID().toString();
        final var recommendationId = UUID.randomUUID();
        final var author = "Author A";
        final var updatedAuthor = "Updated Author A";
        final var rate = 10;
        final var updatedRate = 1;
        final var content = "Content A";
        final var updatedContent = "Updated Content A";
        final var payload = objectMapper.writeValueAsBytes(new RecommendationUpdateCommand(recommendationId, updatedAuthor, updatedRate, updatedContent));

        StepVerifier.create(createRecommendation(courseId, recommendationId.toString(), author, rate, content))
                .expectNextCount(1)
                .verifyComplete();

        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "recommendation.update"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId))
                                .expectNextMatches(recommendation ->
                                        recommendation.getRecommendationId().equals(recommendationId.toString()) &&
                                        recommendation.getCourseId().equals(courseId) &&
                                        recommendation.getAuthor().equals(updatedAuthor) &&
                                        recommendation.getRate() == updatedRate &&
                                        recommendation.getContent().equals(updatedContent)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void recommendationDeleteEvent() throws JsonProcessingException {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createRecommendation(courseId, UUID.randomUUID().toString(), "Author A", 10, "Content A"))
                .expectNextCount(1)
                .verifyComplete();

        inputDestination.send(
                MessageBuilder.withPayload(objectMapper.writeValueAsBytes(courseId))
                        .setHeader("contentType", "application/json")
                        .build(),
                "recommendation.delete"
        );

        StepVerifier.create(repository.findByCourseId(courseId).count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void optimisticLockError() {
        final var courseId = UUID.randomUUID().toString();

        final var conflictSave = createRecommendation(courseId, UUID.randomUUID().toString(), "Author A", 10, "Content A")
                .flatMap(saved -> repository.findById(saved.getId())
                        .zipWith(repository.findById(saved.getId())))
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

    private Mono<RecommendationEntity> createRecommendation(String courseId, String recommendationId, String author, int rate, String content) {
        return repository.save(new RecommendationEntity(
                courseId,
                recommendationId,
                author,
                rate,
                content));
    }
}
