package com.ruider.service.impl;

import com.ruider.mapper.UserCollectionMapper;
import com.ruider.model.NeedsManagement;
import com.ruider.model.User;
import com.ruider.model.UserCollection;
import com.ruider.service.NeedsManagementService;
import com.ruider.service.UserCollectionService;
import com.ruider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 用户收藏表操作类
 */
@Service
public class UserCollectionServiceImpl implements UserCollectionService {

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Autowired
    private NeedsManagementService needsManagementService;

    @Autowired
    private UserService userService;

    @Override
    public int addCollection(HashMap<String,Object> paramMap) throws Exception {
        int needsId = Integer.valueOf(paramMap.get("needsId").toString());
        int userId = Integer.valueOf(paramMap.get("userId").toString());

        if (selectCollection(userId,needsId) == null) {
            UserCollection userCollection = new UserCollection();
            userCollection.setUserId(userId);
            userCollection.setNeedId(needsId);
            userCollection.setCreateTime(new Date());
            return userCollectionMapper.addCollection(userCollection);
        }
        return -1;  //-2代表已经收藏
    }


    /**
     * 根据收藏id删除收藏
     * @param id
     * @throws Exception
     */
    @Override
    public void deleteCollection(int id) throws Exception{
        userCollectionMapper.deleteCollection(id);
    }

    /**
     * 根据需求id删除收藏
     * @param needId
     */
    public void deleteCollectionByNeedId(int needId) throws Exception {
        userCollectionMapper.deleteCollectionByNeedId(needId);
    }

    /**
     * 根据收藏id获取收藏需求的详细信息，包括需求的信息，需求发布者信息
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public UserCollection selectCollection(int userId, int needId) throws Exception{
        return userCollectionMapper.selectCollection(userId,needId);
    }

    /**
     * 根据用户id获取用户收藏的所有需求信息
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap<String, Object>> selectCollections(int userId) throws Exception {
        List<HashMap<String, Object>> result = new ArrayList<>();
        ArrayList<UserCollection> list = (ArrayList<UserCollection>) userCollectionMapper.selectUserCollections(userId);
        for (UserCollection userCollection : list) {
            HashMap<String, Object> map = new HashMap<>();

            //查询被收藏需求的信息
            int needId = userCollection.getNeedId();
            NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(needId);
            if (needsManagement != null) {
                //添加用户收藏信息
                map.put("userCollection",userCollection);
                //添加需求信息
                map.put("needDetails", needsManagement);
                //添加需求组队队员信息
                String joinUsers = needsManagement.getJoinUserId();
                if (!joinUsers.equals("")) {
                    String[] joinUserZZ = joinUsers.split("#@#");
                    List<User> joinUsersList = new ArrayList<>();
                    for (String str : joinUserZZ) {
                        User joinUser = userService.getUserDetails(Integer.valueOf(str));
                        joinUsersList.add(joinUser);
                    }
                    map.put("joinUserInfo",joinUsersList);
                }
                //添加需求的创建者信息
                map.put("needCreater",userService.getUserDetails(needsManagement.getUserId()));
            }
            //需求被删,该收藏可以被删了
            else {
                deleteCollection(userCollection.getId());
            }

            result.add(map);
        }
        return result;
    }
}
