package cn.cyf.chatclient.modules.community.mapper;

import cn.cyf.chatclient.modules.community.model.Imagepost;
import cn.cyf.chatclient.modules.community.model.Imagepostparam;
import cn.cyf.chatclient.modules.community.model.Imagepostparamadmin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImagepostMapper {

    @Select("SELECT image_data from user_image where id = #{imageId}")
    String insertshare(Integer imageId);

//    @Select("SELECT i.*, u.name ,p.steps ,p.cfg_scale from image_post i left join user u on i.user_id = u.id left join lora_load_params p  on i.image_id = p.image_id where status = 1 order by i.updated_at desc ")
    List<Imagepost> list(Imagepostparam imagepostparam);

    @Select("SELECT i.*, u.name from image_post i left join user u on i.user_id = u.id where user_id = #{id} and status = 1")
    Imagepost getById(Integer id);
    
    @Select("SELECT i.*, u.name from image_post i left join user u on i.user_id = u.id where user_id = #{userid}")
    List<Imagepost> getByUserid(Integer userid);

    @Insert("INSERT INTO image_post (user_id, image_url,image_id, title, caption, published_at,created_at, updated_at) values " +
            "(#{userId},#{imageUrl},#{imageId},#{title},#{caption},#{publishedAt},#{createdAt},#{updatedAt})"
    )
    void upload(Imagepost imagepost);

    @Update("UPDATE image_post SET status = 1  where id = #{id} AND user_id = #{userId}")
    void update(Imagepost imagepost);

    @Update("UPDATE image_post SET like_count = #{likeCount} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Integer id, @Param("likeCount") Integer likeCount);

    @Update("UPDATE image_post SET collection_count = #{collectionCount} WHERE id = #{id}")
    void updateCollectionCount(@Param("id") Integer id, @Param("collectionCount") Integer collectionCount);

    @Delete("DELETE FROM image_post where id = #{id} AND user_id = #{userId}")
    void delete(Imagepost imagepost);

    @Select("SELECT * FROM image_post where user_id = #{userId} AND image_url = #{imageUrl}")
    Imagepost check(Imagepost imagepost);

    @Delete("DELETE FROM image_post where image_url = #{fileUrl}")
    void deleteByImageurl(String fileUrl);

    @Select("<script>SELECT i.*, u.name FROM image_post i LEFT JOIN user u ON i.user_id = u.id WHERE i.id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach> AND i.status = 1</script>")
    List<Imagepost> getByIds(@Param("ids") java.util.List<Integer> ids);

    List<Imagepost> listAdmin(Imagepostparamadmin imagepostparamadmin);

    @Select("SELECT * FROM image_post where image_id = #{id} AND user_id = #{userId}")
    Imagepost checkById(Integer id, Integer userId);

    @Select("SELECT * FROM image_post where id = #{id} AND user_id = #{userId}")
    Imagepost getPostById(Integer id, Integer userId);

    /**
     * 统计总图片发布数
     */
    @Select("SELECT COUNT(*) FROM image_post WHERE status = 1")
    Integer countAll();

    /**
     * 按日期统计图片发布数
     */
    @Select("SELECT COUNT(*) FROM image_post WHERE status = 1 AND DATE(created_at) = #{date}")
    Integer countByDate(String date);

    /**
     * 获取热门帖子
     */
    @Select("SELECT i.*, u.name FROM image_post i LEFT JOIN user u ON i.user_id = u.id WHERE i.status = 1 ORDER BY i.like_count DESC, i.comment_count DESC, i.view_count DESC LIMIT #{limit}")
    List<Imagepost> getTopPopular(@Param("limit") Integer limit);

    /**
     * 个性化推荐图片
     */
    List<Imagepost> recommend(@Param("userId") Integer userId);
}
