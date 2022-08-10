package com.github.wujie0628.rankinglist.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.wujie0628.rankinglist.vo.RedisKey;
import com.github.wujie0628.rankinglist.vo.SchedulingPatternProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public class DayUnitHandleMapping implements HandleMapping{

    private RedisKey redisKey;
    RedisTemplate redisTemplate;

    SchedulingPatternProperties schedulingPatternProperties;

    public DayUnitHandleMapping(RedisKey redisKey, SchedulingPatternProperties schedulingPatternProperties, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.schedulingPatternProperties = schedulingPatternProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process() {

        String todayStr = DateUtil.today();
        LocalDateTime todayTime = LocalDateTime.now();

        LocalDateTime beginDay = LocalDateTimeUtil.offset(todayTime, (~(redisKey.getUnionTime() - 1) + 1), ChronoUnit.DAYS);
        String beginDayStr = LocalDateTimeUtil.format(beginDay, "yyyy-MM-dd");
        String nextDayStr = beginDayStr;
        LocalDateTime nextDay = beginDay;

        List<String> otherKeys=new ArrayList<>();
        while (true) {
            if (nextDayStr.equals(todayStr)) {
                break;
            }
            otherKeys.add(redisKey.getUnitKey() + ":" + nextDayStr);
            nextDay = nextDay.plusDays(1);
            nextDayStr = LocalDateTimeUtil.format(nextDay,"yyyy-MM-dd");
        }
        redisTemplate.opsForZSet().unionAndStore(redisKey.getUnitKey() + ":" + todayStr,otherKeys,redisKey.getUnionKey());
    }

    @Override
    public String getSchedulingPattern() {
        return schedulingPatternProperties.getDayUnitSchedulingPattern();
    }

}
