package com.xiepuxin.toutiao.async.handler;

import com.xiepuxin.toutiao.async.EventHandler;
import com.xiepuxin.toutiao.async.EventModel;
import com.xiepuxin.toutiao.async.EventType;
import com.xiepuxin.toutiao.model.Message;
import com.xiepuxin.toutiao.service.MessageService;
import com.xiepuxin.toutiao.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler{
    private static final Logger logger = LoggerFactory.getLogger(LoginExceptionHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {

        //判断是否异常登陆
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setFromId(1);
        message.setConversationId(message.getFromId() < message.getToId() ?
                message.getFromId()+"_"+message.getToId() :
                message.getToId()+"_"+message.getFromId());
        message.setHasRead(0);
        message.setCreatedDate(new Date());
        message.setContent("你上次登陆ip异常");
        messageService.addMessage(message);

        logger.info("登陆异常");

        Map<String,Object> map = new HashMap<>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(
                model.getExt("email"), "登录异常",
                "mails/loginException.ftl",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
