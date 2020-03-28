package com.ruider.service.impl;

import com.ruider.mapper.NeedsManagementMapper;
import com.ruider.model.MessageManage;
import com.ruider.model.NeedsManagement;
import com.ruider.model.User;
import com.ruider.model.UserTeam;
import com.ruider.service.*;
import com.ruider.utils.cache.KeyWordsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 需求管理中心，负责将需求保存在缓存中
 * Created by mahede on 2018/12/2.
 */
@Service
@EnableScheduling
public class NeedsManagementServiceImpl implements NeedsManagementService {

    Logger logger = LoggerFactory.getLogger(NeedsManagementServiceImpl.class);

    @Autowired
    NeedsManagementMapper needsManagementMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageManageService messageManageService;

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private MsgCacheService msgCacheService;

    //需求缓存
    //private ConcurrentHashMap<String,Object> needsCache = new ConcurrentHashMap<>();

    /**
     * 判断需求主人是否参与需求队伍
     * @param needId
     * @return
     */
    @Override
    public int getisUserJoined (int needId) throws Exception{
        NeedsManagement needsManagement = getNeedsDetailsById(needId);
        int userId = needsManagement.getUserId();
        if (userJoinNeed(userId , needId)) {
            return 1;
        }
        return 0;
    }


    /**
     * 需求主人删除指定的队员
     * 需求加入人数减1
     * @param needId
     * @param joinedUserId
     */
    @Override
    public void eliminateJoinUser (int needId, int joinedUserId) throws Exception{
        NeedsManagement needsManagement = getNeedsDetailsById(needId);
        String joinUserIds = needsManagement.getJoinUserId();
        if (!joinUserIds.equals("")) {
            StringBuffer sb = new StringBuffer();
            int joinNo = needsManagement.getJoinNo();
            String[] joinUserIdzz = joinUserIds.split("#@#");
            for (int index = 0, length = joinUserIdzz.length; index < length ; ++index) {
                int joinUserIdd = Integer.valueOf(joinUserIdzz[index]);
                //判断需求队伍是否有这个人
                if (joinUserIdd != joinedUserId) {
                    if (index == length-1) {
                        sb.append(joinUserIdzz[index]);
                    }
                    else {
                        sb.append(joinUserIdzz[index]);
                        sb.append("#@#");
                    }
                }
                else {
                    joinNo--;
                }
            }

            //判断sb字符串结尾是否是#@#
            String joinUserIdsStr = sb.toString();
            if (joinUserIdsStr.endsWith("#@#")) {
                joinUserIdsStr = joinUserIdsStr.substring(0,joinUserIdsStr.length()-3);
            }
            needsManagement.setJoinUserId(joinUserIdsStr);
            //需求加入人数减1
            needsManagement.setJoinNo(joinNo);
            //生成剔除人员消息
            MessageManage messageManage = new MessageManage();
            messageManage.setCategoryId(5);
            messageManage.setSenderId(needsManagement.getUserId());
            messageManage.setNeedsId(needId);
            messageManage.setCreateTime(new Date());
            messageManage.setContent("从队伍移除您");
            messageManage.setRecipientId(joinedUserId);
            messageManage.setIsWatched(0);
            messageManageService.addMessage(messageManage);

            //从个人组队信息记录表userTeam表删除组队信息
            userTeamService.deleteUserTeamByNeedIdAndUserId(needId,joinedUserId);

            //更新需求队员信息
            needsManagementMapper.updateNeedsManage(needsManagement);

            //更新被剔除人员消息缓存列表
            msgCacheService.refreshUnreadMsgNo(joinedUserId,5);

            //刷新缓存消息列表
            msgCacheService.refreshMsgs(needsManagement.getUserId() , 5);
        }
    }

    /**
     * 根据用户id获取用户加入的队伍需求
     * @param userId
     * @return
     * @throws Exception
     */
    public List<NeedsManagement> getMyJoinedNeeds (int userId) throws Exception {
        List<NeedsManagement> result = new ArrayList<>();
        List<UserTeam> userTeams = userTeamService.selectUserTeamsByUserId(userId);
        for (UserTeam userTeam : userTeams) {
            int needsId = userTeam.getNeedId();

            NeedsManagement needsManagement = getNeedsDetailsById(needsId);

            result.add(needsManagement);
        }
        return result;
    }

