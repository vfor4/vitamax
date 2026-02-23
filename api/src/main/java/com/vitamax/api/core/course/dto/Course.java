package com.vitamax.api.core.course.dto;

import java.io.Serializable;

public record Course(String courseId, String name, String serviceAddress) implements Serializable {
}
