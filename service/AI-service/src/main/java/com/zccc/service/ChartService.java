package com.zccc.service;

import com.zccc.common.BaseResponse;
import com.zccc.model.entity.Chart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "chart-service",path = "/chart")
public interface ChartService {
        @PostMapping("/save") // Define the common prefix for all API endpoints
        Long innerSave(@RequestBody Chart chart);

        @PostMapping("/innerUpdate") // Define the common prefix for all API endpoints
        boolean innerUpdate(@RequestBody Chart chart);

        @RequestMapping("/get") // Define the common prefix for all API endpoints
        BaseResponse<Chart> getById(@RequestParam Long id);
}
