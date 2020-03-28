package com.ruider.service.impl;

/**
 * 消息管理服务， 主要完成未读消息数量的统计，未读消息以列表形式从数据库搜索出来
 * 实现方式
 * 问题：考虑到用户的体验效果，在大量的消息数据面前，如果每个请求都直接从数据库查询未读消息的数量和未读消息列表，响应时间会延长。
 * 解决方案：对于未读消息的数量和未读消息列表的查询，分别设定一个后台线程定期查看数据库，将查看的结果保存在消息缓存中，用户每次请求直接从缓存中获取。
 */

import com.ruider.controller.RedisController;
import com.ruider.mapper.MessageManageMapper;
import com.ruider.mapper.NeedsManagementMapper;
import com.ruider.model.MessageManage;
import com.ruider.model.NeedsManagement;
import com.ruider.model.User;
import com.ruider.model.UserTeam;
import com.ruider.service.*;
import com.ruider.utils.stringUtil.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class MessageManageServiceImpl implements MessageManageService {

    private Logger logger = LoggerFactory.getLogger(MessageManageServiceImpl.class);

    @Autowired
    private MessageManageMapper messageManageMapper;

    @Autowired
    private RedisController redisController;

    @Autowired
    private UserService userService;

    @Autowired
    private NeedsManagementService needsManagementService;

    @Autowired
    private NeedsManagementMapper needsManagementMapper;

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private MsgCacheService msgCacheService;

    //消息缓存器，存储未读消息的数量，未读消息的列表
    ConcurrentHashMap<String,HashMap<String,Object>> messageCache = new ConcurrentHashMap<>();


    /**
     * 新增消息
     * @param messageManage
     * @throws Exception
     */
    @Override
    public int addMessage (MessageManage messageManage) throws Exception {
        return messageManageMapper.addMessage(messageManage);
    }


    /**
     * 获取消息详情
     * @param id
     * @return
     */
    @Override
    public MessageManage getMessageDetails(int id) throws Exception{
        return messageManageMapper.getMessageById(id);
    }

    /**
     * 更新消息
     * @param messageManage
     * @return
     */
    @Override
    public int updateMessage(MessageManage messageManage) throws Exception {
        return messageManageMapper.updateMessage(messageManage);
    }

    /**
     * 获取未读消息列表以及消息归属的需求信息
     */
    private void getUnreadMessageList(int userId, List<MessageManage> messageManages) throws Exception{

        ArrayList<HashMap<String,Object>> applyList = new ArrayList<>();     //申请未读消息列表
        ArrayList<HashMap<String,Object>> evaluateList = new ArrayList<>();    //评论未读消息列表
        ArrayList<HashMap<String,Object>> overTimeList = new ArrayList<>();     //过期未读消息列表
        ArrayList<HashMap<String,Object>> eliminateList = new ArrayList<>();     //剔除未读消息列表
        ArrayList<HashMap<String,Object>> overNumberList = new ArrayList<>();     //人员已满未读消息列表

        HashMap<String,Object> map = (HashMap<String,Object>)redisController.getMap(String.valueOf(userId));

        for(MessageManage messageManage : messageManages) {
            int categoryId = messageManage.getCategoryId();
            if(categoryId == 1) { applyList.add( getNeedsAndMessage(messageManage) ); }
            else if(categoryId == 2) { evaluateList.add( getNeedsAndMessage(messageManage) );}
            else if(categoryId == 3) { overTimeList.add( getNeedsAndMessage(messageManage) ); }
            else if(categoryId == 4) { overNumberList.add( getNeedsAndMessage(messageManage)); }
            else if(categoryId == 5) { eliminateList.add( getNeedsAndMessage(messageManage)); }
        }

        map.put("applyList", applyList);
        map.put("evaluateList", evaluateList);
        map.put("overTimeList", overTimeList);
        map.put("overNumberList", overNumberList);
        map.put("eliminateList", eliminateList);
        redisController.putMap(String.valueOf(userId),map);

    }

    /**
     * 获取已读消息列表
     */
    private void getReadedMessageList(int userId, List<MessageManage> messageManages) throws Exception{


        ArrayList<HashMap<String,Object>> ReadedApplyList = new ArrayList<>();     //申请未读消息列表
        ArrayList<HashMap<String,Object>> ReadedEvaluateList = new ArrayList<>();    //评论未读消息列表
        ArrayList<HashMap<String,Object>> ReadedOverTimeList = new ArrayList<>();     //过期未读消息列表
        ArrayList<HashMap<String,Object>> ReadedOverNumberList = new ArrayList<>();     //人员已满未读消息列表
        ArrayList<HashMap<String,Object>> ReadedEliminateList = new ArrayList<>();     //剔除未读消息列表

        HashMap<String,Object> map = (HashMap<String,Object>)redisController.getMap(String.valueOf(userId));
        if (map == null) map = new HashMap<>();

        for(MessageManage messageManage : messageManages) {
            int categoryId = messageManage.getCategoryId();
            if(categoryId == 1) { ReadedApplyList.add(getNeedsAndMessage(messageManage));}
            else if(categoryId == 2) { ReadedEvaluateList.add(getNeedsAndMessage(messageManage)); }
            else if(categoryId == 3) { ReadedOverTimeList.add(getNeedsAndMessage(messageManage));}
            else if(categoryId == 4) { ReadedOverNumberList.add(getNeedsAndMessage(messageManage)); }
            else if(categoryId == 5) { ReadedEliminateList.add(getNeedsAndMessage(messageManage)); }
        }

        map.put("ReadedApplyList", ReadedApplyList);
        map.put("ReadedEvaluateList", ReadedEvaluateList);
        map.put("ReadedOverTimeList", ReadedOverTimeList);
        map.put("ReadedOverNumberList", ReadedOverNumberList);
        map.put("ReadedEliminateList", ReadedEliminateList);
        redisController.putMap(String.valueOf(userId),map);

}

    /**
     * 用户查看未读消息数量服务
     * @param
     * @return
     * @throws Exception
     */
    @Override
     public HashMap<String,Integer> getUnreadMessageNum (int userId) throws Exception {

        HashMap<String,Integer> map = (HashMap<String,Integer>)msgCacheService.acquireUnreadMsgNo(userId);

        return map;
     }

    /**
     * 用户根据消息类型查看未读消息列表服务
     * @param categoryId
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap<String,Object>> getUnreadMessageList (int userId, int categoryId) throws Exception {
        return msgCacheService.acquireMsgs(userId , categoryId);
    }



    /**
     * 用户评论回复消息详情,并且更新需求的观看次数viewNo
     * @param  needsId
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap<String, Object>> getNeedMessageDetails (int needsId) throws Exception{

        //返回前端的结果列表，列表包括所有评论消息列表和评论消息对应的回复列表map结构
        List<HashMap<String, Object>> result = new ArrayList<>();

        //根据需求id获取所有评论消息，不包括回复消息,按照时间升序查询
        List<MessageManage> allEvaluates = messageManageMapper.getallEvaluatesByNeedsId(needsId);
        for (MessageManage messageManage : allEvaluates) {
            //存放各个评论以及对应的回复消息，key为评论消息各个属性，以及评论的回复列表，value为所有属性对应值以及回复消息列表，按照创建时间升序存放
            HashMap<String,Object> resultMap = new HashMap<>();
            resultMap.put("id",messageManage.getId());
            resultMap.put("categoryId",messageManage.getCategoryId());
            resultMap.put("senderId",messageManage.getSenderId());

            User sender = userService.getUserDetails(messageManage.getSenderId());
            resultMap.put("senderNickName",sender.getNickName());
            resultMap.put("senderImage",sender.getImage());

            resultMap.put("needsId",messageManage.getNeedsId());
            resultMap.put("content",messageManage.getContent());
            resultMap.put("createTime",messageManage.getCreateTime());
            resultMap.put("isApproved",messageManage.getIsApproved());
            resultMap.put("messageId",messageManage.getMessageId());
            resultMap.put("recipientId",messageManage.getRecipientId());
            resultMap.put("isWatched",messageManage.getIsWatched());
            resultMap.put("isReply",messageManage.getIsReply());

            //添加回复列表属性
            ArrayList<HashMap<String,Object>> replyResult = new ArrayList<>();
            //针对每个评论查看评论对应的所有回复列表，按照时间升序
            ArrayList<MessageManage> replyLists =  (ArrayList<MessageManage>)messageManageMapper.getEvaluateReplysList(messageManage.getId());
            for (MessageManage replyMsg : replyLists) {
                //存放各个评论以及对应的回复消息，key为评论消息各个属性，以及评论的回复列表，value为所有属性对应值以及回复消息列表，按照创建时间升序存放
                HashMap<String,Object> replyMap = new HashMap<>();
                replyMap.put("id",replyMsg.getId());
                replyMap.put("categoryId",replyMsg.getCategoryId());
                replyMap.put("senderId",replyMsg.getSenderId());

                sender = userService.getUserDetails(replyMsg.getSenderId());
                replyMap.put("senderNickName",sender.getNickName());
                replyMap.put("senderImage",sender.getImage());

                replyMap.put("needsId",replyMsg.getNeedsId());
                replyMap.put("content",replyMsg.getContent());
                replyMap.put("createTime",replyMsg.getCreateTime());
                replyMap.put("isApproved",replyMsg.getIsApproved());
                replyMap.put("messageId",replyMsg.getMessageId());
                replyMap.put("recipientId",replyMsg.getRecipientId());
                replyMap.put("isWatched",replyMsg.getIsWatched());
                replyMap.put("isReply",replyMsg.getIsReply());
                //获取接受者者的信息，为了获取recipient的头像和昵称
                User recipien = userService.getUserDetails(replyMsg.getRecipientId());
                replyMap.put("recipientNickName",recipien.getNickName());
                replyMap.put("recipientImage",recipien.getImage());
                replyResult.add( replyMap);
            }
            resultMap.put("replyLists",replyResult);
            result.add(resultMap);
        }

        //更新当前需求的观看次数viewNo
        NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(needsId);
        needsManagement.setViewNo(needsManagement.getViewNo()+1);
        needsManagementMapper.updateNeedsManage(needsManagement);

        return result;
    }


    /**
     * 添加评论/回复请求消息
     *
     * 需求主人自己评论自己的需求，默认消息已读
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int addEvaluate(HashMap<String,Object> paramMap) throws Exception{
        Date createTime = new Date();
        MessageManage messageManage = new MessageManage();
        messageManage.setCategoryId(2);
        messageManage.setSenderId(Integer.valueOf(paramMap.get("senderId").toString()));
        messageManage.setNeedsId(Integer.valueOf(paramMap.get("needsId").toString()));

        //获取发送者的信息，为了获取sender的头像和昵称
        User sender = userService.getUserDetails(Integer.valueOf(paramMap.get("senderId").toString()));
        messageManage.setSenderNickName(sender.getNickName());
        messageManage.setSenderImage(sender.getImage());

        messageManage.setRecipientId(Integer.valueOf(paramMap.get("recipientId").toString()));
        messageManage.setContent(paramMap.get("content").toString());
        messageManage.setCreateTime(createTime);
        messageManage.setIsApproved(0);           //是否同意默认为0

        NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(Integer.valueOf(paramMap.get("needsId").toString()));

        //需求主人自己评论自己的需求，默认消息已读
        if (Integer.valueOf(paramMap.get("senderId").toString()) == needsManagement.getUserId()) {

            messageManage.setIsWatched(1);

        }
        else {

            messageManage.setIsWatched(0);   //未查看
        }

        //评论
        if(paramMap.get("messageId") == null || paramMap.get("messageId").toString().equals("")) {
            messageManage.setIsReply(1);              //设置是否为评论，评论为1，回复为0
            return messageManageMapper.addEvaluate(messageManage);
        }
        //回复
        else {
            int messageId = Integer.valueOf(paramMap.get("messageId").toString());
            messageManage.setMessageId(messageId);
            messageManage.setIsReply(0);
            messageManageMapper.addEvaluate(messageManage);

        }

        //更新缓存消息列表
        msgCacheService.refreshUnreadMsgNo(StringUtil.str2Int(paramMap.get("recipientId").toString()) , 2);

        //刷新缓存消息列表
        msgCacheService.refreshMsgs(StringUtil.str2Int(paramMap.get("recipientId").toString()) , 2);

        return messageManage.getId();

    }

    /**
     * 1. 添加申请/同意/拒绝请求消息,如果是同意/拒绝消息，改变原申请消息和当前消息的同意拒绝状态isApproved，同时判断如果是同意请求，需求参与人员数量加1，
     * 并且判断当前需求是否满员，如果人员已满，创建人员已满消息
     *
     * 2. 如果是同意/拒绝,刷新缓存存在两种情况：1 申请者消息缓存刷新 2 需求主人消息缓存刷新
     *
     * 3. 如果是申请，主人自己申请自己，默认同意申请，且消息已读
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int addApply(HashMap<String,Object> paramMap) throws Exception {
        Date createTime = new Date();
        MessageManage messageManage = new MessageManage();
        messageManage.setCategoryId(1);
        messageManage.setSenderId(Integer.valueOf(paramMap.get("senderId").toString()));
        messageManage.setNeedsId(Integer.valueOf(paramMap.get("needsId").toString()));

        //获取发送者的信息，为了获取sender的头像和昵称
        User sender = userService.getUserDetails(Integer.valueOf(paramMap.get("senderId").toString()));
        messageManage.setSenderNickName(sender.getNickName());
        messageManage.setSenderImage(sender.getImage());

        messageManage.setRecipientId(Integer.valueOf(paramMap.get("recipientId").toString()));
        messageManage.setCreateTime(createTime);


        //申请
        if(isApplyJoin(paramMap)) {

            NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(Integer.valueOf(paramMap.get("needsId").toString()));

            //需求主人自己申请自己的消息，默认同意申请，且消息已读
            if (Integer.valueOf(paramMap.get("senderId").toString()) == needsManagement.getUserId()) {

                messageManage.setIsApproved(2);

                messageManage.setIsWatched(1);
            }
            else {

                messageManage.setIsApproved(0);              //设置是否同意，同意为2，拒绝为1，默认为0

                messageManage.setIsWatched(0);   //未查看
            }

            messageManage.setContent(paramMap.get("content").toString());

            messageManageMapper.addApply(messageManage);

        }
        //同意/拒绝
        else {
            int messageId = Integer.valueOf(paramMap.get("messageId").toString());

            messageManage.setMessageId(messageId);

            messageManage.setIsApproved(Integer.valueOf(paramMap.get("isApproved").toString()));

            messageManage.setContent(Integer.valueOf(paramMap.get("isApproved").toString()) == 1?"拒绝申请" : "同意申请");

            //处理原申请消息的同意拒绝状态isApproved
            messageManageMapper.updateIsApproved(Integer.valueOf(paramMap.get("isApproved").toString()) , messageId);

            //如果isApproved是同意2的话，处理当前需求的参与人员数量加1，并且将加入人员信息添加到需求组队人员里面以及个人组队表的数据填写，之后判断需求人员是否已满，如果已满，创建新的人员已满消息
            if (isApplyApprove(paramMap)) {

                //申请分为两种,申请加入和申请退出，前端传回值区别在于content，申请加入content为"申请加入"，申请退出content为"申请退出"
                if (isApproveJoin(paramMap)) {

                    //增加组队人数
                    needJoinNoIncrease(StringUtil.str2Int(paramMap.get("needsId").toString()), StringUtil.str2Int(paramMap.get("recipientId").toString()));

                    //NeedsManage需求组队人员属性变更，joinUserId填写
                    needsManagementService.updateJoinUserId(Integer.valueOf(paramMap.get("recipientId").toString()),Integer.valueOf(paramMap.get("needsId").toString()));

                    //个人userTeam中填写组队需求信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(Integer.valueOf(paramMap.get("recipientId").toString()));

                    userTeam.setNeedId(Integer.valueOf(paramMap.get("needsId").toString()));

                    userTeam.setCreateTime(new Date());

                    userTeamService.addUserTeam(userTeam);
                }
                //申请退出
                else {   //1.人数减1  2.joinedUserId修改    3.userTeam删除吧对应人员信息

                    int needsId = Integer.valueOf(paramMap.get("needsId").toString());

                    int joinedUserId = Integer.valueOf(paramMap.get("recipientId").toString());

                    NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(needsId);

                    //判断申请退出用户是否已经退出
                    if (needHasUser(needsManagement, joinedUserId)) {

                        //剔除申请退出用户
                        doRemoveUserFromNeed (needsManagement, joinedUserId);

                        //从个人组队信息记录表userTeam表删除组队信息
                        userTeamService.deleteUserTeamByNeedIdAndUserId(needsId,joinedUserId);

                        //更新需求队员信息
                        needsManagementMapper.updateNeedsManage(needsManagement);
                    }
                    else {

                        logger.error("【同意申请退出失败,申请用户不存在】addApply failed");

                        throw new Exception("用户不存在");
                    }
                }
            }


            messageManageMapper.addApply(messageManage);

            //刷新需求主人缓存消息列表
            msgCacheService.refreshMsgs(StringUtil.str2Int(paramMap.get("senderId").toString()) , 1);


        }

        //更新缓存消息列表
        msgCacheService.refreshUnreadMsgNo(StringUtil.str2Int(paramMap.get("recipientId").toString()) , 1);

        //刷新申请者缓存消息列表
        msgCacheService.refreshMsgs(StringUtil.str2Int(paramMap.get("recipientId").toString()) , 1);



        return messageManage.getId();
    }


    /**
     * 根据用户和消息类型获取未读消息列表
     * @param userId
     * @param categoryId
     * @return
     * @throws Exception
     */
    @Override
    public List<MessageManage> getUnreadMsgByUserIdAndCategory (int userId , int categoryId) throws Exception {
        return messageManageMapper.getUnreadMessageByUserIdAndCategoryId(userId , categoryId);
    }


    /**
     * 获取消息类型列表
     * @param categoryId
     * @return
     * @throws Exception
     */
    @Override
    public List<MessageManage> getMsgByCategory(int userId , int categoryId )throws Exception {
        return messageManageMapper.getMsgByUserIdAndCategory(userId,categoryId);
    }



    /**
     * 获取需求的详情和消息详情
     * @param
     * @return
     */
    @Override
    public HashMap<String,Object> getNeedsAndMessage(MessageManage messageManage) throws  Exception{
        HashMap<String,Object> map = new HashMap<>();

        //消息详情
        map.put("messageContent", messageManage.getContent());
        map.put("id", messageManage.getId());
        map.put("categoryId", messageManage.getCategoryId());
        map.put("createTime", messageManage.getCreateTime());
        map.put("senderId", messageManage.getSenderId());
        map.put("isApproved", messageManage.getIsApproved());
        map.put("isWatched", messageManage.getIsWatched());
        map.put("messageContent",messageManage.getContent());
        map.put("messageId",messageManage.getMessageId());
        int needsId = messageManage.getNeedsId();
        map.put("needsId", needsId);
        map.put("recipientId", messageManage.getRecipientId());
        map.put("isReply", messageManage.getIsReply());

        User recipient = userService.getUserDetails(messageManage.getRecipientId());
        map.put("recipientNickName", recipient.getNickName());
        map.put("recipientImage", recipient.getImage());

        User sender = userService.getUserDetails(messageManage.getSenderId());
        map.put("senderNickName", sender.getNickName());
        map.put("senderImage", sender.getImage());

        //需求详情
        NeedsManagement needsManagement = needsManagementMapper.getNeedsDetailsById(needsId);
        map.put("title", needsManagement.getTitle());
        map.put("content", needsManagement.getContent());
        map.put("startTime", needsManagement.getStartTime());
        map.put("deadline", needsManagement.getDeadline());
        map.put("limitNo", needsManagement.getLimitNo());
        map.put("joinNo", needsManagement.getJoinNo());
        map.put("isOverTime",needsManagement.getIsOverTime());
        map.put("needCreateTime",needsManagement.getCreateTime());

        return map;
    }

    /**
     * 根据需求id删除需求的评论回复
     * @param needId
     * @throws Exception
     */
    public void deleteMessageByNeedId (int needId) throws Exception {
        messageManageMapper.deleteMessageByNeedId(needId);
    }


    /**
     * 改变消息未读（未查看）为已读（已查看,并且将查看的消息转移到已读消息缓存redis中
     */
    private void updateIsWatched (int userId, List<HashMap<String,Object>> list) {

        if (list == null || list.size() == 0) return;

        //改变消息是否查看状态，并且将查看的消息转移到已读消息缓存redis中
        for ( HashMap<String,Object> map : list) {
            if (Integer.valueOf(map.get("isWatched").toString()) == 0) {
                //更改未读状态 --> 已读状态
                messageManageMapper.updateIsWatched(Integer.valueOf(map.get("id").toString()));

            }
        }

    }


    /**
     * 是否同意加入
     * @param paramMap
     * @return
     */
    private boolean isApproveJoin (HashMap<String, Object> paramMap) {
        return paramMap.get("content").toString().equals("申请加入");
    }

    /**
     * 判断是否是同意操作
     * @param paramMap
     * @return
     */
    private boolean isApplyApprove (HashMap<String, Object> paramMap) {
        return Integer.valueOf(paramMap.get("isApproved").toString()) == 2;
    }


    /**
     * 判断是否是申请
     * @param paramMap
     * @return
     */
    private boolean isApplyJoin(HashMap<String, Object> paramMap) {
        return paramMap.get("messageId").toString() == null || paramMap.get("messageId").toString().equals("");
    }


    /**
     * 从需求中剔除组队人员
     * @param needsManagement
     * @param joinedUserId
     */
    private void doRemoveUserFromNeed (NeedsManagement needsManagement, int joinedUserId) {

        String joinedUserIds = needsManagement.getJoinUserId();

        if (!joinedUserIds.equals("")) {

            StringBuffer sb = new StringBuffer();

            int joinNo = needsManagement.getJoinNo();

            String[] joinUserIdzz = joinedUserIds.split("#@#");


            for (int index = 0, length = joinUserIdzz.length; index < length; ++index) {
                int joinUserIdd = Integer.valueOf(joinUserIdzz[index]);
                //判断需求队伍是否有这个人
                if (joinUserIdd != joinedUserId) {
                    if (index == length-1) {
                        sb.append(joinUserIdzz[index]);
                    } else {
                        sb.append(joinUserIdzz[index]);
                        sb.append("#@#");
                    }
                } else {
                    joinNo--;
                }
            }

            //判断sb字符串结尾是否是#@#
            String joinUserIdsStr = sb.toString();
            if (joinUserIdsStr.endsWith("#@#")) {
                joinUserIdsStr = joinUserIdsStr.substring(0,joinUserIdsStr.length()-3);
            }

            needsManagement.setJoinUserId(joinUserIdsStr);

            //需求加入人数更新
            needsManagement.setJoinNo(joinNo);

        }

    }

    /**
     * 判断需求是否包含用户
     * @param needsManagement
     * @param userId
     * @return
     */
    private boolean needHasUser(NeedsManagement needsManagement , int userId) {

        String joinedUserIds = needsManagement.getJoinUserId();

        if (!joinedUserIds.equals("")) {
            String[] joinedUserIdzz = joinedUserIds.split("#@#");

            for (String str : joinedUserIdzz) {
                if (StringUtil.str2Int(str) == userId) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 如果申请加入需求队伍被同意，需求参与人数加1
     * 如果人员满，创建人员已满消息
     */
    private void needJoinNoIncrease (int needsId, int joinUserId) throws Exception{
        logger.info("【需求参与人数加1开始】needJoinNoIncrease --> getNeedsDetailsById start");
        NeedsManagement needsManagement  = needsManagementMapper.getNeedsDetailsById(needsId);

        //判断需求组队队伍是否包含申请人
        if (!needHasUser(needsManagement, joinUserId))  {

            if (needsManagement == null) {
                logger.error("【获取需求详情失败】needJoinNoIncrease --> getNeedsDetailsById fail");
                throw new Exception("获取需求失败");
            }else {

                logger.info("【获取需求详情成功】needJoinNoIncrease --> getNeedsDetailsById succeed");

                if (needsManagement.getJoinNo() == needsManagement.getLimitNo()) {
                    logger.info("【需求参与人数已满】needJoinNoIncrease found");
                    MessageManage messageManage = new MessageManage();
                    Date createTime = new Date();
                    messageManage.setCategoryId(4);
                    messageManage.setSenderId(needsManagement.getUserId());

                    User sender = userService.getUserDetails(needsManagement.getUserId());
                    messageManage.setSenderNickName(sender.getNickName());
                    messageManage.setSenderImage(sender.getImage());

                    messageManage.setNeedsId(needsId);
                    messageManage.setRecipientId(needsManagement.getUserId());
                    messageManage.setContent("人员已满");
                    messageManage.setCreateTime(createTime);
                    messageManage.setIsWatched(0);   //未查看为0
                    addOverNumberMessage(messageManage);
                }
                else{
                    needsManagementMapper.riseJoinNO(needsId);
                }
            }
        }
        else {
            logger.error("【同意申请加入失败,申请号者已存在】addApply --> needJoinNoIncrease failed");
            throw new Exception("用户已存在");
        }

    }

    /**
     * 创建人员已满消息
     * @param messageManage
     * @return
     * @throws Exception
     */
    private int addOverNumberMessage (MessageManage messageManage) throws Exception{
        logger.info("【创建人员已满消息开始】addOverNumberMessage start");
        int ret =  messageManageMapper.addOverNumberMessage(messageManage);
        if (ret < 0) logger.error("【创建人员已满消息失败】addOverNumberMessage fail（error）");
        else {

            //更新缓存消息列表
            msgCacheService.refreshUnreadMsgNo(messageManage.getRecipientId(),4);

            //刷新缓存消息列表
            msgCacheService.refreshMsgs(messageManage.getRecipientId() , 4);

            logger.error("【创建人员已满消息成功】addOverNumberMessage succeed.");
        }
        return ret;
    }
}
