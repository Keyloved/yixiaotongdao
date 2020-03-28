package com.ruider.service;

import com.ruider.model.LeaveingManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface LeaveingManageService {

    int addLeaveingManage (HashMap<String,Object> paramMap) throws Exception;

    ArrayList<HashMap<String,Object>> getAllLeaveingManage(int categoryId) throws Exception;

    int writeToFuture(HashMap<String,Object> paramMap) throws Exception;
}
