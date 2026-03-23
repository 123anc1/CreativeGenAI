package cn.cyf.chatclient.modules.canvas.mapper;

import cn.cyf.chatclient.modules.canvas.model.CanvasSessionImage;
import cn.cyf.chatclient.modules.canvas.model.SessionInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CanvasMapper {

    @Insert("insert into canvas_session(id,user_id,text,created_at,updated_at) values(#{sessionId},#{uid},#{text},#{createdAt},#{updatedAt})")
    void sessionCreate(SessionInfo sessionInfo);

    @Insert("insert into canvas_session_image(id,image_id,session_id,image_url,created_at,updated_at)values(#{imageId},#{userImage},#{sessionId},#{imageUrl},#{createdAt},#{updatedAt})")
    void saveImage(String imageId,Integer userImage, String sessionId, String imageUrl, LocalDate createdAt, LocalDate updatedAt);

    @Select("select id from user_image where image_data=#{path}")
    Integer check(String path);

    @Select("select * from canvas_session where user_id=#{userId}")
    @Results(id = "sessionInfo", value = {
            @Result(property = "uid",column = "user_id"),
            @Result(property = "sessionId",column = "id")
    })
    List<SessionInfo> sessionList(String userId);

    @Select("select * from canvas_session_image where session_id=#{sessionId}")
    List<CanvasSessionImage> getSessionImage(String sessionId);

    @Update("update canvas_session set text=#{text} where id=#{sessionId}")
    void sessionUpdate(String sessionId, String text);

    @Select("SELECT image_url FROM canvas_session_image where session_id=#{id} AND image_id IS NULL")
    List<String> checkImageDelete(String id);

    @Delete("delete from canvas_session where id=#{id}")
    void sessionDelete(String id);

    @Select("select * from canvas_session_image where id=#{id}")
    CanvasSessionImage chackSessionImage(String id);

    @Delete("delete from canvas_session_image where id=#{id}")
    void sessionImageDelete(String imageUrl);
}
