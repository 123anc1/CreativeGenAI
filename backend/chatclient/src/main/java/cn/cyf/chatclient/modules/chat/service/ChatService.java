package cn.cyf.chatclient.modules.chat.service;

import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.chat.model.ChatSqlmsg;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    List<ChatSqlmsg> chatMsg(Integer uid, String sessionid);

    void change(ChatMsg chatMsg);

    Flux<String> chatPost (ChatMsg chatMsg);
}
