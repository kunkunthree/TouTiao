package com.xiepuxin.toutiao.dao;

import com.xiepuxin.toutiao.model.Message;
import com.xiepuxin.toutiao.model.News;
import com.xiepuxin.toutiao.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME , "(", INSERT_FIELDS,
            ")values(#{title}, #{link}, #{image}, #{likeCount}, #{commentCount}, #{createdDate}, #{userId} )"})
    int addNews(News news);

    @Select({"select ",SELECT_FIELDS, " from ",TABLE_NAME, " where id = #{id}"})
    News selectById(int id);

    @Update({"update ",TABLE_NAME, "set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    List<News> selectByUserIdAndOffset(@Param("userId")int userId,
                                       @Param("offset")int offset,
                                       @Param("limit")int limit);

    @Update({"update ",TABLE_NAME, "set like_count=#{likeCount} where id=#{id}"})
    int updateLikeCount(@Param("id") int newsId, @Param("likeCount") int likeCount);
}
