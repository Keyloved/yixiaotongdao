package com.ruider.controller;

/**
 * function 管理用户数据
 * Created by mahede on 2018/11/27.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruider.common.Result;
import com.ruider.model.MessageManage;
import com.ruider.model.User;
import com.ruider.scheduler.MsgScheduler;
import com.ruider.service.MessageManageService;
import com.ruider.utils.CommonUtils;
import com.ruider.utils.HttpClient.HttpUtil;
import com.ruider.utils.HttpRequest;
import com.ruider.utils.RedisUtils.RedisUtils;
import net.sf.json.JSONObject;
import com.ruider.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userInfo")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageManageService messageManageService;

    @Value("${APPID}")
    private String APPID;

    @Value("${APPSECRECT}")
    private String APPSECRECT;

    @Value("${GRANTTYPE}")
    private String GRANTTYPE;

    @Value("${REQUESTURL}")
    private String requestUrl;

    @Autowired
    RedisController redisController;

    @Autowired
    private MsgScheduler msgScheduler;

    /**
     * 测试接口
     * @return
     */
    @GetMapping("test")
    public String test () {
        return " ";
    }

    /*
     * function: 用户访问入口，使用code换取openId
     * @Param code
     * @return openid
     */
    @GetMapping("weixin/session")
    public Result getOpenId(String code) {
        Result result = new Result();
        try {
            logger.info("【根据用户code获取用户openId】");
            String url = this.requestUrl + "?appid=" + APPID + "&secret=" + APPSECRECT + "&js_code=" + code + "&grant_type="
                    + GRANTTYPE;
            // 发送请求
            String data = HttpUtil.get(url);

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> info = null;
            try {
                info = (HashMap<String, Object>) mapper.readValue(data, Map.class);
                System.out.println("info:"+info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //判断数据库是否保存用户数据
            User user = userService.checkUser (info.get("openid").toString());
            HashMap<String,Object> map = new HashMap<>();
            map.put("openid",info.get("openid").toString());
            map.put("user",user);
            result.setData(map);
            result.setMessage("获取成功");
            result.setIsSuccess(true);
            logger.info("【根据用户code获取openId成功】");
        }
        catch (Exception e) {
            result.setCode(Result.EXCEPTION_CODE);
            result.setMessage("系统故障");
            result.setIsSuccess(false);
            logger.info("【根据用户code获取openId失败】", e);
        }
        return result;
    }

    /*
     * function: 根据openID获取用户数据
     * @Param paramMap
     * @return userId,openId
     */
    @PostMapping(value = "getUserInfoByOpenId")
    public Result getUserInfoByOpenId (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try {
            logger.info("getUserInfoByOpenId start");
            User user = userService.getUserInfoByOpenId(paramMap);
            result.setIsSuccess(true);
            result.setData(user);
            result.setMessage("成功");
            logger.info("【根据openID获取用户数据成功】getUserInfoByOpenId success");
            return result;
        } catch (Exception e) {
            logger.error("【根据openID获取用户数据失败】getUserInfoByOpenId fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("失败");
            return result;
        }

    }

    /*
     * function: 用户访问入口，使用code换取openId
     * @Param code
     * @return openid
     */
    @GetMapping("IfAuthorizationEd")
    public Result IfAuthorizationEd(String encryptedData, String iv, String code) {
        Result result = new Result();

        if (code == null || code.length() == 0) {
            result.setIsSuccess(false);
            result.setMessage("code不能为空");
            return result;
        }

        String session_key = "RMB5IApZWVhJypWD26cEtQ==";
        //用户的唯一标识（openid）
        String openid = "ovYLr4vw4CTUKIG_eraz8PMr_oc4";
        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("session_key", session_key);
        userInfo.put("openid", openid);
        result.setIsSuccess(true);
        result.setData(userInfo);
        result.setMessage("获取成功");
        logger.info("【获取微信openId成功】IfAuthorizationEd success");
        return result;
    }



    /*
     * function: 用户访问权限允许，新用户添加到用户表
     * @Param paramMap
     * @return userId,openId
     */
    @PostMapping(value = "addUserInfoIfNoSaved")
    public Result addUserInfoIfNoSaved (@RequestBody HashMap<String,Object> paramMap, HttpServletRequest request) {
        Result result = new Result();
        try{
            System.out.println("request "+request);
            logger.info("addUserInfoIfNoSaved start");
            int ret = userService.addUser(paramMap);
            msgScheduler.checkNeedsOverTime(ret);
            String token = "XyXyRuiDer_userId_"+ret;
            redisController.saveRedis(token ,String.valueOf(ret));
            result.setIsSuccess(true);
            result.setData(ret);
            result.setMessage("成功");
            logger.info("【用户信息保存或者查询成功】addUserInfoIfNoSaved success");
            return result;
        }catch (Exception e) {
            logger.error("【用户信息保存或者查询失败】addUserInfoIfNoSaved fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("失败");
            return result;
        }
    }

    /*
     * function: 获取用户数据
     * @Param paramMap
     * @return Result
     */
    @GetMapping(value = "getUserDetails")
    public Result getUserDetails(@RequestParam("userId") String userId) {
        Result result = new Result();
        try {
            logger.info("getUserDetails start ");
            User user = userService.getUserDetails(Integer.valueOf(userId));
            result.setIsSuccess(true);
            result.setData(user);
            result.setMessage("获取成功");
            logger.info("【获取用户信息成功】getUserDetails success");
            return result;
        }catch (Exception e){
            logger.error("【获取用户信息失败】getUserDetails fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("获取失败");
            return result;
        }
    }

    /*
     * function: 编辑用户数据
     * @Param paramMap
     * @return Result
     */
    @PostMapping(value = "editUserDetails")
    public Result editUserDetails (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【用户信息更新开始】editUserDetails start");
            userService.updateUser(paramMap);
            result.setIsSuccess(true);
            result.setMessage("更新成功");
            logger.info("【用户信息更新成功】editUserDetails success");
            return result;
        }catch (Exception e) {
            logger.error("【用户信息更新失败】editUserDetails fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("更新失败");
            return result;
        }
    }


    /*
     * function: 更改用户个人信息是否公开
     * @Param paramMap
     * @return Result
     */
    @PostMapping(value = "updateUserInfoOpen")
    public Result updateUserInfoOpen (@RequestBody HashMap<String,Object> paramMap) {
        Result result = new Result();
        try{
            logger.info("【更改用户个人信息是否公开开始】updateUserInfoOpen start");
            userService.updateUserInfoOpen(paramMap);
            result.setIsSuccess(true);
            result.setMessage("更新成功");
            logger.info("【更改用户个人信息是否公开成功】updateUserInfoOpen success");
            return result;
        }catch (Exception e) {
            logger.error("【更改用户个人信息是否公开失败】updateUserInfoOpen fail", e);
            result.setCode(Result.FAIL_CODE);
            result.setIsSuccess(false);
            result.setMessage("更新失败");
            return result;
        }
    }


}
