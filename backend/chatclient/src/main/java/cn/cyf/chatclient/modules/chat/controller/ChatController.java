package cn.cyf.chatclient.modules.chat.controller;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.chat.model.ChatSqlmsg;
import cn.cyf.chatclient.modules.chat.service.ChatService;
import cn.cyf.chatclient.modules.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SessionService sessionService;

    /**
     * 聊天 - 普通用户和管理员都可以使用
     */
    @PostMapping(value = "/chat", produces = "text/html;charset=utf-8")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "AI对话聊天", module = "AI对话", logParams = true, logResult = true)
    public Flux<String> chatPost(@RequestBody ChatMsg chatMsg) {
        sessionService.create(chatMsg);
        chatService.change(chatMsg);
        return chatService.chatPost(chatMsg);
    }

    /**
     * 获取聊天记录 - 普通用户和管理员都可以使用
     */
    @GetMapping(value = "/c_msg")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取聊天记录", module = "AI对话")
    public List<ChatSqlmsg> chatMsg(@RequestParam Integer uid, @RequestParam String sessionid) {
        return chatService.chatMsg(uid, sessionid);
    }

}