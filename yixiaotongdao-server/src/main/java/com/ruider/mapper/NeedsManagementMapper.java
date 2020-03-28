package com.ruider.mapper;

import com.ruider.model.NeedsManagement;
import org.apache.ibatis.annotations.*;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;

/**
 * Created by mahede on 2018/12/2.
 */
@Mapper
public interface NeedsManagementMapper {

    @Select("select * from `needsManage` where `userId` = #{userId} ORDER by `createTime` DESC")
    List<NeedsManagement> getNeedsByUserId(int userId);

    @Select("select * from `needsManage` where `userId` = #{userId} AND `categoryId` = #{categoryId}")
    List<NeedsManagement> getNeedsByUserIdAndCategoryId(@Param("userId") int userId,@Param("categoryId") int categoryId);

    @Insert("Insert into `needsManage`(`categoryId`, `stypeId`, `userId`, `title`, `content`, `startTime`, `deadline`,`qq`, `weChat`, `phoneNo`, `limitNo`, `createTime`, `updateTime`, `joinUserId`,`joinNo`) values(#{categoryId}, #{stypeId}, #{userId}, #{title}, #{content}, #{startTime}, #{deadline}, #{qq}, #{weChat}, #{phoneNo}, #{limitNo}, #{createTime}, #{updateTime}, #{joinUserId}, #{joinNo})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addNeeds(NeedsManagement needsManagement);

    @Delete("delete from `needsManage` where `id` = #{id}")
    int deleteNeeds(@Param("id") int id);

    @Select("select * from `needsManage` where `id` = #{id}")
    NeedsManagement getNeedsDetailsById(int id);

    @Update("update `needsManage` set `categoryId` = #{categoryId}, `userId`= #{userId}, `title`= #{title}, `content`= #{content}, `startTime`= #{startTime}, `deadline`= #{deadline},`qq`= #{qq}, `weChat`= #{weChat}, `phoneNo`= #{phoneNo}, `limitNo`= #{limitNo}, `updateTime`= #{updateTime}  where `id` = #{id}")
    int editNeeds(NeedsManagement needsManagement);

    @Select("select * from `needsManage` where `categoryId` = #{categoryId}")
    List<NeedsManagement> getNeedsByCategoryId(int categotyId);

    /*@Select("select * from `needsManage` where `deadline` < #{checkTime} and `userId` = #{userId}")
    List<NeedsManagement> checkOverTimeNeeds(@Param("userId") int userId, Date checkTime);
*/
    @Select("select * from `needsManage` where `joinNo` = `limitNo` and `userId` = #{userId}")
    List<NeedsManagement> checkOverNumber(@Param("userId") int userId);

    @Update("update `needsManage` set `joinNo` = `joinNo`+1 where `id` = #{needId}")
    int riseJoinNO(int needId);

    /**
     * 设置需求过期1
     * @param id
     * @return
     */
    @Update("update `needsManage` set `isOverTime` = 1 where `id`=#{id}")
    int updateIsOverTime(int id);

    @Update("update `needsManage` set `isOverNo` = 1 where `id`=#{id}")
    int updateIsOverNO(int id);

    /**
     * 获取用户未过期0的需求
     */
    @Select("select * from `needsManage` where `isOverTime` = 0 and `userId` = #{userId} ORDER by `createTime` DESC")
    List<NeedsManagement> checkOverTimeNeeds(@Param("userId") int userId);

    /**
     * 获取指定类型的需求
     */
    @Select("select * from `needsManage` where `stypeId` = #{stypeId} ORDER by `updateTime` DESC")
    List<NeedsManagement> getNeedsAndUserInfo(@Param("stypeId") int stypeId);

    /**
     * 更改需求信息
     */
    @Update("update `needsManage` set `categoryId` = #{categoryId},`stypeId` = #{stypeId},`userId` = #{userId},`title` = #{title},`content` = #{content},`startTime` = #{startTime},`deadline` = #{deadline},`qq` = #{qq},`weChat` = #{weChat},`phoneNo` = #{phoneNo},`limitNo` = #{limitNo},`createTime` = #{createTime},`updateTime` = #{updateTime},`joinNo` = #{joinNo},`viewNo` = #{viewNo},`isOverTime` = #{isOverTime},`isOverNo` = #{isOverNo},`joinUserId` = #{joinUserId},`extendField5` = #{extendField5},`extendField6` = #{extendField6}, `extendField7` = #{extendField7} where `id`= #{id}")
    int updateNeedsManage(NeedsManagement needsManagement);

    /**
     * 根据一级类型模糊搜索需求内容
     */
    @Select("select * from `needsManage` where `stypeId` = #{stypeId}")
    List<NeedsManagement> getKeyWordsContentByStypeId(int stypeId);

    /**
     * 根据二级类型模糊搜索需求内容
     */
    @Select("select * from `needsManage` where `categoryId` = #{categoryId}")
    List<NeedsManagement> getKeyWordsContentByCategoryId(int categoryId);

    /**
     * 根据关键字模糊搜索需求相关标题
     */
    @Select("select * from `needsManage` where CONCAT(IFNULL(`title`,'')) LIKE CONCAT('%', #{keyWords},'%')")
    List<NeedsManagement> getKeyWordsByTitle(String keyWords);

    /**
     * 根据关键字模糊搜索需求相关内容
     */
    @Select("select * from `needsManage` where CONCAT( IFNULL(`content`,'')) LIKE CONCAT('%', #{keyWords},'%')")
    List<NeedsManagement> getKeyWordsByContent(String keyWords);


    /**
     * 根据关键字模糊搜索需求相关时间
     */
    @Select("select * from `needsManage` where CONCAT(IFNULL(`startTime`,''),IFNULL(`deadline`,'')) LIKE CONCAT('%', #{keyWords},'%')")
    List<NeedsManagement> getKeyWordsByTime(String keyWords);


}
