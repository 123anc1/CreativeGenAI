package cn.cyf.chatclient.modules.multimodal.mapper;

import cn.cyf.chatclient.modules.multimodal.model.MultiModalResult;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MultiModalResultMapper {
    @Insert("INSERT INTO spring_ai_multimodal_result (uid, sessionid, input_type, input_content, intent, output_type, output_content, output_url, prompt, model_name, created_at) VALUES (#{uid}, #{sessionid}, #{inputType}, #{inputContent}, #{intent}, #{outputType}, #{outputContent}, #{outputUrl}, #{prompt}, #{modelName}, #{createdAt})")
    void insert(MultiModalResult result);

    @Select("SELECT * FROM spring_ai_multimodal_result WHERE uid = #{uid} ORDER BY created_at ASC")
    List<MultiModalResult> findByUid(Integer uid);

    @Select("SELECT * FROM spring_ai_multimodal_result WHERE uid = #{uid} AND sessionid = #{sessionid} ORDER BY created_at ASC")
    List<MultiModalResult> findByUidAndSessionid(Integer uid, String sessionid);

    @Select("SELECT * FROM spring_ai_multimodal_result WHERE id = #{id}")
    MultiModalResult findById(Long id);

    @Delete("DELETE FROM spring_ai_multimodal_result WHERE sessionid = #{sessionid}")
    void deleteBySessionid(String sessionid);

    /**
     * 统计总多模态交互数
     */
    @Select("SELECT COUNT(*) FROM spring_ai_multimodal_result")
    Integer countAll();

    /**
     * 按日期统计多模态交互数
     */
    @Select("SELECT COUNT(*) FROM spring_ai_multimodal_result WHERE DATE(created_at) = #{date}")
    Integer countByDate(String date);
}
