package com.ruider.controller;

import com.ruider.common.Result;
import com.ruider.model.NeedsManagement;
import com.ruider.service.NeedsManagementService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.HashMap;

/**
 * Created by mahede on 2018/12/2.
 */
@RestController
@RequestMapping("needsManagement")
public class NeedsManagementController {

    private final Logger logger = LoggerFactory.getLogger(NeedsManagementController.class);

    @Autowired
    private NeedsManagementService  needsManagementService;

    @RequestMapping("test")
    public String test() {
        return "test";
    }

    /**
     * 获取需求详情，详情内容包含需求信息，以及需求组队队员的信息
     * @param needId
     * @return
     */
    @GetMapping("getNeedAndJoinUsers")
    public Result getNeedAndJoinUsers(@RequestParam("needId") int needId) {
        Result result = new Result();
        try{
            logger.info("【获取需求详情开始】 getNeedAndJoinUsers start");
            result.setData(needsManagementService.getNeedAndJoinUsers(needId));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【获取需求详情成功】 getNeedAndJoinUsers success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【获取需求详情失败】 getNeedAndJoinUsers fail", e);
            return result;
        }
    }

    /**
     * 根据用户id获取用户需求列表
     * @param userId
     * @return
     */
    @GetMapping("getNeedsByUserId")
    public Result getNeedsByUserId(@RequestParam("userId") int userId) {
        Result result = new Result();
        try{
            logger.info("【获取用户所有需求列表开始】 getNeedsByUserId start");
            result.setData(needsManagementService.getNeedsByUserId(userId));
            result.setMessage("获取列表成功");
            result.setIsSuccess(true);
            logger.info("【获取用户所有需求列表成功】 getNeedsByUserId success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取列表失败");
            result.setIsSuccess(false);
            logger.error("【获取用户所有需求列表失败】 getNeedsByUserId fail", e);
            return result;
        }
    }

    /**
     * 获取类型列表
     * @param userId
     * @param categoryId
     * @return
     */
    @GetMapping("getNeedsByUserIdAndCategotyId")
    public Result getNeedsByUserIdAndCategotyId(@RequestParam("userId") int userId, @RequestParam("categoryId") int categoryId) {
        Result result = new Result();
        try{
            logger.info("【获取类型列表开始】 getNeedsByUserIdAndCategotyId start");
            result.setData(needsManagementService.getNeedsByUserIdAndCategoryId(userId, categoryId));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【获取类型列表成功】 getNeedsByUserIdAndCategotyId success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【获取类型列表失败】 getNeedsByUserIdAndCategotyId fail", e);
            return result;
        }
    }

    /**
     * 新增需求
     * @param paramMap
     * @return
     */
    @PostMapping("addNeeds")
    public Result addNeeds(@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【新增需求开始】 addNeeds start");
            needsManagementService.addNeeds(paramMap);
            result.setMessage("新增成功");
            result.setIsSuccess(true);
            logger.info("【新增需求成功】 addNeeds success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("新增失败");
            result.setIsSuccess(false);
            logger.error("【新增需求失败】 addNeeds fail", e);
            return result;
        }
    }

