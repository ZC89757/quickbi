package com.zccc.service;

import common.BaseResponse;
import model.entity.Chart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "chart-service", path = "/chart")
public interface ChartService {
        @RequestMapping("/save") // Define the common prefix for all API endpoints
        boolean save(@RequestParam Chart chart);

        @RequestMapping("/update") // Define the common prefix for all API endpoints
        boolean updateById(@RequestParam Chart chart);

        @RequestMapping("/get") // Define the common prefix for all API endpoints
        BaseResponse<Chart> getById(@RequestParam Long id);


}
