package cn.cyf.chatclient.modules.multimodal.service;

import cn.cyf.chatclient.modules.multimodal.model.MultiModalResult;

import java.util.List;

public interface MultiModalResultService {
    void saveResult(MultiModalResult result);
    List<MultiModalResult> getResultsByUid(Integer uid);
    List<MultiModalResult> getResultsByUidAndSessionid(Integer uid, String sessionid);
    MultiModalResult getResultById(Long id);
    String loadImage(String imageUrl);
}
