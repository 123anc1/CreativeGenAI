package cn.cyf.chatclient.modules.multimodal.service.impl;

import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import cn.cyf.chatclient.modules.image.model.ImageModel;
import cn.cyf.chatclient.modules.image.service.ImageService;
import cn.cyf.chatclient.modules.knowledge.service.SimilaritySearchService;
import cn.cyf.chatclient.modules.multimodal.mapper.MultiModalMapper;
import cn.cyf.chatclient.modules.multimodal.model.MultiModalInput;
import cn.cyf.chatclient.modules.multimodal.model.MultiModalResult;
import cn.cyf.chatclient.modules.multimodal.service.DocumentParsingService;
import cn.cyf.chatclient.modules.multimodal.service.ImageProcessingService;
import cn.cyf.chatclient.modules.multimodal.service.IntentRecognitionService;
import cn.cyf.chatclient.modules.multimodal.service.MultiModalResultService;
import cn.cyf.chatclient.modules.multimodal.service.MultiModalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
@Slf4j
public class MultiModalServiceImpl implements MultiModalService {

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Autowired
    private DocumentParsingService documentParsingService;

    @Autowired
    private ImageProcessingService imageProcessingService;

    @Autowired
    @Qualifier("DeepseekchatClientWithoutMemory")
    private ChatClient chatClientWithoutMemory;

    @Autowired
    @Qualifier("DeepseekchatClientWithMemory")
    private ChatClient chatClient;

    @Autowired
    private SimilaritySearchService similaritySearchService;

    @Autowired
    private MultiModalMapper multiModalMapper;

    @Autowired
    private MultiModalResultService multiModalResultService;
    @Override
    public Flux<String> processInputStream(MultiModalInput input) throws Exception {
        // 1. 输入解析
        String parsedInput = parseInput(input);

        // 2. 意图识别
        String intent = intentRecognitionService.recognizeIntent(parsedInput);
        log.info("意图：{}", intent);
        // 3. 能力调度
        switch (intent) {
            // 文本转图片
            case "text2image":
                return processText2ImageStream(parsedInput,input);
            // 问答
            case "qa":
                return processQAStream(parsedInput, input);
            // 文档分析
            case "doc_analysis":
                return processDocAnalysisStream(input, parsedInput);
            // 知识检索
            case "kb_retrieval":
                return processKbRetrievalStream(input, parsedInput);
            default:
                return processQAStream(parsedInput, input);
        }
    }

    private Flux<String> processText2ImageStream(String query,MultiModalInput multiModalInput) {
        // 生成图片提示词
        String prompt = "请为以下描述生成一个详细的图片提示词，用户输入什么语言为主，回答就按照什么语言，用于AI图像生成。请直接输出提示词内容，不要添加任何解释或说明：" + query;
        Prompt promptObj = new Prompt(new UserMessage(prompt));

        // 创建一个容器来收集完整的提示词
        AtomicReference<StringBuilder> fullPromptBuilder = new AtomicReference<>(new StringBuilder());

        // 实时输出提示词，并收集完整提示词
        Flux<String> promptStream = chatClient.prompt(promptObj)
                .user(query)
                .advisors((a -> a.param(CONVERSATION_ID,
                        String.valueOf(multiModalInput.getUid()) + ":" + multiModalInput.getSessionid())))
                .stream()
                .content()
                .doOnNext(part -> {
                    fullPromptBuilder.get().append(part);
                });

        return promptStream.concatWith(Flux.defer(() -> {
            // 获取完整的提示词
            String fullPrompt = fullPromptBuilder.get().toString();

            Integer imageId = multiModalMapper.getMemoryId(fullPrompt);
            // 清理提示词
            fullPrompt = fullPrompt.replaceAll("^.*提示词建议：", "").trim();
            fullPrompt = fullPrompt.replaceAll("^[\"']|[\"']$", "").trim();

            String ossUrl = getJimengImage_4_1(fullPrompt, multiModalInput);
            String imageUrl;
            log.info("提示词：{}", ossUrl);
            try {
                //获取图片URL
                String cachedUrl = imageCacheService.getCachedSignedUrl(ossUrl);
                if (cachedUrl != null) {
                    imageUrl = cachedUrl;
                    log.info("使用缓存的图片URL：{}", cachedUrl);
                }
                else
                {
                String signedUrl = aliyunOSSOperator.generateSignedUrl(ossUrl, 3600*24);
                imageCacheService.cacheSignedUrl(ossUrl, signedUrl , 3600*24);
                imageUrl = signedUrl;
                }
            } catch (Exception e) {
                imageUrl = "";
            }
            // 保存多模态结果
            MultiModalResult result = new MultiModalResult();
            result.setUid(multiModalInput.getUid());
            result.setSessionid(multiModalInput.getSessionid());
            result.setInputType("text");
            result.setInputContent(query);
            result.setIntent("text2image");
            result.setOutputType("image");
            result.setOutputContent("提示词：" + fullPrompt + "\n图片 URL: " + ossUrl);
            result.setOutputUrl(ossUrl);
            result.setPrompt(fullPrompt);
            result.setModelName("jimeng_t2i_v40");
            result.setCreatedAt(java.time.LocalDateTime.now());
            multiModalResultService.saveResult(result);

            return Flux.just("[图片URL] " + imageUrl);
        }));
        
    }

