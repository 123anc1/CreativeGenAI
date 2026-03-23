package cn.cyf.chatclient.modules.system.service;

import cn.cyf.chatclient.modules.system.model.StatisticsResult;

import java.util.Map;

/**
 * 统计分析服务接口
 * 提供各种维度的数据统计功能
 */
public interface StatisticsService {
    
    /**
     * 获取系统概览统计数据
     */
    StatisticsResult getSystemOverview();
    
    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 获取社区统计数据
     */
    Map<String, Object> getCommunityStatistics();
    
    /**
     * 获取文档统计数据
     */
    Map<String, Object> getDocumentStatistics();
    
    /**
     * 获取图片生成统计数据
     */
    Map<String, Object> getImageGenerationStatistics();
    
    /**
     * 获取多模态交互统计数据
     */
    Map<String, Object> getMultiModalStatistics();
    
    /**
     * 获取最近7天的统计数据
     */
    Map<String, Object> getRecent7DaysStatistics();
}
