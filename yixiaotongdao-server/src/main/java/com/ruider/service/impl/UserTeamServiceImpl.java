package com.ruider.service.impl;

import com.ruider.mapper.UserTeamMapper;
import com.ruider.model.UserTeam;
import com.ruider.service.UserTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTeamServiceImpl implements UserTeamService {

    @Autowired
    private UserTeamMapper userTeamMapper;

    /**
     * 添加新的个人组队信息
     * @param userTeam
     * @return
     * @throws Exception
     */
    @Override
    public int addUserTeam (UserTeam userTeam) throws Exception {
        if (userTeamMapper.selectUserTeam(userTeam.getUserId(),userTeam.getNeedId()) == null) {
            return userTeamMapper.addUserTeam(userTeam);
        }
        return 0;
    }


    /**
     * 根据需求的id删除个人组队信息
     * @param needId
     * @throws Exception
     */
    @Override
    public void deleteUserTeamByNeedId(int needId) throws Exception{
        userTeamMapper.deleteUserTeamByNeedId(needId);
    }

    /**
     * 根据需求id和用户id删除信息
     * @param needId
     * @param userId
     * @throws Exception
     */
    @Override
    public void deleteUserTeamByNeedIdAndUserId(int needId , int userId) throws Exception{
        userTeamMapper.deleteUserTeamByNeedIdAndUserId(needId, userId);
    }

    /**
     * 根据用户id获取组队信息
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public List<UserTeam> selectUserTeamsByUserId(int userId) throws Exception {
        return userTeamMapper.selectUserTeams(userId);
    }

}
