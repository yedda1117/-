package com.plantcloud.scheduler.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

public class ScheduleStrategyJob extends QuartzJobBean {

    @Override
    protected void executeInternal(org.quartz.JobExecutionContext context) {
        // 定时策略执行骨架。
    }
}
