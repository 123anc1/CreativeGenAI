package cn.cyf.chatclient.modules.image.mapper;

import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.model.UserImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {

    @Insert("insert into user_image(user_id,image_data,created_at,updated_at,is_active,prompt)" +
            "values(#{userid},#{imagedata},#{createdat},#{updatedat},'0',#{prompt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(UserImage userImage);

    @Select("select * from user_image where user_id=#{userid}")
    List<UserImage> list(int i);

    @Select("delete from user_image where image_data=#{fileUrl}")
    void delete(String fileUrl);

    @Select("select * from user_image where id=#{id} and user_id=#{userid}")
    UserImage get(String id, String userid);

    @Select("select image_data from user_image where id=#{id}")
    String findUrlById(String id);

    @Insert("INSERT INTO lora_load_params(image_id,lora_id,base_id,prompt,negative_prompt,steps,cfg_scale,width,height) values " +
            "(#{id},#{style},#{model_name},#{prompt},#{negativePrompt},#{steps},#{cfg_scale},#{width},#{height})")
    void saveparam(ImageModel imageModel);

    @Select("select lora_id,base_id,prompt,negative_prompt,steps,cfg_scale,width,height from lora_load_params"
            + " where base_id=#{model_name}")
    ImageModel getparam(String model_name);

    @Select("SELECT COUNT(*) FROM user_image")
    Integer countAll();

    @Select("SELECT COUNT(*) FROM user_image WHERE DATE(created_at) = #{date}")
    Integer countByDate(String date);
}
