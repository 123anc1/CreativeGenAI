package cn.cyf.chatclient.modules.community.mapper;

import cn.cyf.chatclient.modules.community.model.ImageComment;
import cn.cyf.chatclient.modules.community.model.CommentParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageCommentMapper {

    /**
     * 添加评论
     */
    @Insert("INSERT INTO image_comment (post_id, user_id, user_name, content, parent_id, reply_user_id, reply_user_name, like_count, status, created_at, updated_at) " +
            "VALUES (#{postId}, #{userId}, #{userName}, #{content}, #{parentId}, #{replyUserId}, #{replyUserName}, #{likeCount}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ImageComment comment);

    /**
     * 根据ID查询评论
     */
    @Select("SELECT * FROM image_comment WHERE id = #{id} AND status = 0")
    ImageComment selectById(Integer id);

    /**
     * 查询图片的所有评论（包括回复）
     */
    List<ImageComment> selectByPostId(CommentParam param);

    /**
     * 查询父评论下的所有回复
     */
    @Select("SELECT * FROM image_comment WHERE parent_id = #{parentId} AND status = 0 ORDER BY created_at ASC")
    List<ImageComment> selectRepliesByParentId(Integer parentId);

    /**
     * 更新评论点赞数
     */
    @Update("UPDATE image_comment SET like_count = #{likeCount}, updated_at = NOW() WHERE id = #{id}")
    void updateLikeCount(Integer id, Integer likeCount);

    /**
     * 删除评论（软删除）
     */
    @Update("UPDATE image_comment SET status = 1, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
    void deleteByIdAndUserId(Integer id, Integer userId);

    /**
     * 统计图片的评论总数
     */
    @Select("SELECT COUNT(*) FROM image_comment WHERE post_id = #{postId} AND status = 0 AND parent_id IS NULL")
    Integer countCommentsByPostId(Integer postId);

    /**
     * 统计评论的回复数
     */
    @Select("SELECT COUNT(*) FROM image_comment WHERE parent_id = #{parentId} AND status = 0")
    Integer countRepliesByParentId(Integer parentId);

    /**
     * 统计总评论数
     */
    @Select("SELECT COUNT(*) FROM image_comment WHERE status = 0")
    Integer countAll();

    /**
     * 按日期统计评论数
     */
    @Select("SELECT COUNT(*) FROM image_comment WHERE status = 0 AND DATE(created_at) = #{date}")
    Integer countByDate(String date);
}