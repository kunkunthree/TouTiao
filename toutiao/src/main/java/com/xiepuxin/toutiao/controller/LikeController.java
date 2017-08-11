package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.async.EventModel;
import com.xiepuxin.toutiao.async.EventProducer;
import com.xiepuxin.toutiao.async.EventType;
import com.xiepuxin.toutiao.model.EntityType;
import com.xiepuxin.toutiao.model.HostHolder;
import com.xiepuxin.toutiao.model.News;
import com.xiepuxin.toutiao.service.LikeService;
import com.xiepuxin.toutiao.service.NewsService;
import com.xiepuxin.toutiao.service.UserService;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = {"/like"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String likeNews(@RequestParam("newsId")int newsId){
        logger.info("newsId:" + newsId + " , like");
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);

        //产生一个like事件
        News news = newsService.getById(newsId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
            .setActorId(hostHolder.getUser().getId())
            .setEntityType(EntityType.ENTITY_NEWS)
            .setEntityId(newsId)
            .setEntityOwnerId(news.getUserId())
            .setExt("toUserName",userService.getUser(news.getUserId()).getName()+""));


        return ToutiaoUtil.getJSONString(0,likeCount+"");
    }

    @RequestMapping(path = {"/dislike"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislikeNews(@RequestParam("newsId")int newsId){
        logger.info("newsId:" + newsId + " , dislike");
        int userId = hostHolder.getUser().getId();
        long dislikeCount = likeService.dislike(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)dislikeCount);
        return ToutiaoUtil.getJSONString(0,dislikeCount+"");
    }

}
