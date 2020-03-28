package com.ruider.model;

import java.util.Date;

/**
 * 留言管理类
 */
public class LeaveingManage {

    private int id;

    private int categoryId;

    private int userId;   //发表者

    private String content;

    private Date createTime;

    private String mark;   //备注

    private Date future;   //写给未来的ta，发送的时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Date getFuture() {
        return future;
    }

    public void setFuture(Date future) {
        this.future = future;
    }

    @Override
    public String toString() {
        return "LeaveingManage{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", mark='" + mark + '\'' +
                ", future=" + future +
                '}';
    }
}
