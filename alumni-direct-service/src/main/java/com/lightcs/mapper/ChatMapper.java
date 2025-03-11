package com.lightcs.mapper;

import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.pojo.ChatSession;
import com.lightcs.model.pojo.ChatUserSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

public interface ChatMapper {
    @Select("select session_id from chat_user_session where user_id = #{userId} and contact_id = #{contactId}")
    String getSessionIdByUserIdAndContactId(Integer userId, Integer contactId);

    @Insert("insert into chat_user_session(user_id, contact_id,session_id) values(#{userId}, #{contactId},#{sessionId})")
    Integer insertSession(ChatUserSession chatUserSession);

    @Insert("insert into chat_message(session_id, message_type, message_content, send_user_id, send_user_nickname, send_time, contact_id) values(#{sessionId}, #{messageType}, #{messageContent}, #{sendUserId}, #{sendUserNickname}, #{sendTime}, #{contactId})")
    Integer saveTextMessage(ChatMessage chatMessage);

    @Update("update chat_session set last_message = #{msg}, last_time = #{time} where session_id = #{sessionId}")
    Integer updateLastMessage(ChatSession chatSession);

    @Select("select * from chat_message where contact_id = #{contactId} and status = 0 order by send_time desc")
    List<ChatMessage> getUnSendMessage(Integer contactId);

    @Select("select * from chat_message where contact_id = #{contactId} and send_time < #{historyTime} order by send_time desc limit 10")
    List<ChatMessage> getMessageHistory(Integer userId, Integer contactId, Date historyTime);
}
