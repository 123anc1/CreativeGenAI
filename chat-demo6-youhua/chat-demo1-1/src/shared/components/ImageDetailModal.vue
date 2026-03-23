<template>
  <div v-if="visible" class="fixed inset-0 bg-white z-50 flex items-center justify-center p-0">
    <div class="w-full h-full overflow-hidden flex flex-col">
      <!-- 内容区域 -->
      <div class="flex flex-col lg:flex-row flex-1 overflow-hidden">
        <!-- 左侧导航栏 -->
        <div class="fixed top-0 left-0 h-screen z-20">
          <UserSidebar
            :conversations="conversations"
            :current-conversation-id="currentConversationId"
            :current-route="'image-sharing'"
            @switch-conversation="switchConversation"
            @create-temporary-conversation="createTemporaryConversation"
          />
        </div>
        <!-- 中间图片区域 -->
        <div class="lg:w-3/5 ml-56 bg-white flex items-center justify-center p-4 lg:p-8 relative">
          <div class="relative max-w-full max-h-full">
            <img v-if="imageData" :src="processImageSrc(imageData)" :alt="imageData.image_title"
              class="max-w-full max-h-[85vh] object-contain" />
          </div>
          <!-- 关闭按钮 -->
          <button @click="$emit('close')"
            class="absolute top-4 right-4 text-black hover:text-gray-700 transition-colors p-2 rounded-lg hover:bg-gray-100 z-10">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
          <!-- 上一张按钮 -->
          <button v-if="canGoPrev" @click="$emit('prev-image')"
            class="absolute left-4 top-1/2 -translate-y-1/2 text-black hover:text-gray-700 transition-colors p-2 rounded-lg hover:bg-gray-100 z-10">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <!-- 下一张按钮 -->
          <button v-if="canGoNext" @click="$emit('next-image')"
            class="absolute right-4 top-1/2 -translate-y-1/2 text-black hover:text-gray-700 transition-colors p-2 rounded-lg hover:bg-gray-100 z-10">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>

        <!-- 右侧信息区域 -->
        <div class="lg:w-2/5 bg-white border-l border-gray-100 overflow-y-auto">
          <div class="p-6 space-y-6">
            <!-- 标题和作者 -->
            <div>
              <h1 class="text-2xl font-bold text-gray-900 mb-2">{{ imageData?.image_title || '无标题' }}</h1>
              <div class="flex items-center text-gray-500 text-sm">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                {{ imageData?.user_name || '匿名用户' }}
              </div>
            </div>

            <!-- 统计信息 -->
            <div class="flex items-center gap-6 py-4 border-y border-gray-100">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <span class="text-gray-600">{{ imageData?.viewcount || 0 }}</span>
              </div>
              <!-- 可点击的点赞按钮 -->
              <button @click="toggleLike" class="flex items-center gap-2 hover:bg-gray-50 px-2 py-1 rounded transition-colors"
                :class="{ 'text-red-500': props.isLiked, 'text-gray-400': !props.isLiked }"
                :disabled="props.isLiking">
                <div v-if="props.isLiking" class="animate-spin rounded-full h-5 w-5 border-2 border-red-500 border-t-transparent"></div>
                <svg v-else class="w-5 h-5" :fill="props.isLiked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <span :class="props.isLiked ? 'text-red-500' : 'text-gray-600'">{{ imageData?.likeCount || 0 }}</span>
              </button>
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <span class="text-gray-600">{{ imageData?.commentCount || 0 }}</span>
              </div>
            </div>

            <!-- 描述 -->
            <div>
              <h3 class="text-sm font-semibold text-gray-900 mb-2">描述</h3>
              <p class="text-gray-600 text-sm leading-relaxed">
                {{ imageData?.image_description || imageData?.image_title || '暂无描述' }}
              </p>
            </div>

            <!-- 生成参数 -->
            <div v-if="hasGenerationParams">
              <h3 class="text-sm font-semibold text-gray-900 mb-3">生成参数</h3>
              <div class="space-y-2 text-sm">
                <div v-if="imageData?.width && imageData?.height" class="flex justify-between">
                  <span class="text-gray-500">尺寸</span>
                  <span class="text-gray-900">{{ imageData.width }} × {{ imageData.height }}</span>
                </div>
                <div v-if="imageData?.modelName" class="flex justify-between">
                  <span class="text-gray-500">模型</span>
                  <span class="text-gray-900">{{ imageData.modelName }}</span>
                </div>
                <div v-if="imageData?.style" class="flex justify-between">
                  <span class="text-gray-500">风格</span>
                  <span class="text-gray-900">{{ imageData.style }}</span>
                </div>
                <div v-if="imageData?.steps" class="flex justify-between">
                  <span class="text-gray-500">迭代步数</span>
                  <span class="text-gray-900">{{ imageData.steps }}</span>
                </div>
                <div v-if="imageData?.cfgScale" class="flex justify-between">
                  <span class="text-gray-500">CFG Scale</span>
                  <span class="text-gray-900">{{ imageData.cfgScale }}</span>
                </div>
              </div>
            </div>

            <!-- 标签 -->
            <div v-if="generatedTags.length > 0">
              <h3 class="text-sm font-semibold text-gray-900 mb-3">标签</h3>
              <div class="flex flex-wrap gap-2">
                <span v-for="tag in generatedTags" :key="tag"
                  class="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-xs">
                  {{ tag }}
                </span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex flex-col gap-3 pt-4">
              <div class="flex gap-3">
                <button @click="drawSameStyle"
                  class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors text-sm font-medium">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                  画同款
                </button>
                <button @click="useAsReference"
                  class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                  </svg>
                  用做参考图
                </button>
              </div>
              <div class="flex gap-3">
                <button @click="$emit('download')"
                  class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  下载
                </button>
                <button @click="toggleFavorite"
                  class="flex-1 flex items-center justify-center gap-2 px-4 py-3 rounded-lg transition-colors text-sm font-medium"
                  :class="props.isCollected ? 'bg-red-50 text-red-600 hover:bg-red-100' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'">
                  <svg class="w-4 h-4" :fill="props.isCollected ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                  </svg>
                  {{ props.isCollected ? '已收藏' : '收藏' }}
                </button>
                <button @click="$emit('share')"
                  class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                  </svg>
                  分享
                </button>
              </div>
            </div>

            <!-- 评论区 -->
            <ImageCommentSection :post-id="imageData?.id" :visible="visible" @comment-count-changed="handleCommentCountChanged" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { ImagePost } from '@/shared/types';

