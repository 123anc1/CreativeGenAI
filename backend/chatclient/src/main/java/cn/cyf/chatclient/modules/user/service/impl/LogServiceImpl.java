package cn.cyf.chatclient.modules.user.service.impl;

import cn.cyf.chatclient.common.pojo.LoginInfo;
import cn.cyf.chatclient.common.utils.JwtUtils;
import cn.cyf.chatclient.common.utils.MD5Utils;
import cn.cyf.chatclient.modules.system.mapper.LogMapper;
import cn.cyf.chatclient.modules.system.service.LogService;
import cn.cyf.chatclient.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public LoginInfo login(User user) {
        user.setPassword(MD5Utils.encode(user.getPassword()));
        User check = logMapper.login(user);
        if (check != null) {
            //生成JWT令牌，包含用户角色信息
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", check.getId());
            claims.put("username", check.getUsername());
            claims.put("name", check.getName());
            claims.put("role", check.getRole()); // 添加角色信息到JWT
            String jwt = JwtUtils.generateToken(claims);
            log.info("生成JWT令牌：" + jwt);
            return new LoginInfo(check.getId(), check.getUsername(), check.getName(), jwt, check.getRole());
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return logMapper.findByUsername(username);
    }

    @Override
    public boolean hasRole(String username, String role) {
        User user = findByUsername(username);
        return user != null && role.equals(user.getRole());
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            log.info("用户名已存在：{}", user.getUsername());
            return false;
        }
        
        // 对密码进行MD5加密
        user.setPassword(MD5Utils.encode(user.getPassword()));
        
        // 设置默认角色为普通用户
        user.setRole("USER");
        
        // 设置默认激活状态为true
        user.setIsActive(true);
        
        // 插入用户到数据库
        int result = logMapper.insertUser(user);
        log.info("用户注册结果：{}", result > 0 ? "成功" : "失败");
        return result > 0;
    }
}
