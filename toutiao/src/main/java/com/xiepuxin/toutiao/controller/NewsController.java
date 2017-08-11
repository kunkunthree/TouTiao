package com.xiepuxin.toutiao.controller;

import com.xiepuxin.toutiao.model.*;
import com.xiepuxin.toutiao.service.*;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = {"/uploadImage/"},method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try{
//            String fileUrl = newsService.saveImage(file);
            String fileUrl = aliyunService.saveImage(file);
            if(fileUrl == null){
                logger.error("上传图片失败,文件地址返回null");
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch(Exception e){
            logger.error("上传图片失败："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }
    }

    @RequestMapping(path = {"/image"},method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name")String imageName,
                           HttpServletResponse response){
        int doPos = imageName.lastIndexOf(".");
        if(doPos < 0){
//            return ToutiaoUtil.getJSONString(1,"没有文件后缀名");
        }
        String ext = imageName.substring(doPos+1);
        if(!ToutiaoUtil.isFileAllowed(ext)){
//            return ToutiaoUtil.getJSONString(1,"不是要求的文件后缀名");
        }
        response.setContentType("image/"+ext);
        try {
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName))
                    , response.getOutputStream());
        }catch(Exception e){
            logger.error("获取图片异常："+e.getMessage());
//            return ToutiaoUtil.getJSONString(1,"获取图片异常");
        }
    }

    @RequestMapping(path = {"/user/addNews/"},method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image")String image,
                          @RequestParam("title")String title,
                          @RequestParam("link")String link){
        try{
            News news = new News();
            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //匿名id
                news.setUserId(10086);
            }
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            news.setLikeCount(0);
            news.setCommentCount(0);
            news.setCreatedDate(new Date());
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        }catch(Exception e){
            logger.error("发布资讯失败："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布资讯失败");
        }
    }

    @RequestMapping(path = {"/news/{newsId}"},method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId")int newsId,
                             Model model){
        try {
            News news = newsService.getById(newsId);
            int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
            if(localUserId == 0){
                model.addAttribute("like",0);
            }else{
                logger.info("newsId : " + news.getId() + " , like :" + likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
                model.addAttribute("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }
            if (news != null) {
                //评论
                User owner = userService.getUser(news.getUserId());
                if (owner == null) {
                    return ToutiaoUtil.getJSONString(1, "缺少发布人信息");
                }
                model.addAttribute("owner", owner);
                List<ViewObject> commentVOs = new ArrayList<>();
                List<Comment> comments = commentService.getCommentsByEntity(EntityType.ENTITY_NEWS, news.getId());
                for (Comment comment : comments) {
                    ViewObject vo = new ViewObject();
                    User user = userService.getUser(comment.getUserId());
                    vo.set("comment", comment);
                    vo.set("user", user);
                    commentVOs.add(vo);
                }
                model.addAttribute("comments", commentVOs);

            }
            model.addAttribute("news", news);
        }catch(Exception e){
            logger.error("获取资讯错误："+e.getMessage());
        }
        return "detail";
    }

    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("entityType")int entityType,
                             @RequestParam("entityId")int entityId,
                             @RequestParam("content")String content){
        try{
            //过滤content里的敏感词
            int count = commentService.getCommentCountByEntity(entityType,entityId);
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityType(entityType);
            comment.setEntityId(entityId);
            comment.setStatus(0);
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            commentService.addComment(comment);
            logger.info("当前评论数："+count + " , 更新后评论数：" + (count+1));
            // 更新评论数量，以后用异步实现
            if(entityType == EntityType.ENTITY_NEWS){
                newsService.updateCommentCount(entityId,count+1);
            }
//            return ToutiaoUtil.getJSONString(0);
        }catch(Exception e){
            logger.error("发表评论失败："+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发表评论失败");
        }
        String entity_type = "";
        switch (entityType){
            case EntityType.ENTITY_NEWS:
                entity_type = "news";
            default:

        }
        return "redirect:/" + entity_type + "/" + String.valueOf(entityId);
    }
}
