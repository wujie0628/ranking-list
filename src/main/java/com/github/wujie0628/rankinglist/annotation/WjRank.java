package com.github.wujie0628.rankinglist.annotation;

import com.github.wujie0628.rankinglist.enums.RedisKeyTypeEnum;

import java.lang.annotation.*;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface WjRank {

    String businessKey();

    RedisKeyTypeEnum type();

    long unionTime() default 0;
}
