package cn.cyf.chatclient.modules.document.controller;


import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.document.model.WorldSession;
import cn.cyf.chatclient.modules.document.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/word")
public class WordController {
    /**
     * 上传文件Global
     */
    @Autowired
    private WordService wordService;
    @PostMapping("/load")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "上传全局文档", module = "文档管理", logParams = true)
    public Result uploadGlobal(@RequestParam("files") MultipartFile[] files,
                               @RequestParam("userId") String userId) {
        wordService.uploadGlobal(files, userId);
        return Result.success();
    }

    /**
     * 上传文件Session
     */
    @PostMapping("/session")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "会话内上传文档", module = "文档管理", logParams = true)
    public Result uploadInSession(@RequestParam("files") MultipartFile[] files,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("sessionId") String sessionId) {
        wordService.uploadInSession(files, userId, sessionId);
        return Result.success();
    }

    /**
     *查看文档
     */
    @GetMapping("/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "查询文档列表", module = "文档管理")
    public List<WorldSession> list(@RequestParam String sessionId) {
        return wordService.listSession(sessionId);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete_session")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除文档", module = "文档管理", logParams = true, level = LogOperation.LogLevel.WARN)
    public Result delete(@RequestParam("fileName") String fileName,
                         @RequestParam("userId") String userId,
                         @RequestParam("sessionId") String sessionId) {
        wordService.delete(fileName,userId,sessionId);
        return Result.success();
    }
}
