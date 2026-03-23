package cn.cyf.chatclient.modules.system.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 统计分析结果类
 * 用于封装系统概览统计数据
 */
@Data
public class StatisticsResult {
    private Integer totalUsers;                 // 总用户数
    private Integer activeUsers;                // 活跃用户数
    private Integer totalImagePosts;            // 总图片发布数
    private Integer totalComments;              // 总评论数
    private Integer totalDocuments;             // 总文档数
    private Integer totalImageGenerations;      // 总图片生成数
    private Integer totalMultiModalInteractions; // 总多模态交互数
    private Integer totalChatSessions;          // 总聊天会话数
    private Map<String, Integer> dailyActiveUsers; // 每日活跃用户
    private Map<String, Integer> dailyImagePosts;  // 每日图片发布
    private Map<String, Integer> dailyComments;    // 每日评论
    private List<TopUser> topActiveUsers;       // 活跃用户TOP10
    private List<TopPost> topPopularPosts;      // 热门帖子TOP10
    private List<TopModel> topUsedModels;       // 热门模型TOP10
    
    /**
     * 活跃用户内部类
     */
    @Data
    public static class TopUser {
        private Integer userId;
        private String username;
        private String name;
        private Integer activityCount;
    }
    
    /**
     * 热门帖子内部类
     */
    @Data
    public static class TopPost {
        private Integer postId;
        private String title;
        private Integer likeCount;
        private Integer commentCount;
        private Integer viewCount;
    }
    
    /**
     * 热门模型内部类
     */
    @Data
    public static class TopModel {
        private String modelName;
        private Integer usageCount;
    }
}
