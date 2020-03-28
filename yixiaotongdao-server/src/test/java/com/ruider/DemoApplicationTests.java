package com.ruider;

import com.ruider.controller.RedisController;
import com.ruider.utils.ActiveMQ.ActiveMQProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Topic;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private Topic topic;

	@Autowired
	private ActiveMQProducer activeMQProducerTest;
	@Test
	public void contextLoads() throws Exception{
		activeMQProducerTest.sendByTopic(topic , "hello@@@");
		Thread.sleep(10000);
	}

	@Autowired
	RedisController redisController;

	@Test
	public void test() {
		redisController.saveRedis("a","test");
		System.out.println(redisController.getRedis("a"));
	}

}
