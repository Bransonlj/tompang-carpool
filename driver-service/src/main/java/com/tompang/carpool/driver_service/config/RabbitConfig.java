package com.tompang.carpool.driver_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String DRIVER_VERIFICATION_JOB_QUEUE = "driverVerificationJobQueue";
    public static final String DRIVER_VERIFICATION_RESULT_QUEUE = "driverVerificationResultQueue";
    @Bean
    public Queue driverVerificationJobQueue() {
        return new Queue(DRIVER_VERIFICATION_JOB_QUEUE);
    }

    @Bean
    public Queue driverVerificationResultQueue() {
        return new Queue(DRIVER_VERIFICATION_RESULT_QUEUE);
    }


    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
