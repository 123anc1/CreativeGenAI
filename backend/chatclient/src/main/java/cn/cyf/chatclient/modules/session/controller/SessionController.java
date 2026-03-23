package cn.cyf.chatclient.modules.session.controller;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.document.service.WordService;
import cn.cyf.chatclient.modules.session.model.SessionVo;
import cn.cyf.chatclient.modules.session.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private WordService wordService;

    @PostMapping("/create")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "创建会话", module = "会话管理", logParams = true)
    public SessionVo create(@RequestBody ChatMsg chatMsg) {
        log.info("创建会话ID:{},标题：{}", chatMsg.getSessionid(), chatMsg.getTitle());
        return sessionService.create(chatMsg);
    }

    @GetMapping("/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取会话列表", module = "会话管理")
    public List<SessionVo> list(@RequestParam Integer id) {
        return sessionService.list(id);
    }

    @PostMapping("/update")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "更新会话", module = "会话管理", logParams = true)
    public void update(@RequestBody SessionVo sessionVo) {
        log.info("重命名会话ID:" + sessionVo.getSessionid());
        sessionService.update(sessionVo);
    }

    @DeleteMapping("/delete")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除会话", module = "会话管理", level = LogOperation.LogLevel.WARN)
    public void delete(@RequestBody SessionVo sessionVo) {
        wordService.deleteBySessionId(String.valueOf(sessionVo.getUserid()),sessionVo.getSessionid());
        sessionService.delete(sessionVo);

    }

}
