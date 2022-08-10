package com.github.wujie0628.rankinglist.enums;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public enum RedisKeyTypeEnum {

    // 创建常量
    HOUR_UNIT("HOUR_UNIT"), DAY_UNIT("DAY_UNIT"),
    DAY_THIS("DAY_THIS"), MONTH_THIS("MONTH_THIS"), WEEK_THIS("WEEK_THIS");

    // 成员变量
    private final String type;

    // 私有化构造函数
    RedisKeyTypeEnum(String type) {
        this.type = type;
    }

    // getter
    public String getType(){
        return type;
    }
}
