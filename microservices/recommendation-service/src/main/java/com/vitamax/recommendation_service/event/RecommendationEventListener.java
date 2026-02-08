package com.vitamax.recommendation_service.event;

import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.vitamax.api.event.EventConstants.RECOMMENDATION_QUEUE_NAME;

@Service
@RabbitListener(queues = RECOMMENDATION_QUEUE_NAME)
@Slf4j
@RequiredArgsConstructor
public class RecommendationEventListener {

    private final RecommendationService recommendationService;

    @RabbitHandler
    public void onCreate(RecommendationCreateCommand command) {
        recommendationService.createRecommendation(command).subscribe();
    }
}
