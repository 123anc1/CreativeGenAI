package cn.cyf.chatclient.modules.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Userparam {
    private Integer page = 1; // 当前页码
    private Integer pageSize = 20;// 每页数量
}
