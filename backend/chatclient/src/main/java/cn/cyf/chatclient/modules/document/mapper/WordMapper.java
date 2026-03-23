package cn.cyf.chatclient.modules.document.mapper;


import cn.cyf.chatclient.modules.document.model.WorldGlobal;
import cn.cyf.chatclient.modules.document.model.WorldSession;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordMapper {

    @Select("SELECT md5 FROM session_file WHERE user_id = #{userId} AND session_id = #{sessionId}")
    List<String> findMd5sBySessionId(String userId, String sessionId);

    @Insert("INSERT INTO global_file(md5, filename, size,user_id) VALUES(#{md5},#{filename},#{size},#{userid})")
    void insertGlobal(WorldGlobal worldGlobal);

    @Insert("INSERT INTO session_file(md5, filename, chunk_count, user_id, session_id) VALUES(#{md5},#{filename},#{chunkcount},#{userid},#{sessionid})")
    void insertSession(WorldSession worldSession);

    @Delete("DELETE FROM session_file WHERE user_id = #{userId} AND session_id = #{sessionId}")
    void deleteBySessionId(String userId, String sessionId);

    @Select("SELECT md5 FROM session_file WHERE filename = #{fileName} AND session_id = #{sessionId}")
    List<String> findMd5sByfilenameid(String fileName, String sessionId);

    @Delete("DELETE FROM session_file WHERE filename = #{fileName} AND session_id = #{sessionId}")
    void deleteByfilenameid(String fileName, String sessionId);


    @Select("SELECT * FROM session_file WHERE session_id = #{sessionId}")
    List<WorldSession> listSession(String sessionId);

    /**
     * 统计总文档数
     */
    @Select("SELECT (SELECT COUNT(*) FROM global_file) + (SELECT COUNT(*) FROM session_file) AS total")
    Integer countAll();

    /**
     * 按日期统计文档上传数
     */
    @Select("SELECT (SELECT COUNT(*) FROM global_file WHERE DATE(created_at) = #{date}) + (SELECT COUNT(*) FROM session_file WHERE DATE(created_at) = #{date}) AS total")
    Integer countByDate(String date);
}
