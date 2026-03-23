package cn.cyf.chatclient.modules.knowledge.controller;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.modules.document.model.WorldGlobal;
import cn.cyf.chatclient.modules.knowledge.service.KnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/knowledgeBase")
public class KnowledgeBaseController {
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取文档列表
     */
    @GetMapping("/list" )
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取知识库文档列表", module = "知识库管理")
    public List<WorldGlobal> list(@RequestParam String userId) {
//        log.info("文档内容:{}",knowledgeBaseService.listGlobal(userId));
        return knowledgeBaseService.listGlobal(userId);
    }

    /**
     * 删除文档
     */
    @GetMapping("/delete")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除知识库文档", module = "知识库管理", level = LogOperation.LogLevel.WARN)
    public void delete(@RequestParam String fileName, @RequestParam Integer userId) {
        knowledgeBaseService.deleteGlobal(fileName, String.valueOf(userId));
    }
}
