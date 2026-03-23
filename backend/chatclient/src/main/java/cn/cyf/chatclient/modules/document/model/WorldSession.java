package cn.cyf.chatclient.modules.document.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorldSession {
    private long id;
    private String userid;
    private String sessionid;
    private String md5;
    private String filename;
    private int chunkcount;
}
