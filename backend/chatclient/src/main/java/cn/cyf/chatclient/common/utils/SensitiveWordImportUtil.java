package cn.cyf.chatclient.common.utils;

import cn.cyf.chatclient.modules.community.mapper.SensitiveWordMapper;
import cn.cyf.chatclient.modules.community.model.SensitiveWord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词批量导入工具
 */
@Slf4j
@Component
public class SensitiveWordImportUtil {
    
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;
    
    /**
     * 从文本文件批量导入敏感词
     * @param filePath 文本文件路径，每行一个敏感词
     * @param level 敏感等级：1-低，2-中，3-高
     * @return 导入成功的数量
     */
    public int importFromFile(String filePath, int level) {
        int count = 0;
        List<SensitiveWord> sensitiveWords = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    SensitiveWord word = new SensitiveWord();
                    word.setWord(line);
                    word.setStatus(1); // 启用状态
                    word.setLevel(level);
                    word.setCreatedAt(LocalDateTime.now());
                    word.setUpdatedAt(LocalDateTime.now());
                    sensitiveWords.add(word);
                    
                    // 每1000条批量插入一次
                    if (sensitiveWords.size() >= 1000) {
                        count += batchInsert(sensitiveWords);
                        sensitiveWords.clear();
                    }
                }
            }
            
            // 插入剩余的敏感词
            if (!sensitiveWords.isEmpty()) {
                count += batchInsert(sensitiveWords);
            }
            
            log.info("敏感词导入完成，共导入{}条", count);
        } catch (IOException e) {
            log.error("导入敏感词失败", e);
        }
        
        return count;
    }
    
    /**
     * 批量插入敏感词
     */
    private int batchInsert(List<SensitiveWord> sensitiveWords) {
        try {
            int result = sensitiveWordMapper.batchInsert(sensitiveWords);
            log.info("批量插入{}条敏感词成功", result);
            return result;
        } catch (Exception e) {
            log.error("批量插入敏感词失败", e);
            return 0;
        }
    }
}