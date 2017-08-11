package com.xiepuxin.toutiao.service;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.xiepuxin.toutiao.controller.IndexController;
import com.xiepuxin.toutiao.dao.LoginTicketDAO;
import com.xiepuxin.toutiao.dao.UserDAO;
import com.xiepuxin.toutiao.model.LoginTicket;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public void addUser(User user){
        userDAO.addUser(user);
    }

    public Map<String,Object> register(String username, String password){
        Map<String,Object> map = new HashMap<>();

        //用户注册信息合法性检测
        if(StringUtils.isEmpty(username)){
            logger.error("注册异常：用户名不能为空");
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            logger.error("注册异常：密码不能为空");
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user != null){
            logger.error("注册异常：用户名已经被注册");
            map.put("msgname","用户名已经被注册");
            return map;
        }

        //密码强度（可选）

        //注册
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.xiepuxin.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //登陆
        //ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;

//        user.setPassword(password);
    }

    public Map<String,Object> login(String username, String password){
        logger.info("当前登陆用户名：" + username + " , 当前登陆用户密码：" + password);
        Map<String,Object> map = new HashMap<>();

        //用户注册信息合法性检测
        if(StringUtils.isEmpty(username)){
            logger.error("登陆异常：用户名不能为空");
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            logger.error("登陆异常：密码不能为空");
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null){
            logger.error("登陆异常：用户名不存在");
            map.put("msgname","用户名不存在");
            return map;
        }

        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            logger.error("登陆异常：密码不正确");
            map.put("msgpwd","密码不正确");
            return map;
        }

        //密码强度（可选）

        //ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());

        return map;

//        user.setPassword(password);
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);

        return ticket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public void logout(String ticket){
        logger.info("用户登出，ticket："+ticket);
        loginTicketDAO.updateStatus(ticket,1);
    }

}
