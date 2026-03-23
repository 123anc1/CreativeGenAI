package cn.cyf.chatclient.modules.document.service;


import cn.cyf.chatclient.modules.document.model.WorldSession;
import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WordService {

    void uploadGlobal(MultipartFile[] file, String userId);

    List<Document> splitDocuments(List<Document> documents, int chunkSize, int overlap);

    void uploadInSession(MultipartFile[] file, String userId, String sessionId);

    void delete(String fileName, String userId,String sessionId);

    void deleteBySessionId(String userId,String sessionId);

    List<WorldSession> listSession(String sessionId);
}
