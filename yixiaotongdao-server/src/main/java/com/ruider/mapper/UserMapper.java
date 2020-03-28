package com.ruider.mapper;

import com.ruider.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by mahede on 2018/11/27.
 */
@Mapper
@CacheConfig(cacheNames = "users")
public interface UserMapper {

    @Insert("insert into `user`" +
            "(`nickName`, `age`, `image`, `openid`,`headUrl`, `password`, `mobilePhone`,`autograph`, `status`, `realName`, `sex`,`level`,`createTime`,`isQQOpen`,`isWeixinOpen`,`isNeedsOpen`,`extendField1`,`extendField2`, `extendField3`, `extendField4`, `extendField5`, `extendField6`, `extendField7`) " +
            "values(#{nickName}, #{age}, #{image}, #{openid},#{headurl}, #{password}, #{mobilePhone}, #{autograph}, #{status}, #{realName},#{sex},#{level},#{createTime},#{isQQOpen},#{isWeixinOpen},#{isNeedsOpen},#{extendField1},#{extendField2},#{extendField3},#{extendField4},#{extendField5},#{extendField6},#{extendField7})")

    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addUser(User user);

    @Delete("delete from `user` where `id` = #{id}")
    int deleteUserById(int id);

    /**
     * 更新用户基本信息
     * @param user
     * @return
     */
    @Update("update `user` set `nickName` = #{nickName},`age` = #{age}, `password` = #{password}, `mobilePhone` = #{mobilePhone}, `QQ` = #{QQ},`weixin` = #{weixin}, `image` = #{image} , `autograph` = #{autograph}, `status` = #{status}, `realName` = #{realName}, `sex` = #{sex}, `level` = #{level}, `openId` = #{openId}, `headUrl` = #{headurl}, `userIp` = #{userIp} , `createTime` = #{createTime}, `isQQOpen` = #{isQQOpen},`isWeixinOpen` = #{isWeixinOpen},`isNeedsOpen` = #{isNeedsOpen} where `id` = #{id} ")
    int updateUser(User user);

    @Select("select * from `RuiDer`.`user`" +
            " where `nickName`= #{nickName} and `sex` = #{sex} and `image` = #{image} ")
    User selectUser(User user);


    @Select("select * from `RuiDer`.`user`" +
            " where `openid` = #{openid}")
    User selectUserByOpenId(String openid);

    @Select("select * from `RuiDer`.`user` where `id` = #{id}")
    User selectUserById(int id);



}
