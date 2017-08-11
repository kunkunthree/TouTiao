package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.model.*;
import com.xiepuxin.toutiao.service.LikeService;
import com.xiepuxin.toutiao.service.MessageService;
import com.xiepuxin.toutiao.service.NewsService;
import com.xiepuxin.toutiao.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            if(localUserId == 0){
                vo.set("like",0);
            }else{
                logger.info("newsId : " + news.getId() + " , like :" + likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
                vo.set("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path={"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop",defaultValue = "0")int pop){
        model.addAttribute("vos",getNews(0,0,10));
        model.addAttribute("pop",pop);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId")int userId,
                            @RequestParam(value = "pop",defaultValue = "0")int pop){
        model.addAttribute("vos",getNews(userId,0,10));
        model.addAttribute("pop",pop);
        return "home";
    }

    @RequestMapping(path = "/message")
    @ResponseBody
    public String message(Model model){
        StringBuilder sb = new StringBuilder();
        List<Message> messages = messageService.getLatestMessages(0,0,10);
        for(Message message : messages){
            sb.append("Message Id :" + message.getId() + "<br/>");
            sb.append("Conversation Id :" + message.getConversationId() + "<br/>");
            sb.append("From :" + message.getFromId() + "<br/>");
            sb.append("To :" + message.getToId() + "<br/>");
            sb.append("Created Date :" + message.getCreatedDate() + "<br/>");
            sb.append("Content :" + message.getContent() + "<br/>");
            sb.append("<br/>");
        }
        return sb.toString();
    }
}
