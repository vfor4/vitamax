package com.vitamax.course_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitamax.core.course.Course;
import com.vitamax.course_service.course.CourseRepository;
import com.vitamax.course_service.course.entities.CourseEntity;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CourseServiceApplicationTests extends AbstractIntegrationTest {
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
        final var entity = createCourse();

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
    void createCourse_success_return200() throws Exception {
        final var uri = post(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Test Course"
                        }
                        """);
        final var response = mockMvc.perform(uri).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(response, Course.class);
        assertEquals("Test Course", result.name());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateCourseRequests")
    void createCourse_invalidRequest_return400(String description, String json) throws Exception {
        final var uri = post(API_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(uri).andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void updateCourse_success_return200() throws Exception {
        final var entity = createCourse();

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
        assertEquals(0, repository.count());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateCourseRequests")
    void updateCourse_invalidRequest_returns400(String description, String json) throws Exception {
        mockMvc.perform(put(API_COURSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void deleteCourse_success_return204() throws Exception {
        final var entity = createCourse();

        final var uri = delete(API_COURSE_ID, entity.getCourseId());
        mockMvc.perform(uri).andExpect(status().isNoContent()).andExpect(content().string(""));
    }

    @ParameterizedTest(name = "invalid courseId = {0}")
    @ValueSource(strings = {"   ", "abc", "-1", "0", "550e8400-e29b-41d4-a716-44665544000Z"})
    void deleteCourse_invalidId_returns400(String courseId) throws Exception {
        mockMvc.perform(delete(API_COURSE_ID, courseId))
                .andExpect(status().isBadRequest());
    }

    private CourseEntity createCourse() {
        final var entity = CourseEntity.builder()
                .courseId(UUID.randomUUID().toString())
                .name("Test Course Name")
                .build();
        return repository.save(entity);
    }
}
