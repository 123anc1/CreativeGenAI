package cn.cyf.chatclient.common.interceptor;

import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class  LogInterceptor implements HandlerInterceptor {
    //目标资源方法执行前执行，返回true：放行，返回false：不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取到请求路径
        String path = request.getRequestURI();

        //2.判断是否是登录请求，如果路径中末尾/login，说明是登录操作，放行
        if(path.endsWith("/login")) {
            // 登录操作不需要验证
            return true;
        }

        //3.获取请求头中的token
        String token = request.getHeader("token");

        //4.判断token是否存在，如果不存在，说明用户没有登录，返回错误信息（响应401状态码）
        if(token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        //5.如果token存在，校验令牌，如果校验失败->返回错误信息（响应401状态码）
        String userRole = null;
        try {
            userRole = JwtUtils.getUserRole(token);
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }

        //6.检查是否需要特定角色访问控制
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            
            // 检查方法上是否有@RequiresRole注解
            RequiresRole methodAnnotation = handlerMethod.getMethodAnnotation(RequiresRole.class);
            if (methodAnnotation != null) {
                if (!hasRequiredRole(methodAnnotation, userRole)) {
                    response.setStatus(403); // 禁止访问
                    return false;
                }
            }
            
            // 检查类上是否有@RequiresRole注解
            RequiresRole classAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod().getDeclaringClass(), RequiresRole.class);
            if (classAnnotation != null) {
                if (!hasRequiredRole(classAnnotation, userRole)) {
                    response.setStatus(403); // 禁止访问
                    return false;
                }
            }
        }

        //7.校验通过，放行
        return true;
    }
    
    /**
     * 检查用户是否具有访问资源所需的权限
     */
    private boolean hasRequiredRole(RequiresRole annotation, String userRole) {
        if (annotation.value().isEmpty() && annotation.roles().length == 0) {
            // 如果没有指定具体角色，则只要求已登录即可
            return true;
        }
        
        // 检查单一角色要求
        if (!annotation.value().isEmpty() && annotation.value().equals(userRole)) {
            return true;
        }
        
        // 检查多个角色要求
        for (String role : annotation.roles()) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取注解中要求的角色列表
     */
    private String[] getRequiredRoles(RequiresRole annotation) {
        if (!annotation.value().isEmpty()) {
            return new String[]{annotation.value()};
        } else {
            return annotation.roles();
        }
    }
}
