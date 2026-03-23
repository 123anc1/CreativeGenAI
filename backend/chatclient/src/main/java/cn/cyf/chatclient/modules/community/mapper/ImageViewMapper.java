package cn.cyf.chatclient.modules.community.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ImageViewMapper {

    @Insert("INSERT INTO image_view (user_id, post_id, viewed_at) VALUES (#{userId}, #{postId}, #{viewedAt})")
    void addView(@Param("userId") Integer userId, @Param("postId") Integer postId, @Param("viewedAt") java.time.LocalDateTime viewedAt);

    @Select("SELECT COUNT(*) FROM image_view WHERE user_id = #{userId} AND post_id = #{postId}")
    Integer checkView(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Update("UPDATE image_post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(Integer postId);
}
