package com.vitamax.recommendation_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.recommendation_service.recommendation.RecommendationRepository;
import com.vitamax.recommendation_service.recommendation.entities.RecommendationEntity;
import com.vitamax.test.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RecommendationServiceApplicationTests extends AbstractIntegrationTest {
    private static final String COURSE_ID = UUID.randomUUID().toString();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationRepository repository;

    static Stream<Arguments> invalidCreateRecommendationRequests() {
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
                        { "name": "Test Recommendation"
                        """)
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
        final var response = mockMvc.perform(uri).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Recommendation.class);

        assertEquals(COURSE_ID, result.courseId());
        assertEquals("Phuoc Nguyen", result.author());
        assertEquals(5, result.rate());
        assertEquals("Content is here", result.content());
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
    void deleteRecommendation_success_return204() throws Exception {
        final var entity = createRecommendation();

        final var uri = delete("/api/v1/recommendation/{courseId}", entity.getRecommendationId());
        mockMvc.perform(uri).andExpect(status().isNoContent()).andExpect(content().string(""));
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteRecommendation_invalidId_returns400(String courseId) throws Exception {
        mockMvc.perform(delete("/api/v1/recommendation/{courseId}", courseId))
                .andExpect(status().isBadRequest());
    }

    private RecommendationEntity createRecommendation() {
        final var entity = RecommendationEntity.builder()
                .courseId(COURSE_ID)
                .recommendationId(UUID.randomUUID().toString())
                .author(UUID.randomUUID().toString())
                .rate(10)
                .content(UUID.randomUUID().toString())
                .build();
        return repository.save(entity);
    }

    private void assertResult(final RecommendationEntity entity, final Recommendation result) {
        assertEquals(entity.getRecommendationId(), result.recommendationId());
        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getAuthor(), result.author());
        assertEquals(entity.getRate(), result.rate());
        assertEquals(entity.getContent(), result.content());
    }
}

