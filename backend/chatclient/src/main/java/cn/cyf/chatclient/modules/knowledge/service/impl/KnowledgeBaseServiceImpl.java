package cn.cyf.chatclient.modules.knowledge.service.impl;

import cn.cyf.chatclient.modules.knowledge.mapper.KnowledgeBaseMapper;
import cn.cyf.chatclient.modules.document.model.WorldGlobal;
import cn.cyf.chatclient.modules.knowledge.service.KnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private VectorStore vectorStore;
    @Override
    public List<WorldGlobal> listGlobal(String userId) {
        return knowledgeBaseMapper.listGlobals(userId);
    }

    @Override
    public void deleteGlobal(String fileName, String userId) {
        List<String> md5List = knowledgeBaseMapper.findMd5Byfilename(fileName,userId);
        log.info("删除md5：{}", md5List);

//        log.info("删除文件名：{}", fileName);
        while (true) {
            SearchRequest request = SearchRequest.builder()
                    .query("")
                    .topK(10)
                    .filterExpression("type=='global' AND userId == '" + userId +"'" +"AND filename == '" + fileName + "'")
                    .build();
            List<Document> documents = vectorStore.similaritySearch(request);
            if (documents != null && documents.isEmpty()) {
                break;
            }
            // 构建复合过滤条件：filename AND type
            Filter.Expression filenameCondition = new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("filename"),
                    new Filter.Value(fileName)
            );
            Filter.Expression typeCondition = new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("type"),
                    new Filter.Value("global")
            );
            Filter.Expression userIdCondition = new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("userId"),
                    new Filter.Value(userId)
            );
            Filter.Expression andCondition = new Filter.Expression(
                    Filter.ExpressionType.AND,
                    filenameCondition,
                    userIdCondition
            );
            vectorStore.delete(andCondition);

        }

        knowledgeBaseMapper.deleteByfilename(fileName, userId);
        if (!md5List.isEmpty()) {
            List<String> rKeys = md5List.stream()
                    .map(md5 -> String.format("user:%s:global:md5:%s", userId, md5))
                    .collect(Collectors.toList());

            redisTemplate.delete(rKeys); // 批量删除
//            log.info("【清理rKey】删除 {} 个去重标记", rKeys.size());
        }
    }
}