    /**
     * 根据用户id获取用户正在组队的需求列表
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public List<NeedsManagement> getTeamingNeeds(int userId) throws Exception {

        List<NeedsManagement> list = needsManagementMapper.checkOverTimeNeeds(userId);

        return list;
    }

    /**
     * 根据关键字keyWords获取需求内容
     * 对于特殊keyWords像需求类型名称等做处理
     * @param keyWords
     * @return
     * @throws Exception
     */
    @Override
    public List<NeedsManagement> getKeyWordsContent(String keyWords, String typeName) throws Exception {
        List<NeedsManagement> list = new ArrayList<>();

        //按照搜索类型分类搜索
        if (typeName.equals("category")) {
            //对于特殊keyWords像需求类型名称等做处理
            if (KeyWordsCache.contains(keyWords)) {
                Integer categoryId = KeyWordsCache.get(keyWords);
                //一级类型查询
                if(categoryId.toString().length() == 1) {
                    list = needsManagementMapper.getKeyWordsContentByStypeId(categoryId);
                }
                //二级类型
                else {
                    list = needsManagementMapper.getKeyWordsContentByCategoryId(categoryId);
                }
            }
        }
        else if (typeName.equals("title")){
            list = needsManagementMapper.getKeyWordsByTitle(keyWords);
        }
        else if (typeName.equals("content")){
            list = needsManagementMapper.getKeyWordsByContent(keyWords);
        }
        else if (typeName.equals("time")){
            list = needsManagementMapper.getKeyWordsByTime(keyWords);
        }

        return list;
    }

    /**
     * 根据需求id获取需求详情，需求内容包含用户信息，需求信息，以及当前需求组队队员信息
     * @param needId
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, Object> getNeedAndJoinUsers(int needId) throws Exception{
        HashMap<String,Object> map = new HashMap<>();
        NeedsManagement needsManagement = needsManagementMapper.getNeedsDetailsById(needId);

        //列表添加需求信息
        map.put("needInfo",needsManagement);

        //添加组队队员信息
        String joinUsers = needsManagement.getJoinUserId();
        if (!joinUsers.equals("")) {
            String[] joinUserZZ = joinUsers.split("#@#");
            List<User> joinUsersList = new ArrayList<>();
            for (String str : joinUserZZ) {

                User joinUser = userService.getUserDetails(Integer.valueOf(str));
                joinUsersList.add(joinUser);
            }
            map.put("joinUsersInfo",joinUsersList);
        }

        return map;
    }


    /**
     * 根据用户id获取需求列表，需求列表内容包含用户信息，需求信息，以及当前需求组队队员信息
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public List<NeedsManagement> getNeedsByUserId(int userId) throws Exception{

        List<NeedsManagement> list = needsManagementMapper.getNeedsByUserId(userId);

        return list;
    }

    @Override
    public List<NeedsManagement> getNeedsByUserIdAndCategoryId(int userId, int categotyId) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<NeedsManagement> list = needsManagementMapper.getNeedsByUserIdAndCategoryId(userId, categotyId);
        return list;
    }

    @Override
     public void addNeeds(HashMap<String,Object> paramMap) throws Exception {
         NeedsManagement needsManagement = new NeedsManagement();
         int needsTypeId = Integer.valueOf(paramMap.get("needsTypeId").toString()) + 1;
         int categoryId = needsTypeId*10 + Integer.valueOf(paramMap.get("categoryId").toString()) +1;
         needsManagement.setCategoryId(categoryId);
         needsManagement.setStypeId(needsTypeId);
         needsManagement.setUserId(Integer.valueOf(paramMap.get("userId").toString()));
         needsManagement.setTitle(paramMap.get("title").toString());
         needsManagement.setContent(paramMap.get("content").toString());
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
         needsManagement.setStartTime(simpleDateFormat.parse(paramMap.get("startTime").toString()));
         needsManagement.setDeadline(simpleDateFormat.parse(paramMap.get("deadline").toString()));
         needsManagement.setQq(paramMap.get("qq").toString());
         needsManagement.setWeChat(paramMap.get("weChat").toString());
         //needsManagement.setPhoneNo(paramMap.get("phoneNo").toString());
         needsManagement.setLimitNo(Integer.valueOf(paramMap.get("limitNo").toString()));
         needsManagement.setCreateTime(new Date());
         needsManagement.setUpdateTime(new Date());
         //判断个人是否参加
         if (Integer.valueOf(paramMap.get("userJoined").toString()) == 1) {
             needsManagement.setJoinUserId(paramMap.get("userId").toString());
             needsManagement.setJoinNo(1);
         }
         else {
             needsManagement.setJoinUserId("");
             needsManagement.setJoinNo(0);
         }
         needsManagementMapper.addNeeds(needsManagement);

         //用户加入需求记录到userTeam
        //判断个人是否参加
        if (Integer.valueOf(paramMap.get("userJoined").toString()) == 1) {
            int userId = Integer.valueOf(paramMap.get("userId").toString());
            UserTeam userteam = new UserTeam();
            userteam.setUserId(userId);
            userteam.setNeedId(needsManagement.getId());
            Date createTime = new Date();
            userteam.setCreateTime(createTime);
            userTeamService.addUserTeam(userteam);
        }

    }

    /**
     * 删除需求，同时删除需求的评论回复，以及需求被收藏的记录
     * @param id
     * @throws Exception
     */
    @Override
    public void deleteNeeds(int id) throws Exception {
        //删除被收藏记录
        userCollectionService.deleteCollectionByNeedId(id);
        //删除需求评论回复
        messageManageService.deleteMessageByNeedId(id);
        //删除需求加入的所有人员信息
        userTeamService.deleteUserTeamByNeedId(id);
        //删除需求
        needsManagementMapper.deleteNeeds(id);

    }

