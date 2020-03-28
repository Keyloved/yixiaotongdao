package com.ruider.controller;

import com.ruider.common.Result;
import com.ruider.model.User;
import com.ruider.service.UserCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 用户收藏操作接口
 */
@RestController
@RequestMapping("collection")
public class UserCollectionController {

    private Logger logger = LoggerFactory.getLogger(UserCollectionController.class);

    @Autowired
    private UserCollectionService userCollectionService;

    /**
     * 用户添加收藏
     * @param paramMap
     * @return
     */
    @PostMapping("addCollection")
    public Result addCollection (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try {
            logger.info("【添加收藏开始】addCollection start");
            int ret = userCollectionService.addCollection(paramMap);
            if(ret == 0) {
                result.setIsSuccess(false);
                result.setMessage("收藏失败");
                logger.info("【添加收藏失败】addCollection failed");
            }
            else if (ret == -1) {
                result.setIsSuccess(false);
                result.setMessage("禁止重复收藏");
                logger.info("【添加收藏失败,重复收藏】addCollection failed, collect again!");
            }
            else{
                result.setIsSuccess(true);
                result.setMessage("收藏成功");
                logger.info("【添加收藏成功】addCollection succeed");
            }
            return result;
        }catch (Exception e){
            logger.error("【添加收藏失败】addCollection fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("收藏失败");
            return result;
        }
    }

    /**
     * 用户查看收藏
     * @param userId
     * @return
     */
    @GetMapping("getCollections")
    public Result getCollections(int userId) {
        Result result = new Result();
        try {
            logger.info("【添加收藏开始】getCollection start");
            List list = userCollectionService.selectCollections(userId);
            result.setData(list);
            result.setIsSuccess(true);
            result.setMessage("获取收藏成功");
            logger.info("【获取收藏成功】getCollection succeed");
            return result;
        }catch (Exception e){
            logger.error("【获取收藏失败】getCollection fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("获取收藏失败");
            return result;
        }
    }


    /**
     * 删除收藏
     * @param collectionId
     * @return
     */
    @GetMapping("deleteCollection")
    public Result deleteCollection(int collectionId) {
        Result result = new Result();
        try {
            logger.info("【删除收藏开始】deleteCollection start");
            userCollectionService.deleteCollection(collectionId);
            result.setIsSuccess(true);
            result.setMessage("删除收藏成功");
            logger.info("【删除收藏成功】deleteCollection succeed");
            return result;
        }catch (Exception e){
            logger.error("【删除收藏失败】deleteCollection fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("删除收藏失败");
            return result;
        }
    }
}
