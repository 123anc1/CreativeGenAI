package cn.cyf.chatclient.modules.community.service;

public interface ImageCacheService {
    String getCachedSignedUrl(String imageUrl);

    void cacheSignedUrl(String imageUrl, String signedUrl, long expireSeconds);

    void removeCachedSignedUrl(String fileUrl);
}
