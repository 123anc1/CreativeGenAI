package cn.cyf.chatclient.modules.knowledge.service;



import cn.cyf.chatclient.modules.document.model.WorldGlobal;

import java.util.List;

public interface KnowledgeBaseService {
    List<WorldGlobal> listGlobal(String userId);

    void deleteGlobal(String fileName, String userId);
}
