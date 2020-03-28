package com.ruider.service.impl;

/**
 * Created by mahede on 2018/11/27.
 */
import com.ruider.mapper.UserMapper;
import com.ruider.model.User;
import com.ruider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionAttributes("userId")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户openId获取用户数据
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public User getUserInfoByOpenId(HashMap<String,Object> paramMap) throws Exception {
        return userMapper.selectUserByOpenId(paramMap.get("openid").toString());
    }

    /**
     * 根据用户的openId查询用户是否存在
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public User checkUser (String openId) throws Exception {
        User user = userMapper.selectUserByOpenId(openId);
        return user;
    }

    /**
     * 用户设置个人信息是否公开
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateUserInfoOpen (HashMap<String, Object> paramMap) throws Exception{
        int userId = Integer.valueOf(paramMap.get("userId").toString());
        User user = getUserDetails(userId);
        int isQQOpen = ((boolean)paramMap.get("isQQOpen")) == false ? 0:1;
        int isWeixinOpen = ((boolean)paramMap.get("isWeixinOpen")) == false ? 0:1;
        int isNeedsOpen = ((boolean)paramMap.get("isNeedsOpen")) == false ? 0:1;

        user.setIsQQOpen(isQQOpen);
        user.setIsWeixinOpen(isWeixinOpen);
        user.setIsNeedsOpen(isNeedsOpen);
        return userMapper.updateUser(user);
    }


    @Override
    public boolean login(String username,String password){
        User userEntity = new User ();
        userEntity.setNickName( username );
        userEntity.setPassword ( password );

        User user = userMapper.selectUser ( userEntity );
        if (user != null){
            return true;
        }
        return false;
    }

    @Override
    @ModelAttribute("userId")
    public int addUser(HashMap<String, Object> paramMap) throws Exception {
        String openId = paramMap.get("openId").toString();
        User user = userMapper.selectUserByOpenId(openId);
        if(user == null) {
            user = new User();
            user.setOpenid(openId);
            user.setHeadurl(paramMap.get("headUrl").toString());
            user.setImage(paramMap.get("image").toString());
            user.setSex(paramMap.get("sex").toString());
            user.setNickName(paramMap.get("nickName").toString());
            Date createTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            user.setCreateTime(simpleDateFormat.format(createTime));
            user.setIsQQOpen(1);
            user.setIsWeixinOpen(1);
            user.setIsNeedsOpen(1);
            userMapper.addUser(user);
            return user.getId();
        }
        //更新用户信息
        else {
            user.setOpenid(openId);
            user.setHeadurl(paramMap.get("headUrl").toString());
            user.setImage(paramMap.get("image").toString());
            user.setSex(paramMap.get("sex").toString());
            user.setNickName(paramMap.get("nickName").toString());
            userMapper.updateUser(user);
            return user.getId();
        }

    }

    @Override
    public User getUserDetails (int userId) throws Exception {
        User user;
        user = userMapper.selectUserById(userId);
        if (user != null) {
            if (user.getSex().equals("1") || user.getSex().equals("男")) user.setSex("男");
            else user.setSex("女");
        }
        return user;

    }

    @Override
    public int updateUser(HashMap<String,Object> paramMap) throws Exception{
        User user = getUserDetails(Integer.valueOf(paramMap.get("id").toString()));
        user.setImage(paramMap.get("image").toString());
        user.setNickName(paramMap.get("userName").toString());
        //user.setSex(paramMap.get("sex").toString());
        //user.setMobilePhone(paramMap.get("phone").toString());
        user.setQQ(paramMap.get("QQ").toString());
        user.setWeixin(paramMap.get("weixin").toString());
        user.setAutograph(paramMap.get("autograph").toString());
        user.setHeadurl(paramMap.get("headerUrl").toString());
        user.setRealName(paramMap.get("realName").toString());
        user.setAge(Integer.valueOf(paramMap.get("age").toString()));
        return userMapper.updateUser(user);
    }

    @Override
    public User selectUserById(int userId) {
        return userMapper.selectUserById(userId);
    }

}