package cn.cyf.chatclient.modules.system.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日志查询参数类
 * 用于封装日志查询的各种条件
 */
@Data
public class LogQueryParam {
    private String username;        // 用户名
    private String operation;       // 操作描述
    private String module;          // 模块名称
    private String ip;              // IP地址
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private Integer page = 1;       // 当前页码
    private Integer pageSize = 10;  // 每页大小
    private String orderBy = "create_time"; // 排序字段
    private String orderDirection = "desc"; // 排序方向
    private Integer statusCode;     // 状态码
    private String method;          // 请求方法
    private String uri;             // 请求URI
    private Integer offset;         // 偏移量（用于分页计算）
}