package cn.cyf.chatclient.modules.system.service.impl;

import cn.cyf.chatclient.modules.system.mapper.SysLogMapper;
import cn.cyf.chatclient.modules.system.model.LogQueryParam;
import cn.cyf.chatclient.modules.system.model.LogStatistics;
import cn.cyf.chatclient.modules.system.model.SysLog;
import cn.cyf.chatclient.modules.system.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统日志服务实现类
 */
@Slf4j
@Service
public class SysLogServiceImpl implements SysLogService {
    
    @Autowired
    private SysLogMapper sysLogMapper;
    
    @Override
    public void saveLog(SysLog sysLog) {
        try {
            sysLog.setCreateTime(LocalDateTime.now());
            sysLogMapper.insertLog(sysLog);
        } catch (Exception e) {
            log.error("保存日志失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public SysLog getLogById(Long id) {
        return sysLogMapper.selectById(id);
    }
    
    @Override
    public List<SysLog> getLogs(LogQueryParam param) {
        // 计算偏移量
        int offset = (param.getPage() - 1) * param.getPageSize();
        param.setOffset(offset);
        return sysLogMapper.selectLogs(param);
    }
    
    @Override
    public Long getLogCount(LogQueryParam param) {
        return sysLogMapper.selectLogCount(param);
    }
    
    @Override
    public int deleteLogsBefore(LocalDateTime beforeTime) {
        return sysLogMapper.deleteLogsBefore(beforeTime);
    }
    
    @Override
    public LogStatistics getLogStatistics() {
        LogStatistics statistics = new LogStatistics();
        
        // 获取查询参数
        LogQueryParam param = new LogQueryParam();
        
        // 总日志数
        statistics.setTotalLogs(getLogCount(param));
        
        // 今日日志数
        param.setStartTime(LocalDate.now().atStartOfDay());
        param.setEndTime(LocalDateTime.now());
        statistics.setTodayLogs(getLogCount(param));
        
        // 本周日志数
        param.setStartTime(LocalDate.now().minusDays(7).atStartOfDay());
        statistics.setWeekLogs(getLogCount(param));
        
        // 本月日志数
        param.setStartTime(LocalDate.now().minusDays(30).atStartOfDay());
        statistics.setMonthLogs(getLogCount(param));
        
        // 各种统计
        statistics.setModuleStats(getModuleStatsMap());
        statistics.setUserStats(getUserStatsMap());
        statistics.setIpStats(getIpStatsMap());
        statistics.setStatusStats(getStatusStatsMap());
        statistics.setTopOperations(getTopOperations());
        statistics.setDailyStats(getDailyStats());
        
        return statistics;
    }
    
    @Override
    public List<LogStatistics.ModuleStat> getModuleStats() {
        return sysLogMapper.selectModuleStats().stream()
                .map(stat -> {
                    LogStatistics.ModuleStat moduleStat = new LogStatistics.ModuleStat();
                    moduleStat.setModule(stat.getModule());
                    moduleStat.setCount(stat.getCount());
                    return moduleStat;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LogStatistics.UserStat> getUserStats() {
        return sysLogMapper.selectUserStats().stream()
                .map(stat -> {
                    LogStatistics.UserStat userStat = new LogStatistics.UserStat();
                    userStat.setUsername(stat.getUsername());
                    userStat.setCount(stat.getCount());
                    return userStat;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LogStatistics.IpStat> getIpStats() {
        return sysLogMapper.selectIpStats().stream()
                .map(stat -> {
                    LogStatistics.IpStat ipStat = new LogStatistics.IpStat();
                    ipStat.setIp(stat.getIp());
                    ipStat.setCount(stat.getCount());
                    return ipStat;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LogStatistics.StatusStat> getStatusStats() {
        return sysLogMapper.selectStatusStats().stream()
                .map(stat -> {
                    LogStatistics.StatusStat statusStat = new LogStatistics.StatusStat();
                    statusStat.setStatusCode(stat.getStatusCode());
                    statusStat.setCount(stat.getCount());
                    return statusStat;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LogStatistics.OperationCount> getTopOperations() {
        return sysLogMapper.selectTopOperations().stream()
                .map(stat -> {
                    LogStatistics.OperationCount operationCount = new LogStatistics.OperationCount();
                    operationCount.setOperation(stat.getOperation());
                    operationCount.setCount(stat.getCount());
                    return operationCount;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LogStatistics.DailyLogCount> getDailyStats() {
        return sysLogMapper.selectDailyStats().stream()
                .map(stat -> {
                    LogStatistics.DailyLogCount dailyLogCount = new LogStatistics.DailyLogCount();
                    dailyLogCount.setDate(stat.getDate().toString());
                    dailyLogCount.setCount(stat.getCount());
                    return dailyLogCount;
                })
                .collect(Collectors.toList());
    }
    
    // 辅助方法
    private Map<String, Long> getModuleStatsMap() {
        return sysLogMapper.selectModuleStats().stream()
                .collect(Collectors.toMap(
                        SysLogMapper.ModuleStat::getModule,
                        SysLogMapper.ModuleStat::getCount
                ));
    }
    
    private Map<String, Long> getUserStatsMap() {
        return sysLogMapper.selectUserStats().stream()
                .collect(Collectors.toMap(
                        SysLogMapper.UserStat::getUsername,
                        SysLogMapper.UserStat::getCount
                ));
    }
    
    private Map<String, Long> getIpStatsMap() {
        return sysLogMapper.selectIpStats().stream()
                .collect(Collectors.toMap(
                        SysLogMapper.IpStat::getIp,
                        SysLogMapper.IpStat::getCount
                ));
    }
    
    private Map<Integer, Long> getStatusStatsMap() {
        return sysLogMapper.selectStatusStats().stream()
                .collect(Collectors.toMap(
                        SysLogMapper.StatusStat::getStatusCode,
                        SysLogMapper.StatusStat::getCount
                ));
    }
}