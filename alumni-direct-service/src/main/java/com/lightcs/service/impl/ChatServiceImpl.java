package com.lightcs.service.impl;

import cn.hutool.core.lang.UUID;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.ChatMapper;
import com.lightcs.mapper.UserMapper;
import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.pojo.ChatSession;
import com.lightcs.model.pojo.ChatUserSession;
import com.lightcs.model.pojo.JobApplyRecord;
import com.lightcs.model.vo.ChatSessionVO;
import com.lightcs.service.ChatService;
import com.lightcs.service.JobApplyRecordService;
import com.lightcs.utils.CurrentUserUtil;
import com.lightcs.ws.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_FILE;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description: 聊天服务实现类
 * @Version: 1.0
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JobApplyRecordService jobApplyRecordService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveMessage(MessageDTO messageDTO) {

        String sessionId = chatMapper.getSessionIdByUserIdAndContactId(messageDTO.getFromId(), messageDTO.getToId());
        if (sessionId == null) {//首次会话
            // 保存用户会话
            sessionId = UUID.randomUUID().toString();
            ChatUserSession chatSession = new ChatUserSession();
            chatSession.setSessionId(sessionId);
            chatSession.setUserId(messageDTO.getFromId());
            chatSession.setContactId(messageDTO.getToId());
            chatMapper.insertUserSession(chatSession);
            //保存会话和最后一条消息
            chatMapper.insertSession(ChatSession.builder()
                    .sessionId(sessionId)
                    .lastMessage(messageDTO.getMessageContent())
                    .lastReceiveTime(messageDTO.getTime())
                    .jobId(messageDTO.getJobId())//更新会话的职位ID
                    .build());
        } else {
            //更新会话的最后一条消息
            chatMapper.updateLastMessage(ChatSession.builder()
                    .sessionId(sessionId)
                    .lastMessage(messageDTO.getMessageContent())
                    .lastReceiveTime(messageDTO.getTime())
                    .jobId(messageDTO.getJobId())//更新会话的职位ID,不为空则修改
                    .build());
        }
        //保存消息
        chatMapper.saveTextMessage(ChatMessage.builder()
                .messageContent(messageDTO.getMessageContent())
                .messageType(messageDTO.getMessageType())      //文件类型
                .contactId(messageDTO.getToId())
                .sendUserId(messageDTO.getFromId())
                .sendTime(messageDTO.getTime())
                .sessionId(sessionId)
                .fileName(messageDTO.getFileName())//文件名称
                .fileSize(messageDTO.getFileSize())//文件大小
                .status(messageDTO.getStatus())//消息状态:0未发送，1已发送
                .build());
        //发送简历则保存简历投递记录
        if (Objects.equals(messageDTO.getMessageType(), MESSAGE_TYPE_FILE)) {
            // 通过sessionId获取职位ID
            Integer jobId = chatMapper.getJobIdBySessionId(sessionId);
            //保存简历投递记录
            jobApplyRecordService.saveJobApplyRecord(JobApplyRecord.builder()
//                    .jobId(messageDTO.getJobId())   每次发送文件 不需要职位ID通过WS传送过来，直接通过数据库查询出来即可
                    .jobId(jobId)
                    .applicantId(messageDTO.getFromId())//申请人ID
                    .resume(messageDTO.getMessageContent())//简历地址
                    .build());
        }

    }

    /**
     * 获取未发送的消息
     *
     * @param concatId 联系人ID
     * @return
     */
    @Override
    public List<ChatMessage> getUnSendMessage(Integer concatId) {
        return chatMapper.getUnSendMessage(concatId);
    }

    /**
     * 获取消息历史记录
     *
     * @param historyTime 历史时间
     * @return
     */
    @Override
    public List<ChatMessage> getMessageHistory(String sessionId, Date historyTime) {
        // 判断该用户是否有权限查看该会话的消息
        Integer count = chatMapper.getSessionCountBySessionIdAndUserId(sessionId, CurrentUserUtil.getCurrentUserId());
        ThrowUtils.throwIf(count == 0, ErrorCode.OPERATION_ERROR, "参数异常");
        List<ChatMessage> messageHistory = chatMapper.getMessageHistory(sessionId, historyTime);
        Collections.reverse(messageHistory);
        return messageHistory;
    }

    @Override
    public List<ChatSessionVO> getSessionList() {
        //获取用户
        Integer userId = CurrentUserUtil.getCurrentUserId();
        //获取会话ID列表
        List<ChatSessionVO> chatSessionList = chatMapper.getChatSessionList(userId);

        if (Objects.isNull(chatSessionList) || chatSessionList.isEmpty()) {
            return Collections.emptyList();
        }
        //获取会话ID列表
        List<String> sessionIdList = chatSessionList.stream().map(ChatSessionVO::getSessionId).toList();

        //获取会话信息列表
        List<ChatSession> sessionList = chatMapper.getSessionList(sessionIdList);
        //聚合会话信息
        for (ChatSession session : sessionList) {
            for (ChatSessionVO chatSessionVO : chatSessionList) {
                if (session.getSessionId().equals(chatSessionVO.getSessionId())) {
                    chatSessionVO.setLastMessage(session.getLastMessage());
                    chatSessionVO.setLastReceiveTime(session.getLastReceiveTime());
                    break;
                }
            }
        }
        // 根据最后一条消息时间降序排序
        chatSessionList.sort((o1, o2) -> {
            if (o1.getLastReceiveTime() == null || o2.getLastReceiveTime() == null) {
                return 0;
            }
            return o2.getLastReceiveTime().compareTo(o1.getLastReceiveTime());
        });

        //返回 会话列表
        return chatSessionList;
    }

}
