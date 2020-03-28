package com.ruider.utils.ActiveMQ;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

@Component
@EnableScheduling //快速开启任务调度
public class ActiveMQProducer {

    Logger logger = LoggerFactory.getLogger(ActiveMQProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;


    public void sendByQueue () {
        System.out.println("ActiveMQ以 Queue 准备接受用户消息");
        logger.info("【sendByQueue start】ActiveMQ以 Queue 准备接受用户消息");
        jmsTemplate.convertAndSend(this.queue , "queue每三秒执行一次");
        logger.info("【sendByQueue success】ActiveMQ以 Queue 接受用户消息成功");
    }

    //@Scheduled(fixedDelay = 5000)
    public void sendByQueue (Destination destination , String message) {
        System.out.println("ActiveMQ以 Queue 准备接受用户消息");
        logger.info("【sendByQueue start,destination is + " + destination + " ,message is " + message + "】ActiveMQ以 Queue 准备接受用户消息");
        logger.info("【sendByQueue start】ActiveMQ以 Queue 准备接受用户消息");
        this.jmsTemplate.convertAndSend(destination , message);
    }

    public void sendByTopic (Destination destination , String message) {
        System.out.println("ActiveMQ以 Topic 准备接受用户消息");
        logger.info("【sendByTopic start,destination is + " + destination + " ,message is " + message + "】ActiveMQ以 Topic 准备接受用户消息");
        logger.info("【sendByTopic start】ActiveMQ以 Topic 准备接受用户消息");
        this.jmsTemplate.convertAndSend(destination , message);
    }

}
