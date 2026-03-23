package cn.cyf.chatclient.modules.image.service.impl;

import cn.cyf.chatclient.common.pojo.TaskStatus;
import cn.cyf.chatclient.modules.image.service.ImageTaskManagerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ImageTaskManagerServiceImpl implements ImageTaskManagerService {
    private final Map<String, ImageTask> taskMap = new ConcurrentHashMap<>();

    @Override
    public void addTask(ImageTask task){
        taskMap.put(task.getTaskId(), task);
    }

    @Override
    public ImageTask getTask(String taskId){
        return taskMap.get(taskId);
    }

    @Override
    public void updateTask(ImageTask task){
        taskMap.put(task.getTaskId(), task);
    }

    @Override
    public void removeTask(String taskId) {
        taskMap.remove(taskId);
    }

    @Override
    public List<ImageTask> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<ImageTask> getTasksByStatus(TaskStatus status) {
        return taskMap.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsTask(String taskId) {
        return taskMap.containsKey(taskId);
    }

    @Override
    public int getTaskCount() {
        return taskMap.size();
    }
}
