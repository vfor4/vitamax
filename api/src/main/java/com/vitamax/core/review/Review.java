package com.vitamax.core.review;

public record Review(String courseId, String reviewId, String author, String subject, String content,
                     String serviceAddress) {
}
