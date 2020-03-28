package com.ruider.scheduler;

/**
 * 消息调度中心
 */
public interface MsgScheduler {

    void checkNeedsOverTime (int userId) throws Exception;
}
