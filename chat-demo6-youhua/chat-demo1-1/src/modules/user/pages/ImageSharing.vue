<template>
  <div class="flex h-screen bg-white">
    <!-- 左侧全局导航栏 -->
    <UserSidebar mode="compact" :current-route="currentRoute" />

    <!-- 主体内容区域 -->
    <div class="flex-1 overflow-hidden flex flex-col min-w-0">
      <!-- 顶部控制栏 -->
      <div class="bg-white px-4 py-3 flex items-center justify-between">
        <SearchBar :search-title="searchTitle" @search="handleSearch" />
      </div>

      <!-- 主内容区域 -->
      <div class="flex-1 overflow-hidden flex flex-col bg-white">
        <!-- 错误提示 -->
        <ErrorMessage :error="error" @retry="retryLoad" />

        <!-- 固定高度的滚动容器 -->
        <div ref="scrollContainer" class="flex-1 overflow-y-auto p-2 bg-white scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100">
          <!-- 内容容器 -->
          <div class="relative rounded-t-lg overflow-hidden">
            <!-- 加载状态（首次加载） -->
            <div v-if="loading && posts.length === 0" class="h-64 flex items-center justify-center">
              <div class="flex flex-col items-center">
                <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-emerald-500 mb-3"></div>
                <p class="text-sm text-gray-500">加载图片中...</p>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else-if="!loading && posts.length === 0 && !error"
              class="h-64 flex items-center justify-center p-8">
              <div class="text-center">
                <div class="w-16 h-16 mx-auto mb-4 text-gray-400">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <p class="text-gray-500 mb-2">还没有图片</p>
                <p class="text-sm text-gray-400">上传你的第一张AI创作</p>
              </div>
            </div>

            <!-- 图片列表 -->
            <template v-else>
              <!-- 瀑布流图片网格 -->
              <div v-if="posts.length > 0" ref="masonryContainer" class="masonry-container">
                <ImageItem
                  v-for="post in posts"
                  :key="post.id"
                  :post="post"
                  :column-width="columnWidth"
                  :layout-complete="layoutComplete"
                  :get-image-src="getSingleImageSrc"
                  @click="showImageDetail(post, posts)"
                  @image-loaded="onImageLoad"
                />
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- 图片详情弹窗组件 -->
      <ImageDetailModal 
        :visible="showDetailModal" 
        :image-data="selectedImage" 
        :image-list="imageList"
        :current-index="currentIndex"
        :has-more-images="hasMoreImages"
        @close="closeDetailModal"
        @download="downloadSelectedImage" 
        @share="shareSelectedImage" 
        @publish="publishSelectedImage"
        @favorite="toggleFavoriteSelectedImage" 
        @like="() => toggleLikeSelectedImage(selectedImage?.id || 0, updatePostLikeCount)" 
        @draw-same-style="handleDrawSameStyle"
        @use-as-reference="handleUseAsReference" 
        :get-image-src="getSingleImageSrc" 
        :is-liked="isLiked"
        :is-collected="isCollected" 
        :is-liking="isLiking" 
        @comment-count-changed="handleCommentCountChanged"
        @prev-image="goToPrevImage"
        @next-image="goToNextImage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import ImageDetailModal from '@/shared/components/ImageDetailModal.vue';
import UserSidebar from '@/shared/components/UserSidebar.vue';

// 导入自定义组件
import ImageItem from '@/modules/user/components/image-sharing/ImageItem.vue';
import SearchBar from '@/modules/user/components/image-sharing/SearchBar.vue';
import ErrorMessage from '@/modules/user/components/image-sharing/ErrorMessage.vue';

// 导入组合式函数
import { useImageData } from '@/modules/user/composables/useImageData';
import { useMasonryLayout } from '@/modules/user/composables/useMasonryLayout';
import { useImageActions } from '@/modules/user/composables/useImageActions';



// 当前路由状态
const currentRoute = ref('image-sharing');

// 容器引用
// 滚动容器
const scrollContainer = ref<HTMLElement | null>(null);
// 瀑布流容器
const masonryContainer = ref<HTMLElement | null>(null);

// 组合式函数
const {
  posts,
  loading,
  error,
  searchTitle,
  allLoaded,
  handleSearch,
  retryLoad,
  loadMore,
  checkAuthAndLoad
} = useImageData();

const {
  columnWidth,
  layoutComplete,
  onImageLoad,
  handleResize,
  initMasonry,
  masonryItems
} = useMasonryLayout(masonryContainer);

const {
  showDetailModal,
  selectedImage,
  imageList,
  currentIndex,
  isLiked,
  isCollected,
  isLiking,
  getSingleImageSrc,
  showImageDetail,
  goToPrevImage,
  goToNextImage,
  setLoadMoreImagesCallback,
  setCheckHasMoreCallback,
  updateImageList,
  hasMoreImages,
  closeDetailModal,
  downloadSelectedImage,
  shareSelectedImage,
  publishSelectedImage,
  toggleLikeSelectedImage,
  toggleFavoriteSelectedImage,
  handleDrawSameStyle,
  handleUseAsReference,
  handleCommentCountChanged,
  preloadImages,
  cleanup
} = useImageActions();

