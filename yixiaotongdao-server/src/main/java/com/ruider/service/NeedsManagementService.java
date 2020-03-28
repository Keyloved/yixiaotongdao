package com.ruider.service;

import com.ruider.model.NeedsManagement;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahede on 2018/12/2.
 */
public interface NeedsManagementService {

    List<NeedsManagement> getNeedsByUserId(int userId) throws Exception;

    List<NeedsManagement> getNeedsByUserIdAndCategoryId(int userId, int categotyId) throws Exception;

    void addNeeds(HashMap<String,Object> paramMap) throws Exception;

    void deleteNeeds(int id) throws Exception;

    NeedsManagement getNeedsDetailsById(int id) throws Exception;

    void editNeeds(HashMap<String,Object> paramMap) throws Exception;

    List<NeedsManagement> getNeedsByCategoryId(int categotyId) throws Exception;

    HashMap<String, Object> getNeed(int needId) throws Exception;

    List<HashMap<String, Object>> getNeedInfoByNeedTypeId (int needTypeId) throws Exception;

    int updateJoinUserId(int joinUserId, int needId) throws Exception;

    HashMap<String, Object> getNeedAndJoinUsers(int needId) throws Exception;

    List<NeedsManagement> getKeyWordsContent(String keyWords, String typeName) throws Exception;

    List<NeedsManagement> getTeamingNeeds(int userId) throws Exception;

    List<NeedsManagement> getMyJoinedNeeds (int userId) throws Exception;

    void eliminateJoinUser (int needId, int joinedUserId) throws Exception;

    int getisUserJoined (int needId) throws Exception;

    /**
     * 检查需求是否过期
     * @param needsManagement
     * @throws Exception
     */
    void checkOverTime (NeedsManagement needsManagement ) throws Exception;

}
