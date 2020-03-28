package com.ruider.service.impl;

import com.ruider.Enum.msgCategoryEnum.MsgCategoryEnum;
import com.ruider.controller.RedisController;
import com.ruider.model.MessageManage;
import com.ruider.service.MessageManageService;
import com.ruider.service.MsgCacheService;
import com.ruider.utils.stringUtil.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 缓存消息服务实现
 */
@Service
public class MsgCacheServiceImpl implements MsgCacheService {

    /**
     * 消息服务
     */
    @Autowired
    private MessageManageService messageManageService;

    /**
     * 缓存服务
     */
    @Autowired
    private RedisController redisController;

    /**
     * 刷新缓存未读消息数量
     * @param userId
     * @param categoryId
     * @throws Exception
     */
    @Override
    public void refreshUnreadMsgNo (int userId , int categoryId) throws Exception {

        List<MessageManage> unreadMsgs = messageManageService.getUnreadMsgByUserIdAndCategory (userId , categoryId);

        String categoryName = generateCategoryName(categoryId);
        Map<String,Object> redisMap = redisController.getMap(String.valueOf(userId));
        redisMap.put(generateUnreadMsgNo(categoryName) , unreadMsgs.size()) ;
        redisController.putMap(String.valueOf(userId), redisMap);
    }

    /**
     * 刷新缓存消息列表,保证更新消息状态更新
     * @param userId
     * @param categoryId
     * @throws Exception
     */
    @Override
    public List<HashMap<String,Object>> refreshMsgs (int userId , int categoryId) throws Exception {

        List<MessageManage> msgs = messageManageService.getMsgByCategory (userId ,categoryId);

        List<HashMap<String,Object>> needsMsgs = new ArrayList<>();
        for (MessageManage msg : msgs) {
            if (msg.getIsWatched() == 0) {
                msg.setIsWatched(1);
                messageManageService.updateMessage(msg);
            }

            needsMsgs.add(messageManageService.getNeedsAndMessage(msg));
        }

        String categoryName = generateCategoryName(categoryId);
        Map<String,Object> redisMap = redisController.getMap(String.valueOf(userId));
        if (redisMap == null) redisMap = new HashMap<>();
        redisMap.put(generateMsgCategoryName(categoryName), needsMsgs) ;
        redisController.putMap(String.valueOf(userId), redisMap);
        return needsMsgs;

    }

    /**
     * 获取缓存未读消息数量
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,Integer> acquireUnreadMsgNo (int userId) throws Exception {
        Map<String , Integer> resultMap = new HashMap<>();

        Map<String,Object> redisMap = redisController.getMap(String.valueOf(userId));

        if(redisMap == null) redisMap = new HashMap<>();

        int allNoWatchedNo = 0;
        for (MsgCategoryEnum msgCategoryEnum : MsgCategoryEnum.values()) {
            singleAcquireUnreadMsgs(userId , msgCategoryEnum , resultMap , redisMap);
            allNoWatchedNo += StringUtil.str2Int(resultMap.get(generateUnreadMsgNo(msgCategoryEnum.getCategoryName())).toString());
        }

        resultMap.put("allNoWatchedNo" , allNoWatchedNo);

        return resultMap;
    }

    /**
     * 获取缓存消息列表
     * @param userId
     * @param categoryId
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap<String,Object>> acquireMsgs (int userId , int categoryId) throws Exception {
        HashMap<String,Object> redisMap = (HashMap<String,Object>)redisController.getMap(String.valueOf(userId));
        List<HashMap<String,Object>> resultList = new ArrayList<>();
        if (redisMap == null) redisMap = new HashMap<>();

        String categoryName = MsgCategoryEnum.getCategotyNameByCode(categoryId);
        //是否有数据，没有则刷新缓存，更改消息读状态,刷新缓存未读消息数量
        if (redisMap.get(generateMsgCategoryName(categoryName)) == null ) {
            resultList = refreshMsgs(userId,categoryId);
        }
        else {
            resultList = (List<HashMap<String,Object>>) redisMap.get(generateMsgCategoryName(categoryName));

            //时间戳转换为时间格式
            for (int index = 0, size = resultList.size(); index < size ; index++) {
                HashMap<String,Object> map = resultList.get(index);
                String startTimeStr = map.get("startTime").toString();
                String deadlineStr = map.get("deadline").toString();
                String createTimeStr = map.get("createTime").toString();
                String needCreateTimeStr = map.get("needCreateTime").toString();

                map.put("startTime" , new Date(new Long(startTimeStr)));
                map.put("deadline" , new Date(new Long(deadlineStr)));
                map.put("createTime" , new Date(new Long(createTimeStr)));
                map.put("needCreateTime" , new Date(new Long(needCreateTimeStr)));

                resultList.set(index,map);
            }
        }

        //刷新缓存未读消息数量
        refreshUnreadMsgNo(userId,categoryId);
        return resultList;
    }


    private void unreadSwitch (List<Map<String,Object>> msgs) throws Exception {
        for (Map<String,Object> msg : msgs) {
            if(StringUtil.str2Int(msg.get("isWatched").toString().toString()) == 0) {
                int messageId = (Integer) msg.get("id");
                MessageManage messageManage = messageManageService.getMessageDetails(messageId);
                messageManageService.updateMessage(messageManage);
            }
        }
    }

    private Map<String,Object> singleAcquireUnreadMsgs (int userId, MsgCategoryEnum DEFAULT_Category_Enum , Map<String,Integer> resultMap , Map<String,Object> redisMap) throws Exception {

        String categoryName = DEFAULT_Category_Enum.getCategoryName();

        if (redisMap.get(generateUnreadMsgNo(categoryName)) == null) {
            List<MessageManage> unreadMsgs = messageManageService.getUnreadMsgByUserIdAndCategory (userId , DEFAULT_Category_Enum.getCategoryCode());

            //刷新缓存
            redisMap.put(generateUnreadMsgNo(categoryName) , unreadMsgs.size()) ;

            resultMap.put(generateUnreadMsgNo(categoryName) , unreadMsgs.size());
        }
        else {
            resultMap.put(generateUnreadMsgNo(categoryName) , (Integer)redisMap.get(generateUnreadMsgNo(categoryName)));
        }

        return redisMap;
    }


    private String generateCategoryName (int categoryId) {
        String categoryName = null;
        for (MsgCategoryEnum msgCategoryEnum :MsgCategoryEnum.values() ) {
            if (msgCategoryEnum.getCategoryCode() == categoryId) {
                categoryName = msgCategoryEnum.getCategoryName();
                break;
            }
        }
        return categoryName;
    }

    private String generateUnreadMsgNo (String categoryName) {
        return categoryName + "No";
    }

    private String generateMsgCategoryName (String categoryName) {
        return categoryName + "List";
    }

}
