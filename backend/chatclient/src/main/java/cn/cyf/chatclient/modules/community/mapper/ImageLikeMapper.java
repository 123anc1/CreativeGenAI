package cn.cyf.chatclient.modules.community.mapper;

import cn.cyf.chatclient.modules.community.model.ImageLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ImageLikeMapper {

    @Insert("INSERT INTO image_like (user_id, post_id, created_at) VALUES (#{userId}, #{postId}, #{createdAt})")
    void addLike(ImageLike like);

    @Delete("DELETE FROM image_like WHERE user_id = #{userId} AND post_id = #{postId}")
    void removeLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM image_like WHERE user_id = #{userId} AND post_id = #{postId}")
    Integer checkLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM image_like WHERE post_id = #{postId}")
    Integer countLikes(Integer postId);

    /**
     * 统计总点赞数
     */
    @Select("SELECT COUNT(*) FROM image_like")
    Integer countAll();
}
