package com.ruider.service;

import com.ruider.model.UserTeam;

import java.util.List;

public interface UserTeamService {

    int addUserTeam (UserTeam userTeam) throws Exception;

    void deleteUserTeamByNeedIdAndUserId(int needId , int userId) throws Exception;

    void deleteUserTeamByNeedId(int needId) throws Exception;

    List<UserTeam> selectUserTeamsByUserId( int userId) throws Exception;
}
