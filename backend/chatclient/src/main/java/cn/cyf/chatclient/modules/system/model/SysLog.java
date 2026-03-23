package cn.cyf.chatclient.modules.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 * 用于记录用户的操作行为，便于审计和监控
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysLog {
    private Long id;                    // 日志ID
    private String username;            // 操作用户名
    private String operation;           // 操作描述
    private String method;              // 请求方法
    private String params;              // 请求参数
    private String ip;                  // IP地址
    private String userAgent;           // 用户代理
    private Long executionTime;         // 执行时间(毫秒)
    private String returnValue;         // 返回值
    private String exception;           // 异常信息
    private LocalDateTime createTime;   // 创建时间
    private String module;              // 所属模块
    private String uri;                 // 请求URI
    private Integer statusCode;         // HTTP状态码
}