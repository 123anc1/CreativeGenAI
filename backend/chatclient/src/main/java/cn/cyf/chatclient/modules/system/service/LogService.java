package cn.cyf.chatclient.modules.system.service;


import cn.cyf.chatclient.common.pojo.LoginInfo;
import cn.cyf.chatclient.modules.user.model.User;

public interface LogService {
    LoginInfo login(User user);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 验证用户角色
     */
    boolean hasRole(String username, String role);
}
