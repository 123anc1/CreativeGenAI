package cn.cyf.chatclient.modules.chat.service.impl;

import cn.cyf.chatclient.modules.chat.mapper.ChatMapper;
import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.chat.model.ChatSqlmsg;
import cn.cyf.chatclient.modules.chat.service.ChatService;
import cn.cyf.chatclient.modules.knowledge.service.SimilaritySearchService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimilaritySearchService similaritySearchService;

    @Autowired
    @Qualifier("DeepseekchatClientWithMemory")
    private ChatClient chatClient;

    @Override
    public List<ChatSqlmsg> chatMsg(Integer uid, String sessionid) {

        String conversationid = String.valueOf(uid) + ":" + sessionid;
        return chatMapper.chatMsg(conversationid);
    }

    @Override
    public void change(ChatMsg chatMsg) {
        chatMapper.change(chatMsg);
    }

    @Override
    public Flux<String> chatPost(ChatMsg chatMsg) {
        if (chatMsg.getSessionid() == null || chatMsg.getUid() == null){
            return Flux.empty();
        }
        // 获取相关文档
        List<Document> relevantDocs = similaritySearchService.getSimilar(
                chatMsg.getContent(),
                String.valueOf(chatMsg.getUid()),
                chatMsg.getSessionid()
        );

        // 构建优化的提示词
        String ragPrompt = similaritySearchService.buildRagPrompt(chatMsg.getContent(), relevantDocs);

        Prompt prompt = new Prompt(new UserMessage(ragPrompt));

        return chatClient.prompt(prompt)
                .user(chatMsg.getContent())
                .advisors(a -> a.param(CONVERSATION_ID,
                        String.valueOf(chatMsg.getUid()) + ":" + chatMsg.getSessionid()))
                .stream()
                .chatResponse()
                .map(chatResponse -> {
                    try {
                        DeepSeekAssistantMessage msg = (DeepSeekAssistantMessage)
                                chatResponse.getResult().getOutput();
                        String reasoning = msg.getReasoningContent();
                        String answer = msg.getText();
                        if (reasoning != null && !reasoning.isEmpty()) {
                            return "####[推理]####：" + reasoning;
                        } else if (answer != null && !answer.isEmpty()) {
                            return "####[回复]####：" + answer;
                        }
                        return "";
                    } catch (Exception ex) {
                        return "[错误] " + ex.getMessage();
                    }
                })
                .filter(s -> !s.isEmpty());
    }


//    chatClient.prompt(prompt)
//            .user(chatMsg.getContent())
//            .advisors(a -> a.param(CONVERSATION_ID,
//                      String.valueOf(chatMsg.getUid()) + ":" + chatMsg.getSessionid()))
//            .stream()
//            .content();
}
