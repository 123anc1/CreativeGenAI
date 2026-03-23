package cn.cyf.chatclient.modules.image.service.impl;

import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.image.mapper.ImagemodelMapper;
import cn.cyf.chatclient.modules.image.model.Basemodel;
import cn.cyf.chatclient.modules.image.model.Loramodel;
import cn.cyf.chatclient.modules.image.service.ImagemodelService;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ImagemodelServiceImpl implements ImagemodelService {
    @Autowired
    private ImageCacheService imageCacheService;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImagemodelMapper imagemodelMapper;

    @Override
    public List<Basemodel> listBaseModel() {
        return imagemodelMapper.listBaseModel();
    }

    @Override
    public List<Loramodel> listStyleModel() {
        List<Loramodel> listStyleModel = imagemodelMapper.listStyleModel();
        for (Loramodel loramodel : listStyleModel) {
            String cachedUrl = imageCacheService.getCachedSignedUrl(loramodel.getPreviewImage());
            if (cachedUrl != null) {
                loramodel.setPreviewImage(cachedUrl);
//                log.info("使用缓存的图片地址: {}", loramodel.getPreviewImage());
                continue;
            }
            try {
                String signedUrl = aliyunOSSOperator.generateSignedUrl(loramodel.getPreviewImage(), 3600*24);
                imageCacheService.cacheSignedUrl(loramodel.getPreviewImage(), signedUrl, 3600*24);
                loramodel.setPreviewImage(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        return listStyleModel;
    }

    @Override
    public Basemodel selectBaseModel(String id) {
        return imagemodelMapper.selectBaseModelById(id);
    }

    @Override
    public Loramodel selectStyleModel(String id) {
        return imagemodelMapper.selectStyleModelById(id);
    }

    @Override
    public void updateBaseModel(Basemodel basemodel) {

        imagemodelMapper.updateBaseModel(basemodel);
    }
}