    private Flux<String> processQAStream(String input, MultiModalInput multiModalInput) {
        Prompt prompt = new Prompt(new UserMessage(input));

        // 创建容器收集完整输出
        AtomicReference<StringBuilder> fullOutputBuilder = new AtomicReference<>(new StringBuilder());

        return chatClient.prompt(prompt)
                .user(input)
                .advisors((a -> a.param(CONVERSATION_ID,
                        String.valueOf(multiModalInput.getUid()) + ":" + multiModalInput.getSessionid())))
                .stream()
                .content()
                .doOnNext(part -> {
                    // 实时收集输出部分
                    fullOutputBuilder.get().append(part);
                })
                .doFinally(signal -> {
                    // 当流式输出完成后保存结果
                    String fullOutput = fullOutputBuilder.get().toString();
                    MultiModalResult result = new MultiModalResult();
                    result.setUid(multiModalInput.getUid());
                    result.setSessionid(multiModalInput.getSessionid());
                    result.setInputType("text");
                    result.setInputContent(input);
                    result.setIntent("qa");
                    result.setOutputType("text");
                    result.setOutputContent(fullOutput);
                    result.setModelName("Deepseek");
                    result.setCreatedAt(java.time.LocalDateTime.now());
                    multiModalResultService.saveResult(result);
                });
    }

    private Flux<String> processDocAnalysisStream(MultiModalInput input, String parsedInput) throws Exception {
        // 1. 文档解析与内容提取
        StringBuilder fullDocText = new StringBuilder();
        List<String> fileNames = new ArrayList<>();
        
        if (input.getFiles() != null && !input.getFiles().isEmpty()) {
            for (var file : input.getFiles()) {
                try {
                    // 使用DocumentParsingService进行文档解析
                    String docContent = documentParsingService.parseDocument(file);
                    fullDocText.append(docContent).append("\n");
                    fileNames.add(file.getOriginalFilename());
                    log.info("成功解析文档：{}, 内容长度：{}", file.getOriginalFilename(), docContent.length());
                } catch (Exception e) {
                    log.error("文档解析失败：{}", file.getOriginalFilename(), e);
                    return Flux.just("[错误] 文档解析失败：" + e.getMessage());
                }
            }
        }
        
        // 2. 构建文档摘要（用于输出显示）
        String docSummary;
        if (fullDocText.length() > 500) {
            docSummary = "[文档内容摘要] " + fullDocText.substring(0, 500) + "...\n共 " + fileNames.size() + " 个文件，总字符数：" + fullDocText.length();
        } else if (!fullDocText.isEmpty()){
            docSummary = "[文档内容] " + fullDocText.toString();
        }else {
            docSummary = "[文档内容]" + similaritySearchService.getSimilar(parsedInput, String.valueOf(input.getUid()), input.getSessionid());
        }
        
        // 3. 构建分析提示词 - 结合文档内容和用户问题
        // 只使用input.getText()作为用户问题，不包含文档内容
        String userQuestion = input.getText() != null ? input.getText() : "";
        String prompt = "请分析以下文档内容，并回答用户的问题：\n\n" +
                       "【文档内容】\n" + similaritySearchService.getSimilar(parsedInput, String.valueOf(input.getUid()), input.getSessionid()) +
                       "\n\n【用户问题】\n" + userQuestion +
                       "\n\n请基于上述文档内容，详细回答用户的问题。如果文档中没有相关信息，请明确说明。";
        
        Prompt promptObj = new Prompt(new UserMessage(prompt));
        
        // 4. 创建容器收集完整输出
        AtomicReference<StringBuilder> fullOutputBuilder = new AtomicReference<>(new StringBuilder());
        
        // 5. 先输出文档信息，然后流式输出分析结果
        return Flux.just(docSummary)
                .concatWith(chatClient.prompt(promptObj)
                        .user(userQuestion)
                        .advisors((a -> a.param(CONVERSATION_ID,
                                String.valueOf(input.getUid()) + ":" + input.getSessionid())))
                        .stream()
                        .content()
                        .doOnNext(part -> {
                            // 实时收集输出部分
                            fullOutputBuilder.get().append(part);
                        })
                        .doFinally(signal -> {
                            // 当流式输出完成后保存结果
                            String fullOutput = fullOutputBuilder.get().toString();
                            
                            // 构建输入内容描述（只保存用户问题，不保存文档内容）
                            String inputDesc = userQuestion;
                            
                            MultiModalResult result = new MultiModalResult();
                            result.setUid(input.getUid());
                            result.setSessionid(input.getSessionid());
                            result.setInputType("document");
                            result.setInputContent(inputDesc);
                            result.setIntent("doc_analysis");
                            result.setOutputType("text");
                            result.setOutputContent(docSummary + "\n\n[分析结果]\n" + fullOutput);
                            result.setPrompt(userQuestion);
                            result.setModelName("Deepseek");
                            result.setCreatedAt(java.time.LocalDateTime.now());
                            multiModalResultService.saveResult(result);
                            
                            log.info("文档分析完成，uid: {}, sessionid: {}, 文件数：{}", 
                                    input.getUid(), input.getSessionid(), fileNames.size());
                        }));
    }

