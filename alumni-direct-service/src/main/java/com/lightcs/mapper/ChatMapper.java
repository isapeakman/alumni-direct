package com.lightcs.mapper;

import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.pojo.ChatSession;
import com.lightcs.model.pojo.ChatUserSession;
import com.lightcs.model.vo.ChatSessionVO;
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

    @Select("select count(0) from chat_user_session where session_id = #{sessionId} and (user_id = #{userId} or contact_id = #{userId})")
    Integer getSessionCountBySessionIdAndUserId(String sessionId, Integer userId);


    Integer insertUserSession(ChatUserSession chatUserSession);


    @Insert("insert into chat_session(session_id,last_message,last_receive_time) values(#{sessionId}, #{lastMessage}, #{lastReceiveTime})")
    Integer insertSession(ChatSession chatSession);

    @Update("update chat_session set last_message = #{lastMessage}, last_receive_time = #{lastReceiveTime} where session_id = #{sessionId}")
    Integer updateLastMessage(ChatSession chatSession);


    @Insert("insert into chat_message(session_id, message_type, message_content, send_user_id, send_user_nickname, send_time, contact_id) values(#{sessionId}, #{messageType}, #{messageContent}, #{sendUserId}, #{sendUserNickname}, #{sendTime}, #{contactId})")
    Integer saveTextMessage(ChatMessage chatMessage);


    @Select("select * from chat_message where contact_id = #{contactId} and status = 0 order by send_time desc")
    List<ChatMessage> getUnSendMessage(Integer contactId);

    @Select("select * from chat_message where session_id = #{sessionId} and send_time < #{historyTime} order by send_time desc limit 10")
    List<ChatMessage> getMessageHistory(String sessionId, Date historyTime);

    @Select("select session_id, contact_id,user.nickname as contactName,user.user_avatar as contactAvatar " +
            "from chat_user_session c\n" +
            "LEFT JOIN user on user.user_id = c.contact_id\n" +
            "where c.user_id = #{userId}")
    List<ChatSessionVO> getChatSessionList(Integer userId);

    List<ChatSession> getSessionList(List<String> sessionIdList);
}
