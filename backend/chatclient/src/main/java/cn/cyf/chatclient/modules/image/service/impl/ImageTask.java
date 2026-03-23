package cn.cyf.chatclient.modules.image.service.impl;


import cn.cyf.chatclient.common.pojo.TaskStatus;
import lombok.Data;

@Data
public class ImageTask {
    private String taskId;
    private TaskStatus status;
    private Integer progress;// 0-100
    private String resultUrl;
    private String errorMsg;
}
