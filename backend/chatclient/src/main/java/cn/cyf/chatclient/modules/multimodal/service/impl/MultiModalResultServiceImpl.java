package cn.cyf.chatclient.modules.multimodal.service.impl;

import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import cn.cyf.chatclient.modules.multimodal.mapper.MultiModalResultMapper;
import cn.cyf.chatclient.modules.multimodal.model.MultiModalResult;
import cn.cyf.chatclient.modules.multimodal.service.MultiModalResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MultiModalResultServiceImpl implements MultiModalResultService {
    @Autowired
    private ImageCacheService imageCacheService;

    @Autowired
    private MultiModalResultMapper multiModalResultMapper;
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    @Override
    public void saveResult(MultiModalResult result) {
        multiModalResultMapper.insert(result);
    }

    @Override
    public List<MultiModalResult> getResultsByUid(Integer uid) {
        return multiModalResultMapper.findByUid(uid);
    }

    @Override
    public List<MultiModalResult> getResultsByUidAndSessionid(Integer uid, String sessionid) {
//        log.info("获取用户会话多模态结果:{}", multiModalResultMapper.findByUidAndSessionid(uid, sessionid));
        return multiModalResultMapper.findByUidAndSessionid(uid, sessionid);
    }

    @Override
    public MultiModalResult getResultById(Long id) {
        return multiModalResultMapper.findById(id);
    }

    @Override
    public String loadImage(String ossUrl) {
        try {
            //获取图片URL
            String cachedUrl = imageCacheService.getCachedSignedUrl(ossUrl);
            if (cachedUrl != null) {
                return cachedUrl;
            } else {
                String signedUrl = aliyunOSSOperator.generateSignedUrl(ossUrl, 3600 * 24);
                imageCacheService.cacheSignedUrl(ossUrl, signedUrl, 3600 * 24);
                return signedUrl;
            }
        } catch (Exception e) {
            return "";
        }
    }

}
