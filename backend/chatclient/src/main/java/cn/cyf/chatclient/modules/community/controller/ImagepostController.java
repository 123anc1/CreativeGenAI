package cn.cyf.chatclient.modules.community.controller;



import cn.cyf.chatclient.common.annotation.LogOperation;
import cn.cyf.chatclient.common.annotation.RequiresRole;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.community.model.Imagepost;
import cn.cyf.chatclient.modules.community.model.Imagepostparam;
import cn.cyf.chatclient.modules.community.model.Imagepostparamadmin;
import cn.cyf.chatclient.modules.community.model.PageResult;
import cn.cyf.chatclient.modules.community.service.ImageCommentService;
import cn.cyf.chatclient.modules.community.service.ImagepostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/imagepost")
public class ImagepostController {

    @Autowired
    private ImagepostService imagepostService;
    
    @Autowired
    private ImageCommentService imageCommentService;

    /**
     * 分享图片
     */
    @PostMapping("/image")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "分享图片", module = "图片分享", logParams = true)
    public Result upload(@RequestBody Imagepost imagepost) {
//        log.info("分享图片信息：{}", imagepost);
        imagepostService.upload(imagepost);
        return Result.success();
    }


    /**根据id查询图片信息
     *
     */
    @GetMapping("/image/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result checkById(@PathVariable Integer id, @RequestParam Integer userId) {
        log.info("根据id查询图片信息：{}", imagepostService.checkById(id, userId));
        return Result.success(imagepostService.checkById(id, userId));
    }

    /**根据id查询图片信息
     *查看图片发布状态
     */
    @GetMapping("/post/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result getPostById(@PathVariable Integer id, @RequestParam Integer userId) {
        log.info("根据id查询图片信息：{}", imagepostService.getPostById(id, userId));

        return Result.success(imagepostService.getPostById(id, userId));
    }


//     /**
//      * 获取图片列表
//      */
//     @GetMapping("/list")
//     @RequiresRole(roles = {"USER", "ADMIN"})
// //    @LogOperation(value = "获取图片列表", module = "图片管理")
//     public Result list(Imagepostparam imagepostparam) {
// //        log.info("图片列表参数：{}", imagepostparam);
//         PageResult<Imagepost> resultpost = imagepostService.list(imagepostparam);
// //        log.info("图片列表结果：{}", resultpost);
//         return Result.success(resultpost);
//     }

    /**
     * 根据id获取图片（包含评论信息）
     */
    @GetMapping("/list/{id}")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取图片详情", module = "图片管理")
    public Result getById(@PathVariable Integer id, @RequestParam Integer userId) {
        Imagepost imagepost = imagepostService.getById(id,userId);
        if (imagepost == null) {
            return Result.error("图片不存在");
        }
        
        // 记录浏览记录
        imagepostService.recordView(userId, id);
        
        // 获取该图片的评论数
        Integer commentCount = imageCommentService.countCommentsByPostId(id);
        imagepost.setCommentCount(commentCount);
        
        // 构造返回结果，包含图片信息和基本评论统计
        Map<String, Object> result = new HashMap<>();
        result.put("image", imagepost);
        result.put("commentCount", commentCount);
        
        return Result.success(result);
    }

    /**
     * 根据用户id获取图片
     */
    @GetMapping("/list/user/{userid}")
    @RequiresRole(roles = {"USER", "ADMIN"})
//    @LogOperation(value = "获取用户图片列表", module = "图片管理")
    public Result getByUserid(@PathVariable Integer userid) {
        List<Imagepost> image_post = imagepostService.getByUserid(userid);
//        log.info("用户发布图片信息：{}", Result.success(imagepost));
        return Result.success(image_post);
    }

    /**
     * 获取图片列表
     * 管理员
     */
    @GetMapping("/listadmin")
    @RequiresRole(value = "ADMIN")
