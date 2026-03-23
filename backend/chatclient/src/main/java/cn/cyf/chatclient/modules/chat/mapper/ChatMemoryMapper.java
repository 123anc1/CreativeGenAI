package cn.cyf.chatclient.modules.chat.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface ChatMemoryMapper {

    @Insert("INSERT INTO spring_ai_chat_memory(conversation_id, content, type,timestamp) values (#{conversationId},#{content},'ASSISTANT_REASONING',#{createDat})")
    void insertQuery(String conversationId, String content, LocalDateTime createDat);
}
