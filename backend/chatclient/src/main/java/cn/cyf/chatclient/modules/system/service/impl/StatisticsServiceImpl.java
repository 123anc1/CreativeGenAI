package cn.cyf.chatclient.modules.system.service.impl;

import cn.cyf.chatclient.modules.system.model.StatisticsResult;
import cn.cyf.chatclient.modules.system.service.StatisticsService;
import cn.cyf.chatclient.modules.user.mapper.UserMapper;
import cn.cyf.chatclient.modules.user.model.User;
import cn.cyf.chatclient.modules.community.mapper.ImagepostMapper;
import cn.cyf.chatclient.modules.community.mapper.ImageCommentMapper;
import cn.cyf.chatclient.modules.community.mapper.ImageLikeMapper;
import cn.cyf.chatclient.modules.community.mapper.ImageCollectionMapper;
import cn.cyf.chatclient.modules.community.model.Imagepost;
import cn.cyf.chatclient.modules.document.mapper.WordMapper;
import cn.cyf.chatclient.modules.image.mapper.ImagemodelMapper;
import cn.cyf.chatclient.modules.image.mapper.ImageMapper;
import cn.cyf.chatclient.modules.multimodal.mapper.MultiModalResultMapper;
import cn.cyf.chatclient.modules.chat.mapper.ChatMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计分析服务实现类
 * <p>
 * 负责从数据库中获取各种统计数据，为管理员页面提供数据支持
 * 包括用户统计、社区统计、文档统计、图片生成统计、多模态交互统计等
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    /**
     * 用户数据访问接口
     */
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 图片发布数据访问接口
     */
    @Autowired
    private ImagepostMapper imagepostMapper;
    
    /**
     * 评论数据访问接口
     */
    @Autowired
    private ImageCommentMapper imageCommentMapper;
    
    /**
     * 文档数据访问接口
     */
    @Autowired
    private WordMapper wordMapper;
    
    /**
     * 图片模型数据访问接口
     */
    @Autowired
    private ImagemodelMapper imagemodelMapper;
    
    /**
     * 多模态结果数据访问接口
     */
    @Autowired
    private MultiModalResultMapper multiModalResultMapper;
    
    /**
     * 聊天数据访问接口
     */
    @Autowired
    private ChatMapper chatMapper;
    
    /**
     * 点赞数据访问接口
     */
    @Autowired
    private ImageLikeMapper imageLikeMapper;
    
    /**
     * 收藏数据访问接口
     */
    @Autowired
    private ImageCollectionMapper imageCollectionMapper;
    
    /**
     * 图片数据访问接口
     */
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 获取系统概览统计数据
     * <p>
     * 包括用户、社区、文档、图片生成、多模态交互、聊天会话等统计数据
     * 以及最近7天的趋势数据和TOP10数据
     * 
     * @return 系统概览统计结果
     */
    @Override
    public StatisticsResult getSystemOverview() {
        StatisticsResult result = new StatisticsResult();
        
        // 获取用户统计
        result.setTotalUsers(getTotalUsers());
        result.setActiveUsers(getActiveUsers());
        
        // 获取社区统计
        result.setTotalImagePosts(getTotalImagePosts());
        result.setTotalComments(getTotalComments());
        
        // 获取文档统计
        result.setTotalDocuments(getTotalDocuments());
        
        // 获取图片生成统计
        result.setTotalImageGenerations(getTotalImageGenerations());
        
        // 获取多模态交互统计
        result.setTotalMultiModalInteractions(getTotalMultiModalInteractions());
        
        // 获取聊天会话统计
        result.setTotalChatSessions(getTotalChatSessions());
        
        // 获取每日活跃用户
        result.setDailyActiveUsers(getDailyActiveUsers());
        
        // 获取每日图片发布
        result.setDailyImagePosts(getDailyImagePosts());
        
        // 获取每日评论
        result.setDailyComments(getDailyComments());
        
        // 获取活跃用户TOP10
        result.setTopActiveUsers(getTopActiveUsers());
        
        // 获取热门帖子TOP10
        result.setTopPopularPosts(getTopPopularPosts());
        
        // 获取热门模型TOP10
        result.setTopUsedModels(getTopUsedModels());
        
        return result;
    }

    /**
     * 获取用户统计数据
     * <p>
     * 包括总用户数、活跃用户数、用户增长趋势、用户角色分布
     * 
     * @return 用户统计结果
     */
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", getTotalUsers());
        result.put("activeUsers", getActiveUsers());
        result.put("userGrowth", getUserGrowth());
        result.put("userRoleDistribution", getUserRoleDistribution());
        return result;
    }

    /**
     * 获取社区统计数据
     * <p>
     * 包括总帖子数、总评论数、总点赞数、总收藏数、帖子增长趋势
     * 
     * @return 社区统计结果
     */
    @Override
    public Map<String, Object> getCommunityStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalPosts", getTotalImagePosts());
        result.put("totalComments", getTotalComments());
        result.put("totalLikes", getTotalLikes());
        result.put("totalCollections", getTotalCollections());
        result.put("postGrowth", getPostGrowth());
        return result;
    }

    /**
     * 获取文档统计数据
     * <p>
     * 包括总文档数、文档增长趋势
     * 
     * @return 文档统计结果
     */
    @Override
    public Map<String, Object> getDocumentStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalDocuments", getTotalDocuments());
        result.put("documentGrowth", getDocumentGrowth());
        return result;
    }

    /**
     * 获取图片生成统计数据
     * <p>
     * 包括总生成数、生成增长趋势、模型使用情况
     * 
     * @return 图片生成统计结果
     */
    @Override
    public Map<String, Object> getImageGenerationStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalGenerations", getTotalImageGenerations());
        result.put("generationGrowth", getImageGenerationGrowth());
        result.put("modelUsage", getModelUsage());
        return result;
    }

    /**
     * 获取多模态交互统计数据
     * <p>
     * 包括总交互数、交互增长趋势
     * 
     * @return 多模态交互统计结果
     */
    @Override
    public Map<String, Object> getMultiModalStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalInteractions", getTotalMultiModalInteractions());
        result.put("interactionGrowth", getMultiModalInteractionGrowth());
        return result;
    }

    /**
     * 获取最近7天统计数据
     * <p>
     * 包括最近7天的活跃用户、图片发布、评论、图片生成、多模态交互数据
     * 
     * @return 最近7天统计结果
     */
    @Override
    public Map<String, Object> getRecent7DaysStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("dailyActiveUsers", getDailyActiveUsers());
        result.put("dailyImagePosts", getDailyImagePosts());
        result.put("dailyComments", getDailyComments());
        result.put("dailyImageGenerations", getDailyImageGenerations());
        result.put("dailyMultiModalInteractions", getDailyMultiModalInteractions());
        return result;
    }

    // 辅助方法
    
    /**
     * 获取总用户数
     * 
     * @return 总用户数
     */
    private Integer getTotalUsers() {
        try {
            List<User> users = userMapper.selectAll();
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            log.error("获取总用户数失败", e);
            return 0;
        }
    }

    /**
     * 获取活跃用户数
     * 
     * @return 活跃用户数
     */
    private Integer getActiveUsers() {
        try {
            return userMapper.countActiveUsers();
        } catch (Exception e) {
            log.error("获取活跃用户数失败", e);
            return 0;
        }
    }

    /**
     * 获取总图片发布数
     * 
     * @return 总图片发布数
     */
    private Integer getTotalImagePosts() {
        try {
            return imagepostMapper.countAll();
        } catch (Exception e) {
            log.error("获取总图片发布数失败", e);
            return 0;
        }
    }

    /**
     * 获取总评论数
     * 
     * @return 总评论数
     */
    private Integer getTotalComments() {
        try {
            return imageCommentMapper.countAll();
        } catch (Exception e) {
            log.error("获取总评论数失败", e);
            return 0;
        }
    }

    /**
     * 获取总文档数
     * 
     * @return 总文档数
     */
    private Integer getTotalDocuments() {
        try {
            return wordMapper.countAll();
        } catch (Exception e) {
            log.error("获取总文档数失败", e);
            return 0;
        }
    }

    /**
     * 获取总图片生成数
     * 
     * @return 总图片生成数
     */
    private Integer getTotalImageGenerations() {
        try {
            return imageMapper.countAll();
        } catch (Exception e) {
            log.error("获取总图片生成数失败", e);
            return 0;
        }
    }

    /**
     * 获取总多模态交互数
     * 
     * @return 总多模态交互数
     */
    private Integer getTotalMultiModalInteractions() {
        try {
            return multiModalResultMapper.countAll();
        } catch (Exception e) {
            log.error("获取总多模态交互数失败", e);
            return 0;
        }
    }

    /**
     * 获取总聊天会话数
     * <p>
     * 暂时返回0，需要根据实际的聊天会话表结构实现
     * 
     * @return 总聊天会话数
     */
    private Integer getTotalChatSessions() {
        try {
            // 暂时返回0，需要根据实际的聊天会话表结构实现
            return 0;
        } catch (Exception e) {
            log.error("获取总聊天会话数失败", e);
            return 0;
        }
    }

    /**
     * 获取最近7天每日活跃用户数
     * <p>
     * 暂时返回0，需要根据实际的用户活跃表结构实现
     * 
     * @return 最近7天每日活跃用户数
     */
    private Map<String, Integer> getDailyActiveUsers() {
        Map<String, Integer> dailyActiveUsers = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            // 暂时返回0，需要根据实际的用户活跃表结构实现
            dailyActiveUsers.put(dateStr, 0);
        }
        
        return dailyActiveUsers;
    }

    /**
     * 获取最近7天每日图片发布数
     * 
     * @return 最近7天每日图片发布数
     */
    private Map<String, Integer> getDailyImagePosts() {
        Map<String, Integer> dailyImagePosts = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = imagepostMapper.countByDate(dateStr);
                dailyImagePosts.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取每日图片发布数失败", e);
                dailyImagePosts.put(dateStr, 0);
            }
        }
        
        return dailyImagePosts;
    }

    /**
     * 获取最近7天每日评论数
     * 
     * @return 最近7天每日评论数
     */
    private Map<String, Integer> getDailyComments() {
        Map<String, Integer> dailyComments = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = imageCommentMapper.countByDate(dateStr);
                dailyComments.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取每日评论数失败", e);
                dailyComments.put(dateStr, 0);
            }
        }
        
        return dailyComments;
    }

    /**
     * 获取最近7天每日图片生成数
     * 
     * @return 最近7天每日图片生成数
     */
    private Map<String, Integer> getDailyImageGenerations() {
        Map<String, Integer> dailyImageGenerations = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = imageMapper.countByDate(dateStr);
                dailyImageGenerations.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取每日图片生成数失败", e);
                dailyImageGenerations.put(dateStr, 0);
            }
        }
        
        return dailyImageGenerations;
    }

    /**
     * 获取最近7天每日多模态交互数
     * 
     * @return 最近7天每日多模态交互数
     */
    private Map<String, Integer> getDailyMultiModalInteractions() {
        Map<String, Integer> dailyMultiModalInteractions = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = multiModalResultMapper.countByDate(dateStr);
                dailyMultiModalInteractions.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取每日多模态交互数失败", e);
                dailyMultiModalInteractions.put(dateStr, 0);
            }
        }
        
        return dailyMultiModalInteractions;
    }

    /**
     * 获取活跃用户TOP10
     * <p>
     * 暂时返回所有用户，需要根据用户活跃度排序
     * 
     * @return 活跃用户TOP10
     */
    private List<StatisticsResult.TopUser> getTopActiveUsers() {
        List<StatisticsResult.TopUser> topUsers = new ArrayList<>();
        try {
            List<User> users = userMapper.selectAll();
            if (users != null) {
                // 这里需要根据用户活跃度排序，暂时返回所有用户
                for (int i = 0; i < Math.min(10, users.size()); i++) {
                    User user = users.get(i);
                    StatisticsResult.TopUser topUser = new StatisticsResult.TopUser();
                    topUser.setUserId(user.getId());
                    topUser.setUsername(user.getUsername());
                    topUser.setName(user.getName());
                    topUser.setActivityCount(0); // 待实现
                    topUsers.add(topUser);
                }
            }
        } catch (Exception e) {
            log.error("获取活跃用户失败", e);
        }
        return topUsers;
    }

    /**
     * 获取热门帖子TOP10
     * <p>
     * 按点赞数、评论数和浏览量排序
     * 
     * @return 热门帖子TOP10
     */
    private List<StatisticsResult.TopPost> getTopPopularPosts() {
        List<StatisticsResult.TopPost> topPosts = new ArrayList<>();
        try {
            List<Imagepost> posts = imagepostMapper.getTopPopular(10);
            if (posts != null) {
                for (Imagepost post : posts) {
                    StatisticsResult.TopPost topPost = new StatisticsResult.TopPost();
                    topPost.setPostId(post.getId());
                    topPost.setTitle(post.getTitle());
                    topPost.setLikeCount(post.getLikeCount());
                    topPost.setCommentCount(post.getCommentCount());
                    topPost.setViewCount(post.getViewCount());
                    topPosts.add(topPost);
                }
            }
        } catch (Exception e) {
            log.error("获取热门帖子失败", e);
        }
        return topPosts;
    }

    /**
     * 获取热门模型TOP10
     * <p>
     * 暂时返回空，需要根据实际的模型使用表结构实现
     * 
     * @return 热门模型TOP10
     */
    private List<StatisticsResult.TopModel> getTopUsedModels() {
        List<StatisticsResult.TopModel> topModels = new ArrayList<>();
        try {
            // 暂时返回空，需要根据实际的模型使用表结构实现
        } catch (Exception e) {
            log.error("获取热门模型失败", e);
        }
        return topModels;
    }

    /**
     * 获取最近30天用户增长数据
     * <p>
     * 暂时返回0，需要根据实际的用户表结构实现
     * 
     * @return 最近30天用户增长数据
     */
    private Map<String, Integer> getUserGrowth() {
        Map<String, Integer> userGrowth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            // 暂时返回0，需要根据实际的用户表结构实现
            userGrowth.put(dateStr, 0);
        }
        
        return userGrowth;
    }

    /**
     * 获取用户角色分布
     * 
     * @return 用户角色分布
     */
    private Map<String, Integer> getUserRoleDistribution() {
        Map<String, Integer> roleDistribution = new HashMap<>();
        try {
            List<UserMapper.RoleCount> roleCounts = userMapper.countByRole();
            if (roleCounts != null) {
                for (UserMapper.RoleCount roleCount : roleCounts) {
                    roleDistribution.put(roleCount.getRole(), roleCount.getCount());
                }
            }
        } catch (Exception e) {
            log.error("获取用户角色分布失败", e);
        }
        return roleDistribution;
    }

    /**
     * 获取总点赞数
     * 
     * @return 总点赞数
     */
    private Integer getTotalLikes() {
        try {
            return imageLikeMapper.countAll();
        } catch (Exception e) {
            log.error("获取总点赞数失败", e);
            return 0;
        }
    }

    /**
     * 获取总收藏数
     * 
     * @return 总收藏数
     */
    private Integer getTotalCollections() {
        try {
            return imageCollectionMapper.countAll();
        } catch (Exception e) {
            log.error("获取总收藏数失败", e);
            return 0;
        }
    }

    /**
     * 获取最近30天帖子增长数据
     * 
     * @return 最近30天帖子增长数据
     */
    private Map<String, Integer> getPostGrowth() {
        Map<String, Integer> postGrowth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = imagepostMapper.countByDate(dateStr);
                postGrowth.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取帖子增长数据失败", e);
                postGrowth.put(dateStr, 0);
            }
        }
        
        return postGrowth;
    }

    /**
     * 获取最近30天文档增长数据
     * 
     * @return 最近30天文档增长数据
     */
    private Map<String, Integer> getDocumentGrowth() {
        Map<String, Integer> documentGrowth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = wordMapper.countByDate(dateStr);
                documentGrowth.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取文档增长数据失败", e);
                documentGrowth.put(dateStr, 0);
            }
        }
        
        return documentGrowth;
    }

    /**
     * 获取最近30天图片生成增长数据
     * 
     * @return 最近30天图片生成增长数据
     */
    private Map<String, Integer> getImageGenerationGrowth() {
        Map<String, Integer> imageGenerationGrowth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = imageMapper.countByDate(dateStr);
                imageGenerationGrowth.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取图片生成增长数据失败", e);
                imageGenerationGrowth.put(dateStr, 0);
            }
        }
        
        return imageGenerationGrowth;
    }

    /**
     * 获取模型使用情况
     * <p>
     * 暂时返回空，需要根据实际的模型使用表结构实现
     * 
     * @return 模型使用情况
     */
    private Map<String, Integer> getModelUsage() {
        Map<String, Integer> modelUsage = new HashMap<>();
        try {
            // 暂时返回空，需要根据实际的模型使用表结构实现
        } catch (Exception e) {
            log.error("获取模型使用情况失败", e);
        }
        return modelUsage;
    }

    /**
     * 获取最近30天多模态交互增长数据
     * 
     * @return 最近30天多模态交互增长数据
     */
    private Map<String, Integer> getMultiModalInteractionGrowth() {
        Map<String, Integer> multiModalInteractionGrowth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            try {
                Integer count = multiModalResultMapper.countByDate(dateStr);
                multiModalInteractionGrowth.put(dateStr, count != null ? count : 0);
            } catch (Exception e) {
                log.error("获取多模态交互增长数据失败", e);
                multiModalInteractionGrowth.put(dateStr, 0);
            }
        }
        
        return multiModalInteractionGrowth;
    }
}
