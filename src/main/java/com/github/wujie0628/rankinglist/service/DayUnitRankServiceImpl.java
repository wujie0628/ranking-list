package com.github.wujie0628.rankinglist.service;

import cn.hutool.core.date.DateUtil;
import com.github.wujie0628.rankinglist.vo.RedisKey;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public class DayUnitRankServiceImpl implements RankService{

    private RedisKey redisKey;
    @Autowired
    private RedisTemplate redisTemplate;

    public DayUnitRankServiceImpl(RedisKey redisKey, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.redisTemplate = redisTemplate;
    }

    public void putValue(String value, int delta) {

        String today = DateUtil.today();

        redisTemplate.opsForZSet().incrementScore(redisKey.getUnitKey() + ":" + today, value, delta);

        //设置数据365天后过期
        redisTemplate.expire(redisKey.getUnitKey() + ":" + today, 365, TimeUnit.DAYS);

    }

    @Override
    public void putValue(String value) {
        putValue(value, 1);
    }

    public Set getRankList(int start, int end ) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(redisKey.getUnionKey(), start, end);
    }



}
