package com.zccc.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zccc.mapper.ChartMapper;
import com.zccc.model.entity.Chart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CleanerUtil {
    @Autowired
    private ChartMapper chartMapper;
    //定时任务每天清理一次表中status为failed的数据
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanFailedData() {
        // 获取当前时间
        Date now = new Date();
        // 计算前一天的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        // 查询前一天的数据
        List<Chart> charts = chartMapper.selectList(new QueryWrapper<Chart>().eq("status", "failed").lt("updateTime",yesterday));
        //用batch删除数据
        chartMapper.deleteBatchIds(charts);
    }

}
