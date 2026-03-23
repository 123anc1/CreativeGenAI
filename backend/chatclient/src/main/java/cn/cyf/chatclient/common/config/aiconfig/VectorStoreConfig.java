package cn.cyf.chatclient.common.config.aiconfig;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class VectorStoreConfig {
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6380}")
    private int redisPort;
    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(redisHost, redisPort);
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("custom-index")
                .prefix("custom-prefix")
                .metadataFields(
                        RedisVectorStore.MetadataField.tag("type"),
                        RedisVectorStore.MetadataField.tag("userId"),
                        RedisVectorStore.MetadataField.tag("sessionId"),
                        RedisVectorStore.MetadataField.text("filename"),
                        RedisVectorStore.MetadataField.text("md5"),
                        RedisVectorStore.MetadataField.numeric("uploadTime") // 注意：值必须是 Long/Number
                )
                .initializeSchema(true)
                .build();
    }
}
