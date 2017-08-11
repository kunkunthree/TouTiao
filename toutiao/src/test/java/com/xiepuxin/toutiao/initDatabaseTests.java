package com.xiepuxin.toutiao;

import com.xiepuxin.toutiao.dao.*;
import com.xiepuxin.toutiao.model.*;
import com.xiepuxin.toutiao.service.AliyunService;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class initDatabaseTests {
    private static final Logger logger = LoggerFactory.getLogger(initDatabaseTests.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private AliyunService aliyunService;

    @Test
    public void initData() {
        Random rand = new Random();
        int imageCount = 46;
        for(int i = 0 ; i < 10 ; i++){
            User user = new User();
            user.setHeadUrl(String.format(ToutiaoUtil.ALIYUN_DOMAIN_PREFIX+"images/%d.jpg",rand.nextInt(46)));
            user.setName("USER"+i);
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            user.setPassword("new Password");
            userDAO.updatePassword(user);

            int commentCount = 3;

            News news = new News();
            news.setCommentCount(commentCount);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 *3600*5*i);
            news.setCreatedDate(date);
            news.setImage(String.format(ToutiaoUtil.ALIYUN_DOMAIN_PREFIX+"images/%d.jpg",rand.nextInt(imageCount)));
            news.setLikeCount(i+1);
            news.setUserId(i+1);
            news.setTitle(String.format("TITLE%d",i+1));
            news.setLink(String.format("http://127.0.0.1:8080/news/%d",i+1));
            newsDAO.addNews(news);


            Message message = new Message();
            message.setContent("message xiepuxin 中文测试：" + i);
            message.setToId(i);
            message.setFromId(9-i);
            message.setConversationId(
                    message.getFromId() < message.getToId() ?
                            message.getFromId()+"_"+message.getToId() :
                            message.getToId()+"_"+message.getFromId());
            message.setCreatedDate(date);
            message.setHasRead(0);
            messageDAO.addMessage(message);

            for(int j = 0 ; j < commentCount ; j++) {
                Comment comment = new Comment();
                comment.setContent("Comment xiepuxin " + j);
                comment.setCreatedDate(date);
                comment.setUserId(user.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setEntityId(news.getId());
                comment.setStatus(0);
                commentDAO.addComment(comment);
            }

//            comment.setContent("xiepuxin , new Comment");
//            commentDAO.updateContent(comment);
//            commentDAO.updateStatusById(comment.getId(),1);

            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setStatus(0);
            loginTicket.setUserId(i+1);
            loginTicket.setExpired(date);
            loginTicket.setTicket(String.format("TICKET%d",i+1));
            loginTicketDAO.addTicket(loginTicket);

        }

//        Assert.assertEquals("new Password",userDAO.selectById(1).getPassword());
//        userDAO.deleteById(1);
//        Assert.assertNull(userDAO.selectById(1));

//        Assert.assertEquals("message xiepuxin 0",messageDAO.selectById(1).getContent());
//        messageDAO.deleteById(1);
//        Assert.assertNull(messageDAO.selectById(1));

//        Assert.assertEquals(0,loginTicketDAO.selectByTicket("TICKET1").getStatus());
//        loginTicketDAO.updateStatus("TICKET1",1);
//        Assert.assertEquals(1,loginTicketDAO.selectByTicket("TICKET1").getStatus());

//        Assert.assertEquals(1,commentDAO.selectById(1).getStatus());
//        commentDAO.updateStatusById(1,0);
//        Assert.assertEquals(0,commentDAO.selectById(1).getStatus());

        User user = new User();
        user.setHeadUrl(String.format(ToutiaoUtil.ALIYUN_DOMAIN_PREFIX+"images/%d.jpg",rand.nextInt(imageCount)));
        user.setName("sola");
        user.setPassword("5201314");
        user.setSalt("");
        user.setPassword(ToutiaoUtil.MD5(user.getPassword()+user.getSalt()));
        userDAO.addUser(user);

        Assert.assertNotNull(userDAO.selectById(11));
        Assert.assertNotNull(userDAO.selectByName("sola"));
    }

//    @Test
    public void uploadProfilePicture() {
        int count = 46;
        String ext = ".jpg";
        String path = "/home/xiepuxin/Pictures/头像/";
        for(int i = 0 ; i < 46 ; i++){
            try{
                File file = new File(path+i+ext);
                aliyunService.saveImageWithFile(file);
            }catch(Exception e){
                System.out.println("文件访问异常："+e);
            }
        }
    }
}
