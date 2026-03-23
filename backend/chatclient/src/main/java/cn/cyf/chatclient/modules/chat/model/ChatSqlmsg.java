package cn.cyf.chatclient.modules.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSqlmsg {
    private Long id;
    private String conversationid;
    private String content;
    private String type;
    private LocalDateTime createdat;
}
