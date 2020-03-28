package com.ruider.mapper;

import com.ruider.model.UserCollection;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户收藏表
 */
@Mapper
public interface UserCollectionMapper {

    @Insert("Insert into `userCollection`(`userId`, `needId`, `createTime`) values(#{userId}, #{needId}, #{createTime})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addCollection(UserCollection userCollection);

    @Update("update `userCollection` set `userId`= #{userId}, `needId`=#{needId}, `createTime`=#{createTime} where `id`=#{id}")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int updateCollection(UserCollection userCollection);

    /**
     * 根据收藏id删除收藏
     * @param id
     */
    @Delete("delete from `userCollection` where `id`=#{id}")
    void deleteCollection(int id);

    /**
     * 根据需求id删除收藏
     * @param needId
     */
    @Delete("delete from `userCollection` where `needId`=#{needId}")
    void deleteCollectionByNeedId(int needId);


    /**
     * 根据userId，needId查询收藏
     * @param userId
     * @param needId
     * @return
     */
    @Select("select * from `userCollection` where `userId`=#{userId} and `needId` = #{needId}")
    UserCollection selectCollection(int userId, int needId);

    /**
     * 根据userId查询收藏列表
     * @param userId
     * @return
     */
    @Select("select * from `userCollection` where `userId`=#{userId}")
    List<UserCollection> selectUserCollections (int userId);
}

