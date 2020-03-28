package com.ruider.controller;

import com.ruider.common.Result;
import com.ruider.service.LeaveingManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("leaveingManage")
public class LeaveingManageController {

    private Logger logger = LoggerFactory.getLogger(LeaveingManageController.class);

    @Autowired
    private LeaveingManageService leaveingManageService;


    /**
     * 添加留言
     * @param paramMap
     * @return
     */
    @PostMapping("addLeaveingManage")
    public Result addLeaveingManage (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【添加留言开始】addLeaveingManage start");
            leaveingManageService.addLeaveingManage(paramMap);
            result.setIsSuccess(true);
            result.setMessage("添加成功");
            logger.info("【添加留言成功】addLeaveingManage success");
            return result;
        }catch (Exception e) {
            logger.error("【添加留言失败】addLeaveingManage fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("添加失败");
            return result;
        }
    }


    /**
     * 获取所有留言
     * @param
     * @return
     */
    @GetMapping("getAllLeaveingManage")
    public Result getAllLeaveingManage (@RequestParam int categoryId) {
        Result result = new Result();
        try{
            logger.info("【获取所有留言开始】getAllLeaveingManage start");
            result.setData(leaveingManageService.getAllLeaveingManage(categoryId));
            result.setIsSuccess(true);
            result.setMessage("获取成功");
            logger.info("【获取所有留言成功】getAllLeaveingManage success");
            return result;
        }catch (Exception e) {
            logger.error("【获取所有留言失败】getAllLeaveingManage fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("获取失败");
            return result;
        }
    }

    /**
     * 写给未来的ta
     * @param paramMap
     * @return
     */
    @PostMapping("writeToFuture")
    public Result writeToFuture (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【写给未来的自己开始】writeToFuture start");
            leaveingManageService.writeToFuture(paramMap);
            result.setIsSuccess(true);
            result.setMessage("时间到，心意到");
            logger.info("【写给未来的自己成功】writeToFuture success");
            return result;
        }catch (Exception e) {
            logger.error("【写给未来的自己失败】writeToFuture fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("写给未来的自己失败");
            return result;
        }
    }



}
