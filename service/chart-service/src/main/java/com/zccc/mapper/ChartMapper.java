package com.zccc.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.entity.Chart;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.zccc.springbootinit.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {

    List<Map<String, Object>> queryChartData(String querySql);
}




