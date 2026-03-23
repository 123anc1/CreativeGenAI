package cn.cyf.chatclient.modules.image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Basemodel {
    private String id;              // 模型标识，如 "runwayml/stable-diffusion-v1-5"
    private String name;            // 显示名称，如 "Stable Diffusion 1.5"
    private String modelType;       // 模型类型：sd15 / sdxl / sdxl-turbo / lcm / etc.
    private String repoId;          // HuggingFace 仓库ID（可选）
    private String localPath;       // 本地路径（可选）
    private String description;     // 描述
    private Boolean isEnabled;      // 是否启用，默认TRUE
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}
