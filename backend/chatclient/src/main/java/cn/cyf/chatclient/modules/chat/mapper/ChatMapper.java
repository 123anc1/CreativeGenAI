package cn.cyf.chatclient.modules.chat.mapper;


import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.chat.model.ChatSqlmsg;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMapper {

    @Select("select * from spring_ai_chat_memory where conversation_id=#{conversationid}")
    List<ChatSqlmsg> chatMsg(String conversationid);

    @Update("UPDATE chat_session SET is_active = false where session_id = #{sessionid}")
    void change(ChatMsg chatMsg);

    @Delete("DELETE FROM spring_ai_chat_memory WHERE conversation_id = #{coversationId}")
    void delete(String coversationId);
}
