package cn.cyf.chatclient.modules.canvas.model;

import lombok.Data;

import java.util.List;

@Data
public class CanvasImage {
    private Integer uId;
    private String sessionId;
    private List<String> imageBase64;
    private List<String> imageMaskBase64;
    private List<String> imageUrl;
    private List<String> imageMaskUrl;
    private List<String> imageResultUrl;
    private String prompt;
    private int seed;

}
