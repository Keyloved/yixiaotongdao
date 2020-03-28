package com.ruider.model;

import java.util.Date;

public class MessageManage {

    private int id;

    private int categoryId;

    private int senderId;    //发送者id

    private String senderNickName;  //发送者昵称

    private String senderImage;    //发送者头像

    private int needsId;

    private String content;

    private Date createTime;

    private int isApproved;    //是否被同意，同意为1，未同意为0

    private int isWatched;      //用户是否查看的标志，查看为1，未查看为0

    private int messageId;      //归属评论或者申请的消息id

    private int recipientId;    //接受者id

    private int isReply;

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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public int getNeedsId() {
        return needsId;
    }

    public void setNeedsId(int needsId) {
        this.needsId = needsId;
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

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public int getIsWatched() {
        return isWatched;
    }

    public void setIsWatched(int isWatched) {
        this.isWatched = isWatched;
    }

    public int getMessageId() { return messageId; }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getIsReply() {
        return isReply;
    }

    public void setIsReply(int isReply) {
        this.isReply = isReply;
    }

    @Override
    public String toString() {
        return "MessageManage{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", senderId=" + senderId +
                ", senderNickName='" + senderNickName + '\'' +
                ", senderImage='" + senderImage + '\'' +
                ", needsId=" + needsId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", isApproved=" + isApproved +
                ", isWatched=" + isWatched +
                ", messageId=" + messageId +
                ", recipientId=" + recipientId +
                ", isReply=" + isReply +
                '}';
    }
}
