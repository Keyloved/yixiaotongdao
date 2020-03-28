package com.ruider.mapper;

import com.ruider.model.MessageManage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageManageMapper {

    @Select("select * from `messageManage` where `id`=#{id}")
    MessageManage getMessageById(int id);

    @Insert("insert into `messageManage`(`categoryId`, `senderId`, `needsId`, `content`, `createTime`, `isApproved`, `isWatched`, `isReply`, `recipientId`, `messageId`) values(#{categoryId}, #{senderId}, #{needsId}, #{content}, #{createTime}, #{isApproved}, #{isWatched}, #{isReply}, #{recipientId}, #{messageId})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addEvaluate(MessageManage messageManage);

    /*
    有问题
     */
    @Update("update `messageManage` set `replyIds`= #{replyIds} where `id`=#{messageId}")
    int addReply(String replyIds, int messageId);

    @Select("select * from `messageManage` where `needsId`=#{needsId} and `categoryId`=#{categoryId}")
    List<MessageManage> getMessageByNeedsIdAndCategoryId(@Param("needsId") int needsId, @Param("categoryId") int categoryId);

    @Select("select * from `messageManage` where `needsId`=#{needsId} and `categoryId`=#{categoryId} and `isReply`=#{isReply}")
    List<MessageManage> getAndSetMessageByNeedIdAndCategoryId(@Param("needsId") int needsId, @Param("categoryId") int categoryId, int isReply);


    @Select("select * from `messageManage` where `needsId`=#{needsId} and `isWatched` = 0")
    List<MessageManage> getNoWatchedMessage(int needsId);

    @Select("select * from `messageManage` where `senderId`=#{senderId} and `categoryId`=#{categoryId} and `isReply` = #{isReply}")
    List<MessageManage> getMessageByUserIdAndCategoryId(@Param("senderId") int userId, @Param("categoryId") int categoryId, int isReply);

    @Select("select * from `messageManage` where `recipientId`=#{recipientId}")
    List<MessageManage> getMessageByMasterId(int recipientId);

    @Select("select * from `messageManage` where `messageId`=#{messageId}")
    List<MessageManage> getReplyByMessageId(int messageId);

    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `categoryId` = #{categoryId} and `isReply`=#{isReply}")
    List<MessageManage> getMessageByMasterIdAndCategoryId(int recipientId, int categoryId, int isReply);

    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `categoryId` = #{categoryId}")
    List<MessageManage> getMessageByUserIdAndCategoryIdAndIsApprovedAndapprovedOrRefuseIsViewed(int recipientId, int categoryId);

    /*
    有问题
     */
    @Update("update `messageManage` set `approvedOrRefuseIsViewed`=#{approvedOrRefuseIsViewed} where `id`=#{id}")
    int updateApprovedOrRefuseIsViewed(int id, int approvedOrRefuseIsViewed);

    /**
     * 获取评论，回复，申请加入,人员已满,过期的未读消息数getReadedMessage，降序
     * @param recipientId
     * @return
     */
    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `isWatched` = 0 ORDER BY `createTime` DESC")
    List<MessageManage> getUnreadMessage(int recipientId);

    /**
     * 根据消息类型categoryId获取评论，回复，申请加入,人员已满,过期的未读消息数getReadedMessage，降序
     * @param recipientId
     * @return
     */
    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `isWatched` = 0 and categoryId = #{categoryId} ORDER BY `createTime` DESC")
    List<MessageManage> getUnreadMessageByUserIdAndCategoryId(int recipientId, int categoryId);


    /**
     * 获取评论，回复，申请加入,人员已满,过期的已读消息数getReadedMessage，降序
     * @param recipientId
     * @return
     */
    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `isWatched` = 1 ORDER BY `createTime` DESC")
    List<MessageManage> getReadedMessage(int recipientId);

    /**
     * 根据消息类型categoryId获取评论，回复，申请加入,人员已满,过期的已读消息数getReadedMessageByUserIdAndCategoryId，降序
     * @param recipientId
     * @return
     */
    @Select("select * from `messageManage` where `recipientId`=#{recipientId} and `isWatched` = 1 and categoryId = #{categoryId} ORDER BY `createTime` DESC")
    List<MessageManage> getReadedMessageByUserIdAndCategoryId(int recipientId, int categoryId);

    /**
     * 根据需求id获取所有评论消息2，不包括回复消息，按照时间降序查询
     */
    @Select("select * from `messageManage` where `needsId`=#{needsId} and `categoryId` = 2 and `isReply` = 1 ORDER BY `createTime`")
    List<MessageManage> getallEvaluatesByNeedsId (int needsId);

    /**
     * 根据评论消息id获取评论2对应的回复消息0列表，按照时间降序查询
     */
    @Select("select * from `messageManage` where `messageId`=#{messageId} and `categoryId` = 2 and `isReply` = 0 ORDER BY `createTime`")
    List<MessageManage> getEvaluateReplysList (int messageId);

    /**
     * 添加过期消息，
     */
    @Insert("insert into `messageManage`(`categoryId`,`senderId`, `senderNickname`, `senderImage`, `needsId`, `content`, `createTime`, `isWatched`, `recipientId`) values(#{categoryId},#{senderId}, #{senderNickName}, #{senderImage},  #{needsId}, #{content}, #{createTime}, #{isWatched}, #{recipientId})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addOverTimeMessage(MessageManage messageManage);

    /**
     * 添加申请/同意/拒绝消息
     */
    @Insert("insert into `messageManage`(`categoryId`, `needsId`, `senderId`, `senderNickName`, `senderImage`,`content`, `createTime`, `isWatched`, `recipientId`, `isApproved`, `messageId`) values(#{categoryId},  #{needsId}, #{senderId},#{senderNickName}, #{senderImage}, #{content}, #{createTime}, #{isWatched}, #{recipientId}, #{isApproved},  #{messageId})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addApply(MessageManage messageManage);

    /**
     * 添加申请/同意/拒绝消息
     */
    @Insert("insert into `messageManage`(`categoryId`,`senderId`, `senderNickname`, `senderImage`, `needsId`, `content`, `createTime`, `isWatched`, `recipientId`) values(#{categoryId}, #{senderId}, #{senderNickName}, #{senderImage},  #{needsId},  #{content}, #{createTime}, #{isWatched}, #{recipientId})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addOverNumberMessage (MessageManage messageManage);

    /**
     * 更新申请消息是否被同意/拒绝，同意为2，拒绝为1
     */
    @Update("update `messageManage` set `isApproved`=#{isApproved} where `id`=#{id}")
    int updateIsApproved(int isApproved, @Param("id") int id);

    /**
     * 更新消息已被查看
     */
    @Update("update `messageManage` set `isWatched` = 1 where `id` = #{id}")
    int updateIsWatched( int id);

    /**
     * 更新消息message
     */
    @Update("update `messageManage` set `categoryId` = #{categoryId}, `senderId` = #{senderId} ,`senderNickname` = #{senderNickName} , `senderImage` = #{senderImage} , `needsId` = #{needsId} ,`content` = #{content} ,`createTime` = #{createTime} ,`isApproved` = #{isApproved} ,`messageId` = #{messageId} ,`recipientId` = #{recipientId} ,`isWatched` = #{isWatched} ,`isReply` = #{isReply}  where `id`=#{id}")
    int updateMessage(MessageManage messageManage);

    /**
     * 根据需求id删除对应的需求评论和回复
     * @param needId
     */
    @Delete("delete from `messageManage` where `needsId` = #{needId}")
    void deleteMessageByNeedId (int needId);

    /**
     * 添加消息
     * @param messageManage
     */
    @Insert("insert into `messageManage`(`categoryId`,`senderId`, `senderNickname`, `senderImage`, `needsId`, `content`, `createTime`, `isApproved`, `messageId`, `isWatched`, `recipientId`, `isReply`) values(#{categoryId}, #{senderId}, #{senderNickName}, #{senderImage},  #{needsId},  #{content}, #{createTime}, #{isApproved},#{messageId}, #{isWatched}, #{recipientId}, #{isReply})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    int addMessage (MessageManage messageManage);

    /**
     * 根据用户和消息类型获取消息,按照时间降序查询
     */
    @Select("select * from `messageManage` where `recipientId`=#{userId} and `categoryId` = #{categoryId} ORDER BY `createTime` DESC")
    List<MessageManage> getMsgByUserIdAndCategory (int userId , int categoryId);


}
