package com.ruider.utils.RedisUtils;

import com.ruider.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

public class RedisUtils {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;;

    public void saveRedis(String key, String value) {
        stringRedisTemplate.opsForValue().set(key,value,1, TimeUnit.HOURS);
    }

    public String getRedis (String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /*

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //添加
    public void saveRedis(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    //获取
    public String getRedis(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    */

}
