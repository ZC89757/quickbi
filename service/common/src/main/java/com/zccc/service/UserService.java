package com.zccc.service;

import com.zccc.model.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    User getById(Long userId);
}
