package cn.cyf.chatclient.modules.image.controller;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;
import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.common.utils.UrlToMultipartFileConverter;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.model.ImageResponseVo;
import cn.cyf.chatclient.modules.image.model.UserImage;
import cn.cyf.chatclient.modules.image.service.ImageService;
import cn.cyf.chatclient.modules.image.service.impl.ImageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageModelController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImageCacheService imageCacheService;
    /**
     * 显示图像
     */
    @PostMapping("/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "查询用户图像列表", module = "图像管理")
    public List<UserImage> list(@RequestParam Integer userid) throws Exception {
//        log.info("图片请求用户id：{}", userid);
//        log.info("图片数据：{}", imageService.list(String.valueOf(userid)));
        return imageService.list(String.valueOf(userid));
    }
    /**
     * 根据id显示图像
     */
    @GetMapping("/get/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取图像详情", module = "图像管理")
    public UserImage get(@PathVariable Integer id,
                         @RequestParam Integer userid) {
//        log.info("图片请求id：{}", id);
//        log.info("图片数据：{}", imageService.get(String.valueOf(id), String.valueOf(userid)));
        return imageService.get(String.valueOf(id), String.valueOf(userid));
    }

    /**
     * zhipu文生图生成
     */
    @PostMapping("/generate")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "生成AI图像", module = "图像生成", logParams = true)
    public String generateImage(@RequestPart("file") MultipartFile file) {
        String result = imageService.caption(file);
        try {
            ZhipuAiClient client = ZhipuAiClient.builder().ofZHIPU().apiKey("ad197af1d00f4175ba32377f594f6905.6C32duiyW2zEX0nr").build();

            CreateImageRequest request = CreateImageRequest.builder()
                    .model("glm-image")
                    .prompt(result)
                    .size("1024x1024")
                    .build();

            ImageResponse response = client.images().createImage(request);

            if (response != null && response.getData() != null) {
                return response.getData().toString(); // 根据实际响应结构调整
            } else {
                return "图像生成失败";
            }
        } catch (Exception e) {
            return "错误: " + e.getMessage();
        }
    }

    /**
     * 同步图像生成
     */
    @PostMapping("/text_to_image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "文本转图像", module = "图像生成", logParams = true, logResult = true)
    public String text_to_image(    @RequestParam String userid,
                                    @RequestParam String prompt,
                                    @RequestParam String negativePrompt,
                                    @RequestParam(required = false) String model_name,
                                    @RequestParam(required = false) String steps,
                                    @RequestParam(required = false) String cfg_scale,
                                    @RequestParam(required = false) String style,
                                    @RequestParam(required = false) String image_url,
                                    @RequestParam(required = false) String width,
                                    @RequestParam(required = false) String height,
                                    @RequestPart(value = "image_file", required = false) MultipartFile file) {
        ImageModel imageModel = new ImageModel(null,userid, model_name, prompt, negativePrompt, Integer.valueOf(steps),
                Integer.valueOf(cfg_scale),file,style,image_url,Integer.valueOf(width), Integer.valueOf(height));
        log.info("用户输入提示词：{}", imageModel.getPrompt());
        // 如果没有上传文件但有URL，则从URL转换
        if (image_url != null && !image_url.isEmpty()) {
            try {
                MultipartFile convertedFile = UrlToMultipartFileConverter.convertUrlToMultipartFile(image_url);
                imageModel.setImage_file(convertedFile);
            } catch (Exception e) {
                log.error("URL转MultipartFile失败: {}", e.getMessage());
            }
        }
//        log.info("文件参数：{}", file);
        log.info("图像参数：{}", imageModel);
//        log.info("图像描述：{}", prompt);
        ImageResponseVo imageResponseVo = imageService.text_to_image(imageModel);
        imageService.save(imageResponseVo.getImage_url(), imageModel,prompt);
        imageResponseVo.setPrompt(prompt);
        if (imageResponseVo.getImage_url()!=null&&imageResponseVo.getImage_url().startsWith("http")){
            return imageResponseVo.getImage_url();
        }
        try {
            String signedUrl = aliyunOSSOperator.generateSignedUrl(imageResponseVo.getImage_url(), 3600*24);
            imageCacheService.cacheSignedUrl(imageResponseVo.getImage_url(), signedUrl, 3600*24);
            imageResponseVo.setImage_url(signedUrl);
        } catch (Exception e) {
            log.error("生成图片签名URL失败: {}", e.getMessage());
        }
        return imageResponseVo.getImage_url();
    }

    /**
     * 异步图像生成
     */
    @PostMapping("/async_text_to_image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "异步文本转图像", module = "图像生成", logParams = true)
    public Result asyncTextToImage(@RequestParam String userid,
                                   @RequestParam String prompt,
                                   @RequestParam String negativePrompt,
                                   @RequestParam(required = false) String model_name,
                                   @RequestParam(required = false) String steps,
                                   @RequestParam(required = false) String cfg_scale,
                                   @RequestParam(required = false) String style,
                                   @RequestParam(required = false) String image_url,
                                   @RequestParam(required = false) String width,
                                   @RequestParam(required = false) String height,
                                   @RequestPart(value = "image_file", required = false) MultipartFile file) {
        ImageModel imageModel = new ImageModel(null, userid, model_name, prompt, negativePrompt, 
                Integer.valueOf(steps != null ? steps : "30"),
                Integer.valueOf(cfg_scale != null ? cfg_scale : "7"), file, style, image_url, 
                Integer.valueOf(width != null ? width : "512"), 
                Integer.valueOf(height != null ? height : "512"));
        
        log.info("用户发起异步文生图请求，用户ID: {}, 提示词: {}", userid, prompt);
        
        // 如果没有上传文件但有URL，则从URL转换
        if (image_url != null && !image_url.isEmpty()) {
            try {
                MultipartFile convertedFile = UrlToMultipartFileConverter.convertUrlToMultipartFile(image_url);
                imageModel.setImage_file(convertedFile);
            } catch (Exception e) {
                log.error("URL转MultipartFile失败: {}", e.getMessage());
            }
        }
        
        try {
            String taskId = imageService.asyncTextToImage(imageModel);
            return Result.success(taskId);
        } catch (Exception e) {
            log.error("异步文生图启动失败", e);
            return Result.error("异步文生图启动失败: " + e.getMessage());
        }
    }

    /**
     * 图像描述
     */
    @PostMapping(value = "/caption", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "图像描述生成", module = "图像处理", logParams = true)
    public String caption(@RequestBody ImageModel imageModel) {
        //        log.info("图像描述：{}", result);
        return imageService.caption(imageModel.getImage_file());
    }

    /**
     * 获取异步任务状态
     */
    @GetMapping("/task_status/{taskId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result getTaskStatus(@PathVariable String taskId) {
        try {
            ImageTask task = imageService.getTaskStatus(taskId);
            if (task != null) {
                return Result.success(task);
            } else {
                return Result.error("任务不存在");
            }
        } catch (Exception e) {
            log.error("查询任务状态失败，任务ID: {}", taskId, e);
            return Result.error("查询任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 取消异步任务
     */
    @PostMapping("/cancel_task/{taskId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "取消图像生成任务", module = "图像生成", level = LogOperation.LogLevel.WARN)
    public Result cancelTask(@PathVariable String taskId) {
        try {
            imageService.cancelTask(taskId);
            return Result.success("任务取消成功");
        } catch (Exception e) {
            log.error("取消任务失败，任务ID: {}", taskId, e);
            return Result.error("取消任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有任务列表
     */
    @GetMapping("/tasks")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "获取图像生成任务列表", module = "图像生成")
    public Result getAllTasks() {
        // 这里可以根据需要添加分页或筛选参数
        try {
            return Result.success("任务列表功能待实现");
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除图像
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除图像", module = "图像管理", level = LogOperation.LogLevel.WARN)
    public void delete(@PathVariable String id) {
        imageService.delete(id);
    }
}
