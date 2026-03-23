package cn.cyf.chatclient.modules.session.mapper;


import cn.cyf.chatclient.modules.session.model.SessionVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SessionMapper {

    void create(SessionVo sessionVo);

    List<SessionVo> list(Integer userid);

    @Select("select * from chat_session where is_active = true and user_id=#{userid}")
    SessionVo active(SessionVo sessionVo);

    @Select("update chat_session set title = #{title} where session_id = #{sessionid}")
    void update(SessionVo sessionVo);

    @Delete("DELETE from chat_session where session_id = #{sessionid} and user_id = #{userid}")
    void delete(SessionVo sessionVo);

    @Select("select * from chat_session where session_id = #{sessionid}")
    SessionVo chack(SessionVo sessionVo);
}
