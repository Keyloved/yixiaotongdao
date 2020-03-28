package com.ruider.scheduler.handle;

import com.ruider.model.NeedsManagement;
import com.ruider.runnable.CheckNeedsOverTime;
import com.ruider.scheduler.MsgScheduler;
import com.ruider.service.MsgCacheService;
import com.ruider.service.NeedsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息调度中心
 */
@Service
public class MsgSchdulerHandle implements MsgScheduler {

    @Autowired
    private NeedsManagementService needsManagementService;

    @Autowired
    private MsgCacheService msgCacheService;

    private Executor worker = Executors.newFixedThreadPool(20);

    public void checkNeedsOverTime (int userId) throws Exception{
        //worker.execute(new CheckNeedsOverTime(userId));
        List<NeedsManagement> needs = needsManagementService.getNeedsByUserId(userId);
        for (NeedsManagement needsManagement : needs ) {
            needsManagementService.checkOverTime(needsManagement);

            //刷新缓存未读消息数量
            msgCacheService.refreshUnreadMsgNo(needsManagement.getUserId(), 3);

            //刷新缓存消息列表
            msgCacheService.refreshMsgs(needsManagement.getUserId() , 3);
        }
    }

}
