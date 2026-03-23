package cn.cyf.chatclient.modules.canvas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo {
    private Integer uid;
    private String sessionId;
    private String text;
    private List<String> imageUrl;
    private List<String> imageBase64;
    private List<String> imageBackUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
