package cn.cyf.chatclient.modules.image.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;
import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.image.mapper.ImageMapper;
import cn.cyf.chatclient.modules.community.mapper.ImagepostMapper;
import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.model.UserImage;
import cn.cyf.chatclient.modules.image.service.ImageService;
import cn.cyf.chatclient.modules.image.service.ImageTaskManagerService;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import cn.cyf.chatclient.common.pojo.TaskStatus;
import cn.cyf.chatclient.modules.image.model.ImageResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    @Qualifier("DeepseekchatClientWithoutMemory")
    private ChatClient chatClient;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ImageCacheService imageCacheService;
    // 调用python服务
    private final WebClient pyClient = WebClient.builder()
            .baseUrl("http://127.0.0.1:8000")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)) // 30MB
            .build();
    @Autowired
    private ImagepostMapper imagepostMapper;

    @Autowired
    private ImageTaskManagerService taskManagerService;

    @Autowired
    private ExecutorService imageExecutor;

    @Override
    public String caption(MultipartFile file) {
        String result = pyClient
                .post()
                .uri("/caption")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(Map.class)  // 接收为 Map 类型
                .map(responseMap -> (String) responseMap.get("caption"))  // 提取 caption 字段
                .block();  // 阻塞获取结果
        Prompt prompt = new Prompt(
                "你是一个文生图制作能手现在呢有一段关于图像的描述"
                        +result+
                        "，你根据这个描述将描述转化成文生图模型能更好生成图片的描述"+
                        "注意你回复的消息只能是关于图像描述的文字，不能出现好的，我来回复这些文字"
        );
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @Override
    public ImageResponseVo text_to_image(ImageModel imageModel) {

        if (Objects.equals(imageModel.getModel_name(), "zhipu文生图")) {
            ImageResponseVo imageResponseVo = new ImageResponseVo();
//            log.info("zhipuAi提示词：{}", imageModel.getPrompt());
            try {
                ZhipuAiClient client = ZhipuAiClient.builder().ofZHIPU().apiKey("ad197af1d00f4175ba32377f594f6905.6C32duiyW2zEX0nr").build();

                CreateImageRequest request = CreateImageRequest.builder()
                        .model("glm-image")
                        .prompt(imageModel.getPrompt())
                        .size("768x1024")
                        .build();

                ImageResponse response = client.images().createImage(request);
//                log.info("imageResponse:{}",response);
                String imageUrl = response.getData().getData().get(0).getUrl();
                if (response != null && response.getData() != null) {
                    imageResponseVo.setImage_url(imageUrl); // 根据实际响应结构调整
                    return imageResponseVo;
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("userid", imageModel.getUserid());
        formData.add("model_name", imageModel.getModel_name());
        formData.add("negative_prompt", imageModel.getNegativePrompt() != null ? imageModel.getNegativePrompt() : "low quality,blur");
        formData.add("steps", imageModel.getSteps() != null ? imageModel.getSteps().toString() : "30");
        formData.add("cfg_scale", imageModel.getCfg_scale() != null ? imageModel.getCfg_scale().toString() : "7");
        formData.add("style", imageModel.getStyle() != null ? imageModel.getStyle() : "");
        formData.add("width", imageModel.getWidth() != null ? imageModel.getWidth().toString() : "512");
        formData.add("height", imageModel.getHeight() != null ? imageModel.getHeight().toString() : "512");

        if (imageModel.getImage_file() != null) {
            formData.add("image_file", imageModel.getImage_file().getResource());
        }

        String prompter = """
        你是一位 AI 视觉提示词专家。请从以下用户描述中先将用户描述先翻译为英文再将描述信息回复成文本形式。
        注意：
        - 保留原文中的英文专业术语（如 OLED, cyberpunk）
        - 中文风格词需补充英文对应（如"水墨" → "Chinese ink wash painting"）
        - 不要曲解描述的含义
        - 根据中文语义将翻译的英文也以关键词句来说较少缀叙

        用户输入：
        "
        {user_input}
        "

        只输出优化好的用户输入内容文本，无其他内容。
        """;

        Prompt prompt = new Prompt(prompter.replace("{user_input}", imageModel.getPrompt()));
        String promptResult = chatClient.prompt(prompt)
                .call()
                .content();
//        formData.add("prompt", imageModel.getPrompt());
        formData.add("prompt", promptResult);
        return pyClient
                .post()
                .uri("/text_to_image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(ImageResponseVo.class)
                .block();
    }

    @Override
    public String asyncTextToImage(ImageModel imageModel) {
        // 生成唯一任务ID
        String taskId = UUID.randomUUID().toString();
        
        // 创建任务对象
        ImageTask task = new ImageTask();
        task.setTaskId(taskId);
        task.setStatus(TaskStatus.PENDING);
        task.setProgress(0);
        task.setErrorMsg(null);
        task.setResultUrl(null);
        
        // 添加到任务管理器
        taskManagerService.addTask(task);
        
        log.info("开始异步文生图任务，任务ID: {}", taskId);
        
        // 异步执行图像生成
        CompletableFuture.supplyAsync(() -> {
            try {
                // 更新任务状态为运行中
                task.setStatus(TaskStatus.RUNNING);
                task.setProgress(10);
                taskManagerService.updateTask(task);
                
                // 执行图像生成
                ImageResponseVo response = text_to_image(imageModel);
                
                if (response != null && response.getImage_url() != null) {
                    // 更新进度
                    task.setProgress(80);
                    taskManagerService.updateTask(task);
                    
                    // 保存图像
                    save(response.getImage_url(), imageModel, imageModel.getPrompt());
                    
                    // 生成签名URL
                    String signedUrl = response.getImage_url();
                    if (!response.getImage_url().startsWith("http")) {
                        signedUrl = aliyunOSSOperator.generateSignedUrl(response.getImage_url(), 3600*24);
                        imageCacheService.cacheSignedUrl(response.getImage_url(), signedUrl, 3600*24);
                    }
                    
                    // 任务完成
                    task.setStatus(TaskStatus.SUCCESS);
                    task.setProgress(100);
                    task.setResultUrl(signedUrl);
                    taskManagerService.updateTask(task);
                    
                    log.info("异步文生图任务完成，任务ID: {}", taskId);
                    return signedUrl;
                } else {
                    throw new RuntimeException("图像生成失败");
                }
            } catch (Exception e) {
                log.error("异步文生图任务失败，任务ID: {}", taskId, e);
                task.setStatus(TaskStatus.FAILED);
                task.setErrorMsg(e.getMessage());
                task.setProgress(0);
                taskManagerService.updateTask(task);
                return null;
            }
        }, imageExecutor);
        
        return taskId;
    }

    @Override
    public ImageTask getTaskStatus(String taskId) {
        return taskManagerService.getTask(taskId);
    }

    @Override
    public void cancelTask(String taskId) {
        ImageTask task = taskManagerService.getTask(taskId);
        if (task != null) {
            if (task.getStatus() == TaskStatus.RUNNING || task.getStatus() == TaskStatus.PENDING) {
                task.setStatus(TaskStatus.STOPPED);
                taskManagerService.updateTask(task);
                log.info("任务已取消，任务ID: {}", taskId);
            }
        }
    }

    @Override
    public void save(String result,ImageModel imageModel,String description){
        String userid = imageModel.getUserid();
        UserImage userImage = new UserImage();
        userImage.setImagedata(result);
        userImage.setUserid(Integer.parseInt(userid));
        userImage.setPrompt(description);
        userImage.setCreatedat(java.time.LocalDateTime.now());
        userImage.setUpdatedat(java.time.LocalDateTime.now());
//        log.info("插入前 UserImage: {}", userImage);
        imageMapper.save(userImage);
//        log.info("插入后 UserImage: {}", userImage);
        imageModel.setId(userImage.getId());
//        log.info("保存参数: {}", imageModel);
        imageMapper.saveparam(imageModel);
    }

    @Override
    public List<UserImage> list(String userid) throws Exception {

        List<UserImage> userImages =  imageMapper.list(Integer.parseInt(userid));
        for (UserImage userImage : userImages) {
            try {
                if (userImage.getImagedata() != null && userImage.getImagedata().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(userImage.getImagedata());
                if (cachedUrl != null) {
                    userImage.setImagedata(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(userImage.getImagedata(), 3600*24);
                imageCacheService.cacheSignedUrl(userImage.getImagedata(), signedUrl , 3600*24);
                userImage.setImagedata(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userImages;
    }

    @Override
    public void delete(String id) {
        String fileUrl = imageMapper.findUrlById(id);
//        log.info("删除文件:" + fileUrl);
        try {
            aliyunOSSOperator.delete(fileUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        imageMapper.delete(fileUrl);
        imagepostMapper.deleteByImageurl(fileUrl);
    }

    @Override
    public UserImage get(String id,String userid){
        return imageMapper.get(id,userid);
    }

}
