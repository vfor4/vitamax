package com.vitamax.core.recommendation;

public record Recommendation(String courseId, String recommendationId, String author, int rate, String content,
                             String serviceAddress) {
}
