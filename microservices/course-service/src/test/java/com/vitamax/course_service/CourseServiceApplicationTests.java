package com.vitamax.course_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.course_service.entity.CourseEntity;
import com.vitamax.course_service.repository.CourseRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"eureka.client.enabled=false"})
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureWebTestClient
@EnableTestBinder
class CourseServiceApplicationTests {
    private final String API_COURSE = "/api/v1/course";
    private final String API_COURSE_ID = API_COURSE + "/{courseId}";

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CourseRepository repository;

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> invalidCreateCourseRequests() {
        return Stream.of(
                Arguments.of("missing name", "{}"),
                Arguments.of("empty name", """
                        { "name": "" }
                        """),
                Arguments.of("blank name", """
                        { "name": "   " }
                        """),
                Arguments.of("name too long", """
                        { "name": "%s" }
                        """.formatted("A".repeat(256))),
                Arguments.of("invalid json", """
                        { "name": "Test Course"
                        """)
        );
    }

    static Stream<Arguments> invalidUpdateCourseRequests() {
        return Stream.of(
                Arguments.of("missing courseId", """
                        { "name": "Test Course Updated" }
                        """),
                Arguments.of("missing name", """
                        { "courseId": 1 }
                        """),
                Arguments.of("empty name", """
                        { "courseId": 1, "name": "" }
                        """),
                Arguments.of("courseId not number", """
                        { "courseId": "abc", "name": "Test Course Updated" }
                        """),
                Arguments.of("invalid json", """
                        { "courseId": 1, "name": "Test Course Updated"
                        """)
        );
    }

    @BeforeEach
    void beforeEach() {
        StepVerifier.create(repository.deleteAll())
                .verifyComplete();
    }

    @Test
    void getCourse_success_return200() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createCourse(courseId, "Test Course Name"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.get()
                .uri(API_COURSE_ID, courseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Course.class)
                .consumeWith(response -> {
                    final var result = response.getResponseBody();
                    assertNotNull(result);
                    assertEquals(courseId, result.courseId());
                    assertEquals("Test Course Name", result.name());
                });
    }

    @Test
    void getCourse_notFound_return404() {
        webTestClient.get()
                .uri(API_COURSE_ID, UUID.randomUUID().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getCourse_invalidId_return400(String courseId) {
        webTestClient.get()
                .uri(API_COURSE_ID, courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createCourse_success_return201() {
        webTestClient.post()
                .uri(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId" : "%s",
                            "name": "Test Course"
                        }
                        """.formatted(UUID.randomUUID().toString()))
                .exchange()
                .expectStatus().isCreated();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateCourseRequests")
    void createCourse_invalidRequest_return400(String description, String json) {
        webTestClient.post()
                .uri(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void updateCourse_success_return204() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createCourse(courseId, "Test Course Name"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.put()
                .uri(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "name": "Test Course Updated"
                        }
                        """.formatted(courseId))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(repository.findByCourseId(courseId))
                .assertNext(updated -> assertEquals("Test Course Updated", updated.getName()))
                .verifyComplete();
    }

    @Test
    void updateCourse_notFound_return404() {
        webTestClient.put()
                .uri(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "courseId": "%s",
                            "name": "Test Course Updated"
                        }
                        """.formatted(UUID.randomUUID().toString()))
                .exchange()
                .expectStatus().isNotFound();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateCourseRequests")
    void updateCourse_invalidRequest_returns400(String description, String json) {
        webTestClient.put()
                .uri(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        StepVerifier.create(repository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void deleteCourse_success_return204() {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createCourse(courseId, "Test Course Name"))
                .expectNextCount(1)
                .verifyComplete();

        webTestClient.delete()
                .uri(API_COURSE_ID, courseId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(repository.findByCourseId(courseId))
                .verifyComplete();
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteCourse_invalidId_returns400(String courseId) {
        webTestClient.delete()
                .uri(API_COURSE_ID, courseId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void courseCreateEvent_fromBinder_createsCourse() throws JsonProcessingException {
        final var courseId = UUID.randomUUID();
        final var courseName = "Test Course Name";
        final var payload = objectMapper.writeValueAsBytes(new CourseCreateCommand(courseId, courseName));

        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "course.create"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId.toString()))
                                .expectNextMatches(course ->
                                        course.getCourseId().equals(courseId.toString()) &&
                                                course.getName().equals(courseName)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void courseUpdateEvent_fromBinder_updatesCourse() throws JsonProcessingException {
        final var courseId = UUID.randomUUID();
        final var name = "Original Course Name";
        final var updatedName = "Updated Course Name";
        final var payload = objectMapper.writeValueAsBytes(new CourseUpdateCommand(courseId, updatedName));

        StepVerifier.create(createCourse(courseId.toString(), name))
                .expectNextCount(1)
                .verifyComplete();


        inputDestination.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "course.update"
        );

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        StepVerifier.create(repository.findByCourseId(courseId.toString()))
                                .expectNextMatches(course ->
                                        course.getCourseId().equals(courseId.toString()) &&
                                                course.getName().equals(updatedName)
                                )
                                .verifyComplete()
                );
    }

    @Test
    void courseDeleteEvent_fromBinder_deletesCourse() throws JsonProcessingException {
        final var courseId = UUID.randomUUID().toString();
        StepVerifier.create(createCourse(courseId, "Test Course Name"))
                .expectNextCount(1)
                .verifyComplete();

        inputDestination.send(
                MessageBuilder.withPayload(objectMapper.writeValueAsBytes(courseId))
                        .setHeader("contentType", MediaType.APPLICATION_JSON_VALUE)
                        .build(),
                "course.delete"
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

        final var conflictSave = createCourse(courseId, "Test Course Name")
                .flatMap(saved -> repository.findById(saved.getId())
                        .zipWith(repository.findById(saved.getId())))
                .flatMap(tuple -> {
                    final var entity1 = tuple.getT1();
                    final var entity2 = tuple.getT2();

                    entity1.setName("n1");
                    return repository.save(entity1)
                            .thenReturn(entity2);
                })
                .flatMap(entity2 -> {
                    entity2.setName("n2");
                    return repository.save(entity2);
                });

        StepVerifier.create(conflictSave)
                .expectError(OptimisticLockingFailureException.class)
                .verify();

        StepVerifier.create(repository.findByCourseId(courseId))
                .assertNext(updatedEntity -> assertEquals("n1", updatedEntity.getName()))
                .verifyComplete();
    }

    private Mono<CourseEntity> createCourse(final String courseId, final String name) {
        return repository.save(new CourseEntity(courseId, name));
    }
}
