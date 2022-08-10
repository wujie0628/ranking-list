package com.github.wujie0628.rankinglist.handle;

import cn.hutool.core.date.CalendarUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.wujie0628.rankinglist.vo.RedisKey;
import com.github.wujie0628.rankinglist.vo.SchedulingPatternProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public class WeekThisHandleMapping implements HandleMapping{

    SchedulingPatternProperties schedulingPatternProperties;

    private RedisKey redisKey;

    @Autowired
    RedisTemplate redisTemplate;

    public WeekThisHandleMapping(RedisKey redisKey, SchedulingPatternProperties schedulingPatternProperties, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.schedulingPatternProperties = schedulingPatternProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process() {
        String today = DateUtil.today();

        //当前日期所在周的第一天
        Calendar calendar = CalendarUtil.calendar();
        LocalDateTime weekBeginDay = CalendarUtil.toLocalDateTime(CalendarUtil.beginOfWeek(calendar));
        String weekBeginDayStr = LocalDateTimeUtil.format(weekBeginDay, "yyyy-MM-dd");
        LocalDateTime weekNextDay = weekBeginDay;
        String weekNextDayStr = weekBeginDayStr; //周榜初始下一天为一周的第一天

        //计算周榜数据 周榜数据为 当前日期所在周的第一天至当前日期的数据 周榜数据需要累加的key集合
        ArrayList<String> weekKeyList = new ArrayList<>();
        //计算周榜数据
        while (true) {
            //下一天为当天跳出循环，不存入需要计算的周榜key集合，直接在操作方法中添加当天key
            if (weekNextDayStr.equals(today)) {
                break;
            }
            weekKeyList.add(redisKey.getUnitKey() + ":" + weekNextDayStr);
            //在一周第一天的基础上加一天 至到日期与今天相同为周榜到今天为止的数据
            weekNextDay = weekNextDay.plusDays(1);
            weekNextDayStr = LocalDateTimeUtil.format(weekNextDay,"yyyy-MM-dd");
        }
        //循环结束后，集合中的数据为一周第一天到今天前一天的日榜key
        ZSetOperations<String, Object> zsetOperation = redisTemplate.opsForZSet();
        //对集合及今天的日榜数据进行合集计算获取周榜数据
        zsetOperation.unionAndStore(redisKey.getUnitKey() + ":" + today,weekKeyList,redisKey.getUnionKey());


    }

    @Override
    public String getSchedulingPattern() {
        return schedulingPatternProperties.getWeekThisSchedulingPattern();
    }
}
