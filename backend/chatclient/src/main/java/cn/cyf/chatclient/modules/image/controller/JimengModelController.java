package cn.cyf.chatclient.modules.image.controller;


import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/jimengModel")
public class JimengModelController {
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImageService imageService;
    @PostMapping("/jimengModel")
    public Result jimengModel(String prompt){
        IVisualService visualService = VisualServiceImpl.getInstance();
        String accessKey = System.getenv("VOLC_ACCESS_KEY");
        String secretKey = System.getenv("VOLC_SECRET_KEY");

        // 注意：应该使用 setAccessKey 和 setSecretKey
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey); // 修正为 SecretKey
        if (accessKey == null || secretKey == null) {
            return Result.error("环境变量未配置VOLC_ACCESS_KEY或VOLC_SECRET_KEY");
        }

        JSONObject req = new JSONObject();
        req.put("req_key", "jimeng_t2i_v40");
        req.put("prompt", prompt);
        req.put("width",1440);
        req.put("height",2560);
        req.put("return_url", true); // 添加这个参数以返回URL而不是base64

        try {
            Object response = visualService.cvProcess(req);
            String jsonString = JSON.toJSONString(response);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");
            List<String> imageUrls = data.getJSONArray("image_urls").toJavaList(String.class);
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

                    // 保存图片信息
                    ImageModel imageModel = new ImageModel();
                    imageModel.setUserid("1");
                    imageModel.setModel_name("jimeng_t2i_v40");
                    imageModel.setPrompt(prompt);
                    imageService.save(ossUrl, imageModel, prompt);
                    return Result.success(ossUrl);
                } catch (Exception downloadException) {
                    System.err.println("处理图片失败: " + imageUrl + ", 错误: " + downloadException.getMessage());
                }
            }
            return Result.success(JSON.toJSONString(response));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("调用失败: " + e.getMessage());
        }
    }
}
