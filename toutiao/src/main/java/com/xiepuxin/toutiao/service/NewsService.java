package com.xiepuxin.toutiao.service;

import com.xiepuxin.toutiao.controller.IndexController;
import com.xiepuxin.toutiao.dao.NewsDAO;
import com.xiepuxin.toutiao.model.News;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset,int limit){
        return newsDAO.selectByUserIdAndOffset(userId,offset,limit);
    }

    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }

    public News getById(int id){
        return newsDAO.selectById(id);
    }

    public int updateCommentCount(int newsId, int commentCount) {
        return newsDAO.updateCommentCount(newsId, commentCount);
    }

    public int updateLikeCount(int newsId, int likeCount) {
        return newsDAO.updateLikeCount(newsId, likeCount);
    }

    public String saveImage(MultipartFile file) throws IOException{
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        if(doPos < 0){
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(doPos+1).toLowerCase();
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }

//        logger.info("当前文件路径：" + System.getProperty("user.dir"));

        String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
        Files.copy(file.getInputStream(),
                new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        logger.info("上传文件完毕");
        //xxx.jpg
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }


}