    private Flux<String> processKbRetrievalStream(MultiModalInput input, String parsedInput) {
        // 检索知识库
        List<Document> relevantDocs = similaritySearchService.getSimilar(
                parsedInput,
                String.valueOf(input.getUid()),
                input.getSessionid()
        );

        // 构建RAG提示词
        String ragPrompt = similaritySearchService.buildRagPrompt(parsedInput, relevantDocs);

        // 先输出知识库检索结果摘要，然后流式输出回答
        StringBuilder kbSummary = new StringBuilder("[知识库检索结果] ");
        for (Document doc : relevantDocs) {
            kbSummary.append(doc.getText().substring(0, Math.min(100, doc.getText().length()))).append("... ");
        }

        // 创建容器收集完整输出
        AtomicReference<StringBuilder> fullOutputBuilder = new AtomicReference<>(new StringBuilder());

        Prompt prompt = new Prompt(new UserMessage(ragPrompt));
        return Flux.just(kbSummary.toString())
                .concatWith(chatClient.prompt(prompt)
                        .user(parsedInput)
                        .advisors((a -> a.param(CONVERSATION_ID,
                                String.valueOf(input.getUid()) + ":" + input.getSessionid())))
                        .stream()
                        .content()
                        .doOnNext(part -> {
                            // 实时收集输出部分
                            fullOutputBuilder.get().append(part);
                        })
                        .doFinally(signal -> {
                            // 当流式输出完成后保存结果
                            String fullOutput = fullOutputBuilder.get().toString();
                            MultiModalResult result = new MultiModalResult();
                            result.setUid(input.getUid());
                            result.setSessionid(input.getSessionid());
                            result.setInputType("text");
                            result.setInputContent(parsedInput);
                            result.setIntent("kb_retrieval");
                            result.setOutputType("text");
                            result.setOutputContent(kbSummary.toString() + "\n" + fullOutput);
                            result.setModelName("Deepseek");
                            result.setCreatedAt(java.time.LocalDateTime.now());
                            multiModalResultService.saveResult(result);
                        }));
    }

    private String parseInput(MultiModalInput input) throws Exception {
        StringBuilder parsedInput = new StringBuilder();

        // 文本输入
        if (input.getText() != null && !input.getText().isEmpty()) {
            parsedInput.append(input.getText()).append(" ");
        }

        // 文档输入
        if (input.getFiles() != null && !input.getFiles().isEmpty()) {
            for (var file : input.getFiles()) {
                String docContent = documentParsingService.parseDocument(file);
                parsedInput.append("[文档内容]: " + docContent).append(" ");
            }
        }

        // 图片输入
        if (input.getImageUrl() != null && !input.getImageUrl().isEmpty()) {
            String imageDesc = imageProcessingService.processImage(input.getImageUrl());
            parsedInput.append("[图片描述]: " + imageDesc).append(" ");
        }

        if (input.getImageBase64() != null && !input.getImageBase64().isEmpty()) {
            String imageDesc = imageProcessingService.processImageFromBase64(input.getImageBase64());
            parsedInput.append("[图片描述]: " + imageDesc).append(" ");
        }

        return parsedInput.toString().trim();
    }

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageCacheService imageCacheService;

    private String getJimengImage_4_1(String prompt,MultiModalInput input) {
            IVisualService visualService = VisualServiceImpl.getInstance();
            String accessKey = System.getenv("VOLC_ACCESS_KEY");
            String secretKey = System.getenv("VOLC_SECRET_KEY");
            // 注意：应该使用 setAccessKey 和 setSecretKey
            visualService.setAccessKey(accessKey);
            visualService.setSecretKey(secretKey);
            JSONObject req = new JSONObject();
            req.put("req_key", "jimeng_t2i_v40");
            req.put("prompt", prompt);
            req.put("width", 1440);
            req.put("height", 2560 );
            req.put("return_url", true); // 添加这个参数以返回URL

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
                        imageModel.setUserid(String.valueOf(input.getUid()));
                        imageModel.setModel_name("jimeng_t2i_v40");
                        imageModel.setPrompt(prompt);
                        imageService.save(ossUrl, imageModel, prompt);

                        return ossUrl;
                    } catch (Exception downloadException) {
                        System.err.println("处理图片失败: " + imageUrl + ", 错误: " + downloadException.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "调用失败: " + e.getMessage();
            }
            return "处理图片失败";
    }

}