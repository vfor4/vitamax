#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=3.5.9 \
--type=maven-project \
--java-version=21 \
--packaging=jar \
--name=course-service \
--package-name=com.vitamax.course-service \
--groupId=com.vitamax\
--dependencies=web \
--version=1.0.0-SNAPSHOT \
course-service


spring init \
--boot-version=3.5.9 \
--type=maven-project \
--java-version=21 \
--packaging=jar \
--name=recommendation-service \
--package-name=com.vitamax.recommendation-service \
--groupId=com.vitamax\
--dependencies=web \
--version=1.0.0-SNAPSHOT \
recommendation-service

spring init \
--boot-version=3.5.9 \
--type=maven-project \
--java-version=21 \
--packaging=jar \
--name=review-service \
--package-name=com.vitamax.review-service \
--groupId=com.vitamax \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
review-service

spring init \
--boot-version=3.5.9 \
--type=maven-project \
--java-version=21 \
--packaging=jar \
--name=course-composite-service \
--package-name=com.vitamax.course-composite-service \
--groupId=com.vitamax \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
course-composite-service
cd ..

spring init \
--type=maven-build \
--name=parent \
--groupId=com.vitamax \
--version=1.0.0-SNAPSHOT
