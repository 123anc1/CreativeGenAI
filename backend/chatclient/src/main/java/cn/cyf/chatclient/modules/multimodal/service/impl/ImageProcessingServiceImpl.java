package cn.cyf.chatclient.modules.multimodal.service.impl;

import cn.cyf.chatclient.modules.multimodal.service.ImageProcessingService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {

    @Autowired
    @Qualifier("DeepseekchatClientWithoutMemory")
    private ChatClient chatClient;

    @Override
    public String processImage(String imageUrl) throws Exception {
        String prompt = "请描述这张图片的内容：" + imageUrl;
        return chatClient.prompt(new Prompt(new UserMessage(prompt)))
                .call().content();
    }

    @Override
    public String processImage(MultipartFile imageFile) throws Exception {
        byte[] bytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(bytes);
        return processImageFromBase64(base64Image);
    }

    @Override
    public String processImageFromBase64(String base64Image) throws Exception {
        String prompt = "请描述这张图片的内容：data:image/jpeg;base64," + base64Image;
        return chatClient.prompt(new Prompt(new UserMessage(prompt)))
                .call().content();
    }
}