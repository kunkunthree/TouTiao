package com.xiepuxin.toutiao.service;

import com.xiepuxin.toutiao.dao.MessageDAO;
import com.xiepuxin.toutiao.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    public List<Message> getLatestMessages(int commentId,int offset,int limit){
        return messageDAO.selectByConversationIdAndOffset(commentId,offset,limit);
    }

    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }

    public int getConversationUnreadTotalCount(int userId){
        return messageDAO.getConversationUnreadTotalCount(userId);
    }

    public int getConversationUnreadCount(int userId,String conversationId){
        return messageDAO.getConversationUnreadCount(userId,conversationId);
    }
}
