package cn.cyf.chatclient.modules.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private int age;
    private String role;        // 用户角色：ADMIN 或 USER
    private Boolean isActive;   // 用户是否激活，默认为true
}