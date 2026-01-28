package com.vitamax.recommendation_service.recommendation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "recommendation")
@Setter
@Getter
public class RecommendationEntity {
    @Id
    @Size(min = 36, max = 36)
    private String id;

    @Indexed(unique = true)
    @Size(min = 36, max = 36)
    private String recommendationId;

    @Size(min = 36, max = 36)
    private String courseId;

    @Size(min = 1, max = 255)
    private String author;

    @Positive
    private int rate;

    private String content;

    @Version
    private Integer version;

    public RecommendationEntity(String courseId, String recommendationId, String author, int rate, String content) {
        this.courseId = courseId;
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }
}
