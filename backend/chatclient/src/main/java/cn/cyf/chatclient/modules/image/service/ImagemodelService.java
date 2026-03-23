package cn.cyf.chatclient.modules.image.service;




import cn.cyf.chatclient.modules.image.model.Basemodel;
import cn.cyf.chatclient.modules.image.model.Loramodel;

import java.util.List;

public interface ImagemodelService {

    List<Basemodel> listBaseModel();

    List<Loramodel> listStyleModel();

    Basemodel selectBaseModel(String id);

    Loramodel selectStyleModel(String id);

    void updateBaseModel(Basemodel basemodel);
}