// 简化滚动处理 - 更直接的实现
let scrollTimer: ReturnType<typeof setTimeout> | null = null;
const handleScroll = () => {
  if (!scrollContainer.value) return;

  const { scrollTop, scrollHeight, clientHeight } = scrollContainer.value;
  // 当滚动到页面一半时就开始加载新数据，预留加载时间
  const scrollPercentage = scrollTop / (scrollHeight - clientHeight);
  
  // 当滚动超过50%时开始加载
  if (scrollPercentage > 0.5 && !loading.value && !allLoaded.value) {
    if (scrollTimer) clearTimeout(scrollTimer);

    scrollTimer = setTimeout(() => {
      loadMore();
    }, 100); // 100ms 防抖
  }
};

// 初始化懒加载
const initLazyLoad = () => {
  if ('IntersectionObserver' in window) {
    // 简化懒加载实现，减少延迟
    const lazyLoadObserver = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = entry.target as HTMLImageElement;
          const src = img.getAttribute('data-src');
          if (src) {
            // 立即加载图片，不使用setTimeout延迟
            img.onload = () => {
              img.classList.remove('lazy-load');
            };
            img.onerror = () => {
              img.classList.remove('lazy-load');
            };
            img.src = src;
            lazyLoadObserver.unobserve(img);
          }
        }
      });
    }, {
      root: scrollContainer.value,
      rootMargin: '300px', // 提前300px开始加载，平衡性能和体验
      threshold: 0.01 // 只要有1%的区域可见就开始加载
    });

    // 观察所有懒加载图片
    document.querySelectorAll('.lazy-load').forEach((img) => {
      lazyLoadObserver.observe(img as Element);
    });
  } else {
    // 降级方案：直接加载所有图片
    document.querySelectorAll('.lazy-load').forEach((img) => {
      const src = (img as HTMLImageElement).getAttribute('data-src');
      if (src) {
        const imgElement = img as HTMLImageElement;
        // 立即加载，不使用setTimeout
        imgElement.onload = () => {
          imgElement.classList.remove('lazy-load');
        };
        imgElement.onerror = () => {
          imgElement.classList.remove('lazy-load');
        };
        imgElement.src = src;
      }
    });
  }
};

// 更新posts列表中对应帖子的点赞数
const updatePostLikeCount = (postId: number, likeCount: number) => {
  const post = posts.value.find(p => p.id === postId);
  if (post) {
    post.likeCount = likeCount;
  }
};

// 生命周期钩子
onMounted(() => {
  currentRoute.value = 'image-sharing';
  
  // 设置加载更多图片的回调函数
  setLoadMoreImagesCallback(async () => {
    await loadMore();
  });
  
  // 设置检查是否还有更多图片的回调函数
  setCheckHasMoreCallback(() => {
    return !allLoaded.value;
  });
  
  // 检查认证状态并加载数据
  checkAuthAndLoad();
  
  // 100毫秒后再检查一次，确保 localStorage 已经更新
  setTimeout(checkAuthAndLoad, 100);

  // 延迟绑定滚动事件，确保DOM完全渲染
  setTimeout(() => {
    if (scrollContainer.value) {
      // 使用passive事件监听器提高滚动性能
      scrollContainer.value.addEventListener('scroll', handleScroll, { passive: true });
    }
  }, 100);

  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize);
});

// 监听数据变化
let watchTimer: ReturnType<typeof setTimeout> | null = null;
watch(posts, (newPosts) => {
  // 数据获取后立即预加载图片，不等待DOM更新
  if (newPosts && newPosts.length > 0) {
    preloadImages(newPosts);
    if (showDetailModal.value) {
      updateImageList(newPosts);
    }
  }
  
  // 防抖处理，避免频繁触发布局更新
  if (watchTimer) clearTimeout(watchTimer);
  watchTimer = setTimeout(() => {
    requestAnimationFrame(() => {
      // 收集瀑布流项目
      if (masonryContainer.value) {
        const items = Array.from(masonryContainer.value.querySelectorAll('.masonry-item'));
        masonryItems.value = items;
        // 初始化瀑布流布局
        initMasonry();
      }
      // 初始化懒加载
      initLazyLoad();
    });
  }, 150);
}, { deep: true, immediate: true });

onUnmounted(() => {
  // 彻底清理所有副作用
  if (scrollContainer.value) {
    scrollContainer.value.removeEventListener('scroll', handleScroll);
  }

  if (scrollTimer) {
    clearTimeout(scrollTimer);
    scrollTimer = null;
  }

  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize);

  // 清理图片操作相关资源
  cleanup();
});
</script>

<style scoped>
/* 样式已移至各个组件中 */
</style>