package cn.cyf.chatclient.modules.system.mapper;


import cn.cyf.chatclient.modules.system.model.LogQueryParam;
import cn.cyf.chatclient.modules.system.model.SysLog;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统日志 Mapper 接口
 */
@Mapper
public interface SysLogMapper {
    
    /**
     * 插入日志记录
     */
    int insertLog(SysLog sysLog);
    
    /**
     * 根据ID查询日志
     */
    @Select("SELECT * FROM sys_log WHERE id = #{id}")
    SysLog selectById(Long id);
    
    /**
     * 分页查询日志列表
     */
    List<SysLog> selectLogs(LogQueryParam param);
    
    /**
     * 查询日志总数
     */
    Long selectLogCount(LogQueryParam param);
    
    /**
     * 删除指定时间之前的日志
     */
    @Delete("DELETE FROM sys_log WHERE create_time < #{beforeTime}")
    int deleteLogsBefore(java.time.LocalDateTime beforeTime);
    
    /**
     * 按模块统计日志数量
     */
    @Select("SELECT module, COUNT(*) as count FROM sys_log GROUP BY module ORDER BY count DESC")
    List<ModuleStat> selectModuleStats();
    
    /**
     * 按用户统计操作次数
     */
    @Select("SELECT username, COUNT(*) as count FROM sys_log GROUP BY username ORDER BY count DESC LIMIT 10")
    List<UserStat> selectUserStats();
    
    /**
     * 按IP统计访问次数
     */
    @Select("SELECT ip, COUNT(*) as count FROM sys_log GROUP BY ip ORDER BY count DESC LIMIT 10")
    List<IpStat> selectIpStats();
    
    /**
     * 按状态码统计
     */
    @Select("SELECT status_code, COUNT(*) as count FROM sys_log GROUP BY status_code ORDER BY status_code")
    List<StatusStat> selectStatusStats();
    
    /**
     * 统计热门操作
     */
    @Select("SELECT operation, COUNT(*) as count FROM sys_log GROUP BY operation ORDER BY count DESC LIMIT 10")
    List<OperationStat> selectTopOperations();
    
    /**
     * 按日期统计日志数量
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM sys_log GROUP BY DATE(create_time) ORDER BY date DESC LIMIT 30")
    List<DailyStat> selectDailyStats();
    
    /**
     * 模块统计结果类
     */
    @Setter
    @Getter
    class ModuleStat {
        private String module;
        private Long count;

    }
    
    /**
     * 用户统计结果类
     */
    @Setter
    @Getter
    class UserStat {
        private String username;
        private Long count;

    }
    
    /**
     * IP统计结果类
     */
    @Setter
    @Getter
    class IpStat {
        private String ip;
        private Long count;

    }
    
    /**
     * 状态码统计结果类
     */
    @Setter
    @Getter
    class StatusStat {
        private Integer statusCode;
        private Long count;

    }
    
    /**
     * 操作统计结果类
     */
    @Setter
    @Getter
    class OperationStat {
        private String operation;
        private Long count;

    }
    
    /**
     * 日常统计结果类
     */
    @Setter
    @Getter
    class DailyStat {
        private java.sql.Date date;
        private Long count;
    }
}