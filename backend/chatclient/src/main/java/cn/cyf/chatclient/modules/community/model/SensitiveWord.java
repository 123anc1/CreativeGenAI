package cn.cyf.chatclient.modules.community.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 敏感词实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWord {
    private Integer id;           // 主键ID
    private String word;          // 敏感词内容
    private Integer status;       // 启用状态：1-启用，0-禁用
    private Integer level;        // 敏感等级：1-低，2-中，3-高
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}