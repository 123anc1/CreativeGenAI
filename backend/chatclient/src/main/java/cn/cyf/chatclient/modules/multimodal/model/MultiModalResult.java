package cn.cyf.chatclient.modules.multimodal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiModalResult {
    private Long id;
    private Integer uid;
    private String sessionid;
    private String inputType;
    private String inputContent;
    private String intent;
    private String outputType;
    private String outputContent;
    private String outputUrl;
    private String prompt;
    private String modelName;
    private LocalDateTime createdAt;
}
