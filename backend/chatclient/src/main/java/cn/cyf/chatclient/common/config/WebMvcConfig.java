package cn.cyf.chatclient.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类
 * 主要配置异步请求执行的线程池
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置异步请求支持的线程池
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(asyncTaskExecutor());
    }

    /**
     * 定义异步任务执行器 Bean
     * 使用 ThreadPoolTaskExecutor 替代默认的 SimpleAsyncTaskExecutor
     */
    @Bean(name = "mvcTaskExecutor")
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(50);
        // 线程名称前缀
        executor.setThreadNamePrefix("mvc-async-");
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 等待任务全部完成后再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭时等待的时间（秒）
        executor.setAwaitTerminationSeconds(30);
        // 初始化
        executor.initialize();
        return executor;
    }
}
