package com.ruider.runnable;

import com.ruider.model.NeedsManagement;
import com.ruider.service.MsgCacheService;
import com.ruider.service.NeedsManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 检查需求是否过期
 */
public class CheckNeedsOverTime implements Runnable{

    Logger logger = LoggerFactory.getLogger(CheckNeedsOverTime.class);

    @Autowired
    private MsgCacheService msgCacheService;

    @Autowired
    private NeedsManagementService needsManagementService;

    private int userId;

    ThreadLocal threadLocal;

    public CheckNeedsOverTime (int userId) {
        threadLocal = new ThreadLocal();
        threadLocal.set(userId);
    }

    @Override
    public void run () {
        try {
            int userId = (Integer)threadLocal.get();
            threadLocal.remove();
            List<NeedsManagement> needs = needsManagementService.getNeedsByUserId(userId);
            for (NeedsManagement needsManagement : needs ) {
                needsManagementService.checkOverTime(needsManagement);

                //刷新缓存未读消息数量
                msgCacheService.refreshUnreadMsgNo(needsManagement.getUserId(), 3);

                //刷新缓存消息列表
                msgCacheService.refreshMsgs(needsManagement.getUserId() , 3);
            }
        }
        catch (Exception e) {
            logger.error("【检查用户需求是否过期失败】CheckNeedsOverTime failed", e);
        }
    }

}
