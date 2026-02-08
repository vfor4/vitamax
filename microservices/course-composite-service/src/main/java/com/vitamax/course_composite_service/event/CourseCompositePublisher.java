package com.vitamax.course_composite_service.event;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CourseCompositePublisher {

    @Bean
    public Queue course() {
        return new Queue("course");
    }

//    @Bean
//    public Queue review() {
//        return new Queue("review");
//    }


    @Bean
    public ConnectionFactory connectionFactory() {
        final var factory = new CachingConnectionFactory();
        factory.setUsername("user");
        factory.setPassword("password");
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, Jackson2JsonMessageConverter converter) {
        final var template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2Converter() {
        return new Jackson2JsonMessageConverter();
    }
}
