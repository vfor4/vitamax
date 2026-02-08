package com.vitamax.course_service.event;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.vitamax.api.event.EventConstants.COURSE_QUEUE_NAME;

@Service
@RabbitListener(queues = COURSE_QUEUE_NAME)
@Slf4j
@RequiredArgsConstructor
public class CourseEventListener {

    private final CourseService courseService;

    @RabbitHandler
    public void onCreate(CourseCreateCommand command) {
        courseService.createCourse(command).subscribe();
    }
}
