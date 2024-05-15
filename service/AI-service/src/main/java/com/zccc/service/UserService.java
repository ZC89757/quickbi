package com.zccc.service;

import model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient(name = "user-service", path = "/user")
public interface UserService {

    @RequestMapping("/getLoginUser") // Define the common prefix for all API endpoints
    User getLoginUser(@RequestParam HttpServletRequest request);
    @RequestMapping("/isAdmin") // Define the common prefix for all API endpoints
    boolean isAdmin(@RequestParam HttpServletRequest request);
    @RequestMapping("/isAdmin") // Define the common prefix for all API endpoints
    boolean isAdmin(@RequestParam User loginUser);
}
