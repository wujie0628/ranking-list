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
public class DayThisRankServiceImpl implements RankService{

    private RedisKey redisKey;
    @Autowired
    private RedisTemplate redisTemplate;

    public DayThisRankServiceImpl(RedisKey redisKey, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.redisTemplate = redisTemplate;
    }

    public void putValue(String value, int delta) {

        String todayString = DateUtil.today();

        redisTemplate.opsForZSet().incrementScore(redisKey.getUnionKey() + ":" + todayString, value, delta);

        //设置数据2天后过期
        redisTemplate.expire(redisKey.getUnionKey() + ":" +todayString, 2, TimeUnit.DAYS);

    }

    @Override
    public void putValue(String value) {
        putValue(value, 1);
    }

    public Set getRankList(int start, int end ) {
        String todayString = DateUtil.today();
        return redisTemplate.opsForZSet().reverseRangeWithScores(redisKey.getUnionKey() + ":" + todayString, start, end);
    }



}
