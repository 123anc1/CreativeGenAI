package cn.cyf.chatclient.modules.community.mapper;

import cn.cyf.chatclient.modules.community.model.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 敏感词Mapper
 */
@Mapper
public interface SensitiveWordMapper {
    
    /**
     * 获取所有启用的敏感词
     */
    @Select("SELECT * FROM sensitive_word WHERE status = 1")
    List<SensitiveWord> getAllEnabledSensitiveWords();
    
    /**
     * 批量插入敏感词
     */
    @Insert("<script>" +
            "INSERT IGNORE INTO sensitive_word (word, status, level, created_at, updated_at) " +
            "VALUES " +
            "<foreach collection=\"words\" item=\"word\" separator=\",\">" +
            "(#{word.word}, #{word.status}, #{word.level}, #{word.createdAt}, #{word.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("words") List<SensitiveWord> words);
}