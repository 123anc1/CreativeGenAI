package cn.cyf.chatclient.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class KnowledgeBuilder {

    public static List<Document> parse(MultipartFile file, int chunkSize, int overlap) throws IOException {
        String filename = file.getOriginalFilename();

        List<Document> rawDocs;

        if (filename != null && filename.endsWith(".xlsx")) {
            rawDocs = QaExcelUtils.read(file.getInputStream())
                    .stream()
                    .map(p -> new Document("Q: " + p.getQuestion() + "\nA: " + p.getAnswer()))
                    .collect(Collectors.toList());
//        } else if (filename != null && filename.endsWith(".txt")) {
//            rawDocs = TxtQaParser.parse(new String(file.getBytes(), StandardCharsets.UTF_8))
//                    .stream()
//                    .map(p -> new Document("Q: " + p.getQuestion() + "\nA: " + p.getAnswer()))
//                    .collect(Collectors.toList());
        } else {
            // 修复：标准化 Tika 的输出
            TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
            rawDocs = reader.get();
        }

        // 标准化处理
        List<Document> standardizedDocs = rawDocs.stream()
                .map(doc -> {
                    // 确保内容在 content 字段
                    String content = doc.getText();
                    if (content == null || content.trim().isEmpty()) {
                        content = (String) doc.getMetadata().get("text");
                    }

                    // 创建新的 Document，只保留必要的元数据
                    Map<String, Object> cleanMetadata = new HashMap<>();
                    if (filename != null) {
                        cleanMetadata.put("filename", filename);
                    }
                    // 可以添加其他需要的元数据

                    return new Document(content != null ? content : "", cleanMetadata);
                })
                .collect(Collectors.toList());

        // 切块
        TokenTextSplitter splitter = new TokenTextSplitter(chunkSize, overlap, 5, 10000, true);
        return splitter.apply(standardizedDocs);
    }
}