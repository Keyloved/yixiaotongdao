package com.ruider.model;

import java.util.Date;

public class UserTeam {

    private int id;

    private int userId;

    private int needId;

    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNeedId() {
        return needId;
    }

    public void setNeedId(int needId) {
        this.needId = needId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserTeam{" +
                "id=" + id +
                ", userId=" + userId +
                ", needId=" + needId +
                ", createTime=" + createTime +
                '}';
    }
}
