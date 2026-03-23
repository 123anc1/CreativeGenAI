package cn.cyf.chatclient.modules.user.controller;

import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.user.model.User;
import cn.cyf.chatclient.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 提供用户注册、查询、更新等功能
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 管理员创建用户
     */
    @PostMapping("/create")
    @RequiresRole(value = "ADMIN")
    public Result createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getAge(),
                user.getRole() != null ? user.getRole() : "USER"
            );
            return Result.success(createdUser);
        } catch (Exception e) {
            log.error("创建用户失败: ", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员查询所有用户
     */
    @GetMapping("/list")
    @RequiresRole(value = "ADMIN")
    public Result listUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    /**
     * 根据ID查询用户信息
     */
    @GetMapping("/{id}")
    @RequiresRole(value = "ADMIN")
    public Result getUserById(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 更新用户角色（仅管理员）
     */
    @PutMapping("/role/{userId}")
    @RequiresRole(value = "ADMIN")
    public Result updateUserRole(@PathVariable Integer userId, @RequestParam String newRole) {
        boolean success = userService.updateUserRole(userId, newRole);
        if (success) {
            return Result.success("用户角色更新成功");
        } else {
            return Result.error("用户角色更新失败");
        }
    }

    /**
     * 激活/禁用用户（仅管理员）
     */
    @PutMapping("/status/{userId}")
    @RequiresRole(value = "ADMIN")
    public Result updateUserStatus(@PathVariable Integer userId, @RequestParam Boolean active) {
        boolean success = userService.setActiveStatus(userId, active);
        if (success) {
            return Result.success(active ? "用户激活成功" : "用户禁用成功");
        } else {
            return Result.error(active ? "用户激活失败" : "用户禁用失败");
        }
    }
    
    /**
     * 删除用户（仅管理员）
     */
//    @DeleteMapping("/{userId}")
//    @RequiresRole(value = "ADMIN")
//    public Result deleteUser(@PathVariable Integer userId) {
//        boolean success = userService.deleteUser(userId);
//        if (success) {
//            return Result.success("用户删除成功");
//        } else {
//            return Result.error("用户删除失败");
//        }
//    }
}