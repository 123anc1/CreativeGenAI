import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Imagepost } from '@/shared/types';
import imagepostService from '@/modules/user/services/imagepost';

// 增强的图片类型接口
interface EnhancedImagePost {
  // 基础字段（来自Imagepost）
  id: number;
  userid: number;
  user_name?: string;
  imageId?: number;
  image_url?: string;
  imageData?: string | number[];
  image_title?: string;
  image_description?: string;
  created_at?: Date;
  updated_at?: Date;
  commentCount?: number;
  likeCount?: number;
  // 新增字段
  imageUrl?: string; // 图片URL（备选字段）
  title: string; // 图片标题（备选字段，始终为string）
  description?: string; // 图片描述
  userId?: number; // 用户ID
  createdAt?: string; // 创建时间（ISO格式）
  updatedAt?: string; // 更新时间（ISO格式）
  negativePrompt?: string; // 负面提示词
  steps?: number; // 生成步数
  cfgScale?: number; // 配置缩放
  modelName?: string; // 模型名称
  style?: string; // 风格
  width?: number; // 宽度
  height?: number; // 高度
}

export function useImageActions() {
  const router = useRouter();
  
  // 图片详情弹窗相关
  const showDetailModal = ref(false);
  const selectedImage = ref<EnhancedImagePost | null>(null);
  const imageList = ref<EnhancedImagePost[]>([]);
  const currentIndex = ref(0);
  const isLoadingMore = ref(false);
  const hasMoreImages = ref(true);
  
  // 外部传入的加载更多图片函数
  let loadMoreImagesCallback: (() => Promise<void>) | null = null;
  
  // 设置加载更多图片的回调函数
  const setLoadMoreImagesCallback = (callback: () => Promise<void>) => {
    loadMoreImagesCallback = callback;
  };
  
  // 设置是否还有更多图片的状态
  let checkHasMoreCallback: (() => boolean) | null = null;
  const setCheckHasMoreCallback = (callback: () => boolean) => {
    checkHasMoreCallback = callback;
  };
  
  // 更新图片列表
  const updateImageList = (newPosts: Imagepost[]) => {
    const currentId = selectedImage.value?.id;
    const newImages = newPosts.map(p => convertToImagePost(p));
    imageList.value = newImages;
    if (currentId) {
      const index = newImages.findIndex(img => img.id === currentId);
      if (index >= 0) {
        currentIndex.value = index;
      }
    }
  };
  
  // 点赞和收藏状态
  const isLiked = ref(false);
  const isCollected = ref(false);
  const currentUserId = ref<number>(0);
  const isLiking = ref(false);
  const pendingLikeAction = ref<{ postId: number; action: 'like' | 'unlike' } | null>(null);
  
  // 路由跳转状态
  const isNavigating = ref(false);
  
  // 图片缓存 - 优化版本
  const imageCache = new Map<string, string>();
  const MAX_CACHE_SIZE = 50; // 限制缓存大小，避免内存占用过大
  
  // 获取当前用户ID
  const getCurrentUserId = (): number => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        return user.id || 0;
      } catch (e) {
        return 0;
      }
    }
    return 0;
  };
  
  // 添加转换函数：将Imagepost转换为EnhancedImagePost格式
  const convertToImagePost = (imagepost: Imagepost): EnhancedImagePost => {
    // 创建新的对象，避免属性重复
    const result: EnhancedImagePost = {
      // 基础字段
      id: imagepost.id || 0,
      userid: imagepost.userid,
      user_name: imagepost.user_name,
      imageId: imagepost.imageId,
      image_url: imagepost.image_url,
      imageData: imagepost.imageData,
      image_title: imagepost.image_title,
      image_description: imagepost.image_description,
      created_at: imagepost.created_at,
      updated_at: imagepost.updated_at,
      commentCount: imagepost.commentCount,
      likeCount: imagepost.likeCount,
      // 增强字段
      title: imagepost.image_title ?? '未命名图片',
      imageUrl: imagepost.image_url,
      description: imagepost.image_description,
      userId: imagepost.userid,
      createdAt: imagepost.created_at?.toISOString(),
      updatedAt: imagepost.updated_at?.toISOString(),
      // 数值类型转换
      steps: imagepost.steps ? Number(imagepost.steps) : undefined,
      cfgScale: imagepost.cfgScale ? Number(imagepost.cfgScale) : undefined,
      width: imagepost.width ? Number(imagepost.width) : undefined,
      height: imagepost.height ? Number(imagepost.height) : undefined
    };
    // 保证 title 一定为 string
    if (result.title === undefined || result.title === null) {
      result.title = '未命名图片';
    }
    return result;
  };
  
  // 图像处理函数 - 自动选择最佳显示方式
  const getSingleImageSrc = (post: any) => {
    // 生成缓存键 - 优化：使用更简单的缓存键
    const cacheKey = post.id ? `img_${post.id}` : `img_${JSON.stringify(post).substring(0, 100)}`;
    
    // 检查缓存
    if (imageCache.has(cacheKey)) {
      return imageCache.get(cacheKey) || '';
    }
    
    let imageSrc = '';
    
    // 优先处理 base64 字符串数据
    if (post.imageData && typeof post.imageData === 'string' && (post.imageData as string).startsWith('iVBOR')) {
      try {
        // 如果是 base64 数据，直接创建 data URL
        imageSrc = `data:image/png;base64,${post.imageData}`;
      } catch (error) {
        console.error('处理 base64 图像数据失败:', error);
      }
    } else if (post.imageData && Array.isArray(post.imageData)) {
      // 处理字节数组数据
      try {
        const uint8Array = new Uint8Array(post.imageData);
        const blob = new Blob([uint8Array], { type: 'image/png' });
        imageSrc = URL.createObjectURL(blob);
      } catch (error) {
        console.error('解码图像数据失败:', error);
      }
    } else if (post.image_url) {
      // 如果有 image_url，使用它作为后备
      imageSrc = post.image_url;
    } else if (post.imageUrl) {
      // 如果有 imageUrl（驼峰命名），也使用它作为后备
      imageSrc = post.imageUrl;
    }
    
    // 缓存结果 - 增加缓存大小限制
    if (imageSrc) {
      // 如果缓存达到最大大小，删除最早的缓存项
      if (imageCache.size >= MAX_CACHE_SIZE) {
        const firstKey = imageCache.keys().next().value;
        if (firstKey) {
          const oldSrc = imageCache.get(firstKey);
          if (oldSrc && oldSrc.startsWith('blob:')) {
            URL.revokeObjectURL(oldSrc);
          }
          imageCache.delete(firstKey);
        }
      }
      imageCache.set(cacheKey, imageSrc);
    }
    
    return imageSrc;
  };
  
  // 预加载图片函数
  const preloadImages = (posts: Imagepost[]) => {
    // 预加载前20张图片，增加预加载数量
    const postsToPreload = posts.slice(0, 20);
    
    // 立即加载所有预加载图片，不使用交错加载以加快速度
    postsToPreload.forEach((post) => {
      const imageSrc = getSingleImageSrc(convertToImagePost(post));
      if (imageSrc && imageSrc.startsWith('http')) {
        const img = new Image();
        img.src = imageSrc;
      }
    });
  };
  
  // 显示图片详情
  const showImageDetail = (post: Imagepost, posts?: Imagepost[]) => {
    try {
      // 检查 post 是否有效
      if (!post || !post.id) {
        showToast('图片信息无效', 'error');
        return;
      }

      // 获取当前用户ID
      currentUserId.value = getCurrentUserId();
      if (!currentUserId.value) {
        showToast('请先登录', 'error');
        router.push('/login');
        return;
      }

      // 设置图片列表和当前索引
      if (posts && posts.length > 0) {
        imageList.value = posts.map(p => convertToImagePost(p));
        const index = posts.findIndex(p => p.id === post.id);
        currentIndex.value = index >= 0 ? index : 0;
      } else {
        imageList.value = [convertToImagePost(post)];
        currentIndex.value = 0;
      }

      // 立即使用已有数据作为默认值并显示弹窗（快速响应）
      selectedImage.value = convertToImagePost(post);
      showDetailModal.value = true;
      
      // 异步加载API数据和检查状态（不阻塞UI）
      loadImageDetailData(post.id);
    } catch (error) {
      console.error('显示图片详情失败:', error);
      // 最终降级处理
      if (post) {
        selectedImage.value = convertToImagePost(post);
        showDetailModal.value = true;
      } else {
        showToast('无法显示图片详情', 'error');
      }
    }
  };
  
  // 切换到上一张图片
  const goToPrevImage = () => {
    if (currentIndex.value > 0) {
      currentIndex.value--;
      selectedImage.value = imageList.value[currentIndex.value];
      loadImageDetailData(selectedImage.value.id);
    }
  };
  
  // 切换到下一张图片
  const goToNextImage = async () => {
    if (currentIndex.value < imageList.value.length - 1) {
      currentIndex.value++;
      selectedImage.value = imageList.value[currentIndex.value];
      loadImageDetailData(selectedImage.value.id);
      if (checkHasMoreCallback) {
        hasMoreImages.value = checkHasMoreCallback();
      }
    } else if (loadMoreImagesCallback && !isLoadingMore.value && hasMoreImages.value) {
      isLoadingMore.value = true;
      await loadMoreImagesCallback();
      if (imageList.value.length > currentIndex.value + 1) {
        currentIndex.value++;
        selectedImage.value = imageList.value[currentIndex.value];
        loadImageDetailData(selectedImage.value.id);
      }
      if (checkHasMoreCallback) {
        hasMoreImages.value = checkHasMoreCallback();
      }
      isLoadingMore.value = false;
    }
  };
  
  // 更新是否还有更多图片的状态
  const updateHasMoreImages = (hasMore: boolean) => {
    hasMoreImages.value = hasMore;
  };
  
  // 异步加载图片详情数据
  const loadImageDetailData = async (postId: number) => {
    try {
      // 并行处理API请求，提高加载速度
      const [detailData, likeStatus, collectionStatus] = await Promise.all([
        imagepostService.getImagepostById(postId).catch(err => {
          // 静默处理"图片不存在"错误，因为我们已经有本地数据
          if (err.message && err.message.includes('图片不存在')) {
            return null;
          }
          throw err;
        }),
        imagepostService.checkLike(postId, currentUserId.value),
        imagepostService.checkCollection(postId, currentUserId.value)
      ]);

      // 更新点赞和收藏状态
      isLiked.value = likeStatus;
      isCollected.value = collectionStatus;

      // 检查返回的数据是否有效
      if (detailData && selectedImage.value?.id === postId) {
        // 保存本地修改的点赞数和状态
        const localLikeCount = selectedImage.value.likeCount;
        const localCommentCount = selectedImage.value.commentCount;
        
        // 更新为详细数据
        const updatedImage = convertToImagePost(detailData);
        
        // 优先保留本地修改的点赞数，避免API数据覆盖用户刚点的赞
        if (localLikeCount !== undefined) {
          updatedImage.likeCount = localLikeCount;
        }
        // 保留评论数
        if (localCommentCount !== undefined) {
          updatedImage.commentCount = localCommentCount;
        }
        
        // 更新选中的图片数据
        selectedImage.value = updatedImage;
      }
    } catch (error) {
      // API调用失败时，不显示错误信息，使用已有数据
      console.log('获取图片详情数据失败，使用本地数据:', error);
    }
  };
  
  // 发送待处理的点赞操作
  const sendPendingLikeAction = async () => {
    if (pendingLikeAction.value && currentUserId.value) {
      const { postId, action } = pendingLikeAction.value;
      try {
        if (action === 'like') {
          await imagepostService.addLike(postId, currentUserId.value);
        } else {
          await imagepostService.removeLike(postId, currentUserId.value);
        }
        // 操作成功后清空待处理状态
        pendingLikeAction.value = null;
      } catch (error) {
        console.error('发送点赞操作失败:', error);
      }
    }
  };
  
  const closeDetailModal = async () => {
    // 关闭前发送待处理的点赞操作
    await sendPendingLikeAction();
    showDetailModal.value = false;
    selectedImage.value = null;
    // 不重置点赞和收藏状态，这样下次打开相同图片时可以保持状态
  };
  
  // 下载选中的图片 - 同步最简版本
  const downloadSelectedImage = () => {
    if (!selectedImage.value) {
      showToast('请选择要下载的图片', 'error');
      return;
    }

    const imageUrl = getSingleImageSrc(selectedImage.value);

    if (!imageUrl) {
      showToast('无法获取图片地址', 'error');
      return;
    }

    // 直接下载 - 最可靠的方式
    const link = document.createElement('a');
    link.href = imageUrl;
    link.download = `${selectedImage.value.image_title || selectedImage.value.title || 'ai-image'}.png`;
    link.click();

    showToast('图片下载已开始', 'success');
  };
  
  // 分享选中的图片
  const shareSelectedImage = () => {
    if (!selectedImage.value) return;

    const imageSrc = getSingleImageSrc(selectedImage.value);
    if (!imageSrc) return;

    if (navigator.share) {
      navigator.share({
        title: selectedImage.value.image_title || 'AI图像',
        text: '来看看这个AI作品',
        url: imageSrc
      }).catch(error => console.log('分享失败:', error));
    } else {
      // 降级到复制链接
      const shareText = `来看看这个AI作品：${selectedImage.value.image_title || '无标题'}\n${imageSrc}`;
      navigator.clipboard.writeText(shareText).then(() => {
        alert('链接已复制到剪贴板！');
      }).catch(() => {
        // 最后的降级方案
        prompt('复制链接:', imageSrc);
      });
    }
  };
  
  // 发布选中的图片
  const publishSelectedImage = async () => {
    if (!selectedImage.value) return;

    try {
      // TODO: 实现发布功能
      alert('图片发布功能待实现');
      closeDetailModal();
    } catch (error) {
      console.error('发布图片失败:', error);
      alert('发布失败，请稍后重试');
    }
  };
  
  // 点赞/取消点赞选中的图片
  const toggleLikeSelectedImage = (postId: number, updateLikeCount: (id: number, count: number) => void) => {
    if (!selectedImage.value || !selectedImage.value.id) {
      showToast('图片信息无效', 'error');
      return;
    }

    const userId = currentUserId.value;

    if (!userId) {
      showToast('请先登录', 'error');
      router.push('/login');
      return;
    }

    // 立即更新UI状态（乐观更新）
    if (isLiked.value) {
      // 取消点赞
      isLiked.value = false;
      // 更新本地点赞数
      if (selectedImage.value.likeCount && selectedImage.value.likeCount > 0) {
        selectedImage.value.likeCount--;
      }
      // 同时更新posts列表中的点赞数
      updateLikeCount(postId, (selectedImage.value.likeCount || 0));
      // 记录等待发送的操作
      pendingLikeAction.value = { postId, action: 'unlike' };
    } else {
      // 点赞
      isLiked.value = true;
      // 更新本地点赞数
      const newCount = (selectedImage.value.likeCount || 0) + 1;
      selectedImage.value.likeCount = newCount;
      // 同时更新posts列表中的点赞数
      updateLikeCount(postId, newCount);
      // 记录等待发送的操作
      pendingLikeAction.value = { postId, action: 'like' };
    }
  };
  
  // 收藏/取消收藏选中的图片
  const toggleFavoriteSelectedImage = async () => {
    if (!selectedImage.value || !selectedImage.value.id) {
      showToast('图片信息无效', 'error');
      return;
    }

    const postId = selectedImage.value.id;
    const userId = currentUserId.value;

    if (!userId) {
      showToast('请先登录', 'error');
      router.push('/login');
      return;
    }

    try {
      if (isCollected.value) {
        // 取消收藏
        await imagepostService.removeCollection(postId, userId);
        isCollected.value = false;
        showToast('已取消收藏', 'success');
      } else {
        // 收藏
        await imagepostService.addCollection(postId, userId);
        isCollected.value = true;
        showToast('收藏成功', 'success');
      }
    } catch (error) {
      console.error('收藏操作失败:', error);
      showToast('操作失败，请稍后重试', 'error');
    }
  };
  
  // 画同款功能 - 完善实现
  const handleDrawSameStyle = async (imageData: any) => {
    // 防止重复点击
    if (isNavigating.value) {
      return;
    }

    isNavigating.value = true;

    try {
      // 关闭当前弹窗
      closeDetailModal();

      // 验证必要数据
      if (!imageData) {
        throw new Error('图片数据为空');
      }

      // 将图片参数存储到 sessionStorage 中，供 ImageGeneration 组件读取
      const generationParams = {
        prompt: imageData.image_description || imageData.image_title || imageData.title || '',
        title: imageData.image_title || imageData.title || '画同款作品',
        imageUrl: getSingleImageSrc(imageData) || imageData.image_url || imageData.imageUrl || '',
        timestamp: Date.now(),
        // 添加新参数
        negativePrompt: imageData.negativePrompt,
        steps: imageData.steps,
        cfgScale: imageData.cfgScale,
        modelName: imageData.modelName,
        style: imageData.style,
        width: imageData.width,
        height: imageData.height
      };

      // 验证图片URL是否有效
      if (!generationParams.imageUrl) {
        throw new Error('无法获取图片数据');
      }

      sessionStorage.setItem('sameStyleParams', JSON.stringify(generationParams));

      // 检查认证状态
      const token = localStorage.getItem('token');

      if (!token) {
        // 显示友好的提示信息
        showToast('请先登录后再使用此功能', 'error');
        await router.push('/login');
        return;
      }

      // 执行路由跳转
      await router.push('/image-generation');

      // 显示成功提示
      showToast('已跳转到图像生成页面，参数已自动填充', 'success');

    } catch (error) {
      console.error('画同款功能执行失败:', error);

      // 显示用户友好的错误提示
      const errorMessage = error instanceof Error ? error.message : '操作失败，请稍后重试';
      showToast(errorMessage, 'error');

    } finally {
      // 确保状态重置
      isNavigating.value = false;
    }
  };
  
  // 用做参考图功能
  const handleUseAsReference = async (imageData: any) => {
    // 防止重复点击
    if (isNavigating.value) {
      return;
    }

    isNavigating.value = true;

    try {
      // 关闭当前弹窗
      closeDetailModal();

      // 验证必要数据
      if (!imageData) {
        throw new Error('图片数据为空');
      }

      // 将图片参数存储到 sessionStorage 中，供 ImageGeneration 组件读取
      const referenceParams = {
        referenceImageUrl: getSingleImageSrc(imageData) || imageData.image_url || imageData.imageUrl || '',
        timestamp: Date.now()
      };

      // 验证图片URL是否有效
      if (!referenceParams.referenceImageUrl) {
        throw new Error('无法获取图片数据');
      }

      sessionStorage.setItem('referenceImageParams', JSON.stringify(referenceParams));

      // 检查认证状态
      const token = localStorage.getItem('token');

      if (!token) {
        // 显示友好的提示信息
        showToast('请先登录后再使用此功能', 'error');
        await router.push('/login');
        return;
      }

      // 执行路由跳转
      await router.push('/image-generation');

      // 显示成功提示
      showToast('已跳转到图像生成页面，参考图已设置', 'success');

    } catch (error) {
      console.error('用做参考图功能执行失败:', error);

      // 显示用户友好的错误提示
      const errorMessage = error instanceof Error ? error.message : '操作失败，请稍后重试';
      showToast(errorMessage, 'error');

    } finally {
      // 确保状态重置
      isNavigating.value = false;
    }
  };
  
  // 处理评论数变化
  const handleCommentCountChanged = (count: number) => {
    if (selectedImage.value) {
      selectedImage.value.commentCount = count;
    }
  };
  
  // 添加通用的Toast通知函数
  const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
    // 创建toast元素
    const toast = document.createElement('div');
    toast.className = `fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 ${type === 'success' ? 'bg-green-500 text-white' :
      type === 'error' ? 'bg-red-500 text-white' :
        'bg-blue-500 text-white'
      }`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // 动画显示
    setTimeout(() => {
      toast.style.opacity = '1';
      toast.style.transform = 'translate(-50%, 0)';
    }, 10);

    // 2-3秒后自动消失
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translate(-50%, -20px)';
      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
      }, 300);
    }, type === 'error' ? 4000 : 2500);
  };
  
  // 清理函数
  const cleanup = () => {
    // 清理图片缓存，释放对象URL
    imageCache.forEach((src) => {
      if (src.startsWith('blob:')) {
        URL.revokeObjectURL(src);
      }
    });
    imageCache.clear();
    
    // 重置状态
    isNavigating.value = false;
  };
  
  return {
    showDetailModal,
    selectedImage,
    imageList,
    currentIndex,
    hasMoreImages,
    isLiked,
    isCollected,
    currentUserId,
    isLiking,
    isNavigating,
    convertToImagePost,
    getSingleImageSrc,
    preloadImages,
    showImageDetail,
    goToPrevImage,
    goToNextImage,
    setLoadMoreImagesCallback,
    setCheckHasMoreCallback,
    updateImageList,
    updateHasMoreImages,
    closeDetailModal,
    downloadSelectedImage,
    shareSelectedImage,
    publishSelectedImage,
    toggleLikeSelectedImage,
    toggleFavoriteSelectedImage,
    handleDrawSameStyle,
    handleUseAsReference,
    handleCommentCountChanged,
    showToast,
    cleanup
  };
}