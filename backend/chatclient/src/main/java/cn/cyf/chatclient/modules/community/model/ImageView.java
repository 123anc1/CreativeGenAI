package cn.cyf.chatclient.modules.community.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageView {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private LocalDateTime viewedAt;
}
