package com.su.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author suweitao
 */
@Configuration
public class RabbitMQConfig {

    public static final String TOPIC_EXCHANGE_NAME = "employee_exchange";
    public static final String TOPIC_QUEUE_NAME_ES = "employee_queue_es";
    public static final String TOPIC_ROUTING_KEY_ES = "employee_routing_key_es"; // 使用通配符进行匹配

    @Bean
    public Exchange topicExchange() {
        return ExchangeBuilder.directExchange(TOPIC_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue topicQueue() {
        return new Queue(TOPIC_QUEUE_NAME_ES, true);
    }

    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TOPIC_ROUTING_KEY_ES).noargs();
    }

}
