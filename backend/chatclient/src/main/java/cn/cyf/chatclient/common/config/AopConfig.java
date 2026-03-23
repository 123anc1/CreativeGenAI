package cn.cyf.chatclient.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP配置类
 * 启用AspectJ自动代理支持
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true) // 使用CGLIB代理
public class AopConfig {
    // AOP配置可以在这里添加更多自定义设置
}