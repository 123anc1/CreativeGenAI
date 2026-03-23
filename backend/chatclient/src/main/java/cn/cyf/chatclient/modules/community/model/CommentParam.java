package cn.cyf.chatclient.modules.community.model;

import lombok.Data;

/**
 * 评论查询参数
 */
@Data
public class CommentParam {
    private Integer postId;       // 图片帖子ID
    private Integer page = 1;     // 当前页码
    private Integer pageSize = 10; // 每页数量
    private Integer parentId;     // 父评论ID（用于查询特定评论的回复）
}