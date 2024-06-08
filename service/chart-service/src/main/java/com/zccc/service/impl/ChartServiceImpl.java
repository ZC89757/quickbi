package com.zccc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zccc.service.ChartService;
import com.zccc.mapper.ChartMapper;
import com.zccc.model.entity.Chart;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@DubboService
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    public boolean updateById(Chart updateChartResult){
        return super.updateById(updateChartResult);
    }


    public Long save_chart(Chart chart){
        return super.save(chart) ? chart.getId() : -1;
    }

    public Chart getById(long chartId){
        return super.getById(chartId);
    }
}