    /**
     * 新增申请加入人员，根据需求id更改组队人员joinUserId属性
     * @param joinUserId
     * @param needId
     * @throws Exception
     */
    @Override
    public int updateJoinUserId(int joinUserId, int needId) throws Exception{

        NeedsManagement needsManagement = getNeedsDetailsById(needId);

        if (needsManagement != null) {
            String userIds = needsManagement.getJoinUserId();
            if (userIds == null || userIds.equals("")) {
                userIds = String.valueOf(joinUserId);
            }
            else {
                userIds += "#@#" + String.valueOf(joinUserId);
            }

            needsManagement.setJoinUserId(userIds);
            return needsManagementMapper.updateNeedsManage(needsManagement);
        }
        return 0;
    }

    /**
     * 根据需求id获取需求详情
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public NeedsManagement getNeedsDetailsById(int id) throws Exception {
        NeedsManagement needsManagement = new NeedsManagement();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        needsManagement = needsManagementMapper.getNeedsDetailsById(id);
        needsManagement.setStartTime(simpleDateFormat.parse(simpleDateFormat.format(needsManagement.getStartTime())));
        needsManagement.setDeadline(simpleDateFormat.parse(simpleDateFormat.format(needsManagement.getDeadline())));

        return needsManagement;
    }

    /**
     * 编辑需求
     * @param paramMap
     * @throws Exception
     */
    @Override
    public void editNeeds(HashMap<String,Object> paramMap) throws Exception{

        int needId = Integer.valueOf(paramMap.get("id").toString());

        int userId = Integer.valueOf(paramMap.get("userId").toString());

        NeedsManagement needsManagement = getNeedsDetailsById(needId);

        int needsTypeId = Integer.valueOf(paramMap.get("needsTypeId").toString()) + 1;

        int categoryId = needsTypeId*10 + Integer.valueOf(paramMap.get("categoryId").toString()) +1;

        needsManagement.setCategoryId(categoryId);

        needsManagement.setStypeId(needsTypeId);

        needsManagement.setUserId(userId);

        needsManagement.setTitle(paramMap.get("title").toString());

        needsManagement.setContent(paramMap.get("content").toString());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        needsManagement.setStartTime(simpleDateFormat.parse(paramMap.get("startTime").toString()));

        needsManagement.setDeadline(simpleDateFormat.parse(paramMap.get("deadline").toString()));

        needsManagement.setQq(paramMap.get("qq").toString());

        needsManagement.setWeChat(paramMap.get("weChat").toString());

        //needsManagement.setPhoneNo(paramMap.get("phoneNo").toString());

        needsManagement.setLimitNo(Integer.valueOf(paramMap.get("limitNo").toString()));

        needsManagement.setCreateTime(new Date());

        needsManagement.setUpdateTime(new Date());


        //判断个人是否参加
        if (Integer.valueOf(paramMap.get("userJoined").toString()) == 1) {

            int joinNo = needsManagement.getJoinNo();

            //当前用户不参与，需求添加该用户，需求参与人数+1，个人参与需求表userTeam添加数据
            if (!userJoinNeed(userId,needId)) {

                StringBuffer sb = new StringBuffer(needsManagement.getJoinUserId());

                if (needsManagement.getJoinUserId().equals("")) {
                    sb.append(userId);
                }
                else {
                    sb.append("#@#"+userId);
                }

                needsManagement.setJoinUserId(sb.toString());

                needsManagement.setJoinNo(joinNo+1);

                UserTeam userTeam = new UserTeam();

                userTeam.setUserId(userId);

                userTeam.setNeedId(needId);

                userTeam.setCreateTime(new Date());

                userTeamService.addUserTeam(userTeam);
            }

        }
        //编辑不参与该需求
        else {

            int joinNo = needsManagement.getJoinNo();

            //当前用户已经参与，需求删除该用户，需求参与人数-1，个人参与需求表userTeam删除数据
            if (userJoinNeed(userId,needId)) {
                StringBuffer sb = new StringBuffer();
                String joinUserIds = needsManagement.getJoinUserId();
                String[] joinUserIdzz = joinUserIds.split("#@#");
                for (int index = 0, length = joinUserIdzz.length; index < length ; ++index) {
                    int joinUserIdd = Integer.valueOf(joinUserIdzz[index]);
                    //判断需求队伍是否有这个人
                    if (joinUserIdd != userId) {
                        if (index == length-1) {
                            sb.append(joinUserIdzz[index]);
                        }
                        else {
                            sb.append(joinUserIdzz[index]);
                            sb.append("#@#");
                        }
                    }
                    else {
                        --joinNo;
                    }
                }

                //判断sb字符串结尾是否是#@#
                String joinUserIdsStr = sb.toString();
                if (joinUserIdsStr.endsWith("#@#")) {
                    joinUserIdsStr = joinUserIdsStr.substring(0,joinUserIdsStr.length()-3);
                }

                needsManagement.setJoinUserId(joinUserIdsStr);
                needsManagement.setJoinNo(joinNo);
                //userTeam表删除该用户数据
                userTeamService.deleteUserTeamByNeedIdAndUserId(needId,userId);
            }
        }
        //更改需求
        needsManagementMapper.updateNeedsManage(needsManagement);
    }

