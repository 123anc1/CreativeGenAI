package cn.cyf.chatclient.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义日志注解
 * 用于标记需要特殊日志处理的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 是否记录参数
     */
    boolean logParams() default true;
    
    /**
     * 是否记录返回值
     */
    boolean logResult() default false;
    
    /**
     * 是否忽略异常
     */
    boolean ignoreException() default false;
    
    /**
     * 自定义日志级别
     */
    LogLevel level() default LogLevel.INFO;
    
    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}