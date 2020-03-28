package com.ruider.utils.ActiveMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.Topic;

@Component
@EnableScheduling
public class ActiveMQComsumer {

    Logger logger = LoggerFactory.getLogger(ActiveMQComsumer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    /*
    @JmsListener(destination = "queueTest")
    public void receiveByQueue (String message) {
        System.out.println("以Queue收到的消息为:" + message);
        logger.info("【receiveByQueue succeed】以 Queue 开始接受来自AvtiveMQ的消息");
    }

    @JmsListener(destination = "topicTest")
    public void receiveByTopic1 (String message) {
        System.out.println("receiveByTopic1以Topic收到消息:" + message);
        logger.info("【receiveByTopic1 succeed】以 Topic 成功接受来自AvtiveMQ的消息");
    }

    @JmsListener(destination = "topicTest")
    public void receiveByTopic2 (String message) {
        System.out.println("receiveByTopic1以Topic收到消息:" + message);
        logger.info("【receiveByTopic2 succeed】以 Topic 成功接受来自AvtiveMQ的消息");
    }
    */
}