    /**
     * 删除需求
     * @param id
     * @return
     */
    @GetMapping("deleteNeeds/{id}")
    public Result deleteNeeds(@PathVariable("id") String id) {
        Result result = new Result();
        try{
            logger.info("【删除需求开始】 deleteNeeds start");
            needsManagementService.deleteNeeds(Integer.valueOf(id));
            result.setMessage("删除成功");
            result.setIsSuccess(true);
            logger.info("【删除需求成功】 deleteNeeds success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("删除失败");
            result.setIsSuccess(false);
            logger.error("【删除需求失败】 deleteNeeds fail", e);
            return result;
        }
    }

    /**
     * 获取需求信息
     * @param id
     * @return
     */
    @GetMapping("getNeedsDetailsById")
    public Result getNeedsDetailsById(@RequestParam String id) {
        Result result = new Result();
        try{
            logger.info("【获取需求信息开始】 getNeedsDetailsById start");
            //获取需求详情
            NeedsManagement needsManagement = needsManagementService.getNeedsDetailsById(Integer.valueOf(id));
            //添加需求主人是否参与标志
            Integer isUserJoined = needsManagementService.getisUserJoined(Integer.valueOf(id));

            //检查需求是否过期
            needsManagementService.checkOverTime(needsManagement);

            HashMap<String,Object> map = new HashMap<>();
            map.put("needInfo",needsManagement);
            map.put("isUserJoined",isUserJoined);
            result.setMessage("获取成功");
            result.setData(map);
            result.setIsSuccess(true);
            logger.info("【获取需求信息成功】 getNeedsDetailsById success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【获取需求信息失败】 getNeedsDetailsById fail", e);
            return result;
        }
    }

    /**
     * 修改需求
     * @param paramMap
     * @return
     */
    @PostMapping("editNeeds")
    public Result editNeeds(@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【修改需求开始】 editNeeds start");
            needsManagementService.editNeeds(paramMap);
            result.setMessage("修改成功");
            result.setIsSuccess(true);
            logger.info("【修改需求成功】 editNeeds success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("修改失败");
            result.setIsSuccess(false);
            logger.error("【修改需求失败】 editNeeds fail", e);
            return result;
        }
    }

    /**
     * 获取类型列表
     * @param needTypeId
     * @return
     */
    @GetMapping("getNeedInfoByNeedTypeId")
    public Result getNeedInfoByNeedTypeId(@RequestParam("needTypeId") int needTypeId) {
        Result result = new Result();
        try{
            logger.info("【获取类型列表开始】 getNeedInfoByNeedTypeId start");
            result.setData(needsManagementService.getNeedInfoByNeedTypeId(needTypeId+1 ));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【获取类型列表成功】 getNeedInfoByNeedTypeId success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【获取类型列表失败】 getNeedInfoByNeedTypeId fail", e);
            return result;
        }
    }

    /**
     * 根据关键字获取需求内容列表
     * @param keyWords
     * @return
     */
    @GetMapping("getKeyWordsContent")
    public Result getKeyWordsContent (@RequestParam("keyWords") String keyWords, @RequestParam("typeName") String typeName) {
        Result result = new Result();
        try{
            logger.info("【根据关键字获取需求内容列表开始】 getKeyWordsContent start");
            result.setData(needsManagementService.getKeyWordsContent(keyWords, typeName));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【根据关键字获取需求内容列表成功】 getKeyWordsContent success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【根据关键字获取需求内容列表失败】 getKeyWordsContent fail", e);
            return result;
        }
    }

    /**
     * 根据用户id获取正在组队的需求列表
     * @param userId
     * @return
     */
    @GetMapping("getTeamingNeeds")
    public Result getTeamingNeeds (@RequestParam int userId) {
        Result result = new Result();
        try{
            logger.info("【根据用户id获取正在组队的需求列表开始】 getTeamingNeeds start");
            result.setData(needsManagementService.getTeamingNeeds(userId));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【根据用户id获取正在组队的需求列表成功】 getTeamingNeeds success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【根据用户id获取正在组队的需求列表失败】 getTeamingNeeds fail", e);
            return result;
        }
    }


    /**
     * 根据用户id获取用户加入队伍需求列表
     * @param userId
     * @return
     */
    @GetMapping("getMyJoinedNeeds")
    public Result getMyJoinedNeeds (@RequestParam int userId) {
        Result result = new Result();
        try{
            logger.info("【根据用户id获取用户加入队伍需求列表开始】 getMyJoinedNeeds start");
            result.setData(needsManagementService.getMyJoinedNeeds(userId));
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【根据用户id获取用户加入队伍需求列表成功】 getMyJoinedNeeds success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("获取失败");
            result.setIsSuccess(false);
            logger.error("【根据用户id获取用户加入队伍需求列表失败】 getMyJoinedNeeds fail", e);
            return result;
        }
    }


    /**
     * 根据需求id和用户id移除需求队员
     * @param userId
     * @return
     */
    @GetMapping("eliminateJoinUser")
    public Result eliminateJoinUser (@RequestParam int needId, @RequestParam int joinedUserId) {
        Result result = new Result();
        try{
            logger.info("【根据需求id和用户id移除需求队员开始】 eliminateJoinUser start");
            needsManagementService.eliminateJoinUser(needId,joinedUserId);
            result.setMessage("剔除成功");
            result.setIsSuccess(true);
            logger.info("【根据需求id和用户id移除需求队员成功】 eliminateJoinUser success");
            return result;
        }
        catch (Exception e) {
            result.setMessage("剔除失败");
            result.setIsSuccess(false);
            logger.error("【根据需求id和用户id移除需求队员失败】 eliminateJoinUser fail", e);
            return result;
        }
    }
}
