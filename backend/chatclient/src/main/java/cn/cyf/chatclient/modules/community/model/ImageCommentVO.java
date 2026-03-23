package cn.cyf.chatclient.modules.community.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片评论VO类（用于前端展示，包含回复列表）
 */
@Data
public class ImageCommentVO {
    private Integer id;           // 评论ID
    private Integer postId;       // 图片帖子ID
    private Integer userId;       // 用户ID
    private String userName;      // 用户名
    private String content;       // 评论内容
    private Integer parentId;     // 父评论ID（null表示一级评论）
    private Integer replyUserId;  // 被回复用户ID
    private String replyUserName; // 被回复用户名
    private Integer likeCount;    // 点赞数
    private Integer status;       // 状态：0-正常，1-删除
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
    
    // 回复列表
    private List<ImageCommentVO> replies;
    
    // 回复总数（用于显示"查看更多回复"）
    private Integer replyCount;
}