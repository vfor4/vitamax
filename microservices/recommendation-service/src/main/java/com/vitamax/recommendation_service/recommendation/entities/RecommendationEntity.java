package com.vitamax.recommendation_service.recommendation.entities;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "recommendation")
@Builder
@Getter
public class RecommendationEntity {
    @Id
    private String id;
    private String courseId;
    private String recommendationId;
    private String author;
    private int rate;
    private String content;
}
