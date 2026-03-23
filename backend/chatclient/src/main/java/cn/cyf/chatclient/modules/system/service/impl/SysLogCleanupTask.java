package cn.cyf.chatclient.modules.system.service.impl;

import cn.cyf.chatclient.modules.system.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 系统日志定时任务
 * 定期清理过期日志，保持系统性能
 */
@Slf4j
@Component
public class SysLogCleanupTask {
    
    @Autowired
    private SysLogService sysLogService;
    
    /**
     * 每天凌晨2点执行日志清理任务
     * 删除90天前的日志记录
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldLogs() {
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(5);
            int deletedCount = sysLogService.deleteLogsBefore(beforeTime);
            log.info("定时清理日志任务执行完成，删除了 {} 条 {} 天前的日志记录", deletedCount, 5);
        } catch (Exception e) {
            log.error("执行日志清理任务失败", e);
        }
    }
    
    /**
     * 每周日凌晨3点执行一次全面的日志统计分析
     * 可以扩展为生成报表等功能
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void weeklyLogAnalysis() {
        try {
            log.info("开始执行每周日志分析任务");
            // 这里可以添加更复杂的日志分析逻辑
            // 比如生成周报、发送邮件通知等
            log.info("每周日志分析任务执行完成");
        } catch (Exception e) {
            log.error("执行每周日志分析任务失败", e);
        }
    }
}