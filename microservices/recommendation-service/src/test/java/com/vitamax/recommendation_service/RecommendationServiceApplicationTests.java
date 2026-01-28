package com.vitamax.recommendation_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.recommendation_service.recommendation.RecommendationEntity;
import com.vitamax.recommendation_service.recommendation.RecommendationRepository;
import com.vitamax.test.MongoIntegrationTest;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RecommendationServiceApplicationTests extends MongoIntegrationTest {
    private static final String COURSE_ID = UUID.randomUUID().toString();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationRepository repository;

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
                        "rate is zero",
                        """
                                {
                                  "courseId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 0,
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
                        "null courseId",
                        """
                                {
                                  "courseId": null,
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "null recommendationId",
                        """
                                {
                                  "courseId": "22222222-2222-2222-2222-222222222222",
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
                                  "courseId": "22222222-2222-2222-2222-222222222222",
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
                                  "courseId": "22222222-2222-2222-2222-222222222222",
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "rate": 5,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "rate is zero",
                        """
                                {
                                  "courseId": "22222222-2222-2222-2222-222222222222",
                                  "recommendationId": "11111111-1111-1111-1111-111111111111",
                                  "author": "John",
                                  "rate": 0,
                                  "content": "Good course"
                                }
                                """
                ),
                Arguments.of(
                        "rate is negative",
                        """
                                {
                                  "courseId": "22222222-2222-2222-2222-222222222222",
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
                                  "courseId": "22222222-2222-2222-2222-222222222222",
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
                                  "courseId": "22222222-2222-2222-2222-222222222222",
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
                                  "courseId": "not-a-uuid",
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
        repository.deleteAll();
    }

    @Test
    void getRecommendation_success_return200() throws Exception {
        final var entity = createRecommendation();

        final var uri = get("/api/v1/recommendation/{courseId}", entity.getCourseId());
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Recommendation[].class);

        assertEquals(1, result.length);
        assertResult(entity, result[0]);
    }

    @Test
    void getRecommendation_notFound_return404() throws Exception {
        final var uri = get("/api/v1/recommendation/{courseId}", UUID.randomUUID().toString());
        mockMvc.perform(uri).andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getRecommendation_invalidId_return400(String courseId) throws Exception {
        final var uri = get("/api/v1/recommendation/{courseId}", courseId);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
    }

    @Test
    void createRecommendation_success_return200() throws Exception {
        final var uri = post("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "author": "Phuoc Nguyen",
                            "rate": 5,
                            "content": "Content is here"
                        }
                        """.formatted(COURSE_ID));
        mockMvc.perform(uri).andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateRecommendationRequests")
    void createRecommendation_invalidRequest_return400(String description, String json) throws Exception {
        final var uri = post("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void updateRecommendation_success_return200() throws Exception {
        final var entity = createRecommendation();

        final var uri = put("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "recommendationId": "%s",
                            "author": "%s",
                            "rate": 1,
                            "content": "Test Recommendation Updated"
                        }
                        """.formatted(entity.getCourseId(), entity.getRecommendationId(), entity.getAuthor()));
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Recommendation.class);

        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getRecommendationId(), result.recommendationId());
        assertEquals(entity.getAuthor(), result.author());
        assertNotEquals(entity.getRate(), result.rate());
        assertEquals(1, result.rate());
        assertNotEquals(entity.getContent(), result.content());
        assertEquals("Test Recommendation Updated", result.content());
    }

    @Test
    void updateRecommendation_notFound_return404() throws Exception {
        final var uri = put("/api/v1/recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "recommendationId": "%s",
                            "author": "Phuoc Nguyen",
                            "rate": 10,
                            "content": "Content is here"
                        }
                        """.formatted(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        mockMvc.perform(uri).andExpect(status().isNotFound());
        assertEquals(0, repository.count());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateRecommendationRequests")
    void updateRecommendation_invalidRequest_returns400(String description, String json) throws Exception {
        mockMvc.perform(put("/api/v1/recommendation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void deleteRecommendations_success_return204() throws Exception {
        final var entity = createRecommendation();

        final var uri = delete("/api/v1/recommendation/{courseId}", entity.getRecommendationId());
        mockMvc.perform(uri).andExpect(status().isNoContent()).andExpect(content().string(""));
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteRecommendations_invalidId_returns400(String courseId) throws Exception {
        mockMvc.perform(delete("/api/v1/recommendation/{courseId}", courseId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optimisticLockError() {
        final var entity = createRecommendation();
        // Store the saved entity in two separate entity objects
        final var entity1 = repository.findById(entity.getId()).get();
        final var entity2 = repository.findById(entity.getId()).get();

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
        final var updatedEntity = repository.findById(entity.getId()).get();
        assertEquals("n1", updatedEntity.getAuthor());
    }

    private RecommendationEntity createRecommendation() {
        return repository.save(new RecommendationEntity(
                COURSE_ID,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                10,
                UUID.randomUUID().toString()));
    }

    private void assertResult(final RecommendationEntity entity, final Recommendation result) {
        assertEquals(entity.getRecommendationId(), result.recommendationId());
        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getAuthor(), result.author());
        assertEquals(entity.getRate(), result.rate());
        assertEquals(entity.getContent(), result.content());
    }
}

