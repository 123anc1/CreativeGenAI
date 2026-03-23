package cn.cyf.chatclient.modules.multimodal.controller;

import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.modules.multimodal.model.MultiModalResult;
import cn.cyf.chatclient.modules.multimodal.service.MultiModalResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/multimodal/result")
public class MultiModalResultController {

    @Autowired
    private MultiModalResultService multiModalResultService;



    /**
     * 根据用户ID和会话ID获取多模态结果列表
     */
    @GetMapping("/session")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public List<MultiModalResult> getResultsBySession(@RequestParam Integer uid, @RequestParam String sessionid) {
        log.info("获取用户会话多模态结果，uid: {}, sessionid: {}", uid, sessionid);
        return multiModalResultService.getResultsByUidAndSessionid(uid, sessionid);
    }

    /**
     * 根据结果ID获取多模态结果详情
     */
    @GetMapping("/detail/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public MultiModalResult getResultById(@PathVariable Long id) {
        log.info("获取多模态结果详情，id: {}", id);
        return multiModalResultService.getResultById(id);
    }

    /**
     * 根据用户ID获取多模态结果列表
     */
    @GetMapping("/user")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public List<MultiModalResult> getResultsByUid(@RequestParam Integer uid) {
        log.info("获取用户多模态结果，uid: {}", uid);
        return multiModalResultService.getResultsByUid(uid);
    }

    /**
     * 加载图片
     */
    @PostMapping("/image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public String loadImage(@RequestParam String imageUrl) {
        return multiModalResultService.loadImage(imageUrl);
    }
}
