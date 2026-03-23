package cn.cyf.chatclient.modules.knowledge.service.impl;

import cn.cyf.chatclient.modules.knowledge.service.SimilaritySearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SimilaritySearchServiceImpl implements SimilaritySearchService {
    @Autowired
    private VectorStore vectorStore;

    @Value("${app.search.top-k:5}")
    private int topK;

    @Value("${app.search.similarity-threshold:0.5}")
    private float similarityThreshold;


    // 相似度搜索
    @Override
    public List<Document> getSimilar(String query, String userId, String sessionId) {
        String filterGlobal =  "type=='global' AND userId == '"+userId+"' " ;
        String filterSession = "type == 'session' AND userId == '" + userId + "' AND sessionId == '" + sessionId + "'";
        SearchRequest requestGlobal = SearchRequest.builder()
                .query(query)
                .topK(topK*3)
                .similarityThreshold(similarityThreshold)
                .filterExpression(filterGlobal)
                .build();

        SearchRequest requestSession = SearchRequest.builder()
                .query(query)
                .topK(topK*6)
                .similarityThreshold(similarityThreshold)
                .filterExpression(filterSession)
                .build();

        List<Document> resultGlobal = vectorStore.similaritySearch(requestGlobal);
        List<Document> resultSession = vectorStore.similaritySearch(requestSession);
        // 合并结果列表
        List<Document> results = new java.util.ArrayList<>();
        if (resultGlobal != null) {
            results.addAll(resultGlobal);
        }
        if (resultSession != null) {
            results.addAll(resultSession);
        }

//        log.info("相似度搜索到块数为：{}", results.size());

        return results;

    }

    // 构建优化的提示模板
    public String buildRagPrompt(String query, List<Document> contextDocs) {
        StringBuilder contextBuilder = new StringBuilder();
        if (contextDocs != null && !contextDocs.isEmpty()) {
            for (int i = 0; i < contextDocs.size(); i++) {
                contextBuilder.append("参考信息 ").append(i + 1).append(":\n")
                        .append(contextDocs.get(i).getText())
                        .append("\n\n");
            }
        } else {
            contextBuilder.append("未找到相关的参考信息。\n");
        }

        return String.format(
                "请基于以下参考信息回答用户问题。如果参考信息与问题无关，请忽略参考信息并基于常识回答。\n\n" +
                        "参考信息:\n%s\n" +
                        "用户问题: %s\n\n" +
                        "请简洁明了地回答用户问题，如有必要可分点说明。",
                contextBuilder.toString(),
                query
        );
    }
}
