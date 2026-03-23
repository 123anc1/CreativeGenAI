package cn.cyf.chatclient.modules.system.controller;


import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.system.model.LogQueryParam;
import cn.cyf.chatclient.modules.system.model.LogStatistics;
import cn.cyf.chatclient.modules.system.model.SysLog;
import cn.cyf.chatclient.modules.system.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统日志管理控制器
 * 提供日志查询、统计、删除等管理功能
 */
@Slf4j
@RestController
@RequestMapping("/sys/log")
public class SysLogController {
    
    @Autowired
    private SysLogService sysLogService;
    
    /**
     * 分页查询日志列表 - 管理员专用
     */
    @GetMapping("/list")
    @RequiresRole(value = "ADMIN")
    public Result getLogList(LogQueryParam param) {
        try {
            List<SysLog> logs = sysLogService.getLogs(param);
            Long total = sysLogService.getLogCount(param);
            
            Map<String, Object> result = new HashMap<>();
            result.put("logs", logs);
            result.put("total", total);
            result.put("page", param.getPage());
            result.put("pageSize", param.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询日志列表失败", e);
            return Result.error("查询日志列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询日志详情 - 管理员专用
     */
    @GetMapping("/{id}")
    @RequiresRole(value = "ADMIN")
    public Result getLogById(@PathVariable Long id) {
        try {
            SysLog sysLog = sysLogService.getLogById(id);
            if (sysLog == null) {
                return Result.error("日志不存在");
            }
            log.info("查询日志详情:{}", sysLog.toString());
            return Result.success(sysLog);
        } catch (Exception e) {
            log.error("查询日志详情失败", e);
            return Result.error("查询日志详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取日志统计信息 - 管理员专用
     */
    @GetMapping("/statistics")
    @RequiresRole(value = "ADMIN")
    public Result getLogStatistics() {
        try {
            LogStatistics statistics = sysLogService.getLogStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取日志统计失败", e);
            return Result.error("获取日志统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取模块统计 - 管理员专用
     */
    @GetMapping("/stats/module")
    @RequiresRole(value = "ADMIN")
    public Result getModuleStats() {
        try {
            List<LogStatistics.ModuleStat> stats = sysLogService.getModuleStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取模块统计失败", e);
            return Result.error("获取模块统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户操作统计 - 管理员专用
     */
    @GetMapping("/stats/user")
    @RequiresRole(value = "ADMIN")
    public Result getUserStats() {
        try {
            List<LogStatistics.UserStat> stats = sysLogService.getUserStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return Result.error("获取用户统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取IP访问统计 - 管理员专用
     */
    @GetMapping("/stats/ip")
    @RequiresRole(value = "ADMIN")
    public Result getIpStats() {
        try {
            List<LogStatistics.IpStat> stats = sysLogService.getIpStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取IP统计失败", e);
            return Result.error("获取IP统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取状态码统计 - 管理员专用
     */
    @GetMapping("/stats/status")
    @RequiresRole(value = "ADMIN")
    public Result getStatusStats() {
        try {
            List<LogStatistics.StatusStat> stats = sysLogService.getStatusStats();
            log.info("获取状态码:{}", stats.toString());
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取状态码统计失败", e);
            return Result.error("获取状态码统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门操作统计 - 管理员专用
     */
    @GetMapping("/stats/top-operations")
    @RequiresRole(value = "ADMIN")
    public Result getTopOperations() {
        try {
            List<LogStatistics.OperationCount> operations = sysLogService.getTopOperations();
            return Result.success(operations);
        } catch (Exception e) {
            log.error("获取热门操作统计失败", e);
            return Result.error("获取热门操作统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取每日日志统计 - 管理员专用
     */
    @GetMapping("/stats/daily")
    @RequiresRole(value = "ADMIN")
    public Result getDailyStats() {
        try {
            List<LogStatistics.DailyLogCount> stats = sysLogService.getDailyStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取每日统计失败", e);
            return Result.error("获取每日统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除指定时间之前的日志 - 管理员专用
     */
    @DeleteMapping("/cleanup")
    @RequiresRole(value = "ADMIN")
    public Result cleanupLogs(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        try {
            int deletedCount = sysLogService.deleteLogsBefore(beforeTime);
            return Result.success("成功删除 " + deletedCount + " 条日志记录");
        } catch (Exception e) {
            log.error("清理日志失败", e);
            return Result.error("清理日志失败: " + e.getMessage());
        }
    }
}