package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.async.EventModel;
import com.xiepuxin.toutiao.async.EventProducer;
import com.xiepuxin.toutiao.async.EventType;
import com.xiepuxin.toutiao.model.EntityType;
import com.xiepuxin.toutiao.model.News;
import com.xiepuxin.toutiao.model.ViewObject;
import com.xiepuxin.toutiao.service.NewsService;
import com.xiepuxin.toutiao.service.ToutiaoService;
import com.xiepuxin.toutiao.service.UserService;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private ToutiaoService toutiaoService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);

        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path={"/reg/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password")String password,
                      @RequestParam(value = "rember", defaultValue = "0")int rememberme,
                      HttpServletResponse response){
        try{
            Map<String,Object> map = userService.register(username,password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24*5);
                }

                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }else{
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch(Exception e){
            logger.error("注册异常："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"注册异常");
        }
    }

    @RequestMapping(path={"/login/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password")String password,
                        @RequestParam(value = "rember", defaultValue = "0")int rememberme,
                        HttpServletResponse response){
        try{
            Map<String,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24*5);
                }

                response.addCookie(cookie);
                //产生登陆事件
                logger.info("产生登陆事件");
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                    .setActorId((int)map.get("userId"))
                    .setEntityType(EntityType.ENTITY_LOGIN)
                    .setExt("username",username)
                    .setExt("email","toutiaoxpx@163.com")
                );
                logger.info("登陆成功");
                return ToutiaoUtil.getJSONString(0, "登陆成功");
            }else{
                logger.info("登陆异常,不存在该用户或密码不正确");
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("登陆异常："+e);
            return ToutiaoUtil.getJSONString(1,"登陆异常");
        }
    }

    @RequestMapping(path={"/logout/"},method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
