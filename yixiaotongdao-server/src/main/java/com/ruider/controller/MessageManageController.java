package com.ruider.controller;

import com.ruider.common.Result;
import com.ruider.scheduler.MsgScheduler;
import com.ruider.service.MessageManageService;
import com.ruider.service.NeedsManagementService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("MessageManage")
public class MessageManageController {

    private final Logger logger = LoggerFactory.getLogger(MessageManageController.class);

    @Autowired
    private MessageManageService messageManageService;

    @Autowired
    private NeedsManagementService needsManagementService;

    @Autowired
    private MsgScheduler msgScheduler;

    /*
     * 添加评论与回复
     */
    @PostMapping("addEvaluate")
    public Result addEvaluate(@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【新增评论/回复开始】 addEvaluate start");
            int count = messageManageService.addEvaluate(paramMap);
            if(count <= 0) {
                logger.info("【新增评论/回复失败】 addEvaluate fail");
                result.setMessage("发表失败");
                result.setCode(Result.FAIL_CODE);
                result.setIsSuccess(false);
            }else {
                logger.info("【新增评论/回复成功】 addEvaluate success");
                result.setMessage("发表成功");
                result.setIsSuccess(true);
            }
            return result;
        }
        catch (Exception e) {
            logger.error("【新增评论/回复失败】 addEvaluate fail", e);
            result.setMessage("发表失败");
            result.setCode(Result.EXCEPTION_CODE);
            result.setIsSuccess(false);
            return result;
        }
    }

    /*
     * 添加申请加入/退出
     */
    @PostMapping("addApply")
    public Result addApply(@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【添加申请加入/退出】 addApply start");
            int count = messageManageService.addApply(paramMap);
            if(count < 0) {
                logger.info("【添加申请加入/退出失败】 addApply fail");
                result.setMessage("申请失败");
                result.setCode(Result.FAIL_CODE);
                result.setIsSuccess(false);
            }else {
                logger.info("【添加申请加入/退出成功】 addApply success");
                result.setMessage("申请成功");
                result.setIsSuccess(true);
            }
            return result;
        }
        catch (Exception e) {
            logger.error("【添加申请加入/退出失败】 addApply fail", e);
            result.setMessage("申请失败");
            result.setCode(Result.EXCEPTION_CODE);
            result.setIsSuccess(false);
            return result;
        }
    }

    /*
     * 根据需求id和消息类型id获取未查看的信息，并且设置查看状态为查看状态
     */
    @GetMapping("getMessageList")
    public Result getMessageList(@Param("userId") String userId, @Param("categoryId") String categoryId, @Param("isReply") String isReply) {
        Result result = new Result();
        try{
            logger.info("【根据需求id和消息类型id获取未查看的信息，并且设置查看状态为查看状态】 getMessageList start");
            List list = messageManageService.getUnreadMessageList(Integer.valueOf(userId), Integer.valueOf(categoryId));
            result.setIsSuccess(true);
            result.setMessage("获取成功");
            result.setData(list);
            logger.info("【根据需求id和消息类型id获取未查看的信息，并且设置查看状态为查看状态成功】 getMessageList success");
            return result;
        }
        catch(Exception e) {
            result.setIsSuccess(false);
            result.setMessage("获取失败");
            result.setCode(Result.EXCEPTION_CODE);
            logger.error("【根据需求id和消息类型id获取未查看的信息，并且设置查看状态为查看状态失败】 getMessageList fail",e);
            return result;
        }
    }

    /**
     * 获取需求评论回复具体详情
     * @param needId
     * @return
     */
    @GetMapping("getNeedMessageDetails")
    public Result getNeedMessageDetails(@Param("needId") int needId) {
        Result result = new Result();
        try{
            logger.info("【获取需求信息，需求评论回复具体详情开始】 getNeedMessageDetails start");
            List map = messageManageService.getNeedMessageDetails(needId);
            result.setIsSuccess(true);
            result.setMessage("获取成功");
            result.setData(map);
            logger.info("【获取需求信息，需求评论回复具体详情成功】 getNeedMessageDetails success");
            return result;
        }
        catch(Exception e) {
            result.setIsSuccess(false);
            result.setMessage("获取失败");
            result.setCode(Result.EXCEPTION_CODE);
            logger.error("【获取需求信息，需求评论回复具体详情失败】 getNeedMessageDetails fail",e);
            return result;
        }
    }

    /**
     * 获取未被查看的消息数量
     */
    @GetMapping("getNoWatchedMessageNo")
    public Result getNoWatchedMessageNo(@Param("userId") int userId) {
        Result result = new Result();
        try{
            logger.info("【获取未被查看的消息数量】 getNoWatchedMessageNo start");
            HashMap map = messageManageService.getUnreadMessageNum(userId);
            result.setIsSuccess(true);
            result.setMessage("获取成功");
            result.setData(map);
            logger.info("【获取未被查看的消息数量成功】 getNoWatchedMessageNo success");
            return result;
        }
        catch(Exception e) {
            result.setIsSuccess(false);
            result.setMessage("获取失败");
            result.setCode(Result.EXCEPTION_CODE);
            logger.error("【获取未被查看的消息数量失败】 getNoWatchedMessageNo fail",e);
            return result;
        }
    }

    /**
     * 申请消息是否被同意或者拒绝
     * @param paramMap
     * @return
     */
    @PostMapping("setIsApproved")
    public Result setIsApproved(@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try {
            logger.info("【申请消息是否被同意或者拒绝开始】 setIsApproved start");
            int count = messageManageService.addApply(paramMap);
            if(count <= 0) {
                logger.info("【申请消息是否被同意或者拒绝失败】 setIsApproved fail");
                result.setCode(Result.FAIL_CODE);
                result.setMessage("操作失败");
                result.setIsSuccess(false);
            }
            else {
                logger.info("【申请消息是否被同意或者拒绝成功】 setIsApproved success");
                result.setMessage("操作成功");
                result.setIsSuccess(true);
            }
        }
        catch (Exception e) {
            logger.error("【申请消息是否被同意或者拒绝失败】 setIsApproved fail",e);
            result.setCode(Result.EXCEPTION_CODE);
            result.setMessage("操作失败");
            result.setIsSuccess(false);
        }
        return result;
    }
}
