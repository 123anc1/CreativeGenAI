package cn.cyf.chatclient.modules.multimodal.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessingService {
    String processImage(String imageUrl) throws Exception;
    String processImage(MultipartFile imageFile) throws Exception;
    String processImageFromBase64(String base64Image) throws Exception;
}