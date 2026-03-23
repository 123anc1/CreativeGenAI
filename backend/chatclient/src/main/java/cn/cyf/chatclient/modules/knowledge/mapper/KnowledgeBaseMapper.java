package cn.cyf.chatclient.modules.knowledge.mapper;

import cn.cyf.chatclient.modules.document.model.WorldGlobal;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    @Select("SELECT * FROM global_file WHERE user_id = #{userId}")
    List<WorldGlobal> listGlobals(String userId);

    @Select("SELECT md5 FROM global_file WHERE filename = #{fileName} AND user_id = #{userId}")
    List<String> findMd5Byfilename(String fileName,String userId);

    @Delete("DELETE FROM global_file WHERE filename = #{fileName} AND user_id = #{userId}")
    void deleteByfilename(String fileName,String userId);
}
