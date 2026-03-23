package cn.cyf.chatclient.modules.multimodal.controller;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.chat.service.ChatService;
import cn.cyf.chatclient.modules.multimodal.model.MultiModalInput;
import cn.cyf.chatclient.modules.multimodal.service.MultiModalService;
import cn.cyf.chatclient.modules.session.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/multimodal")
public class MultiModalController {

    @Autowired
    private MultiModalService multiModalService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ChatService chatService;
    /**
     * 处理多模态输入（流式输出）
     */
    @PostMapping(value = "/process", produces = "text/html;charset=utf-8")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "处理多模态输入", module = "多模态处理", logParams = true, logResult = true)
    public Flux<String> processMultiModalInput(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageBase64", required = false) String imageBase64,
            @RequestParam(value = "uid", required = true) Integer uid,
            @RequestParam(value = "sessionid", required = true) String sessionid,
            @RequestParam(value = "title", required = true) String title
    ) {

        try {
            MultiModalInput input = new MultiModalInput();
            input.setText(text);
            input.setFiles(files);
            input.setImageUrl(imageUrl);
            input.setImageBase64(imageBase64);
            input.setUid(uid);
            input.setSessionid(sessionid);
            log.info("多模态参数输入,{}", input);

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setUid(uid);
            chatMsg.setSessionid(sessionid);
            chatMsg.setTitle(title);
            //创建会话
            sessionService.create(chatMsg);
            chatService.change(chatMsg);

            return multiModalService.processInputStream(input);
        } catch (Exception e) {
            return Flux.just("[错误] 处理多模态输入失败: " + e.getMessage());
        }
    }
}