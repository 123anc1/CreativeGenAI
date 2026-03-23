package cn.cyf.chatclient.modules.community.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Imagepost {
    private Integer id;
    private Integer userId;
    private Integer imageId;
    private String name;
    private String imageUrl;
    private String thumbnailUrl;
    private byte[] imageData;
    private String title;
    private String caption;
    private int status;
    private LocalDateTime publishedAt;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private int collectionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String negativePrompt;
    private String steps;
    private String cfgScale;
    private String modelName;
    private String style;
    private Integer width;
    private Integer height;
}
