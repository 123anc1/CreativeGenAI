package cn.cyf.chatclient.modules.document.service.impl;

import cn.cyf.chatclient.common.utils.KnowledgeBuilder;
import cn.cyf.chatclient.common.utils.MD5Utils;
import cn.cyf.chatclient.modules.document.mapper.WordMapper;
import cn.cyf.chatclient.modules.session.model.SessionVo;
import cn.cyf.chatclient.modules.document.model.WorldGlobal;
import cn.cyf.chatclient.modules.document.model.WorldSession;
import cn.cyf.chatclient.modules.document.service.WordService;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WordServiceImpl implements WordService {
    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${app.document.chunk-size:800}")
    private int chunkSize;

    @Value("${app.document.chunk-overlap:200}")
    private int chunkOverlap;

    @Value("${app.document.duplicate-check-enabled:true}")
    private boolean duplicateCheckEnabled;

    @Autowired
    private WordMapper wordMapper;

    /*
     * 上传文件Global
     */
    @Override
    public void uploadGlobal(MultipartFile[] files, String userId) {
        for (MultipartFile f : files) {
            try {
                String md5 = MD5Utils.calculateFileMd5(f);
                String rKey = String.format("user:%s:global:md5:%s", userId, md5);
                log.info("【GLOBAL-上传入口】文件名={}, MD5={}", Objects.requireNonNull(f.getOriginalFilename()).replace("-", ""), rKey);
                if (duplicateCheckEnabled && redisTemplate.hasKey(rKey)) {
                    log.warn("【GLOBAL】跳过重复文件：{}", f.getOriginalFilename().replace("-", ""));
                    continue;
                }

                WorldGlobal worldGlobal = new WorldGlobal();
                worldGlobal.setFilename(f.getOriginalFilename().replace("-", ""));
                worldGlobal.setMd5(rKey);
                worldGlobal.setSize(f.getSize());
                worldGlobal.setUserid(Integer.valueOf(userId));
                wordMapper.insertGlobal(worldGlobal);

                List<Document> docs = KnowledgeBuilder.parse(f, chunkSize, chunkOverlap);
                docs = docs.stream()
                        .map(d -> new Document(d.getText(),
                                        Map.of("type", "global",
                                                "userId", userId,
                                                "sessionId", "global",
                                                "filename", f.getOriginalFilename().replace("-", ""),
                                                "md5", rKey,
                                                "uploadTime", System.currentTimeMillis()
                                        )
                                )
                        )
                        .collect(Collectors.toList());
                vectorStore.add(docs);
                if (duplicateCheckEnabled) redisTemplate.opsForValue().set(rKey, userId);
//                log.info("【GLOBAL-写入完成】文件名={}, 写入块数={}, RedisKey={}", f.getOriginalFilename().replace("-", ""), docs.size(), rKey);
            } catch (Exception e) {
                log.error("【GLOBAL】上传失败", e);
            }
        }
    }

    /**
     * 上传文件session
     */
    public void uploadInSession(MultipartFile[] files, String userId, String sessionId) {
        log.warn("获取到sessionid：{}",sessionId);
        SessionVo sessionVo = new SessionVo();
        sessionVo.setSessionid(sessionId);
        if (sessionId == null) {
            log.warn("【SESSION】sessionId为空");
            return;
        }
        for (MultipartFile f : files) {
            try {
                String md5 = MD5Utils.calculateFileMd5(f);
                String rKey = String.format("session:%s:%s:md5:%s", userId, sessionId, md5);
//                log.info("【SESSION-上传入口】userId={}, sessionId={}, 文件名={}, MD5={}", userId, sessionId, f.getOriginalFilename().replace("-", ""), md5);

                if (redisTemplate.hasKey(rKey)) {
                    log.warn("【SESSION】跳过重复文件：{}", f.getOriginalFilename().replace("-", ""));
                    return;
                }
                List<Document> docs = KnowledgeBuilder.parse(f, chunkSize, chunkOverlap);
                docs = docs.stream()
                        .map(d -> new Document(d.getText(),
                                Map.of("type", "session",
                                        "userId", userId,
                                        "sessionId", sessionId,
                                        "filename", f.getOriginalFilename().replace("-", ""),
                                        "md5", rKey,
                                        "uploadTime", System.currentTimeMillis())))
                        .collect(Collectors.toList());
                vectorStore.add(docs);
                redisTemplate.opsForValue().set(rKey, userId);

                WorldSession worldSession = new WorldSession();
                worldSession.setUserid(userId);
                worldSession.setSessionid(sessionId);
                worldSession.setMd5(rKey);
                worldSession.setFilename(f.getOriginalFilename().replace("-", ""));
                worldSession.setChunkcount(docs.size());
                wordMapper.insertSession(worldSession);

//                log.info("【SESSION-写入完成】userId={}, sessionId={}, 文件名={}, 写入块数={}, RedisKey={}",
//                        userId, sessionId, f.getOriginalFilename().replace("-", ""), docs.size(), rKey);
            } catch (Exception e) {
                log.error("【SESSION】上传失败", e);
            }
        }
    }

    /**
     * 分割文档为小块
     */
    @Override
    public List<Document> splitDocuments(List<Document> documents, int chunkSize, int overlap) {
        TokenTextSplitter splitter = new TokenTextSplitter(chunkSize, overlap, 5, 10000, true);
        return splitter.apply(documents);
    }

    @Override
    public void delete(String fileName, String userId,String sessionId) {

        List<String> md5List = wordMapper.findMd5sByfilenameid(fileName , sessionId);

        wordMapper.deleteByfilenameid(fileName, sessionId);

//        log.info("删除文件名：{}", fileName);
        while (true) {
            SearchRequest request = SearchRequest.builder()
                    .query("")
                    .topK(10)
                    .filterExpression("sessionId =='"+sessionId+"' AND filename == '" + fileName + "'")
                    .build();
            List<Document> documents = vectorStore.similaritySearch(request);
            if (documents.isEmpty()) {
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
                    new Filter.Key("sessionId"),
                    new Filter.Value(sessionId)
            );
            Filter.Expression andCondition = new Filter.Expression(
                    Filter.ExpressionType.AND,
                    filenameCondition,
                    typeCondition
            );
            vectorStore.delete(andCondition);

        }

        if (!md5List.isEmpty()) {
            List<String> rKeys = md5List.stream()
                    .map(md5 -> String.format("session:%s:%s:md5:%s", userId, sessionId, md5))
                    .collect(Collectors.toList());

            redisTemplate.delete(rKeys); // 批量删除
//            log.info("【清理rKey】删除 {} 个去重标记", rKeys.size());
        }

    }

    @Override
    public void deleteBySessionId(String userId, String sessionId) {

        List<String> md5List = wordMapper.findMd5sBySessionId(userId , sessionId);

        while (true) {
            SearchRequest request = SearchRequest.builder()
                    .query("")
                    .topK(10)
                    .filterExpression("sessionId == '" + sessionId + "'")
                    .build();
            List<Document> documents = vectorStore.similaritySearch(request);
            if (documents.isEmpty()) {
                break;
            }
            Filter.Expression filterExpression = new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("sessionId"),
                    new Filter.Value(sessionId)
            );
            vectorStore.delete(filterExpression);

        }

        wordMapper.deleteBySessionId(userId, sessionId);

        if (!md5List.isEmpty()) {
            List<String> rKeys = md5List.stream()
                    .map(md5 -> String.format("session:%s:%s:md5:%s", userId, sessionId, md5))
                    .collect(Collectors.toList());

            redisTemplate.delete(rKeys); // 批量删除
//            log.info("【清理rKey】删除 {} 个去重标记", rKeys.size());
        }
    }


    @Override
    public List<WorldSession> listSession(String sessionId) {
        return wordMapper.listSession(sessionId);

    }

    /**
     * 手动清除特定文件的重复检查缓存
     */
    public void clearDuplicateCache(String md5Hash) {
        String cacheKey = "document_md5:" + md5Hash;
        redisTemplate.delete(cacheKey);
//        log.info("已清除MD5缓存: {}", md5Hash);
    }

    /**
     * 检查文件是否已存在
     */
    public boolean isFileAlreadyProcessed(String md5Hash) {
        String cacheKey = "document_md5:" + md5Hash;
        return redisTemplate.hasKey(cacheKey);
    }

    /**
     * 清除所有重复检查缓存（谨慎使用）
     */
    public void clearAllDuplicateCache() {
        String pattern = "document_md5:*";
        redisTemplate.keys(pattern).forEach(key -> redisTemplate.delete(key));
//        log.info("已清除所有文档MD5缓存");
    }

}
