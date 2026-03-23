package cn.cyf.chatclient.modules.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionVo {
    private Long id;
    private Integer userid;
    private String sessionid;
    private String title;
    private Boolean isActive;
    private LocalDateTime createdat;
    private LocalDateTime updatedat;
}
