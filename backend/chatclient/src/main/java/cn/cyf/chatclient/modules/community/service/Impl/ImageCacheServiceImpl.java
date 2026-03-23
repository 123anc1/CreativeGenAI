package cn.cyf.chatclient.modules.community.service.Impl;


import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ImageCacheServiceImpl implements ImageCacheService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_PREFIX = "signed_url:";

    /**
     * 获取缓存中的签名 URL
     */
    public String getCachedSignedUrl(String objectKey) {
        String cacheKey = CACHE_PREFIX + objectKey;
//        log.info("获取objectKey：{}", objectKey);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 缓存签名 URL 并设置过期时间
     */
    public void cacheSignedUrl(String objectKey, String signedUrl, long expireSeconds) {
        String cacheKey = CACHE_PREFIX + objectKey;
//        log.info("缓存objectKey：{}", objectKey);
        redisTemplate.opsForValue().set(cacheKey, signedUrl, expireSeconds, TimeUnit.SECONDS);
//        log.info("缓存签名 URL: {}", signedUrl);
    }

    /**
     * 删除缓存中的签名 URL
     */
    public void removeCachedSignedUrl(String objectKey) {
        String cacheKey = CACHE_PREFIX + objectKey;
        redisTemplate.delete(cacheKey);
    }
}
