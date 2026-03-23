package cn.cyf.chatclient.modules.image.service;

import cn.cyf.chatclient.common.pojo.TaskStatus;
import cn.cyf.chatclient.modules.image.service.impl.ImageTask;

import java.util.List;

public interface ImageTaskManagerService {
    void addTask(ImageTask task);

    ImageTask getTask(String taskId);

    void updateTask(ImageTask task);

    void removeTask(String taskId);

    List<ImageTask> getAllTasks();

    List<ImageTask> getTasksByStatus(TaskStatus status);

    boolean existsTask(String taskId);

    int getTaskCount();
}
