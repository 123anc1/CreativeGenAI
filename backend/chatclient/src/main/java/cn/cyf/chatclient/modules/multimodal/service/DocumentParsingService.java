package cn.cyf.chatclient.modules.multimodal.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentParsingService {
    String parseDocument(MultipartFile file) throws Exception;
}