package com.zccc.service;

import com.zccc.model.entity.Chart;

public interface ChartService {
    boolean updateById(Chart updateChartResult);

    Long save_chart(Chart chart);

    Chart getById(long chartId);
}
