package cn.cyf.chatclient.modules.multimodal.service.impl;

import cn.cyf.chatclient.modules.multimodal.service.IntentRecognitionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    @Autowired
    @Qualifier("DeepseekchatClientWithoutMemory")
    private ChatClient chatClient;

    @Override
    public String recognizeIntent(String userInput) {
        String promptTemplate = """
        请分析用户输入内容的核心意图，仅返回以下枚举值之一：
        注意返回的内容只需要返回（text2image,qa,doc_analysis,kb_retrieval）不要输出多余内容
        1. text2image（文生图，输入内容包含图像生成相关描述，如"生成/绘制/创作+图片/图像"）
        2. qa（通用问答，输入为问题类内容，如"什么是/如何做/解释+XXX"）
        3. doc_analysis（文档分析，输入为文档/表格，需求为分析/提取/总结文档内容）
        4. kb_retrieval（知识库检索，输入为需要从知识库匹配答案的问题）
        用户输入内容：{user_input_content}
        """;

        String formattedPrompt = promptTemplate.replace("{user_input_content}", userInput);
        Prompt prompt = new Prompt(new UserMessage(formattedPrompt));

        return chatClient.prompt(prompt).call().content().trim();
    }
}