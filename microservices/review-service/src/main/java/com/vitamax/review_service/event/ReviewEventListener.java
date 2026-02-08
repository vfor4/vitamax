package com.vitamax.review_service.event;

import com.vitamax.api.core.review.ReviewService;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.vitamax.api.event.EventConstants.REVIEW_QUEUE_NAME;

@Service
@RabbitListener(queues = REVIEW_QUEUE_NAME)
@Slf4j
@RequiredArgsConstructor
public class ReviewEventListener {

    private final ReviewService reviewService;

    @RabbitHandler
    public void onCreate(ReviewCreateCommand command) {
        reviewService.createReview(command).subscribe();
    }
}
