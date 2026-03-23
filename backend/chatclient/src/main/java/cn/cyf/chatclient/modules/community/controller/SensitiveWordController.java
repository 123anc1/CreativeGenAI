package cn.cyf.chatclient.modules.community.controller;

import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.common.utils.SensitiveWordFilter;
import cn.cyf.chatclient.common.utils.SensitiveWordImportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 敏感词管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/sensitive-word")
public class SensitiveWordController {
    
    @Autowired
    private SensitiveWordImportUtil sensitiveWordImportUtil;
    
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    
    /**
     * 批量导入敏感词
     */
    @PostMapping("/import")
    @RequiresRole(roles = {"ADMIN"})
    @LogOperation(value = "批量导入敏感词", module = "敏感词管理", level = LogOperation.LogLevel.WARN)
    public Result importSensitiveWords(@RequestParam("file") MultipartFile file,
                                      @RequestParam(defaultValue = "1") Integer level) {
        try {
            // 保存临时文件
            File tempFile = File.createTempFile("sensitive", ".txt");
            file.transferTo(tempFile);
            
            // 导入敏感词
            int count = sensitiveWordImportUtil.importFromFile(tempFile.getAbsolutePath(), level);
            
            // 删除临时文件
            tempFile.delete();
            
            // 重新加载敏感词库
            sensitiveWordFilter.reload();
            
            return Result.success("导入成功，共导入" + count + "条敏感词");
        } catch (IOException e) {
            log.error("导入敏感词失败", e);
            return Result.error("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 重新加载敏感词库
     */
    @PostMapping("/reload")
    @RequiresRole(roles = {"ADMIN"})
    @LogOperation(value = "重新加载敏感词库", module = "敏感词管理")
    public Result reloadSensitiveWords() {
        try {
            sensitiveWordFilter.reload();
            return Result.success("敏感词库重新加载成功");
        } catch (Exception e) {
            log.error("重新加载敏感词库失败", e);
            return Result.error("重新加载失败：" + e.getMessage());
        }
    }
}