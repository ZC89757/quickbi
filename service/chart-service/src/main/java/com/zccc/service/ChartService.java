package com.zccc.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zccc.model.entity.Chart;

/**
 *
 */
public interface ChartService extends IService<Chart> {
    boolean updateById(Chart updateChartResult);

    Long save_chart(Chart chart);

    Chart getById(long chartId);
}
