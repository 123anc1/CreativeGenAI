package cn.cyf.chatclient.modules.community.mapper;

import cn.cyf.chatclient.modules.community.model.ImageCollection;
import cn.cyf.chatclient.modules.community.model.Imagepost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageCollectionMapper {

    @Insert("INSERT INTO image_collection (user_id, post_id, created_at) VALUES (#{userId}, #{postId}, #{createdAt})")
    void addCollection(ImageCollection collection);

    @Delete("DELETE FROM image_collection WHERE user_id = #{userId} AND post_id = #{postId}")
    void removeCollection(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM image_collection WHERE user_id = #{userId} AND post_id = #{postId}")
    Integer checkCollection(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM image_collection WHERE post_id = #{postId}")
    Integer countCollections(Integer postId);

    @Select("SELECT i.*, u.name FROM image_post i LEFT JOIN user u ON i.user_id = u.id INNER JOIN image_collection c ON i.id = c.post_id WHERE c.user_id = #{userId} AND i.status = 1")
    List<Imagepost> getCollectionByUserId(Integer userId);

    /**
     * 统计总收藏数
     */
    @Select("SELECT COUNT(*) FROM image_collection")
    Integer countAll();
}
