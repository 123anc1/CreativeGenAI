package cn.cyf.chatclient.modules.multimodal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MultiModalMapper {
    @Select("select id from spring_ai_chat_memory where content = #{content}")
    Integer getMemoryId(String content);
}
