package cn.cyf.chatclient.modules.system.service;



import cn.cyf.chatclient.modules.system.model.LogQueryParam;
import cn.cyf.chatclient.modules.system.model.LogStatistics;
import cn.cyf.chatclient.modules.system.model.SysLog;

import java.util.List;

/**
 * 系统日志服务接口
 */
public interface SysLogService {
    
    /**
     * 保存日志记录
     */
    void saveLog(SysLog sysLog);
    
    /**
     * 根据ID查询日志
     */
    SysLog getLogById(Long id);
    
    /**
     * 分页查询日志列表
     */
    List<SysLog> getLogs(LogQueryParam param);
    
    /**
     * 获取日志总数
     */
    Long getLogCount(LogQueryParam param);
    
    /**
     * 删除指定时间之前的日志
     */
    int deleteLogsBefore(java.time.LocalDateTime beforeTime);
    
    /**
     * 获取日志统计信息
     */
    LogStatistics getLogStatistics();
    
    /**
     * 获取模块统计
     */
    List<LogStatistics.ModuleStat> getModuleStats();
    
    /**
     * 获取用户操作统计
     */
    List<LogStatistics.UserStat> getUserStats();
    
    /**
     * 获取IP访问统计
     */
    List<LogStatistics.IpStat> getIpStats();
    
    /**
     * 获取状态码统计
     */
    List<LogStatistics.StatusStat> getStatusStats();
    
    /**
     * 获取热门操作统计
     */
    List<LogStatistics.OperationCount> getTopOperations();
    
    /**
     * 获取每日日志统计
     */
    List<LogStatistics.DailyLogCount> getDailyStats();
}