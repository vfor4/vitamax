#!/usr/bin/env bash

cd spring-cloud

spring init \
--boot-version=3.0.4 \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=eureka-server \
--package-name=se.magnus.springcloud \
--groupId=se.magnus.springcloud \
--dependencies=cloud-eureka-server \
--version=1.0.0-SNAPSHOT \
eureka-server

cd ..