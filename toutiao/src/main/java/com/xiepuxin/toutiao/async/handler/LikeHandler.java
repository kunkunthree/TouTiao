package com.xiepuxin.toutiao.async.handler;

import com.xiepuxin.toutiao.async.EventHandler;
import com.xiepuxin.toutiao.async.EventModel;
import com.xiepuxin.toutiao.async.EventType;
import com.xiepuxin.toutiao.model.Message;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.service.MessageService;
import com.xiepuxin.toutiao.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Component
public class LikeHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        User userFrom = userService.getUser(model.getActorId());
        User userTo = userService.getUser(model.getActorId());
        message.setContent("用户"+userFrom.getName()+"赞了你的资讯");
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        message.setConversationId(message.getFromId() < message.getToId() ?
                message.getFromId()+"_"+message.getToId() :
                message.getToId()+"_"+message.getFromId());
        messageService.addMessage(message);
        logger.info("like doHandle , 用户"+userFrom.getName()+"赞了"+model.getExt("toUserName")+"的资讯");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
