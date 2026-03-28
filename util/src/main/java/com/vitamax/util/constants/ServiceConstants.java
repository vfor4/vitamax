package com.vitamax.util.constants;

public class ServiceConstants {
    public static final String COURSE_HOST = "http://course-service";
    public static final String RECOMMENDATION_HOST = "http://recommendation-service";
    public static final String REVIEW_HOST = "http://review-service";
    public static final String COURSE_COMPOSITE_URL = "/api/v1/aggregate/course";
    public static final String COURSE_API_V1_URL = COURSE_HOST + "/api/v1/course/{courseId}";
    public static final String RECOMMENDATION_API_V1_URL =  RECOMMENDATION_HOST + "/api/v1/recommendation/{courseId}";
    public static final String REVIEW_API_V1_URL =  REVIEW_HOST + "/api/v1/review/{courseId}";
}
