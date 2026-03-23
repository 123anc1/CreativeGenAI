package cn.cyf.chatclient.modules.system.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 日志统计结果类
 * 用于封装各种维度的日志统计数据
 */
@Data
public class LogStatistics {
    private Long totalLogs;                     // 总日志数
    private Long todayLogs;                     // 今日日志数
    private Long weekLogs;                      // 本周日志数
    private Long monthLogs;                     // 本月日志数
    private Map<String, Long> moduleStats;      // 各模块统计
    private Map<String, Long> userStats;        // 用户操作统计
    private Map<String, Long> ipStats;          // IP访问统计
    private Map<Integer, Long> statusStats;     // 状态码统计
    private List<OperationCount> topOperations; // 热门操作统计
    private List<DailyLogCount> dailyStats;     // 每日日志统计
    
    /**
     * 模块统计内部类
     */
    @Data
    public static class ModuleStat {
        private String module;
        private Long count;
    }
    
    /**
     * 用户统计内部类
     */
    @Data
    public static class UserStat {
        private String username;
        private Long count;
    }
    
    /**
     * IP统计内部类
     */
    @Data
    public static class IpStat {
        private String ip;
        private Long count;
    }
    
    /**
     * 状态码统计内部类
     */
    @Data
    public static class StatusStat {
        private Integer statusCode;
        private Long count;
    }
    
    /**
     * 操作计数内部类
     */
    @Data
    public static class OperationCount {
        private String operation;
        private Long count;
    }
    
    /**
     * 每日日志计数内部类
     */
    @Data
    public static class DailyLogCount {
        private String date;
        private Long count;
    }
}