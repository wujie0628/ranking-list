# ranking-list
基于redis的sorted set（zset）实现的排行榜，可以自定义多少小时和多少天内的排行榜数据，也可以支持当天排行榜、本周排行榜、本月排行榜。使用springboot自定义starter实现自动装配，开箱即用

# 快速使用
### 1、pom引入
```
<dependency>
    <groupId>com.github.wujie0628</groupId>
    <artifactId>ranking-list-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2、填写redis配置信息
```
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=123456
```  
### 3、项目中直接引入使用
```java
public class TestController {
    
  @WjRank(businessKey = "hourUnit", type = RedisKeyTypeEnum.HOUR_UNIT, unionTime = 3)
  private RankService hourUnitRankService;
  
  //…………
}
```
```java
hourUnitRankService.putValue("1#我要上热搜", 1);
hourUnitRankService.getRankList(0, 4);
```
------
# 使用详解
### 1. 首先来看看注解上参数的意义
* **businessKey：** 自定义key值，做业务隔离使用
* **type**： 排行榜类型，目前一共有以下几种类型
    * HOUR_UNIT: *小时排行榜，定义为这个类型必须要指定unionTime参数*
    * DAY_UNIT: *天数排行榜，定义为这个类型必须要制定unionTime参数*
    * DAY_THIS: *当日排行榜*
    * WEEK_THIS: *本周排行榜*
    * MONTH_THIS: *本月排行榜*
* **unionTime**： 当type为HOUR_UNIT或DAY_UNIT类型时必须制定，确定要归并多久的排行榜数据。如24h排行榜，5d排行榜等
### 2.再来看看方法的说明
```java
public interface RankService {

    /**
     * @description: put方法。如果已经存在这个value，那么将分数加上delta。若没有，则创建后将分值设为delta的值
     * @author: jiewu19
     * @date: 2022/8/9 19:15
     * @param: value 自定义的业务数据。如1#我是一个帖子标题。“1”为帖子的id，“我是一个帖子标题”为页面显示的文本
     * @param: delta 增量数值
     **/
    void putValue(String value, int delta);

    /**
     * @description: 同上，默认delta的值为1
     * @author: jiewu19
     * @date: 2022/8/9 19:21
     * @param: value
     **/
    void putValue(String value);

    /**
     * @description: 获取排行榜数据
     * @author: jiewu19
     * @date: 2022/8/9 19:21
     * @param: start 从0开始计算，起始位的位值
     * @param: end 末尾数的位置
     * @return: java.util.Set
     **/
    Set getRankList(int start, int end );
}
```
### 3.怎么自定义归并定时任务的执行时间
### 除了DAY_THIS这种类型，其他类型都会有一个定时任务来进行归并任务。
* HOUR_UNIT *每个小时生成一个key值，定时任务执行时将“unionTime”个单位小时生成的key对应的set集合进行归并排序*
* DAY_UNIT *每天生成一个key，定时任务执行时将“unionTime”个单位天生成的key对应的set集合进行归并排序*
* WEEK_THIS *每天生成key，定时任务执行时将本周第一天到当天的所有key的set集合进行归并排序*
* MONTH_THIS *每天生成key，定时任务执行时将本月第一天到稻田的所有key的set集合进行归并排序*
* 以上，get方法直接取归并后的集合列表
### 自定义定时任务时间的配置如下：
```yaml
rank.schedulingPattern.GLOBAL=*/2 * * * * *
rank.schedulingPattern.HOUR_UNIT=*/2 * * * * *
rank.schedulingPattern.DAY_UNIT=*/2 * * * * *
rank.schedulingPattern.WEEK_THIS=*/2 * * * * *
rank.schedulingPattern.MONTH_THIS=*/2 * * * * *
```
* GLOBAL： *为全局配置，其他类型若没有配置那么将使用此配置*
* 其他类型： *若配置，那么此种类型的定时任务将使用对应的配置执行。若没有配置但GLOBAL有配置，那么将使用GLOBAL的配置执行*
* 若全部没有配置，那么所有类型的定时策略为“0 0 */1 * * ?”（每小时执行一次）