package cn.cyf.chatclient.modules.multimodal.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultiModalInput {
    private String text;
    private List<MultipartFile> files;
    private String imageUrl;
    private String imageBase64;
    private Integer uid;
    private String sessionid;

    public MultiModalInput() {
    }

    public MultiModalInput(String text, List<MultipartFile> files, String imageUrl, String imageBase64, Integer uid, String sessionid) {
        this.text = text;
        this.files = files;
        this.imageUrl = imageUrl;
        this.imageBase64 = imageBase64;
        this.uid = uid;
        this.sessionid = sessionid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
}