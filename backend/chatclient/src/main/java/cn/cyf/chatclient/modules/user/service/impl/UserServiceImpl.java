package cn.cyf.chatclient.modules.user.service.impl;


import cn.cyf.chatclient.common.utils.MD5Utils;
import cn.cyf.chatclient.modules.system.mapper.LogMapper;
import cn.cyf.chatclient.modules.user.model.User;
import cn.cyf.chatclient.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public User createUser(String username, String password, String name, Integer age, String role) {
        // 检查用户名是否已存在
        User existingUser = logMapper.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在: " + username);
        }

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(MD5Utils.encode(password)); // 密码加密
        newUser.setName(name);
        newUser.setAge(age != null ? age : 0);
        newUser.setRole(role != null ? role : "USER"); // 默认为普通用户
        newUser.setIsActive(true); // 默认激活

        // 插入新用户到数据库
        int result = logMapper.insertUser(newUser);
        if (result <= 0) {
            throw new RuntimeException("创建用户失败");
        }
        
        return newUser;
    }

    @Override
    public boolean updateUserRole(Integer userId, String newRole) {
        int result = logMapper.updateRole(userId, newRole);
        return result > 0;
    }

    @Override
    public User findById(Integer id) {
        return logMapper.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return logMapper.findByUsername(username);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        int result = logMapper.deleteUser(userId);
        return result > 0;
    }

    @Override
    public boolean setActiveStatus(Integer userId, Boolean active) {
        int result = logMapper.updateActiveStatus(userId, active);
        return result > 0;
    }
    
    @Override
    public List<User> getAllUsers() {
        return logMapper.findAllUsers();
    }
}