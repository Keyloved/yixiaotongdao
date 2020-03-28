package com.ruider.service;

import com.ruider.model.MessageManage;
import com.ruider.model.NeedsManagement;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MessageManageService {

    //void getUnreadMessage (String token) throws Exception;
    //void getReadedMessage (String token) throws Exception;

    int addEvaluate(HashMap<String,Object> paramMap) throws Exception;

    int addApply(HashMap<String,Object> paramMap) throws Exception;

    //List<HashMap<String, Object>> getMessageByNeedsIdAndCategoryId(@Param("needsId") int needsId, @Param("categoryId") int categoryId) throws Exception;

    //List<HashMap<String, Object>> getAndSetMessageByUserIdAndCategoryId(@Param("userId") int userId, @Param("categoryId") int categoryId, @Param("isReply") int isReply) throws Exception;

    //HashMap<String,Integer> getNoWatchedMessageNo(int userId) throws Exception;

    //int updateIsApproved(@Param("isApproved") int isApproved, @Param("id") int id) throws Exception;

    //List<HashMap<String,Object>> checkOverTimeNeeds(@Param("userId") int userId) throws Exception;

    //List<HashMap<String,Object>> checkOverNumber(@Param("userId") int userId) throws Exception;

    HashMap<String,Integer> getUnreadMessageNum (int userId) throws Exception ;

    List<HashMap<String,Object>> getUnreadMessageList (int userId, int categoryId) throws Exception;


    List<HashMap<String, Object>> getNeedMessageDetails (int needsId) throws Exception;

    void deleteMessageByNeedId (int needId) throws Exception;

    int addMessage (MessageManage messageManage) throws Exception;


    /**
     * 更新消息
     * @param messageManage
     * @return
     */
    int updateMessage(MessageManage messageManage) throws Exception;


    /**
     * 获取消息详情
     * @param id
     * @return
     */
    MessageManage getMessageDetails(int id) throws Exception;


    /**
     * 根据用户和消息类型获取未读消息列表
     * @param userId
     * @param categoryId
     * @return
     * @throws Exception
     */
    List<MessageManage> getUnreadMsgByUserIdAndCategory (int userId , int categoryId) throws Exception;

    /**
     * 获取消息类型列表
     * @param categoryId
     * @return
     * @throws Exception
     */
    List<MessageManage> getMsgByCategory (int userId , int categoryId )throws Exception;


    /**
     * 获取需求的详情和消息详情
     * @param
     * @return
     */
    HashMap<String,Object> getNeedsAndMessage(MessageManage messageManage) throws  Exception;
}
