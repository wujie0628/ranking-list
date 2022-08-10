package com.github.wujie0628.rankinglist.job;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.github.wujie0628.rankinglist.handle.HandleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
@Component("rankTask")
public class RankTask implements ApplicationRunner {

    private List<HandleMapping> handleMappings;

    @Autowired
    RedisTemplate redisTemplate;

    public RankTask(List<HandleMapping> handleMappings) {

        this.handleMappings = handleMappings;
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < handleMappings.size(); i++) {
            HandleMapping handleMapping = handleMappings.get(i);
            runTask(handleMapping);
        }

    }

    private void runTask(HandleMapping handleMapping) {
        //schedulingPattern = "*/2 * * * * *";
        System.out.println("定时任务启动>>>>>>" + handleMapping.getSchedulingPattern());
        CronUtil.schedule("*/2 * * * * *", new Task() {
            @Override
            public void execute() {
                handleMapping.process();
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        if(!CronUtil.getScheduler().isStarted()) {
            CronUtil.start();
        }
    }

    public List<HandleMapping> getHandleMappings() {
        return handleMappings;
    }
}