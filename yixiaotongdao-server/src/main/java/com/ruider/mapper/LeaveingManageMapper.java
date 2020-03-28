package com.ruider.mapper;

import com.ruider.model.LeaveingManage;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface LeaveingManageMapper {

    /**
     * 新增留言
     * @param leaveingManage
     * @return
     */
    @Insert("insert into `leaveingManage`(`categoryId`, `userId`, `content`, `createTime`, `mark`, `future`) values(#{categoryId}, #{userId}, #{content}, #{createTime}, #{mark}, #{future})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addLeaveingManage (LeaveingManage leaveingManage);

    /**
     * 更新留言
     * @param leaveingManage
     * @return
     */
    @Update("update `leaveingManage` set `categoryId` = #{categoryId}, `userId` = #{userId}, `content` = #{content},`createTime` = #{createTime}, `mark` = #{mark}, `future` = #{future} where `id` = #{id}")
    int updateLeaveingManage (LeaveingManage leaveingManage);

    /**
     * 根据消息类型获取所有消息
     * @param categoryId
     * @return
     */
    @Select("select * from `leaveingManage` where `categoryId` = #{categoryId} ORDER by `createTime` DESC")
    List<LeaveingManage>  getAllLeaveingManage(int categoryId);

    /**
     * 获取「写给未来的ta」的满足时间now的消息列表
     * @param now
     * @return
     */
    @Select("select * from `leaveingManage` where `categoryId` = 7 and `future` = #{now} ORDER by `createTime` DESC")
    List<LeaveingManage>  getTimeoutComplaints(Date now);
}
