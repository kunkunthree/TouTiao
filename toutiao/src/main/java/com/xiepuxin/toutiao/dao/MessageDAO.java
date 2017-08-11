package com.xiepuxin.toutiao.dao;

import com.xiepuxin.toutiao.model.Comment;
import com.xiepuxin.toutiao.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, created_date, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME , "(", INSERT_FIELDS,
            ") values(#{fromId}, #{toId}, #{content}, #{hasRead}, #{createdDate}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS, " from ",TABLE_NAME, " where id = #{id}"})
    Message selectById(int id);

    @Update({"update ",TABLE_NAME, "set content=#{content} where id=#{id}"})
    void updateContent(Message message);

    @Delete({"delete from ",TABLE_NAME , " where id=#{id}"})
    void deleteById(int id);

    List<Message> selectByConversationIdAndOffset(@Param("id")int commentId,
                                       @Param("offset")int offset,
                                       @Param("limit")int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId")String conversationId,
                                        @Param("offset")int offset,
                                        @Param("limit")int limit);

    @Select({"select ",INSERT_FIELDS,", count(id) as id from ",
            " (select * from ", TABLE_NAME ,
            " where from_id=#{userId} or to_id=#{userId} order by id desc) ",
            " tt group by conversation_id order by id desc",
            " limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId")int userId,
                                      @Param("offset")int offset,
                                      @Param("limit")int limit);

    @Select({"select count(id) from ", TABLE_NAME,
            " where has_read = 0 and to_id=#{userId}"})
    int getConversationUnreadTotalCount(@Param("userId")int userId);

    @Select({"select count(id) from ", TABLE_NAME,
            " where has_read = 0 and to_id=#{userId} ",
            "and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId")int userId,
                                        @Param("conversationId")String conversationId);
}
