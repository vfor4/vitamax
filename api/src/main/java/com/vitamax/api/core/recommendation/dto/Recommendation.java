package com.vitamax.api.core.recommendation.dto;

public record Recommendation(String courseId, String recommendationId, String author, int rate, String content,
                             String serviceAddress) {
}
