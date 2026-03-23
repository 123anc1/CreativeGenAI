package cn.cyf.chatclient.modules.community.model;

import lombok.Data;

@Data
public class Imagepostparamadmin {
    private Integer page = 1; // 当前页码
    private Integer pageSize = 10;// 每页数量
}
