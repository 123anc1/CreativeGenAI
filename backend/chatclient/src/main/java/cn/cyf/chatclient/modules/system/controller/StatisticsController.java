package cn.cyf.chatclient.modules.system.controller;

import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.system.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计分析控制器
 * 提供统计分析相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/statistics")
@RequiresRole(value = "ADMIN")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取系统概览统计数据
     */
    @GetMapping("/overview")
    public Result getSystemOverview() {
        try {
//            log.info("获取系统概览统计数据:{}", statisticsService.getSystemOverview());
            return Result.success(statisticsService.getSystemOverview());
        } catch (Exception e) {
            log.error("获取系统概览统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/user")
    public Result getUserStatistics() {
        try {
//            log.info("获取用户统计数据:{}", statisticsService.getUserStatistics());
            return Result.success(statisticsService.getUserStatistics());
        } catch (Exception e) {
            log.error("获取用户统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }

    /**
     * 获取社区统计数据
     */
    @GetMapping("/community")
    public Result getCommunityStatistics() {
        try {
//            log.info("获取社区统计数据");
            return Result.success(statisticsService.getCommunityStatistics());
        } catch (Exception e) {
            log.error("获取社区统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }
    /**
     * 获取图片生成统计数据
     */
    @GetMapping("/image-generation")
    public Result getImageGenerationStatistics() {
        try {
//            log.info("获取图片生成统计数据");
            return Result.success(statisticsService.getImageGenerationStatistics());
        } catch (Exception e) {
            log.error("获取图片生成统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }

    /**
     * 获取多模态交互统计数据
     */
    @GetMapping("/multimodal")
    public Result getMultiModalStatistics() {
        try {
//            log.info("获取多模态交互统计数据");
            return Result.success(statisticsService.getMultiModalStatistics());
        } catch (Exception e) {
            log.error("获取多模态交互统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }

    /**
     * 获取最近7天的统计数据
     */
    @GetMapping("/recent-7days")
    public Result getRecent7DaysStatistics() {
        try {
//            log.info("获取最近7天的统计数据");
            return Result.success(statisticsService.getRecent7DaysStatistics());
        } catch (Exception e) {
            log.error("获取最近7天的统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }
}
