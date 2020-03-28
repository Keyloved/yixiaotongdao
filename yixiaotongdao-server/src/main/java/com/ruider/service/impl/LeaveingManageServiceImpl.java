package com.ruider.service.impl;

import com.ruider.mapper.LeaveingManageMapper;
import com.ruider.model.LeaveingManage;
import com.ruider.model.User;
import com.ruider.service.LeaveingManageService;
import com.ruider.service.UserService;
import com.ruider.utils.MailUtil.EmailCreator;
import com.ruider.utils.MailUtil.EmailSendInfo;
import com.ruider.utils.MailUtil.EmailSender;
import org.apache.ibatis.annotations.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableScheduling
public class LeaveingManageServiceImpl implements LeaveingManageService {

    private Logger logger = LoggerFactory.getLogger(LeaveingManageServiceImpl.class);

    @Autowired
    private LeaveingManageMapper leaveingManageMapper;

    @Autowired
    private UserService userService;

    /**
     * 每一天定时查看是否有「写给未来的ta」信件
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 *1000)
    public void checkTimeOut () {
        try {
            logger.info("【每24小时执行一次，检查是否有需要发送给「未来的ta」的信件开始】checkTimeOut start");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String nowStr = simpleDateFormat.format(now);
            List<LeaveingManage> list = leaveingManageMapper.getTimeoutComplaints(simpleDateFormat.parse(nowStr));
            for (LeaveingManage leaveingManage : list) {
                User user = userService.getUserDetails(leaveingManage.getUserId());
                EmailSendInfo emailSendInfo = EmailCreator.createEmail(leaveingManage.getMark() , user.getNickName(), leaveingManage.getContent());
                EmailSender.sendTextMail(emailSendInfo);
                logger.info("【「写给未来的ta」的信件发送成功】");
            }
            logger.info("【所有信件-->每24小时执行一次，检查是否有需要发送给「未来的ta」的信件成功】checkTimeOut succeed");
        }
        catch (Exception e) {
            logger.error("【每24小时执行一次，检查是否有需要发送给「未来的ta」的信件失败】checkTimeOut failed",e);
        }
    }


    /**
     * 添加留言
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int addLeaveingManage (HashMap<String,Object> paramMap) throws Exception {
        LeaveingManage leaveingManage = new LeaveingManage();
        leaveingManage.setCategoryId(Integer.valueOf(paramMap.get("categoryId").toString()));
        leaveingManage.setUserId(Integer.valueOf(paramMap.get("userId").toString()));
        leaveingManage.setContent(paramMap.get("content").toString());
        if (!paramMap.get("mark").toString().equals("") ) {
            leaveingManage.setMark(paramMap.get("mark").toString());
        }
        leaveingManage.setCreateTime(new Date());
        return leaveingManageMapper.addLeaveingManage(leaveingManage);
    }

    /**
     * 获取所有留言以及各个留言对应的发表者信息
     * @return
     * @throws Exception
     */
    @Override
    public ArrayList<HashMap<String,Object>> getAllLeaveingManage(int categoryId) throws Exception {
        ArrayList<HashMap<String,Object>> result = new ArrayList<>();
        List<LeaveingManage> list = leaveingManageMapper.getAllLeaveingManage(categoryId);
        for (LeaveingManage leaveingManage : list) {
            HashMap<String,Object> map = new HashMap<>();
            int userId = leaveingManage.getUserId();
            User user = userService.getUserDetails(userId);
            map.put("userInfo", user);
            map.put("leaveingManage", leaveingManage);
            result.add(map);
        }
        return result;
    }

    /**
     * 写给未来的ta
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public int writeToFuture(HashMap<String,Object> paramMap) throws Exception {
        LeaveingManage leaveingManage = new LeaveingManage();
        leaveingManage.setCategoryId(Integer.valueOf(paramMap.get("categoryId").toString()));
        leaveingManage.setUserId(Integer.valueOf(paramMap.get("userId").toString()));
        leaveingManage.setContent(paramMap.get("content").toString());
        String futureStr = paramMap.get("future").toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date future = simpleDateFormat.parse(futureStr);
        leaveingManage.setFuture(future);
        leaveingManage.setMark(paramMap.get("mark").toString());

        leaveingManage.setCreateTime(new Date());
        return leaveingManageMapper.addLeaveingManage(leaveingManage);
    }
}
