package com.xiepuxin.toutiao.interceptor;

import com.xiepuxin.toutiao.dao.LoginTicketDAO;
import com.xiepuxin.toutiao.dao.UserDAO;
import com.xiepuxin.toutiao.model.HostHolder;
import com.xiepuxin.toutiao.model.LoginTicket;
import com.xiepuxin.toutiao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{
    private static final Logger logger = LoggerFactory.getLogger(PassportInterceptor.class);

    @Autowired
    private LoginTicketDAO loginTicketDAO;


    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {
        if(hostHolder.getUser() == null){
            logger.info("没有登陆，重定向到首页登陆界面");
            httpServletResponse.sendRedirect("/?pop=1");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o,
                                Exception e) throws Exception {
    }

}
