package cn.cyf.chatclient.modules.image.mapper;


import cn.cyf.chatclient.modules.image.model.Basemodel;
import cn.cyf.chatclient.modules.image.model.Loramodel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ImagemodelMapper {

    /**
     * 获取基础模型列表
     *
     * @return 基础模型列表
     */
    @Select("SELECT * from base_models where is_enabled = 1 ")
    List<Basemodel> listBaseModel();

    /**
     * 获取风格模型列表
     *
     * @return 风格模型列表
     */
    @Select("SELECT * from lora_models")
    List<Loramodel> listStyleModel();

    @Select("SELECT * from base_models where name = #{id}")
    Basemodel selectBaseModelById(String id);

    @Select("SELECT * from lora_models where id = #{id}")
    Loramodel selectStyleModelById(String id);

    @Update("UPDATE base_models SET name = #{name}, description = #{description}, is_enabled = #{is_enabled} WHERE id = #{id}")
    void updateBaseModel(Basemodel basemodel);
}
