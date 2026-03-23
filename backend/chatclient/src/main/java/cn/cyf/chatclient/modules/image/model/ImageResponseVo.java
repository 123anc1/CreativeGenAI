package cn.cyf.chatclient.modules.image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseVo {
    private String model_name;
    private String prompt;
    private String Negative_prompt;
    private Integer steps;
    private Integer cfg_scale;
    private String image_url;
    private String style;
    private Integer width;
    private Integer height;
}
