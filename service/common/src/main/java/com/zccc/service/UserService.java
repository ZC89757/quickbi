package com.zccc.service;

import com.zccc.model.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);
}
