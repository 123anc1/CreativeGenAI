package cn.cyf.chatclient.modules.canvas.service.impl;

import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.canvas.mapper.CanvasMapper;
import cn.cyf.chatclient.modules.canvas.model.CanvasImage;
import cn.cyf.chatclient.modules.canvas.model.CanvasSessionImage;
import cn.cyf.chatclient.modules.canvas.model.SessionInfo;
import cn.cyf.chatclient.modules.canvas.service.CanvasService;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CanvasServiceImpl implements CanvasService {
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    @Autowired
    private CanvasMapper canvasMapper;

    @Autowired
    private ImageCacheService imageCacheService;
    /**图片编辑
     *@return CanvasImage实体类
     */
    public Result inpainting(CanvasImage canvasImage) {

        List<String> imageUrl = canvasImage.getImageUrl();
        String prompt = canvasImage.getPrompt();
        String sessionId = canvasImage.getSessionId();
        //图片组合、迁移、替换、衍生
        List<String> returnUrl = new ArrayList<>();
        List<String> GetUrls= cvProcess(imageUrl,prompt,sessionId);
        if (GetUrls != null) {
            for (String GetUrl : GetUrls){
                returnUrl.add(getUrl(GetUrl));
            }
        }
        return Result.success(returnUrl);
    }

    /**
     * 画布会话列表
     */
    public List<SessionInfo> sessionList(String userId) {
        return canvasMapper.sessionList(userId);
    }

    /**
     * 会话图片列表
     */
    public List<CanvasSessionImage> sessionListImage(String sessionId){
        List<CanvasSessionImage> ImageList =  canvasMapper.getSessionImage(sessionId);
        for(CanvasSessionImage imageUrl : ImageList){
            imageUrl.setImageUrl(getUrl(imageUrl.getImageUrl()));
        }
        return ImageList;
    }

    /**
     *创建会话
     */
    public SessionInfo sessionCreate(SessionInfo sessionInfo) throws Exception {
        List<String> backUrl = new ArrayList<>();
        String sessionId = UUID.randomUUID().toString();
        sessionInfo.setSessionId(sessionId);
        sessionInfo.setCreatedAt(LocalDate.now());
        sessionInfo.setUpdatedAt(LocalDate.now());
        if (sessionInfo.getText() == null) {
            sessionInfo.setText("未命名项目");
        }
        canvasMapper.sessionCreate(sessionInfo);
        if (sessionInfo.getImageBase64() != null) {
            for (String imageBase64 : sessionInfo.getImageBase64()) {
                byte[] imageByte = Base64.getDecoder().decode(imageBase64);
                String imageUrl = aliyunOSSOperator.upload(imageByte, ".png");
                String imageId = UUID.randomUUID().toString();
                canvasMapper.saveImage(imageId, null, sessionInfo.getSessionId(), imageUrl, sessionInfo.getCreatedAt(), sessionInfo.getUpdatedAt());
                backUrl.add(getUrl(imageUrl));
            }
        }
        if (sessionInfo.getImageUrl() != null){
            for(String imageUrl : sessionInfo.getImageUrl()){
                //字符分割
                String path = imageUrl.substring(imageUrl.indexOf(".com/")+5, imageUrl.indexOf("?"));
                String imageId = UUID.randomUUID().toString();
                Integer userImage = canvasMapper.check(path);
                canvasMapper.saveImage(imageId, userImage, sessionInfo.getSessionId(), path, sessionInfo.getCreatedAt(), sessionInfo.getUpdatedAt());
                backUrl.add(imageUrl);
            }
        }
        sessionInfo.setImageBackUrl(backUrl);
        return sessionInfo;
    }

    /**
     *修改图片标题
     */
    public void sessionUpdate(String sessionId, String text){
        canvasMapper.sessionUpdate(sessionId, text);
    }

    /**
     *删除画布会话
     */
    public void sessionDelete(String id) throws Exception {
        List<String> imageUrls = canvasMapper.checkImageDelete(id);
        for (String imageUrl : imageUrls) {
            aliyunOSSOperator.delete(imageUrl);
            imageCacheService.removeCachedSignedUrl(imageUrl);
        }
        canvasMapper.sessionDelete(id);
    }

    /**
     *删除会话图像信息
     */
    public void sessionImageDelete(String id) throws Exception {
        CanvasSessionImage canvasSessionImage =  canvasMapper.chackSessionImage(id);
        log.info("canvasSessionImage:{}",canvasSessionImage);
        if(canvasSessionImage.getImageId() == null){
            log.info("删除会话中图片oss");
            aliyunOSSOperator.delete(canvasSessionImage.getImageUrl());
            imageCacheService.removeCachedSignedUrl(canvasSessionImage.getImageUrl());
        }
        canvasMapper.sessionImageDelete(id);
    }

    /**
     *保存图像信息
     */
    public List<String> saveImage(SessionInfo sessionInfo) throws Exception {
        List<String> backUrl = new ArrayList<>();
        sessionInfo.setCreatedAt(LocalDate.now());
        sessionInfo.setUpdatedAt(LocalDate.now());
        if (sessionInfo.getText() == null) {
            sessionInfo.setText("未命名项目");
        }
        if (sessionInfo.getImageBase64() != null) {
            for (String imageBase64 : sessionInfo.getImageBase64()) {
                String pureBase64 = imageBase64;
                if (imageBase64.contains(",")) {
                    pureBase64 = imageBase64.substring(imageBase64.indexOf(",") + 1);
                }
                byte[] imageByte = Base64.getDecoder().decode(pureBase64.trim());
                String imageUrl = aliyunOSSOperator.upload(imageByte, ".png");
                String imageId = UUID.randomUUID().toString();
                canvasMapper.saveImage(imageId, null, sessionInfo.getSessionId(), imageUrl, sessionInfo.getCreatedAt(), sessionInfo.getUpdatedAt());
                backUrl.add(getUrl(imageUrl));
            }
        }
        if (sessionInfo.getImageUrl() != null){
            for(String imageUrl : sessionInfo.getImageUrl()){
                //字符分割
                String path = imageUrl.substring(imageUrl.indexOf(".com/") + 5, imageUrl.indexOf("?"));
                String imageId = UUID.randomUUID().toString();
                Integer userImage = canvasMapper.check(path);
                canvasMapper.saveImage(imageId, userImage,sessionInfo.getSessionId(), path, sessionInfo.getCreatedAt(), sessionInfo.getUpdatedAt());
                backUrl.add(imageUrl);
            }
        }
        return backUrl;
    }

    /**
     * 异步图片处理
     */
    public Result asyncInpainting(CanvasImage canvasImage) throws IOException {
        List<String> imageUrl = canvasImage.getImageUrl();
        List<String> imageMaskBase64 = canvasImage.getImageMaskBase64();
        String prompt = canvasImage.getPrompt();
        String sessionId = canvasImage.getSessionId();

        if(imageUrl == null||prompt == null){
            return Result.error("参数错误");
        }
        String taskId;
        // 1. 提交任务
        if(imageMaskBase64 != null&& !imageMaskBase64.isEmpty()){
            log.info("开始处理图片局部重绘");
            List<String> PutImage = new ArrayList<>();
            List<String> BaseImage = new ArrayList<>();
            for(String image : imageUrl){
                URL url = new URL(image);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                byte[] imageBytes = inputStream.readAllBytes();
                inputStream.close();
                connection.disconnect();

                // 2. 将图片转换为 Base64 编码
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                BaseImage.add(base64Image);
            }
            for(String image : imageMaskBase64){
                // 去除可能存在的前缀和首尾空白
                if (image.contains(",")) {
                    PutImage.add(image.substring(image.indexOf(",") + 1).trim());
                } else {
                    PutImage.add(image.trim());
                }
            }
//            log.info("url:{}",BaseImage);
            taskId = CVSync2AsyncSubmitTask1(BaseImage, PutImage, prompt);
        }
        else{
            taskId = CVSync2AsyncSubmitTask(imageUrl, prompt);
        }

        if (taskId == null) {
            return Result.error("提交任务失败");
        }
        
        // 2. 轮询获取结果，直到获取到结果为止
        List<String> returnUrl = new ArrayList<>();
        int retryInterval = 2000; // 每2秒轮询一次
        
        while (true) {
            try {
                Thread.sleep(retryInterval);
                List<String> GetUrls = new ArrayList<>();
                if (imageMaskBase64 == null||imageMaskBase64.isEmpty()){
                    GetUrls = CVSync2AsyncGetResult(taskId, sessionId,"jimeng_t2i_v40");
                    log.info("GetUrls:{}",GetUrls);
                }
                else {
                    GetUrls = CVSync2AsyncGetResult(taskId, sessionId,"jimeng_image2image_dream_inpaint");
                }
                if (GetUrls != null && !GetUrls.isEmpty()) {
                    for (String GetUrl : GetUrls) {
                        returnUrl.add(GetUrl);
                        log.info("returnUrl:{}",returnUrl);
                    }
                    return Result.success(returnUrl);
                }
                // 如果GetUrls为null，继续轮询
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Result.error("处理中断");
            }
        }
    }


    private String getUrl(String ossUrl) {
        try {
            //获取图片URL
            String cachedUrl = imageCacheService.getCachedSignedUrl(ossUrl);
            if (cachedUrl != null) {
                return cachedUrl;
            } else {
                String signedUrl = aliyunOSSOperator.generateSignedUrl(ossUrl, 3600 * 24);
                imageCacheService.cacheSignedUrl(ossUrl, signedUrl, 3600 * 24);
                return signedUrl;
            }
        } catch (Exception e) {
            return "";
        }
    }

    //图片组合、迁移、替换、衍生
    //同步请求
    private List<String> cvProcess(List<String> PutImage,String prompt,String sessionId){
        IVisualService visualService = VisualServiceImpl.getInstance();
        String accessKey = System.getenv("VOLC_ACCESS_KEY");
        String secretKey = System.getenv("VOLC_SECRET_KEY");
        List<String> returnList = new ArrayList<>();
        // 注意：应该使用 setAccessKey 和 setSecretKey
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);

        JSONObject req = new JSONObject();
        req.put("req_key", "jimeng_t2i_v40");
        req.put("prompt", prompt);
        req.put("image_urls",PutImage);
        req.put("return_url", true);
        try {
            Object response = visualService.cvProcess(req);
            String jsonString = JSON.toJSONString(response);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");
            List<String> imageUrls = data.getJSONArray("image_urls").toJavaList(String.class);
            log.info("图片数据：{}",imageUrls);
            for (String imageUrl : imageUrls) {
                try {
                    // 1. 下载图片获取二进制数据
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    byte[] imageBytes = inputStream.readAllBytes();
                    inputStream.close();
                    connection.disconnect();

                    // 2. 上传到OSS并获取访问URL
                    String fileName = "jimeng_image_" + System.currentTimeMillis() + ".png";
                    String ossUrl = aliyunOSSOperator.upload(imageBytes, fileName);
                    String imageId = UUID.randomUUID().toString();
                    LocalDate CreatedAt = LocalDate.now();
                    LocalDate UpdatedAt = LocalDate.now();
                    canvasMapper.saveImage(imageId, null, sessionId, ossUrl, CreatedAt, UpdatedAt);

                    returnList.add(ossUrl);
                } catch (Exception downloadException) {
                    System.err.println("处理图片失败: " + imageUrl + ", 错误: " + downloadException.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnList;
    }

    //同步转异步请求 - 提交任务
    private String CVSync2AsyncSubmitTask(List<String> PutImage,String prompt){
        IVisualService visualService = VisualServiceImpl.getInstance();
        String accessKey = System.getenv("VOLC_ACCESS_KEY");
        String secretKey = System.getenv("VOLC_SECRET_KEY");
        // 注意：应该使用 setAccessKey 和 setSecretKey
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);

        JSONObject req = new JSONObject();
        req.put("req_key", "jimeng_t2i_v40");
        req.put("prompt", prompt);
        req.put("image_urls",PutImage);
        req.put("return_url", true);
        try {
            Object response = visualService.cvSync2AsyncSubmitTask(req);
            log.info("任务提交成功：{}",response);
            String jsonString = JSON.toJSONString(response);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");
            String taskId = data.getString("task_id");
            log.info("任务ID：{}",taskId);
            return taskId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //同步转异步,局部重绘，提交任务
    private String CVSync2AsyncSubmitTask1(List<String> imageUrl, List<String> imageMaskBase64, String prompt) {
        IVisualService visualService = VisualServiceImpl.getInstance();
        String accessKey = System.getenv("VOLC_ACCESS_KEY");
        String secretKey = System.getenv("VOLC_SECRET_KEY");
        // 注意：应该使用 setAccessKey 和 setSecretKey
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
        List<String> binaryDataBase64 = new ArrayList<>();

        binaryDataBase64.addAll(imageUrl);
        binaryDataBase64.addAll(imageMaskBase64);
        log.info("图片数据：{}",binaryDataBase64);

        JSONObject req = new JSONObject();
        req.put("req_key", "jimeng_image2image_dream_inpaint");
        req.put("prompt", prompt);
        req.put("binary_data_base64",binaryDataBase64);
        req.put("return_url", true);
        try {
            Object response = visualService.cvSync2AsyncSubmitTask(req);
            log.info("任务提交成功：{}",response);
            String jsonString = JSON.toJSONString(response);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");
            String taskId = data.getString("task_id");
            log.info("任务ID：{}",taskId);
            return taskId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //同步转异步获取结果，查询结果
    private List<String> CVSync2AsyncGetResult(String taskId, String sessionId,String key){
        IVisualService visualService = VisualServiceImpl.getInstance();
        String accessKey = System.getenv("VOLC_ACCESS_KEY");
        String secretKey = System.getenv("VOLC_SECRET_KEY");
        List<String> returnList = new ArrayList<>();
        // 注意：应该使用 setAccessKey 和 setSecretKey
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
        JSONObject req = new JSONObject();
        req.put("req_key", key);
        req.put("task_id", taskId);
        req.put("req_json", "{\"return_url\":true}");
        try {
            Object response = visualService.cvSync2AsyncGetResult(req);
            String jsonString = JSON.toJSONString(response);
            JSONObject jsonObject = JSON.parseObject(jsonString);

            log.info("任务结果：{}",response);
            // 检查响应状态
            Integer code = jsonObject.getInteger("code");
            if (code != null && code != 10000) {
                String message = jsonObject.getString("message");
                log.error("获取任务结果失败: code={}, message={}", code, message);
                return null;
            }
            
            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                log.error("获取任务结果失败: data为null");
                return null;
            }
            
            JSONArray imageUrlsArray = data.getJSONArray("image_urls");
            if (imageUrlsArray == null || imageUrlsArray.isEmpty()) {
                return null;
            }
            
            List<String> imageUrls = imageUrlsArray.toJavaList(String.class);
            log.info("图片数据：{}",imageUrls);
            for (String imageUrl : imageUrls) {
                try {
                    // 1. 下载图片获取二进制数据
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    byte[] imageBytes = inputStream.readAllBytes();
                    inputStream.close();
                    connection.disconnect();

                    // 2. 上传到OSS并获取访问URL
                    String fileName = "jimeng_image_" + System.currentTimeMillis() + ".png";
                    String ossUrl = aliyunOSSOperator.upload(imageBytes, fileName);
                    log.info("图片上传成功，访问URL：{}", ossUrl);
                    String imageId = UUID.randomUUID().toString();
                    LocalDate CreatedAt = LocalDate.now();
                    LocalDate UpdatedAt = LocalDate.now();
                    canvasMapper.saveImage(imageId, null, sessionId, ossUrl, CreatedAt, UpdatedAt);

                    String getUrl = getUrl(ossUrl);
                    returnList.add(getUrl);
                } catch (Exception downloadException) {
                    System.err.println("处理图片失败: " + imageUrl + ", 错误: " + downloadException.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        log.info("图片处理完成：{}",returnList);
        return returnList;
    }
}