package cn.cyf.chatclient.modules.community.controller;


import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.common.utils.SensitiveWordFilter;
import cn.cyf.chatclient.modules.community.model.CommentParam;
import cn.cyf.chatclient.modules.community.model.ImageComment;
import cn.cyf.chatclient.modules.community.model.ImageCommentVO;
import cn.cyf.chatclient.modules.community.model.PageResult;
import cn.cyf.chatclient.modules.community.service.ImageCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图片评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/imagepost/comment")
public class ImageCommentController {

    @Autowired
    private ImageCommentService imageCommentService;
    
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    /**
     * 发布评论
     */
    @PostMapping("/add")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "发布图片评论", module = "评论管理", logParams = true)
    public Result addComment(@RequestParam Integer userid,
                             @RequestParam Integer postid,
                             @RequestParam String content,
                             @RequestParam(required = false) Integer parentid) {
        try {
            // 敏感词过滤
            String filteredContent = sensitiveWordFilter.filter(content);
            
            ImageComment comment = new ImageComment();
            comment.setUserId(userid);
            comment.setPostId(postid);
            comment.setContent(filteredContent);
            comment.setParentId(parentid);
            
            imageCommentService.addComment(comment);
            return Result.success("评论发布成功");
        } catch (Exception e) {
            log.error("发布评论失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "回复图片评论", module = "评论管理", logParams = true)
    public Result replyComment(@RequestParam Integer userid,
                             @RequestParam Integer postid,
                             @RequestParam String content,
                             @RequestParam Integer parentid) {
        try {
            // 敏感词过滤
            String filteredContent = sensitiveWordFilter.filter(content);
            
            ImageComment comment = new ImageComment();
            comment.setUserId(userid);
            comment.setPostId(postid);
            comment.setContent(filteredContent);
            comment.setParentId(parentid);
            
            imageCommentService.replyComment(comment);
            return Result.success("回复成功");
        } catch (Exception e) {
            log.error("回复评论失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取图片评论列表（分页）
     */
    @PostMapping("/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取图片评论列表", module = "评论管理")
    public PageResult<ImageCommentVO> getComments(@RequestParam Integer postid,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pagesize) {
        try {
            CommentParam param = new CommentParam();
            param.setPostId(postid);
            param.setPage(page);
            param.setPageSize(pagesize);
            
            PageResult<ImageCommentVO> result = imageCommentService.getCommentsByPostId(param);
            log.info("获取评论列表成功:{}", result);
            return result;
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return null;
        }
    }

    /**
     * 获取评论的回复列表
     */
    @PostMapping("/replies")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取评论回复列表", module = "评论管理")
    public List<ImageComment> getReplies(@RequestParam Integer parentid) {
        try {
            return imageCommentService.getRepliesByParentId(parentid);
        } catch (Exception e) {
            log.error("获取回复列表失败", e);
            return null;
        }
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除评论", module = "评论管理", level = LogOperation.LogLevel.WARN)
    public Result deleteComment(@RequestParam Integer commentid,
                              @RequestParam Integer userid) {
        try {
            imageCommentService.deleteComment(commentid, userid);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 点赞评论
     */
    @PostMapping("/like")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "点赞评论", module = "评论管理")
    public Result likeComment(@RequestParam Integer commentid) {
        try {
            imageCommentService.likeComment(commentid);
            return Result.success("点赞成功");
        } catch (Exception e) {
            log.error("点赞失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消点赞评论
     */
    @PostMapping("/unlike")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "取消点赞评论", module = "评论管理")
    public Result unlikeComment(@RequestParam Integer commentid) {
        try {
            imageCommentService.unlikeComment(commentid);
            return Result.success("取消点赞成功");
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论详情
     */
    @PostMapping("/detail")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取评论详情", module = "评论管理")
    public ImageComment getCommentDetail(@RequestParam Integer commentid) {
        try {
            return imageCommentService.getCommentById(commentid);
        } catch (Exception e) {
            log.error("获取评论详情失败", e);
            return null;
        }
    }
}