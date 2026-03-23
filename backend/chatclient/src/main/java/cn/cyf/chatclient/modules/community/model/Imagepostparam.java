package cn.cyf.chatclient.modules.community.model;

import lombok.Data;

@Data
public class Imagepostparam {
    private Integer page = 1;
    private Integer pageSize = 20;
    private String keyword;
}
