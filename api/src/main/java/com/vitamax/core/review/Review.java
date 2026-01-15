package com.vitamax.core.review;

public record Review(int courseId, int reviewId, String author, String subject, String content, String serviceAddress) {
}
