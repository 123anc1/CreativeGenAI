package cn.cyf.chatclient.modules.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsg {
    private Long id;
    private Integer uid;
    private String sessionid;
    private String content;
    private String type;
    private String title;
    private LocalDateTime createdat;
}
