package com.vitamax.core.review;

public record Review(int productId, int revId, String author, String subject, String content, String serviceAddress) {
}
