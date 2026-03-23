package cn.cyf.chatclient.modules.image.controller;


import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.image.model.Basemodel;
import cn.cyf.chatclient.modules.image.model.Loramodel;
import cn.cyf.chatclient.modules.image.service.ImagemodelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {
    @Autowired
    private ImagemodelService imagemodelService;

    /*
     *获取基础模型列表
     */
    @GetMapping("/basemodels")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取基础模型列表", module = "模型管理")
    public Result getBaseModel() {
//        log.info("获取基础模型列表");
        List<Basemodel> listBaseModel = imagemodelService.listBaseModel();
//        log.info("基础模型列表：{}", listBaseModel);
        return Result.success(listBaseModel);
    }
    /*
     *选择基础模型
     */
    @GetMapping("/basemodels/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "选择基础模型", module = "模型管理")
    public Result selectBaseModel(@PathVariable String id) {
        Basemodel basemodel =  imagemodelService.selectBaseModel(id);
//        log.info("选择基础模型：{}", id);
        return Result.success(basemodel);
    }

    /*
     *获取风格模型列表
     */
    @GetMapping("/stylemodels")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取风格模型列表", module = "模型管理")
    public Result getStyleModel() {
        List<Loramodel> listStyleModel = imagemodelService.listStyleModel();
//        log.info("风格模型列表：{}", listStyleModel);
        return Result.success(listStyleModel);
    }

    /*
     *选择风格模型
     */
    @GetMapping("/stylemodels/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "选择风格模型", module = "模型管理")
    public Result selectStyleModel(@PathVariable String id) {
        Loramodel loramodel = imagemodelService.selectStyleModel(id);
//        log.info("选择风格模型：{}", id);
        return Result.success(loramodel);
    }

    /*
     *上传基础模型
     */
    @PostMapping("/basemodels")
    @RequiresRole(value = "ADMIN")
    @LogOperation(value = "上传基础模型", module = "模型管理", level = LogOperation.LogLevel.WARN)
    public Result uploadModel(@RequestBody Basemodel basemodel) {
        return Result.success("上传成功");
    }

    /*
     *上传风格模型
     */
    @PostMapping("/stylemodels")
    @RequiresRole(value = "ADMIN")
    @LogOperation(value = "上传风格模型", module = "模型管理", level = LogOperation.LogLevel.WARN)
    public Result uploadStyleModel(@RequestBody Loramodel loramodel) {
        return Result.success("上传成功");
    }

    /*
     *修改基础模型
     */
    @PutMapping("/basemodels")
    @RequiresRole(value = "ADMIN")
    @LogOperation(value = "修改基础模型", module = "模型管理", level = LogOperation.LogLevel.WARN)
    public Result updateBaseModel(@RequestBody Basemodel basemodel) {
        imagemodelService.updateBaseModel(basemodel);
        return Result.success("修改成功");
    }
}
