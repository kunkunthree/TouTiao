package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.model.HostHolder;
import com.xiepuxin.toutiao.model.Message;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.model.ViewObject;
import com.xiepuxin.toutiao.service.MessageService;
import com.xiepuxin.toutiao.service.UserService;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId")int fromId,
                          @RequestParam("toId")int toId,
                          @RequestParam("content")String content){
        try{
            Message message = new Message();
            message.setFromId(fromId);
            message.setToId(toId);
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setHasRead(0);
            message.setConversationId(fromId < toId ? fromId+"_"+toId : toId+"_"+fromId);
            messageService.addMessage(message);
            logger.info(fromId + " to " + toId + " : " + content);
            return ToutiaoUtil.getJSONString(0);
        }catch(Exception e){
            logger.error("发送消息失败："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发送消息失败");
        }
    }

    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String conversationDetail(Model model,
                                     @RequestParam("conversationId")String conversationId){
        logger.info("msg detail");
        try {
            List<ViewObject> messageVOs = new ArrayList<>();
            List<Message> messages = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message message : messages) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("headUrl", userService.getUser(message.getFromId()).getHeadUrl()+"?"+ToutiaoUtil.ALIYUN_PROFILE_PIC_STYLE);
                messageVOs.add(vo);
            }
            model.addAttribute("messages", messageVOs);
        }catch(Exception e){
            logger.error("获取站内信会话列表失败：" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String conversationList(Model model){
        logger.info("msg list");
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", message);
                vo.set("unreadCount",messageService.getConversationUnreadCount(localUserId,message.getConversationId()));
                vo.set("totalCount",messageService.getConversationUnreadTotalCount(localUserId));
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetId);
                vo.set("userId",user.getId());
                vo.set("userName",user.getName());
                vo.set("headUrl", user.getHeadUrl()+"?"+ToutiaoUtil.ALIYUN_PROFILE_PIC_STYLE);
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        }catch(Exception e){
            logger.error("获取站内信会话分组列表失败：" + e.getMessage());
        }
        return "letter";
    }
}
