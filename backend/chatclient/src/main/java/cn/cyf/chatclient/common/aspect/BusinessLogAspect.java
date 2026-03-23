package cn.cyf.chatclient.common.aspect;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.modules.system.model.SysLog;
import cn.cyf.chatclient.modules.system.service.SysLogService;
import cn.cyf.chatclient.common.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 业务日志切面类
 * 处理带有@LogOperation注解的方法的日志记录
 */
@Slf4j
@Aspect
@Component
public class BusinessLogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 定义切点：拦截所有带有@LogOperation注解的方法
     */
    @Around("@annotation(cn.cyf.chatclient.common.annotation.LogOperation)")
    public Object logBusinessOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 调试信息
        log.debug("BusinessLogAspect拦截方法: {}", joinPoint.getSignature().getName());
        
        // 额外验证：确保方法确实有@LogOperation注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        
        if (logOperation == null) {
            log.warn("警告：BusinessLogAspect拦截了没有@LogOperation注解的方法: {}", 
                    joinPoint.getSignature().getName());
            return joinPoint.proceed();
        }
        
        // 获取当前请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String username = "system";
        String ip = "unknown";
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            username = getUsernameFromRequest(request);
            ip = getClientIpAddress(request);
        }
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            // 记录业务操作日志
            recordBusinessLog(joinPoint, logOperation, username, ip, startTime, result, exception);
        }
    }

    /**
     * 记录业务操作日志
     */
    private void recordBusinessLog(ProceedingJoinPoint joinPoint, LogOperation logOperation, 
                                 String username, String ip, long startTime, 
                                 Object result, Exception exception) {
        try {
            String operation = logOperation.value().isEmpty() ? 
                getMethodName(joinPoint) : logOperation.value();
            String module = logOperation.module().isEmpty() ? 
                "业务操作" : logOperation.module();
            
            // 构建日志对象
            SysLog sysLog = new SysLog();
            sysLog.setUsername(username != null ? username : "anonymous");
            sysLog.setOperation(operation);
            sysLog.setModule(module);
            sysLog.setIp(ip);
            sysLog.setCreateTime(java.time.LocalDateTime.now());
            sysLog.setExecutionTime(System.currentTimeMillis() - startTime);
            
            // 设置其他基本信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setMethod(request.getMethod());
                sysLog.setUri(request.getRequestURI());
                sysLog.setUserAgent(request.getHeader("User-Agent"));
            }
            
            // 根据注解配置决定是否记录参数和结果
            if (logOperation.logParams()) {
                sysLog.setParams(getMethodParameters(joinPoint));
            }
            
            if (logOperation.logResult() && result != null) {
                sysLog.setReturnValue(result.toString());
            }
            
            // 设置状态码和异常信息
            if (exception != null) {
                sysLog.setStatusCode(500);
                sysLog.setException(exception.getClass().getSimpleName() + ": " + exception.getMessage());
            } else {
                sysLog.setStatusCode(200);
            }
            
            // 保存日志
            sysLogService.saveLog(sysLog);
            
            // 根据日志级别记录不同级别的日志
            switch (logOperation.level()) {
                case TRACE:
                    log.trace("业务操作日志: {} - {}", operation, result);
                    break;
                case DEBUG:
                    log.debug("业务操作日志: {} - {}", operation, result);
                    break;
                case INFO:
                    log.info("业务操作日志: {} - {}", operation, result);
                    break;
                case WARN:
                    log.warn("业务操作日志: {} - {}", operation, result);
                    break;
                case ERROR:
                    log.error("业务操作日志: {} - {}", operation, exception != null ? exception.getMessage() : result);
                    break;
            }
            
        } catch (Exception e) {
            log.error("记录业务操作日志失败", e);
        }
    }

    /**
     * 从请求中获取用户名
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            try {
                return JwtUtils.getUsername(token);
            } catch (Exception e) {
                log.debug("解析token获取用户名失败", e);
            }
        }
        return null;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取方法名称
     */
    private String getMethodName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    /**
     * 获取方法参数
     */
    private String getMethodParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("arg").append(i).append(": ").append(args[i]);
        }
        sb.append("}");
        
        return sb.toString();
    }
}