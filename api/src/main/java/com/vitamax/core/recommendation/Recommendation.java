package com.vitamax.core.recommendation;

public record Recommendation(int courseId, int recommendationId, String author, int rate, String content, String serviceAddress) {
}
