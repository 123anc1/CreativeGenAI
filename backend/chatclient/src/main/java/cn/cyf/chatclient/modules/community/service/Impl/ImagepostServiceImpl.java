package cn.cyf.chatclient.modules.community.service.Impl;

import cn.cyf.chatclient.common.utils.AliyunOSSOperator;
import cn.cyf.chatclient.modules.community.mapper.ImageViewMapper;
import cn.cyf.chatclient.modules.community.mapper.ImagepostMapper;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.community.model.ImageCollection;
import cn.cyf.chatclient.modules.community.model.ImageLike;
import cn.cyf.chatclient.modules.community.model.Imagepost;
import cn.cyf.chatclient.modules.community.model.Imagepostparam;
import cn.cyf.chatclient.modules.community.model.Imagepostparamadmin;
import cn.cyf.chatclient.modules.community.model.PageResult;
import cn.cyf.chatclient.modules.community.service.ImageCacheService;
import cn.cyf.chatclient.modules.community.service.ImagepostService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.cyf.chatclient.modules.community.mapper.ImageLikeMapper;
import cn.cyf.chatclient.modules.community.mapper.ImageCollectionMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ImagepostServiceImpl implements ImagepostService {

    @Autowired
    private ImagepostMapper imagepostMapper;
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private ImageCacheService imageCacheService;
    @Autowired
    private ImageLikeMapper imageLikeMapper;
    @Autowired
    private ImageCollectionMapper imageCollectionMapper;
    @Autowired
    private ImageViewMapper imageViewMapper;
  
    @Override
    public PageResult<Imagepost> list(Imagepostparam imagepostparam) {

        if (imagepostparam == null || imagepostparam.getPage() == null || imagepostparam.getPageSize() == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }


        PageHelper.startPage(imagepostparam.getPage(), imagepostparam.getPageSize());


        List<Imagepost> imagepostList = imagepostMapper.list(imagepostparam);


        for (Imagepost imagepost : imagepostList) {
            try {
                if (imagepost.getImageUrl() != null && imagepost.getImageUrl().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
                if (cachedUrl != null) {
//                    log.info("使用缓存的图片链接:{}", cachedUrl);
                    imagepost.setImageUrl(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
//                log.info("生成新的图片链接:{}", signedUrl);
                imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl , 3600*24);
                imagepost.setImageUrl(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Page<Imagepost> pageResult = (Page<Imagepost>) imagepostList;


        return new PageResult<>(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public PageResult<Imagepost> listAdmin(Imagepostparamadmin imagepostparamadmin) {

        if (imagepostparamadmin == null || imagepostparamadmin.getPage() == null || imagepostparamadmin.getPageSize() == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }


        PageHelper.startPage(imagepostparamadmin.getPage(), imagepostparamadmin.getPageSize());


        List<Imagepost> imagepostList = imagepostMapper.listAdmin(imagepostparamadmin);


        for (Imagepost imagepost : imagepostList) {
            try {
                if (imagepost.getImageUrl() != null && imagepost.getImageUrl().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
                if (cachedUrl != null) {
//                    log.info("使用缓存的图片链接:{}", cachedUrl);
                    imagepost.setImageUrl(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
//                log.info("生成新的图片链接:{}", signedUrl);
                imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl , 3600*24);
                imagepost.setImageUrl(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Page<Imagepost> pageResult = (Page<Imagepost>) imagepostList;


        return new PageResult<>(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public Imagepost getById(Integer id, Integer userId) {
        return imagepostMapper.getById(id);
    }

    @Override
    public List<Imagepost> getByUserid(Integer userid) {
        List<Imagepost> imagepostList = imagepostMapper.getByUserid(userid);

        for (Imagepost imagepost : imagepostList) {
            try {
                if (imagepost.getImageUrl() != null && imagepost.getImageUrl().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
                if (cachedUrl != null) {
                    imagepost.setImageUrl(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
                imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl , 3600*24);
                imagepost.setImageUrl(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imagepostList;
    }

    @Override
    public void upload(Imagepost imagepost) {
        imagepost.setImageUrl(imagepostMapper.insertshare(imagepost.getImageId()));
        if (imagepostMapper.check(imagepost) != null){
            Result.error("图片已存在");
            return;
        }
        imagepost.setCreatedAt(LocalDateTime.now());
        imagepost.setUpdatedAt(LocalDateTime.now());
        imagepostMapper.upload(imagepost);
        Result.success("上传成功");
    }

    @Override
    public void update(Imagepost imagepost) {
        imagepostMapper.update(imagepost);
    }

    @Override
    public void delete(Imagepost imagepost) {
        imagepostMapper.delete(imagepost);
    }

    @Override
    public void addLike(Integer userId, Integer postId) {
        if (imageLikeMapper.checkLike(userId, postId) == 0) {
            ImageLike like = new ImageLike();
            like.setUserId(userId);
            like.setPostId(postId);
            like.setCreatedAt(LocalDateTime.now());
            imageLikeMapper.addLike(like);
            Integer likeCount = imageLikeMapper.countLikes(postId);
            imagepostMapper.updateLikeCount(postId, likeCount);
        }
    }

    @Override
    public void removeLike(Integer userId, Integer postId) {
        if (imageLikeMapper.checkLike(userId, postId) > 0) {
            imageLikeMapper.removeLike(userId, postId);
            Integer likeCount = imageLikeMapper.countLikes(postId);
            imagepostMapper.updateLikeCount(postId, likeCount);
        }
    }

    @Override
    public Integer checkLike(Integer userId, Integer postId) {
        return imageLikeMapper.checkLike(userId, postId);
    }

    @Override
    public void addCollection(Integer userId, Integer postId) {
        if (imageCollectionMapper.checkCollection(userId, postId) == 0) {
            ImageCollection collection = new ImageCollection();
            collection.setUserId(userId);
            collection.setPostId(postId);
            collection.setCreatedAt(LocalDateTime.now());
            imageCollectionMapper.addCollection(collection);
            Integer collectionCount = imageCollectionMapper.countCollections(postId);
            imagepostMapper.updateCollectionCount(postId, collectionCount);
        }
    }

    @Override
    public void removeCollection(Integer userId, Integer postId) {
        if (imageCollectionMapper.checkCollection(userId, postId) > 0) {
            imageCollectionMapper.removeCollection(userId, postId);
            Integer collectionCount = imageCollectionMapper.countCollections(postId);
            imagepostMapper.updateCollectionCount(postId, collectionCount);
        }
    }

    @Override
    public Integer checkCollection(Integer userId, Integer postId) {
        return imageCollectionMapper.checkCollection(userId, postId);
    }

    @Override
    public void recordView(Integer userId, Integer postId) {
        if (imageViewMapper.checkView(userId, postId) == 0) {
            imageViewMapper.addView(userId, postId, java.time.LocalDateTime.now());
            imageViewMapper.incrementViewCount(postId);
        }
    }

    @Override
    public List<Imagepost> getCollectionByUserid(Integer userId) {
        List<Imagepost> imagepostList = imageCollectionMapper.getCollectionByUserId(userId);
        for (Imagepost imagepost : imagepostList) {
            try {
                if (imagepost.getImageUrl() != null && imagepost.getImageUrl().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
                if (cachedUrl != null) {
                    imagepost.setImageUrl(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
                imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl, 3600*24);
                imagepost.setImageUrl(signedUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imagepostList;
    }

    @Override
    public Imagepost checkById(Integer id, Integer userId) {
        return imagepostMapper.checkById(id, userId);
    }

    @Override
    public Imagepost getPostById(Integer id, Integer userId){
        Imagepost imagepost =  imagepostMapper.getPostById(id, userId);
        try {
            String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
            log.info("图片地址：{}", cachedUrl);
            if (cachedUrl != null) {
                imagepost.setImageUrl(cachedUrl);
                log.info("使用缓存的图片URL：{}", imagepost.getImageUrl());
                return imagepost;
            }
            String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
            imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl , 3600*24);
            imagepost.setImageUrl(signedUrl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return imagepost;
    }

    @Override
    public PageResult<Imagepost> recommend(Integer userId, Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }

        PageHelper.startPage(page, pageSize);

        List<Imagepost> imagepostList = imagepostMapper.recommend(userId);

        for (Imagepost imagepost : imagepostList) {
            try {
                if (imagepost.getImageUrl() != null && imagepost.getImageUrl().startsWith("http")) {
                    continue;
                }
                String cachedUrl = imageCacheService.getCachedSignedUrl(imagepost.getImageUrl());
                if (cachedUrl != null) {
                    imagepost.setImageUrl(cachedUrl);
                    continue;
                }
                String signedUrl = aliyunOSSOperator.generateSignedUrl(imagepost.getImageUrl(), 3600*24);
                imageCacheService.cacheSignedUrl(imagepost.getImageUrl(), signedUrl , 3600*24);
                imagepost.setImageUrl(signedUrl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Page<Imagepost> pageResult = (Page<Imagepost>) imagepostList;

        return new PageResult<>(pageResult.getTotal(), pageResult.getResult());
    }
}
