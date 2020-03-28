package com.ruider.service;

import com.ruider.mapper.UserCollectionMapper;
import com.ruider.model.NeedsManagement;
import com.ruider.model.UserCollection;

import java.util.HashMap;
import java.util.List;

public interface UserCollectionService {

    int addCollection(HashMap<String,Object> paramMap) throws Exception;

    void deleteCollection(int id) throws Exception;

    void deleteCollectionByNeedId(int needId) throws Exception;

    UserCollection selectCollection(int userId, int needId) throws Exception;

    List<HashMap<String, Object>> selectCollections(int userId) throws Exception;
}
