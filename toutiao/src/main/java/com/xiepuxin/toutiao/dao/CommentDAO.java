package com.xiepuxin.toutiao.dao;

import com.xiepuxin.toutiao.model.Comment;
import com.xiepuxin.toutiao.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, user_id, created_date,  entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME,"(", INSERT_FIELDS,
            ") values(#{content}, #{userId}, #{createdDate}, #{entityId}, #{entityType}, #{status})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS, " from ",TABLE_NAME, " where id = #{id}"})
    Comment selectById(int id);

    @Select({"select ",SELECT_FIELDS, " from ",TABLE_NAME, " where entity_type=#{entityType} and entity_id=#{entityId} order by id desc "})
    List<Comment> selectByEntity(@Param("entityType")int entityType, @Param("entityId")int entityId);

    @Update({"update ",TABLE_NAME, "set content=#{content} where id=#{id}"})
    void updateContent(Comment comment);

    @Update({"update ",TABLE_NAME, "set status=#{status} where id=#{id}"})
    void updateStatusById(@Param("id")int commentId, @Param("status") int status);

    @Delete({"delete from ",TABLE_NAME , " where id=#{id}"})
    void deleteById(int id);

    @Select({"select count(id) from", TABLE_NAME, " where entity_type=#{entityType} and entity_id=#{entityId}"})
    int getCommentCount(@Param("entityType")int entityType, @Param("entityId")int entityId);

}
