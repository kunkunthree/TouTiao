package com.xiepuxin.toutiao.interceptor;

import com.xiepuxin.toutiao.controller.IndexController;
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
public class PassportInterceptor implements HandlerInterceptor{
    private static final Logger logger = LoggerFactory.getLogger(PassportInterceptor.class);

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if(ticket != null){
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                if(loginTicket == null){
                    logger.info("loginTicket == null");
                }else if(loginTicket.getExpired().before(new Date())){
                    logger.info("ticket已过期");
                }else if(loginTicket.getStatus() != 0){
                    logger.info("ticket已失效");
                }
                logger.info("用户数据获取失败或ticket已过期");
                return true;
            }

            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
            logger.info("获得用户数据");
            logger.info("用户名："+user.getName());
            logger.info("ticket："+loginTicket.getTicket());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o,
                           ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && hostHolder.getUser() != null){
            logger.info("加载已有用户数据");
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o,
                                Exception e) throws Exception {
        logger.info("清空已记录的用户数据");
        hostHolder.clear();
    }
}
