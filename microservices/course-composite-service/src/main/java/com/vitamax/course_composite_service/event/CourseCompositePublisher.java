package com.vitamax.course_composite_service.event;

import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.api.event.EventConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseCompositePublisher {

    private final StreamBridge streamBridge;

    public void publishCourseCreate(CourseCreateCommand command) {
        streamBridge.send(EventConstants.COURSE_CREATE_OUT_0, command);
    }

    public void publishCourseUpdate(CourseUpdateCommand command) {
        streamBridge.send(EventConstants.COURSE_UPDATE_OUT_0, command);
    }

    public void publishCourseDelete(UUID id) {
        streamBridge.send(EventConstants.COURSE_DELETE_OUT_0, id);
    }

    public void publishReviewCreate(ReviewCreateCommand command) {
        streamBridge.send(EventConstants.REVIEW_CREATE_OUT_0, command);
    }

    public void publishReviewUpdate(ReviewUpdateCommand command) {
        streamBridge.send(EventConstants.REVIEW_UPDATE_OUT_0, command);
    }

    public void publishReviewDelete(UUID id) {
        streamBridge.send(EventConstants.REVIEW_DELETE_OUT_0, id);
    }

    public void publishRecommendationCreate(RecommendationCreateCommand command) {
        streamBridge.send(EventConstants.RECOMMENDATION_CREATE_OUT_0, command);
    }

    public void publishRecommendationUpdate(RecommendationUpdateCommand command) {
        streamBridge.send(EventConstants.RECOMMENDATION_UPDATE_OUT_0, command);
    }

    public void publishRecommendationIdDelete(UUID id) {
        streamBridge.send(EventConstants.RECOMMENDATION_DELETE_OUT_0, id);
    }
}
