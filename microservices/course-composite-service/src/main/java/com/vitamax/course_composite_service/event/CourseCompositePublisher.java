package com.vitamax.course_composite_service.event;

import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.event.EventConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCompositePublisher {

    private final StreamBridge streamBridge;

    public void publishCourse(CourseCreateCommand command) {
        streamBridge.send(EventConstants.COURSE_BINDING_OUT, command);
    }

    public void publishReview(ReviewCreateCommand command) {
        streamBridge.send(EventConstants.REVIEW_BINDING_OUT, command);
    }

    public void publishRecommendation(RecommendationCreateCommand command) {
        streamBridge.send(EventConstants.RECOMMENDATION_BINDING_OUT, command);
    }
}
