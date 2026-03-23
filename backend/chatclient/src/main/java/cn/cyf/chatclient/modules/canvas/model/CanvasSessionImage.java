package cn.cyf.chatclient.modules.canvas.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CanvasSessionImage {
    private String id;
    private Integer imageId;
    private String sessionId;
    private String imageUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