//    @LogOperation(value = "管理员获取图片列表", module = "图片管理")
    public Result listAdmin(Imagepostparamadmin imagepostparamadmin) {
//        log.info("图片列表参数：{}", imagepostparam);
        PageResult<Imagepost> resultpost = imagepostService.listAdmin(imagepostparamadmin);
//        log.info("图片列表结果：{}", resultpost);
        return Result.success(resultpost);
    }

    /**
     * 修改图片
     */
    @PutMapping("/image")
    @RequiresRole(value = "ADMIN")
    @LogOperation(value = "图片审核通过", module = "图片管理", level = LogOperation.LogLevel.WARN)
    public Result update(@RequestBody Imagepost imagepost) {
        imagepostService.update(imagepost);
        return Result.success();
    }


    /**
     * 删除图片
     */
    @DeleteMapping("/delete")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "删除图片", module = "图片管理", level = LogOperation.LogLevel.WARN)
    public Result delete(@RequestBody Imagepost imagepost) {
//        log.info("删除图片信息：{}", imagepost);
        imagepostService.delete(imagepost);
        return Result.success();
    }

    /**
     * 点赞图片
     */
    @PostMapping("/like/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "点赞图片", module = "图片管理")
    public Result addLike(@PathVariable Integer postId, @RequestParam Integer userId) {
        imagepostService.addLike(userId, postId);
        return Result.success();
    }

    /**
     * 取消点赞图片
     */
    @DeleteMapping("/like/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "取消点赞图片", module = "图片管理")
    public Result removeLike(@PathVariable Integer postId, @RequestParam Integer userId) {
        imagepostService.removeLike(userId, postId);
        return Result.success();
    }

    /**
     * 检查是否点赞图片
     */
    @GetMapping("/like/check/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result checkLike(@PathVariable Integer postId, @RequestParam Integer userId) {
        Integer result = imagepostService.checkLike(userId, postId);
        return Result.success(result > 0);
    }

    /**
     * 收藏图片
     */
    @PostMapping("/collection/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "收藏图片", module = "图片管理")
    public Result addCollection(@PathVariable Integer postId, @RequestParam Integer userId) {
        imagepostService.addCollection(userId, postId);
        return Result.success();
    }

    /**
     * 取消收藏图片
     */
    @DeleteMapping("/collection/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    @LogOperation(value = "取消收藏图片", module = "图片管理")
    public Result removeCollection(@PathVariable Integer postId, @RequestParam Integer userId) {
        imagepostService.removeCollection(userId, postId);
        return Result.success();
    }

    /**
     * 检查是否收藏图片
     */
    @GetMapping("/collection/check/{postId}")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result checkCollection(@PathVariable Integer postId, @RequestParam Integer userId) {
        Integer result = imagepostService.checkCollection(userId, postId);
        return Result.success(result > 0);
    }

    /**
     * 获取用户收藏的图片列表
     */
    @GetMapping("/collection/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result getCollectionByUserid(@RequestParam Integer userId) {
        log.info("获取用户收藏的图片列表：{}", userId);
        List<Imagepost> imagepostList = imagepostService.getCollectionByUserid(userId);
        log.info("获取用户收藏的图片列表结果：{}", imagepostList);
        return Result.success(imagepostList);
    }

    /**
     * 个性化推荐图片
     */
    @GetMapping("/list")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result recommend(@RequestParam Integer userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        log.info("个性化推荐参数：userId={}, page={}, pageSize={}", userId, page, pageSize);
        PageResult<Imagepost> resultpost = imagepostService.recommend(userId, page, pageSize);
        log.info("个性化推荐结果：{}", resultpost);
        return Result.success(resultpost);
    }

    /**
     * 搜索图片
     * GET /imagepost/search?keyword=关键词&page=1&pageSize=20
     */
    @GetMapping("/search")
    @RequiresRole(roles = {"USER", "ADMIN"})
    public Result search(@RequestParam String keyword,
                         @RequestParam(required = false, defaultValue = "1") Integer page,
                         @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        log.info("搜索图片参数：keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        Imagepostparam param = new Imagepostparam();
        param.setKeyword(keyword);
        param.setPage(page);
        param.setPageSize(pageSize);
        PageResult<Imagepost> result = imagepostService.list(param);
        log.info("搜索图片结果：{}", result);
        return Result.success(result);
    }

}
