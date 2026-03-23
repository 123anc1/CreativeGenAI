package cn.cyf.chatclient.modules.system.mapper;


import cn.cyf.chatclient.modules.user.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LogMapper {

    // 查询完整用户信息用于登录
    @Select("SELECT id, username, name, age, role, is_active FROM user WHERE username = #{username} AND password = #{password} AND is_active = 1")
    User login(User user);
    
    // 根据用户名查询用户
    @Select("SELECT id, username, name, age, role, is_active FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    // 管理员功能：查询所有用户
    @Select("SELECT id, username, name, age, role, is_active FROM user")
    List<User> findAllUsers();
    
    // 管理员功能：更新用户角色
    @Update("UPDATE user SET role = #{newRole} WHERE id = #{userId}")
    int updateRole(@Param("userId") Integer userId, @Param("newRole") String newRole);
    
    // 管理员功能：更新用户激活状态
    @Update("UPDATE user SET is_active = #{active} WHERE id = #{userId}")
    int updateActiveStatus(@Param("userId") Integer userId, @Param("active") Boolean active);
    
    // 管理员功能：插入新用户
    @Insert("INSERT INTO user (username, password, name, age, role, is_active) VALUES (#{username}, #{password}, #{name}, #{age}, #{role}, #{isActive})")
    int insertUser(User user);
    
    // 管理员功能：软删除用户（设置为非活跃）
    @Update("UPDATE user SET is_active = 0 WHERE id = #{userId}")
    int deleteUser(@Param("userId") Integer userId);

    // 根据ID查询用户
    @Select("SELECT id, username, name, age, role, is_active FROM user WHERE id = #{id}")
    User findById(Integer id);
}