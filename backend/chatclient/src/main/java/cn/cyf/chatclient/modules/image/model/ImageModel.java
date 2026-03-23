package cn.cyf.chatclient.modules.image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageModel {
    private Long id;
    private String userid;
    private String model_name;
    private String prompt;
    private String negativePrompt;
    private Integer steps;
    private Integer cfg_scale;
    private MultipartFile image_file;
    private String style;
    private String model_url;
    private Integer width;
    private Integer height;
}
