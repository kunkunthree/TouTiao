package com.xiepuxin.toutiao.service;

import com.xiepuxin.toutiao.dao.CommentDAO;
import com.xiepuxin.toutiao.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentDAO commentDAO;

    public void addComment(Comment comment){
        commentDAO.addComment(comment);
    }

    public List<Comment> getCommentsByEntity(int entityType, int entityId){
        return commentDAO.selectByEntity(entityType,entityId);
    }

    public Comment getCommentById(int commentId){
        return commentDAO.selectById(commentId);
    }

    public int getCommentCountByEntity(int entityType,int entityId){
        return commentDAO.getCommentCount(entityType,entityId);
    }

    public void deleteCommentById(int commentId){
        commentDAO.deleteById(commentId);
    }

    public void updateComment(Comment comment){
        commentDAO.updateContent(comment);
    }

    public void deleteComment(int commentId){
        commentDAO.updateStatusById(commentId,1);
    }
}
