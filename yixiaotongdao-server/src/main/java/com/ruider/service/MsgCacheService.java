package com.ruider.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息缓存服务接口
 */
public interface MsgCacheService {

    /**
     * 刷新缓存未读消息数量
     * @param userId
     * @param categoryId
     * @throws Exception
     */
    void refreshUnreadMsgNo (int userId , int categoryId) throws Exception;

    /**
     * 刷新缓存消息列表
     * @param userId
     * @param categoryId
     * @throws Exception
     */
    List<HashMap<String,Object>> refreshMsgs (int userId , int categoryId) throws Exception;

    /**
     * 获取缓存未读消息数量
     * @param userId
     * @return
     * @throws Exception
     */
    Map<String,Integer> acquireUnreadMsgNo (int userId) throws Exception;

    /**
     * 获取缓存消息列表
     * @param userId
     * @param categoryId
     * @return
     * @throws Exception
     */
    List<HashMap<String,Object>> acquireMsgs (int userId , int categoryId) throws Exception;
}
