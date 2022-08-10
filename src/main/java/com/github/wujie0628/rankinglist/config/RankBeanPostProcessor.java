package com.github.wujie0628.rankinglist.config;

import com.github.wujie0628.rankinglist.annotation.WjRank;
import com.github.wujie0628.rankinglist.constant.RedisConstant;
import com.github.wujie0628.rankinglist.enums.RedisKeyTypeEnum;
import com.github.wujie0628.rankinglist.handle.*;
import com.github.wujie0628.rankinglist.vo.RedisKey;
import com.github.wujie0628.rankinglist.job.RankTask;
import com.github.wujie0628.rankinglist.service.*;
import com.github.wujie0628.rankinglist.vo.SchedulingPatternProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.*;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
@Component
public class RankBeanPostProcessor implements BeanFactoryPostProcessor, InstantiationAwareBeanPostProcessor {
 
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithLocalFields(bean.getClass(), field -> {
            WjRank wjRank = field.getAnnotation(WjRank.class);
            if (null != wjRank) {

                //构建定时任务必须参数
                RedisKey redisKey = createRedisKey(wjRank);

                //注册各个类型的处理器
                registerHandleMappingForJob(redisKey);

                //根据类型获取实现类
                RankService rankServiceImpl = getRankServiceImpl(redisKey);

                String injectBeanName = field.getName();
                Class injectBeanType =field.getType();

                beanFactory.registerSingleton(injectBeanName, rankServiceImpl);

                ReflectionUtils.makeAccessible(field);
                field.set(bean,beanFactory.containsBean(injectBeanName)?beanFactory.getBean(injectBeanName):beanFactory.getBean(injectBeanType));
            }
        });
 
        return pvs;
    }

    private RankService getRankServiceImpl(RedisKey redisKey) {
        RankService rankServiceImpl = null;
        if (redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.HOUR_UNIT)) {

            rankServiceImpl = new HourUnitRankServiceImpl(redisKey, (RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.DAY_UNIT)) {

            rankServiceImpl = new DayUnitRankServiceImpl(redisKey,(RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.DAY_THIS)) {

            rankServiceImpl = new DayThisRankServiceImpl(redisKey,(RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.WEEK_THIS)) {

            rankServiceImpl = new WeekThisRankServiceImpl(redisKey,(RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.MONTH_THIS)) {

            rankServiceImpl = new MonthThisRankServiceImpl(redisKey,(RedisTemplate) beanFactory.getBean("redisTemplate"));
        }
        return rankServiceImpl;
    }

    private RedisKey createRedisKey(WjRank wjRank) {
        RedisKey redisKey = new RedisKey();
        String businessKey = wjRank.businessKey();
        RedisKeyTypeEnum type = wjRank.type();
        long unionTime = wjRank.unionTime();

        redisKey.setUnitKey(RedisConstant.KEY_PREFIX + businessKey + ":" + type.getType());
        redisKey.setUnionKey(RedisConstant.KEY_PREFIX + businessKey + RedisConstant.KEY_UNION);
        redisKey.setRedisKeyTypeEnum(type);
        redisKey.setUnionTime(unionTime);
        return redisKey;
    }

    private void registerHandleMappingForJob(RedisKey redisKey) {
        HandleMapping handleMapping = getHandleMapping(redisKey);
        if(null != handleMapping) {

            RankTask rankTask = (RankTask) beanFactory.getBean("rankTask");

            List<HandleMapping> handleMappings = rankTask.getHandleMappings();

            handleMappings.add(handleMapping);

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("rankTask");
            beanDefinition.setAttribute("handleMappings", handleMappings);
        }
    }

    private HandleMapping getHandleMapping(RedisKey redisKey) {

        HandleMapping handleMapping = null;
        if (redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.HOUR_UNIT)) {
            if(redisKey.getUnionTime() == 0) {
                throw new RuntimeException("HOUR_UNIT类型必须定义unionTime，且不能为0");
            }
            handleMapping = new HourUnitHandleMapping(redisKey,
                    (SchedulingPatternProperties) beanFactory.getBean("schedulingPatternProperties"),
                    (RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.DAY_UNIT)) {
            if(redisKey.getUnionTime() == 0) {
                throw new RuntimeException("HOUR_UNIT类型必须定义unionTime，且不能为0");
            }
            handleMapping = new DayUnitHandleMapping(redisKey,
                    (SchedulingPatternProperties) beanFactory.getBean("schedulingPatternProperties"),
                    (RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.WEEK_THIS)) {
            handleMapping = new WeekThisHandleMapping(redisKey,
                    (SchedulingPatternProperties) beanFactory.getBean("schedulingPatternProperties"),
                    (RedisTemplate) beanFactory.getBean("redisTemplate"));
        }else if(redisKey.getRedisKeyTypeEnum().equals(RedisKeyTypeEnum.MONTH_THIS)) {
            handleMapping = new MonthThisHandleMapping(redisKey,
                    (SchedulingPatternProperties) beanFactory.getBean("schedulingPatternProperties"),
                    (RedisTemplate) beanFactory.getBean("redisTemplate"));
        }
        return handleMapping;
    }

}