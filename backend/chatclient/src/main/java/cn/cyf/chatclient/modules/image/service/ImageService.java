package cn.cyf.chatclient.modules.image.service;


import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.model.ImageResponseVo;
import cn.cyf.chatclient.modules.image.model.UserImage;
import cn.cyf.chatclient.modules.image.service.impl.ImageTask;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ImageService {
    String caption(MultipartFile file);


    ImageResponseVo text_to_image(ImageModel imageModel);

    String asyncTextToImage(ImageModel imageModel);

    ImageTask getTaskStatus(String taskId);

    void cancelTask(String taskId);

    void save(String result,ImageModel imageModel,String description);

    List<UserImage> list(String userid) throws Exception;

    void delete(String fileName);

    UserImage get(String s, String s1);

}
