package cn.cyf.chatclient.modules.user.service;


import cn.cyf.chatclient.modules.user.model.User;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface UserService {
    
    /**
     * 创建新用户
     */
    User createUser(String username, String password, String name, Integer age, String role);
    
    /**
     * 更新用户角色
     */
    boolean updateUserRole(Integer userId, String newRole);
    
    /**
     * 根据ID查找用户
     */
    User findById(Integer id);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Integer userId);
    
    /**
     * 激活/禁用用户
     */
    boolean setActiveStatus(Integer userId, Boolean active);
    
    /**
     * 获取所有用户（管理员功能）
     */
    List<User> getAllUsers();
}