    /**
     * 检测用户是否参与需求组队
     * @param userId
     * @param needId
     * @return
     */
    private boolean userJoinNeed (int userId , int needId) throws Exception{
        boolean hasUser = false;
        NeedsManagement needsManagement = getNeedsDetailsById(needId);
        String joinedUserIds = needsManagement.getJoinUserId();
        if (!joinedUserIds.equals("")) {
            String[] joinUserIdzz = joinedUserIds.split("#@#");
            for (String str : joinUserIdzz) {
                int joinUserIdd = Integer.valueOf(str);
                //判断需求队伍是否有这个人
                if (joinUserIdd == userId) {
                    hasUser = true;
                }
            }
        }
        return hasUser;
    }

    @Override
    public List<NeedsManagement> getNeedsByCategoryId(int categotyId) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<NeedsManagement> list = needsManagementMapper.getNeedsByCategoryId(categotyId);
        for(int i = 0; i<list.size(); i++) {
            NeedsManagement needsManagement = list.get(i);

            if(needsManagement.getIsOverTime() == 0) {
                if (needsManagement.getDeadline().compareTo(new Date()) < 0 ) {
                    needsManagement.setIsOverTime(1);
                    needsManagementMapper.updateIsOverTime(needsManagement.getId());

                    list.set(i, needsManagement);
                }
            }
            needsManagement.setStartTime(simpleDateFormat.parse(simpleDateFormat.format(needsManagement.getStartTime())));
            needsManagement.setDeadline(simpleDateFormat.parse(simpleDateFormat.format(needsManagement.getDeadline())));
        }
        return list;
    }

    /**
     * 根据需求id获取需求详情，包括需求信息，需求的发布者信息
     * @param needId
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, Object> getNeed(int needId) throws Exception{
        HashMap<String, Object> result = new HashMap<>();
        NeedsManagement needsManagement = needsManagementMapper.getNeedsDetailsById(needId);
        User user = userService.getUserDetails(needsManagement.getUserId());
        result.put("needDetails", needsManagement);
        result.put("user", user);
        return result;
    }


    /**
     * 检查需求是否过期，如果过期添加过期消息，并且更新需求主人的未读消息缓存列表
     * @param needsManagement
     * @throws Exception
     */
    @Override
    public void checkOverTime (NeedsManagement needsManagement ) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date now = new Date();

        String nowStr = simpleDateFormat.format(now);

        String deadlineStr = simpleDateFormat.format(needsManagement.getDeadline());

        //判断需求过期标志isOverTime,1代表过期，0代表未过期
        if (needsManagement.getIsOverTime() == 0 && simpleDateFormat.parse(deadlineStr).getTime() < simpleDateFormat.parse(nowStr).getTime()) { //需求过期了,创建过期消息

            logger.info("【检查用户未过期的需求过期,过期需求title：" + needsManagement.getTitle() + "】checkOverTime");

            //创建过期消息
            MessageManage messageManage = new MessageManage();

            messageManage.setCategoryId(3);

            messageManage.setNeedsId(needsManagement.getId());

            messageManage.setRecipientId(needsManagement.getUserId());

            messageManage.setSenderId(needsManagement.getUserId());

            User sender = userService.getUserDetails(needsManagement.getUserId());

            messageManage.setSenderNickName(sender.getNickName());

            messageManage.setSenderImage(sender.getImage());

            messageManage.setContent("需求过期");

            messageManage.setIsWatched(0);

            messageManage.setCreateTime(new Date());

            logger.info("【添加用户未过期的需求过期消息,过期需求title：" + needsManagement.getTitle() + "】checkOverTime --> addOverTimeMessage");
            int ret = messageManageService.addMessage(messageManage);

            if (ret < 0) {
                logger.info("【添加用户未过期的需求过期消息,过期需求title：" + needsManagement.getTitle() + "失败】checkOverTime --> addMessage fail");
            }else {
                logger.info("【添加用户未过期的需求过期消息,过期需求title：" + needsManagement.getTitle() + "成功】checkOverTime --> addMessage succeed");
                //过期消息创建成功，改变原需求的过期状态
                logger.info("【改变需求过期状态,过期需求title：" + needsManagement.getTitle() + "开始】checkOverTime --> updateIsOverTime succeed");
                needsManagementMapper.updateIsOverTime(needsManagement.getId());
                logger.info("【改变需求过期状态,过期需求title：" + needsManagement.getTitle() + "成功】checkOverTime --> updateIsOverTime succeed");
            }
        }
    }

    /**
     * 根据需求类型id获取需求列表以及发布者信息
     */
    private List<HashMap<String, Object>> getNeedsAndUserInfo (int needTypeId) throws Exception{

        List<HashMap<String, Object>> result = new ArrayList<>();

        List<NeedsManagement> list = needsManagementMapper.getNeedsAndUserInfo(needTypeId);

        for(NeedsManagement needsManagement : list) {

            HashMap<String, Object> map = new HashMap<>();

            int userId = needsManagement.getUserId();

            map.put("user", userService.getUserDetails(userId));

            map.put("needDetails", needsManagement);

            result.add(map);
        }
        return result;
    }

    /**
     * 将需求缓存，按照需求类型分类，分别为日常，娱乐，驾校，旅游，拼车，学习，体育运动，其他
     * 每50毫秒钟执行一次
     */
    /*@Scheduled(fixedDelay = 1000)
    public void addNeeds2Cache() {
        logger.info("【addNeeds2Cache start】每50毫秒将需求刷新添加到缓存中开始");
        try {
            needsCache.put("dailyNeeds",getNeedsAndUserInfo(1));
            needsCache.put("entertainmentNeeds",getNeedsAndUserInfo(2));
            needsCache.put("drivingSchoolNeeds",getNeedsAndUserInfo(3));
            needsCache.put("tourismNeeds",getNeedsAndUserInfo(4));
            needsCache.put("carpoolNeeds",getNeedsAndUserInfo(5));
            needsCache.put("studyNeeds",getNeedsAndUserInfo(6));
            needsCache.put("sportsNeeds",getNeedsAndUserInfo(7));
            needsCache.put("otherNeeds",getNeedsAndUserInfo(8));
            logger.info("【addNeeds2Cache succeed】每50毫秒将需求刷新添加到缓存中成功");
        }
        catch (Exception e) {
            logger.error("addNeeds2Cache failed】每50毫秒将需求刷新添加到缓存中失败", e);
        }
    }*/

    /**
     * 提供用户访问接口，根据需求类型id获取数据
     * @param needTypeId
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap<String, Object>> getNeedInfoByNeedTypeId (int needTypeId) throws Exception {

        return getNeedsAndUserInfo(needTypeId);

        /*if (needTypeId == 1) return getNeedsAndUserInfo(1);
        else if(needTypeId == 2) return (List<HashMap<String, Object>>)needsCache.get("entertainmentNeeds");
        else if(needTypeId == 3) return (List<HashMap<String, Object>>)needsCache.get("drivingSchoolNeeds");
        else if(needTypeId == 4) return (List<HashMap<String, Object>>)needsCache.get("tourismNeeds");
        else if(needTypeId == 5) return (List<HashMap<String, Object>>)needsCache.get("carpoolNeeds");
        else if(needTypeId == 6) return (List<HashMap<String, Object>>)needsCache.get("studyNeeds");
        else if(needTypeId == 7) return (List<HashMap<String, Object>>)needsCache.get("sportsNeeds");
        else if(needTypeId == 8) return (List<HashMap<String, Object>>)needsCache.get("otherNeeds");
        else return null;*/
    }




}
