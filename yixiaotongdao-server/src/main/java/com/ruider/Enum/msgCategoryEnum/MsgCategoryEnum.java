package com.ruider.Enum.msgCategoryEnum;

/**
 * 消息类型枚举
 */
public enum MsgCategoryEnum {

    /**
     * 申请加入/退出
     */
    APPLY( 1, "apply"),

    /**
     * 评论/回复
     */
    EVALUATE (2, "evaluate"),


    /**
     * 过期
     */
    OVERTIME (3, "overTime"),

    /**
     * 人数满
     */
    OVERNUMBER ( 4 , "overNumber" ),

    /**
     * 剔除
     */
    ELIMINATE (5 , "eliminate" );


    /**
     * 消息类型码，对应消息类型id
     */
    private int categoryCode ;

    /**
     * 消息类型名称
     */
    private String categoryName ;

    public static String getCategotyNameByCode (int categoryCode) {

        String categoryName = null;
        for (MsgCategoryEnum msgCategoryEnum : values()) {
            if (msgCategoryEnum.getCategoryCode() == categoryCode) {
                categoryName = msgCategoryEnum.getCategoryName();
                break;
            }
        }
        return categoryName;
    }

    public static int getCategoryCodeByName (String categoryName) {
        int categoryCode = 0;
        for (MsgCategoryEnum msgCategoryEnum : values()) {
            if (msgCategoryEnum.getCategoryName() == categoryName) {
                categoryCode = msgCategoryEnum.getCategoryCode();
                break;
            }
        }
        return categoryCode;
    }


    private MsgCategoryEnum (int categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
