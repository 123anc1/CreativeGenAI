package cn.cyf.chatclient.modules.image.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {
    private long id;
    private Integer userid;
    private String imagedata;
    private String prompt;
    private Integer imagesize;
    private LocalDateTime createdat;
    private LocalDateTime updatedat;
    private boolean isactive;

}
