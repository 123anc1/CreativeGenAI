package cn.cyf.chatclient.modules.community.service.Impl;

import cn.cyf.chatclient.modules.community.mapper.ImageCommentMapper;
import cn.cyf.chatclient.modules.community.service.ImageCommentService;
import cn.cyf.chatclient.modules.user.model.User;
import cn.cyf.chatclient.modules.user.service.UserService;
import cn.cyf.chatclient.modules.community.model.ImageComment;
import cn.cyf.chatclient.modules.community.model.CommentParam;
import cn.cyf.chatclient.modules.community.model.PageResult;
import cn.cyf.chatclient.modules.community.model.ImageCommentVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片评论服务实现类
 */
@Slf4j
@Service
public class ImageCommentServiceImpl implements ImageCommentService {

    @Autowired
    private ImageCommentMapper imageCommentMapper;
    
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void addComment(ImageComment comment) {
        // 验证用户是否存在
        User user = userService.findById(comment.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 设置默认值
        comment.setUserName(user.getName());
        comment.setParentId(null); // 一级评论
        comment.setLikeCount(0);
        comment.setStatus(0); // 正常状态
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // 插入评论
        imageCommentMapper.insert(comment);
        
        log.info("用户 {} 发布了评论，评论ID: {}", user.getName(), comment.getId());
    }

    @Override
    @Transactional
    public void replyComment(ImageComment comment) {
        // 验证用户是否存在
        User user = userService.findById(comment.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证父评论是否存在
        ImageComment parentComment = imageCommentMapper.selectById(comment.getParentId());
        if (parentComment == null) {
            throw new RuntimeException("被回复的评论不存在");
        }
        
        // 设置回复相关信息
        comment.setUserName(user.getName());
        comment.setReplyUserId(parentComment.getUserId());
        comment.setReplyUserName(parentComment.getUserName());
        comment.setLikeCount(0);
        comment.setStatus(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // 插入回复
        imageCommentMapper.insert(comment);
        
        log.info("用户 {} 回复了评论 {}，回复ID: {}", user.getName(), comment.getParentId(), comment.getId());
    }

    @Override
    public PageResult<ImageCommentVO> getCommentsByPostId(CommentParam param) {
        if (param == null || param.getPage() == null || param.getPageSize() == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(param.getPage(), param.getPageSize());
        
        List<ImageComment> commentList = imageCommentMapper.selectByPostId(param);
        
        // 转换为VO并加载回复列表
        List<ImageCommentVO> voList = commentList.stream().map(this::convertToVOWithReplies).toList();
        
        Page<ImageComment> pageResult = (Page<ImageComment>) commentList;
        return new PageResult<>(pageResult.getTotal(), voList);
    }
    
    /**
     * 加载回复
     */
    private ImageCommentVO convertToVOWithReplies(ImageComment comment) {
        ImageCommentVO vo = convertToVO(comment);
        
        // 加载回复列表
        List<ImageComment> replies = imageCommentMapper.selectRepliesByParentId(comment.getId());
        vo.setReplyCount(replies.size());
        
        // 转换回复为VO格式（限制显示前3条）
        List<ImageCommentVO> replyVOs = replies.stream()
            .limit(3)
            .map(this::convertToVO)
            .toList();
        
        vo.setReplies(replyVOs);
        return vo;
    }

    @Override
    public List<ImageComment> getRepliesByParentId(Integer parentId) {
        return imageCommentMapper.selectRepliesByParentId(parentId);
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        ImageComment comment = imageCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的评论");
        }
        
        imageCommentMapper.deleteByIdAndUserId(commentId, userId);
        log.info("用户 {} 删除了评论 {}", userId, commentId);
    }

    @Override
    @Transactional
    public void likeComment(Integer commentId) {
        ImageComment comment = imageCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 点赞数+1
        Integer newLikeCount = comment.getLikeCount() + 1;
        imageCommentMapper.updateLikeCount(commentId, newLikeCount);
        
        log.info("评论 {} 获得点赞，当前点赞数: {}", commentId, newLikeCount);
    }

    @Override
    @Transactional
    public void unlikeComment(Integer commentId) {
        ImageComment comment = imageCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        Integer newLikeCount = Math.max(0, comment.getLikeCount() - 1);
        imageCommentMapper.updateLikeCount(commentId, newLikeCount);
        
        log.info("评论 {} 取消点赞，当前点赞数: {}", commentId, newLikeCount);
    }

    @Override
    public ImageComment getCommentById(Integer id) {
        return imageCommentMapper.selectById(id);
    }

    /**
     * 将ImageComment转换为ImageCommentVO的基础方法
     */
    private ImageCommentVO convertToVO(ImageComment comment) {
        ImageCommentVO vo = new ImageCommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setUserName(comment.getUserName());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setReplyUserId(comment.getReplyUserId());
        vo.setReplyUserName(comment.getReplyUserName());
        vo.setLikeCount(comment.getLikeCount());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setUpdatedAt(comment.getUpdatedAt());
        return vo;
    }

    @Override
    public Integer countCommentsByPostId(Integer postId) {
        return imageCommentMapper.countCommentsByPostId(postId);
    }
}