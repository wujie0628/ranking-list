package com.github.wujie0628.rankinglist.handle;

import com.github.wujie0628.rankinglist.vo.RedisKey;
import com.github.wujie0628.rankinglist.vo.SchedulingPatternProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public class HourUnitHandleMapping implements HandleMapping{

    SchedulingPatternProperties schedulingPatternProperties;

    private RedisKey redisKey;

    @Autowired
    RedisTemplate redisTemplate;

    public HourUnitHandleMapping(RedisKey redisKey, SchedulingPatternProperties schedulingPatternProperties, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.schedulingPatternProperties = schedulingPatternProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process() {
        long hour=System.currentTimeMillis()/(1000*60*60);
        List<String> otherKeys=new ArrayList<>();

        for(int i=1;i<(redisKey.getUnionTime());i++){
            String key= redisKey.getUnitKey() + ":" +(hour-i);
            otherKeys.add(key);
        }
        redisTemplate.opsForZSet().unionAndStore(redisKey.getUnitKey() + ":" +hour,otherKeys,redisKey.getUnionKey());
    }

    @Override
    public String getSchedulingPattern() {
        return schedulingPatternProperties.getHourUnitSchedulingPattern();
    }
}
