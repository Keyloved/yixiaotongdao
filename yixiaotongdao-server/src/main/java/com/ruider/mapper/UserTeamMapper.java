package com.ruider.mapper;
/**
 * 用户个人申请加入的队伍信息
 */

import com.ruider.model.UserCollection;
import com.ruider.model.UserTeam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserTeamMapper {

    @Insert("Insert into `userTeam`(`userId`, `needId`, `createTime`) values(#{userId}, #{needId}, #{createTime})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addUserTeam(UserTeam userTeam);

    @Update("update `userTeam` set `userId`= #{userId}, `needId`=#{needId}, `createTime`=#{createTime} where `id`=#{id}")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int updateUserTeam(UserTeam userTeam);

    /**
     * 根据组队信息id删除组队信息
     * @param id
     */
    @Delete("delete from `userTeam` where `id`=#{id}")
    void deleteUserTeam(int id);

    /**
     * 根据需求id删除组队
     * @param needId
     */
    @Delete("delete from `userTeam` where `needId`=#{needId}")
    void deleteUserTeamByNeedId(int needId);

    /**
     * 根据需求id和用户id删除信息
     * @param needId
     * @param userId
     */
    @Delete("delete from `userTeam` where `needId`=#{needId} and `userId` = #{userId}")
    void deleteUserTeamByNeedIdAndUserId(int needId, int userId);


    /**
     * 根据userId，needId查询组队信息
     * @param userId
     * @param needId
     * @return
     */
    @Select("select * from `userTeam` where `userId`=#{userId} and `needId` = #{needId}")
    UserTeam selectUserTeam(int userId, int needId);

    /**
     * 根据userId查询组队列表
     * @param userId
     * @return
     */
    @Select("select * from `userTeam` where `userId`=#{userId} ORDER by `createTime` DESC")
    List<UserTeam> selectUserTeams (int userId);
}
