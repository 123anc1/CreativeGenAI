package cn.cyf.chatclient.modules.community.service;


import cn.cyf.chatclient.modules.community.model.Imagepost;
import cn.cyf.chatclient.modules.community.model.Imagepostparam;
import cn.cyf.chatclient.modules.community.model.Imagepostparamadmin;
import cn.cyf.chatclient.modules.community.model.PageResult;

import java.util.List;

public interface ImagepostService {
    void upload(Imagepost imagepost);

    void delete(Imagepost imagepost);

    void update(Imagepost imagepost);

    List<Imagepost> getByUserid(Integer userid);

    Imagepost getById(Integer id, Integer userId);

    PageResult<Imagepost> list(Imagepostparam imagepostparam);

    PageResult<Imagepost> listAdmin(Imagepostparamadmin imagepostparamadmin);

    void addLike(Integer userId, Integer postId);

    void removeLike(Integer userId, Integer postId);

    Integer checkLike(Integer userId, Integer postId);

    void addCollection(Integer userId, Integer postId);

    void removeCollection(Integer userId, Integer postId);

    Integer checkCollection(Integer userId, Integer postId);

    void recordView(Integer userId, Integer postId);

    List<Imagepost> getCollectionByUserid(Integer userId);

    Imagepost checkById(Integer id, Integer userId);

    Imagepost getPostById(Integer id, Integer userId);

    /**
     * 个性化推荐图片
     */
    PageResult<Imagepost> recommend(Integer userId, Integer page, Integer pageSize);
}
