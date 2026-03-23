package cn.cyf.chatclient.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlToMultipartFileConverter {

    public static MultipartFile convertUrlToMultipartFile(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();

            // 获取文件名（从URL中提取）
            String fileName = extractFileNameFromUrl(imageUrl);

            // 获取Content-Type
            String contentType = connection.getContentType();

            // 创建MockMultipartFile
            return new MockMultipartFile(
                    fileName,
                    fileName,
                    contentType,
                    bytes
            );
        }
    }

    private static String extractFileNameFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        if (fileName.contains("?")) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        if (!fileName.contains(".")) {
            fileName += ".jpg"; // 默认扩展名
        }
        return fileName;
    }
}
