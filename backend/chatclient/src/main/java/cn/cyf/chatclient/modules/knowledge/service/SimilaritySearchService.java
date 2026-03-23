package cn.cyf.chatclient.modules.knowledge.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface SimilaritySearchService {
    List<Document> getSimilar (String query, String userId, String sessionId);

    String buildRagPrompt(String query, List<Document> contextDocs);
}
