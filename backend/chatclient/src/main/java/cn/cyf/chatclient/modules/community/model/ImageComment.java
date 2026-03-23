package cn.cyf.chatclient.modules.community.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图片评论实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageComment {
    private Integer id;           // 评论ID
    private Integer postId;       // 图片帖子ID
    private Integer userId;       // 用户ID
    private String userName;      // 用户名（冗余字段，便于查询）
    private String content;       // 评论内容
    private Integer parentId;     // 父评论ID（用于回复功能，null表示一级评论）
    private Integer replyUserId;  // 被回复用户ID
    private String replyUserName; // 被回复用户名
    private Integer likeCount;    // 点赞数
    private Integer status;       // 状态：0-正常，1-删除
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}