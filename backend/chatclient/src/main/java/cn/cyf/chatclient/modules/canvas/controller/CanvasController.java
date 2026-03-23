package cn.cyf.chatclient.modules.canvas.controller;

import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.canvas.model.CanvasImage;
import cn.cyf.chatclient.modules.canvas.model.SessionInfo;
import cn.cyf.chatclient.modules.canvas.service.CanvasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/canvas")
public class CanvasController {
    @Autowired
    private CanvasService canvasService;

    /**
     *创建会话
     */
    @PostMapping("/session")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result session(@RequestBody SessionInfo sessionInfo) throws Exception {
        log.info("创建会话：{}", sessionInfo);
        return Result.success(canvasService.sessionCreate(sessionInfo));
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/session/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result sessionList(@RequestParam String userId) {
        log.info("获取会话列表:{}", userId);
        return Result.success(canvasService.sessionList(userId));
    }
    /**
     * 获取会话图片
     */
    @GetMapping("/session/list/image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result sessionListImage(@RequestParam String sessionId) {
        log.info("获取会话图片：{}", sessionId);
        return Result.success(canvasService.sessionListImage(sessionId));
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result session(@PathVariable String id) throws Exception {
        log.info("删除会话：{}", id);
        canvasService.sessionDelete(id);
        return Result.success();
    }

    /**
     * 删除会话中图片
     */
    @DeleteMapping("/image/delete/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result sessionImage(@PathVariable String id) throws Exception {
        log.info("删除会话中图片：{}", id);
        canvasService.sessionImageDelete(id);
        return Result.success();
    }

    /**
     * 修改会话标题
     */
    @PostMapping("/session/update")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result sessionUpdate(@RequestParam String sessionId, @RequestParam String title) {
        log.info("修改会话标题：{}", sessionId);
        canvasService.sessionUpdate(sessionId, title);
        return Result.success();
    }
    /**
     *保存图像信息
     */
    @PostMapping("/image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result image(@RequestBody SessionInfo sessionInfo) throws Exception {
        log.info("保存图像信息：{}", sessionInfo);
        return Result.success(canvasService.saveImage(sessionInfo));
    }

    /**
     * 画图
     */
    @PostMapping("/inpainting")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result inpainting(@RequestBody CanvasImage canvasImage) throws IOException {
//        log.info("画图：{}", canvasImage);
        Result result = canvasService.asyncInpainting(canvasImage);
        log.info("返回参数：{}", result);
        return result;
    }
}
