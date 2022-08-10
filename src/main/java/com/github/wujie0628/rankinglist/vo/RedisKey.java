package com.github.wujie0628.rankinglist.vo;

import com.github.wujie0628.rankinglist.enums.RedisKeyTypeEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
@Data
public class RedisKey {

    private RedisKeyTypeEnum redisKeyTypeEnum;

    private String unitKey;
    private String unionKey;

    private long unionTime;

}
