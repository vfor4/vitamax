package com.vitamax.course_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.api.core.course.dto.Course;
import com.vitamax.common_test.MongoIntegrationTest;
import com.vitamax.course_service.entity.CourseEntity;
import com.vitamax.course_service.repository.CourseRepository;
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
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"eureka.client.enabled=false"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CourseServiceApplicationTests extends MongoIntegrationTest {
    public static final String API_COURSE_ID = "/api/v1/course/{courseId}";
    public static final String API_COURSE = "/api/v1/course";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository repository;

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
        repository.deleteAll();
    }

    @Test
    void getCourse_success_return200() throws Exception {
        final var entity = createCourse().block();

        final var uri = get(API_COURSE_ID, entity.getCourseId());
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Course.class);
        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals(entity.getName(), result.name());
    }

    @Test
    void getCourse_notFound_return404() throws Exception {
        final var uri = get(API_COURSE_ID, UUID.randomUUID().toString());
        mockMvc.perform(uri).andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void getCourse_invalidId_return400(String courseId) throws Exception {
        final var uri = get(API_COURSE_ID, courseId);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_success_return201() throws Exception {
        final var uri = post(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Test Course"
                        }
                        """);
        mockMvc.perform(uri).andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateCourseRequests")
    void createCourse_invalidRequest_return400(String description, String json) throws Exception {
        final var uri = post(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
        assertEquals(0, repository.count().block());
    }

    @Test
    void updateCourse_success_return200() throws Exception {
        final var entity = createCourse().block();

        final var uri = put(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "name": "Test Course Updated"
                        }
                        """.formatted(entity.getCourseId()));
        final var response = mockMvc.perform(uri).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Course.class);
        assertEquals(entity.getCourseId(), result.courseId());
        assertEquals("Test Course Updated", result.name());
    }

    @Test
    void updateCourse_notFound_return404() throws Exception {
        final var uri = put(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "courseId": "%s",
                            "name": "Test Course Updated"
                        }
                        """.formatted(UUID.randomUUID().toString()));
        mockMvc.perform(uri).andExpect(status().isNotFound());
        assertEquals(0, repository.count().block());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateCourseRequests")
    void updateCourse_invalidRequest_returns400(String description, String json) throws Exception {
        mockMvc.perform(put(API_COURSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count().block());
    }

    @Test
    void deleteCourse_success_return204() throws Exception {
        final var entity = createCourse().block();

        final var uri = delete(API_COURSE_ID, entity.getCourseId());
        mockMvc.perform(uri).andExpect(status().isNoContent()).andExpect(content().string(""));
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteCourse_invalidId_returns400(String courseId) throws Exception {
        mockMvc.perform(delete(API_COURSE_ID, courseId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optimisticLockError() {
        final var entity = createCourse().block();
        // Store the saved entity in two separate entity objects
        final var entity1 = repository.findById(entity.getId()).block();
        final var entity2 = repository.findById(entity.getId()).block();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1).block();

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version
        // number, that is, an Optimistic Lock Error
        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setName("n2");
            repository.save(entity2).block();
        });

        // Get the updated entity from the database and verify its new state
        final var updatedEntity = repository.findById(entity.getId()).block();
        assertEquals("n1", updatedEntity.getName());
    }


    private Mono<CourseEntity> createCourse() {
        return repository.save(new CourseEntity(UUID.randomUUID().toString(), "Test Course Name"));
    }
}
