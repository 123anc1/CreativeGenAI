package cn.cyf.chatclient.modules.document.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorldGlobal {
    private long id;
    private String md5;
    private String filename;
    private long size;
    private Integer userid;
}
