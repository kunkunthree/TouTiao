package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.aspect.LogAspect;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    @Autowired
    private ToutiaoService toutiaoService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("Visit index");
        return "Hello World : "  + session.getAttribute("msg")
                + "<br>" + toutiaoService.say();
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key",defaultValue = "xiepuxin")String key){
        return String.format("{%s},{%d},{%d},{%s}",groupId,userId,type,key);
    }

    @RequestMapping(value = {"/ftl"})
    public String news(Model model){
        model.addAttribute("value1","1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BLUE"});

        Map<String,String> map = new HashMap<>();
        for(int i = 0 ; i < 4 ; i++){
            map.put(i+"",i*i+"");
        }
        model.addAttribute("colors",colors);
        model.addAttribute("map",map);

        model.addAttribute("user",new User("Jim"));
        return "news";
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String profile(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headNames = request.getHeaderNames();
        while(headNames.hasMoreElements()){
            String name = headNames.nextElement();
            sb.append(name + " : " + request.getHeader(name) + "<br/>");
        }
        for(Cookie cookie : request.getCookies()){
            sb.append("Cookie : " + cookie.getName() + " : " + cookie.getValue() + "<br/>");
        }

        sb.append("getMethod : " + request.getMethod() + "<br/>");
        sb.append("getPathInfo : " + request.getPathInfo() + "<br/>");
        sb.append("getQueryString : " + request.getQueryString() + "<br/>");
        sb.append("getRequestURI : " + request.getRequestURI() + "<br/>");

        return sb.toString();
//        return String.format("{%s},{%d},{%d},{%s}",groupId,userId,type,key);
    }

    @RequestMapping(value = "/response")
    @ResponseBody
    public String response(@CookieValue(value = "userId",defaultValue = "a") String userId,
                           @RequestParam(value = "key",defaultValue = "key") String key,
                           @RequestParam(value = "value",defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "userId From Cookie: " + userId;
    }

    @RequestMapping(value = "/redirect/{code}")
    @ResponseBody
    public RedirectView redirect(@PathVariable("code") int code){
        RedirectView red = new RedirectView("/",true);
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(value = "/redirect2/{code}")
    public String redirect2(@PathVariable("code") int code,
                            HttpSession session){
        session.setAttribute("msg","Jump from redirect.");
        //302跳转
        return "redirect:/";
    }

    @RequestMapping(value = "/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key){
        if(key.equals("admin")){
            return "hello admin";
        }
        throw new IllegalArgumentException("key 错误");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "Error : " + e.getMessage();
    }
}
