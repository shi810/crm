package com.shsxt.crm.interceptors;

import com.shsxt.Utils.LoginUserUtil;
import com.shsxt.crm.exceptions.NoLoginException;
import com.shsxt.crm.service.UserService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 获取cookie 解析用户id
         *      如果用户id存在 并且 数据库存在对应的用户记录  放行  否则进行拦截  重定向到登陆
         */
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        if(userId==0 || null==userService.selectByPrimaryKey(userId)){
            throw new NoLoginException();
        }

        return true;
    }
}
