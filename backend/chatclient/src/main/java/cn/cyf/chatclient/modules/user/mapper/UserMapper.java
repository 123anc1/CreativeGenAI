package cn.cyf.chatclient.modules.user.mapper;

import cn.cyf.chatclient.modules.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Integer id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM user")
    List<User> selectAll();

    /**
     * 查询活跃用户数
     */
    @Select("SELECT COUNT(*) FROM user WHERE is_active = true")
    Integer countActiveUsers();

    /**
     * 统计用户角色分布
     */
    @Select("SELECT role, COUNT(*) as count FROM user GROUP BY role")
    List<RoleCount> countByRole();

    /**
     * 角色计数内部类
     */
    static class RoleCount {
        private String role;
        private Integer count;
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public Integer getCount() {
            return count;
        }
        
        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
