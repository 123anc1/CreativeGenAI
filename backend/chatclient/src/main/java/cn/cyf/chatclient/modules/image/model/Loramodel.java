package cn.cyf.chatclient.modules.image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loramodel {
    private String id;              // 唯一ID（可以是文件hash或自定义标识）
    private String name;            // 显示名称
    private String filePath;        // LoRA文件路径或HF ID
    private String baseModelId;     // 关联 base_models.id
    private String loraType;        // 类型：character/style/pose等
    private Integer rank;           // LoRA rank值，默认4
    private Float alpha;            // alpha值，默认4.0
    private String version;         // 版本号
    private String author;          // 作者
    private String description;     // 描述
    private String previewImage;    // 预览图路径/URL
    private String tags;            // 标签数组，JSON字符串
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}
