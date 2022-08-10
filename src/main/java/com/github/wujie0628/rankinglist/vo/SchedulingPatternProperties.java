package com.github.wujie0628.rankinglist.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
@Data
@Service("schedulingPatternProperties")
public class SchedulingPatternProperties {

    @Value("${rank.schedulingPattern.GLOBAL:0 0 */1 * * ?}")
    private String globalSchedulingPattern;

    @Value("${rank.schedulingPattern.HOUR_UNIT:${rank.schedulingPattern.GLOBAL}}")
    private String hourUnitSchedulingPattern;

    @Value("${rank.schedulingPattern.DAY_UNIT:${rank.schedulingPattern.GLOBAL}}")
    private String dayUnitSchedulingPattern;

    @Value("${rank.schedulingPattern.WEEK_THIS:${rank.schedulingPattern.GLOBAL}}")
    private String weekThisSchedulingPattern;

    @Value("${rank.schedulingPattern.MONTH_THIS:${rank.schedulingPattern.GLOBAL}}")
    private String monthThisSchedulingPattern;


}
