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
public class MonthThisHandleMapping implements HandleMapping{

    SchedulingPatternProperties schedulingPatternProperties;

    private RedisKey redisKey;

    @Autowired
    RedisTemplate redisTemplate;

    public MonthThisHandleMapping(RedisKey redisKey, SchedulingPatternProperties schedulingPatternProperties, RedisTemplate redisTemplate) {
        this.redisKey = redisKey;
        this.schedulingPatternProperties = schedulingPatternProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process() {
        String today = DateUtil.today();

        //当前日期所在月的第一天
        Calendar calendar = CalendarUtil.calendar();
        LocalDateTime monthBeginDay = CalendarUtil.toLocalDateTime(CalendarUtil.beginOfMonth(CalendarUtil.calendar()));
        String monthBeginDayStr = LocalDateTimeUtil.format(monthBeginDay, "yyyy-MM-dd");
        LocalDateTime monthNextDay = monthBeginDay;
        String monthNextDayStr = monthBeginDayStr;

        //计算月榜数据 月榜数据为 当前日期所在月的第一天至当前日期的数据月榜数据需要累加的key集合
        ArrayList<String> monthKeyList = new ArrayList<>();
        //计算月榜数据
        while (true) {
            //下一天为当天跳出循环，不存入需要计算的月榜key集合，直接在操作方法中添加当天key
            if (monthNextDayStr.equals(today)) {
                break;
            }
            monthKeyList.add(redisKey.getUnitKey() + ":" + monthNextDayStr);
            //在一月第一天的基础上加一天 至到日期与今天相同为月榜到今天为止的数据
            monthNextDay = monthNextDay.plusDays(1);
            monthNextDayStr = LocalDateTimeUtil.format(monthNextDay,"yyyy-MM-dd");
        }
        //循环结束后，集合中的数据为一月第一天到今天前一天的日榜key
        ZSetOperations<String, Object> zsetOperation = redisTemplate.opsForZSet();
        //对集合及今天的日榜数据进行合集计算获取月榜数据
        zsetOperation.unionAndStore(redisKey.getUnitKey() + ":" + today,monthKeyList,redisKey.getUnionKey());


    }

    @Override
    public String getSchedulingPattern() {
        return schedulingPatternProperties.getMonthThisSchedulingPattern();
    }
}
