package com.ruider.config.ActiveMQConfig;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.Topic;

@Component
public class ActiveMQConfig {
    @Bean
    public Queue queue () {
        return new ActiveMQQueue("queueTest");
    }

    @Bean
    public Topic topic () {
        return new ActiveMQTopic("topicTest");
    }
}
