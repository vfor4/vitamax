package com.vitamax.review_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.core.review.Review;
import com.vitamax.review_service.review.ReviewEntity;
import com.vitamax.review_service.review.ReviewRepository;
import com.vitamax.test.PostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
//        "spring.jpa.show-sql=true",
//        "spring.jpa.properties.hibernate.format_sql=true",
//        "logging.level.org.hibernate.SQL=DEBUG",
//        "logging.level.org.hibernate.orm.jdbc.bind=TRACE"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReviewServiceApplicationTests extends PostgresIntegrationTest {
    private static final String COURSE_ID = UUID.randomUUID().toString();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReviewRepository repository;

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
                                  "courseId": "COURSE_1",
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
                                  "courseId": "COURSE_1",
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
                                  "courseId": "COURSE_1",
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
                                  "courseId": "COURSE_1",
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
                        "courseId is null",
                        """
                                {
                                  "courseId": null,
                                  "reviewId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "subject": "Good",
                                  "content": "Nice course"
                                }
                                """
                ),
                Arguments.of(
                        "reviewId is null",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
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
                                  "courseId": "11111111-1111-1111-1111-111111111111",
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
                                  "courseId": "11111111-1111-1111-1111-111111111111",
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
                                  "courseId": "11111111-1111-1111-1111-111111111111",
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
        repository.deleteAll();
    }

    @Test
    void getReview_success_return200() throws Exception {
        final var entity = createReview();

        final var uri = get("/api/v1/review/{courseId}", entity.getCourseId());
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Review[].class);

        assertEquals(1, result.length);
        assertResult(entity, result[0]);
    }

    @Test
    void getReview_notFound_return404() throws Exception {
        final var uri = get("/api/v1/review/{courseId}", UUID.randomUUID().toString());
        mockMvc.perform(uri).andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getReview_invalidId_return400(String courseId) throws Exception {
        final var uri = get("/api/v1/review/{courseId}", courseId);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
    }

    @Test
    void createReview_success_return200() throws Exception {
        final var uri = post("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "author": "Phuoc Nguyen",
                            "subject": "Subject is here",
                            "content": "Content is here"
                        }
                        """.formatted(COURSE_ID));
        mockMvc.perform(uri).andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateReviewRequests")
    void createReview_invalidRequest_return400(String description, String json) throws Exception {
        final var uri = post("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void updateReview_success_return200() throws Exception {
        final var entity = createReview();

        final var uri = put("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "reviewId": "%s",
                            "author": "%s",
                            "subject": "Test Subject Updated",
                            "content": "Test Content Updated"
                        }
                        """.formatted(entity.getCourseId(), entity.getReviewId(), entity.getAuthor()));
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Review.class);

        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getReviewId(), result.reviewId());
        assertEquals(entity.getAuthor(), result.author());
        assertNotEquals(entity.getSubject(), result.subject());
        assertEquals("Test Subject Updated", result.subject());
        assertNotEquals(entity.getContent(), result.content());
        assertEquals("Test Content Updated", result.content());
    }

    @Test
    void updateReview_notFound_return404() throws Exception {
        final var uri = put("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "reviewId": "%s",
                            "author": "Phuoc Nguyen",
                            "subject": "Subject is here",
                            "content": "Content is here"
                        }
                        """.formatted(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        mockMvc.perform(uri).andExpect(status().isNotFound());
        assertEquals(0, repository.count());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateReviewRequests")
    void updateReview_invalidRequest_returns400(String description, String json) throws Exception {
        mockMvc.perform(put("/api/v1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void deleteReviews_success_return204() throws Exception {
        final var entity = createReview();

        final var uri = delete("/api/v1/review/{courseId}", entity.getReviewId());
        mockMvc.perform(uri).andExpect(status().isNoContent()).andExpect(content().string(""));
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteReviews_invalidId_returns400(String courseId) throws Exception {
        mockMvc.perform(delete("/api/v1/review/{courseId}", courseId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optimisticLockError() {
        final var entity = createReview();
        // Store the saved entity in two separate entity objects
        final var entity1 = repository.findById(entity.getReviewId()).get();
        final var entity2 = repository.findById(entity.getReviewId()).get();

        // Update the entity using the first entity object
        entity1.setAuthor("n1");
        repository.save(entity1);

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version
        // number, that is, an Optimistic Lock Error
        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setAuthor("n2");
            repository.save(entity2);
        });

        // Get the updated entity from the database and verify its new state
        final var updatedEntity = repository.findById(entity.getReviewId()).get();
        assertEquals("n1", updatedEntity.getAuthor());
    }

    private ReviewEntity createReview() {
        return repository.save(new ReviewEntity(
                COURSE_ID,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        ));
    }

    private void assertResult(final ReviewEntity entity, final Review result) {
        assertEquals(entity.getReviewId(), result.reviewId());
        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getAuthor(), result.author());
        assertEquals(entity.getSubject(), result.subject());
        assertEquals(entity.getContent(), result.content());
    }
}
