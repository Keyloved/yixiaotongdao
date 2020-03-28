package com.ruider.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("redisC")
public class RedisController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String,String> redisTemplate;


    //添加
    @GetMapping(value="/redisAdd")
    public void saveRedis(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key,value,1, TimeUnit.HOURS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取
    @GetMapping(value="/redisGet")
    public String getRedis (String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }


    public void putMap(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, 1, TimeUnit.HOURS); //设置超时时间1小时 第三个参数控制时间单位

    }

    public Map getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

}
