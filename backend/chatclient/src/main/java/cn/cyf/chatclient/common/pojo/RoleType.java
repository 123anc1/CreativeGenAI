package cn.cyf.chatclient.common.pojo;

/**
 * 用户角色枚举
 * 定义系统中的所有角色类型
 */
public enum RoleType {
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),
    USER_ADMIN("USER_ADMIN", "用户管理员"),
    CONTENT_ADMIN("CONTENT_ADMIN", "内容审核管理员"),
    ADMIN("ADMIN", "管理员"),
    USER("USER", "普通用户");

    private final String code;
    private final String description;

    RoleType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据角色代码获取角色枚举
     */
    public static RoleType getByCode(String code) {
        for (RoleType role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}