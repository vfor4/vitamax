#!/usr/bin/env bash

cd spring-cloud

spring init \
--force \
--boot-version=3.1.0 \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=config-server \
--package-name=se.magnus.springcloud \
--groupId=se.magnus.springcloud \
--dependencies=cloud-config-server,security \
--version=1.0.0-SNAPSHOT \
config-server

cd ..