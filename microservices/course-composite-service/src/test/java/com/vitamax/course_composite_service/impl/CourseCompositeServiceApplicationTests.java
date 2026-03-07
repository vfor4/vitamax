package com.vitamax.course_composite_service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@EnableTestBinder
class CourseCompositeServiceApplicationTests {
    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void deleteCourseComposite() throws Exception {
        final var courseId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/aggregate/course/{courseId}", courseId)
                .exchange()
                .expectStatus().isNoContent();

        assertEquals(courseId, assertPayloadAndDeserialize(outputDestination.receive(5_000, "course.delete"), UUID.class));
        assertEquals(courseId, assertPayloadAndDeserialize(outputDestination.receive(5_000, "recommendation.delete"), UUID.class));
        assertEquals(courseId, assertPayloadAndDeserialize(outputDestination.receive(5_000, "review.delete"), UUID.class));
    }

    @Test
    void createCourseComposite() throws Exception {
        webTestClient.post()
                .uri("/api/v1/aggregate/course")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "course": {
                            "name": "Spring Cloud Mastery"
                          },
                          "recommendations": [
                            {
                              "author": "Alice",
                              "rate": 5,
                              "content": "Great course!"
                            },
                            {
                              "author": "Bob",
                              "rate": 4,
                              "content": "Very practical"
                            }
                          ],
                          "reviews": [
                            {
                              "author": "Carol",
                              "subject": "Solid",
                              "content": "Clear explanations"
                            },
                            {
                              "author": "Dave",
                              "subject": "Useful",
                              "content": "Hands-on and concise"
                            }
                          ]
                        }
                        """)
                .exchange()
                .expectStatus().isCreated();

        final var createdCourse =
                assertPayloadAndDeserialize(outputDestination.receive(5_000, "course.create"), CourseCreateCommand.class);
        assertEquals("Spring Cloud Mastery", createdCourse.name());

        final var createdRecommendations = new ArrayList<RecommendationCreateCommand>();
        createdRecommendations.add(assertPayloadAndDeserialize(
                outputDestination.receive(5_000, "recommendation.create"), RecommendationCreateCommand.class));
        createdRecommendations.add(assertPayloadAndDeserialize(
                outputDestination.receive(5_000, "recommendation.create"), RecommendationCreateCommand.class));

        final var createdReviews = new ArrayList<ReviewCreateCommand>();
        createdReviews.add(assertPayloadAndDeserialize(
                outputDestination.receive(5_000, "review.create"), ReviewCreateCommand.class));
        createdReviews.add(assertPayloadAndDeserialize(
                outputDestination.receive(5_000, "review.create"), ReviewCreateCommand.class));

        assertFalse(createdRecommendations.isEmpty());
        assertFalse(createdReviews.isEmpty());

        createdRecommendations.forEach(rec -> assertEquals(createdCourse.courseId(), rec.courseId()));
        createdReviews.forEach(review -> assertEquals(createdCourse.courseId(), review.courseId()));
    }

    private <T> T assertPayloadAndDeserialize(final Message<byte[]> message, final Class<T> type) throws Exception {
        assertNotNull(message, "Expected a message but none was published");
        final var rawPayload = new String(message.getPayload(), StandardCharsets.UTF_8);
        return objectMapper.readValue(rawPayload, type);
    }
}