import { Conversation } from '@/shared/types/type';
import ImageCommentSection from './ImageCommentSection.vue';
import UserSidebar from './UserSidebar.vue';

interface Props {
  visible: boolean;
  imageData?: ImagePost | null;
  getImageSrc?: (post: ImagePost) => string;
  isLiked?: boolean;
  isCollected?: boolean;
  isLiking?: boolean;
  imageList?: ImagePost[];
  currentIndex?: number;
  hasMoreImages?: boolean;
}

interface Emits {
  (e: 'close'): void;
  (e: 'download'): void;
  (e: 'share'): void;
  (e: 'favorite'): void;
  (e: 'like'): void;
  (e: 'drawSameStyle', imageData: ImagePost): void;
  (e: 'useAsReference', imageData: ImagePost): void;
  (e: 'comment-count-changed', count: number): void;
  (e: 'prev-image'): void;
  (e: 'next-image'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const canGoPrev = computed(() => {
  return props.imageList && props.imageList.length > 0 && (props.currentIndex || 0) > 0;
});

const canGoNext = computed(() => {
  if (!props.imageList || props.imageList.length === 0) return false;
  const currentIdx = props.currentIndex || 0;
  if (props.hasMoreImages) {
    return true;
  }
  return currentIdx < props.imageList.length - 1;
});

// 导航栏相关状态
const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);

// 导航栏相关方法
const switchConversation = (sessionid: string | number) => {
  currentConversationId.value = String(sessionid);
  // 可以在这里添加切换对话的逻辑
};

const createTemporaryConversation = () => {
  // 创建临时对话的逻辑
};

const processImageSrc = (post: ImagePost) => {
  if (props.getImageSrc) {
    return props.getImageSrc(post);
  }
  return getSingleImageSrc(post);
};

const getSingleImageSrc = (post: ImagePost) => {
  if (post.imageData && typeof post.imageData === 'string' && (post.imageData as string).startsWith('iVBOR')) {
    try {
      const dataUrl = `data:image/png;base64,${post.imageData}`;
      return dataUrl;
    } catch (error) {
      console.error('处理 base64 图像数据失败:', error);
    }
  }

  if (post.imageData && Array.isArray(post.imageData)) {
    try {
      const uint8Array = new Uint8Array(post.imageData);
      const blob = new Blob([uint8Array], { type: 'image/png' });
      return URL.createObjectURL(blob);
    } catch (error) {
      console.error('解码图像数据失败:', error);
    }
  }

  if (post.image_url) {
    return post.image_url;
  }

  return '';
};

const generatedTags = computed(() => {
  if (!props.imageData) return [];

  const tags = ['AI生成'];
  const prompt = (props.imageData.image_description || props.imageData.image_title || '').toLowerCase();

  if (prompt.includes('风景') || prompt.includes('landscape')) tags.push('风景');
  if (prompt.includes('人物') || prompt.includes('portrait')) tags.push('人物');
  if (prompt.includes('动物') || prompt.includes('animal')) tags.push('动物');
  if (prompt.includes('建筑') || prompt.includes('architecture')) tags.push('建筑');
  if (prompt.includes('抽象') || prompt.includes('abstract')) tags.push('抽象');
  if (prompt.includes('写实') || prompt.includes('realistic')) tags.push('写实');

  return tags.slice(0, 5);
});

const hasGenerationParams = computed(() => {
  if (!props.imageData) return false;
  return props.imageData.steps ||
    props.imageData.cfgScale ||
    props.imageData.negativePrompt ||
    props.imageData.width ||
    props.imageData.height ||
    props.imageData.modelName ||
    props.imageData.style;
});

// 画同款功能
const drawSameStyle = () => {
  if (props.imageData) {
    emit('drawSameStyle', props.imageData);
  }
};

// 用做参考图功能
const useAsReference = () => {
  if (props.imageData) {
    emit('useAsReference', props.imageData);
  }
};

// 切换点赞状态
const toggleLike = () => {
  emit('like');
};

// 切换收藏状态
const toggleFavorite = () => {
  emit('favorite');
};

// 处理评论数变化
const handleCommentCountChanged = (count: number) => {
  emit('comment-count-changed', count);
};
</script>
