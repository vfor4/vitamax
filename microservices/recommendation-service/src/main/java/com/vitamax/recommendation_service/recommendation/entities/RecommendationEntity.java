package com.vitamax.recommendation_service.recommendation.entities;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "recommendation")
@AllArgsConstructor
public class RecommendationEntity {
    @Id
    private String id;
    private int courseId;
    private int recommendationId;
    private String author;
    private int rate;
    private String content;
}
