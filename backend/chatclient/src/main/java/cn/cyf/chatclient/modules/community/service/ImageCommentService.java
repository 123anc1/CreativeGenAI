package cn.cyf.chatclient.modules.community.service;



import cn.cyf.chatclient.modules.community.model.CommentParam;
import cn.cyf.chatclient.modules.community.model.ImageComment;
import cn.cyf.chatclient.modules.community.model.ImageCommentVO;
import cn.cyf.chatclient.modules.community.model.PageResult;

import java.util.List;

/**
 * 图片评论服务接口
 */
public interface ImageCommentService {
    
    /**
     * 发布评论
     */
    void addComment(ImageComment comment);
    
    /**
     * 回复评论
     */
    void replyComment(ImageComment comment);
    
    /**
     * 获取图片的所有评论（分页）
     */
    PageResult<ImageCommentVO> getCommentsByPostId(CommentParam param);
    
    /**
     * 获取评论的回复列表
     */
    List<ImageComment> getRepliesByParentId(Integer parentId);
    
    /**
     * 删除评论
     */
    void deleteComment(Integer commentId, Integer userId);
    
    /**
     * 点赞评论
     */
    void likeComment(Integer commentId);
    
    /**
     * 取消点赞评论
     */
    void unlikeComment(Integer commentId);
    
    /**
     * 根据ID获取评论详情
     */
    ImageComment getCommentById(Integer id);
    
    /**
     * 统计图片评论数
     */
    Integer countCommentsByPostId(Integer postId);
}