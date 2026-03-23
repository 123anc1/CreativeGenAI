<template>
  <div class="middle-sidebar flex flex-col flex-shrink-0 w-20 min-h-0 border-r border-green-100 dark:border-emerald-900 bg-white/60 dark:bg-gray-900/60 backdrop-blur-sm">
    <h2 class="text-xs font-medium py-3 text-center text-gray-500 dark:text-gray-400 border-b border-green-100 dark:border-emerald-900/50 flex-shrink-0">我的图像</h2>
    
    <!-- 任务进度显示区域 -->
    <div v-if="currentTaskId" class="task-progress-area p-2 border-b border-green-100 dark:border-emerald-900/50 flex-shrink-0">
      <div class="progress-card bg-yellow-50 dark:bg-yellow-900/20 rounded-lg p-2 border border-yellow-200 dark:border-yellow-800">
        <div class="flex flex-col items-center">
          <!-- 进度圆环 -->
          <div class="relative w-12 h-12 mb-1">
            <svg class="w-12 h-12 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="#e5e7eb"
                stroke-width="3"
              />
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="#f59e0b"
                stroke-width="3"
                stroke-dasharray="100"
                :stroke-dashoffset="100 - (taskStatus?.progress || 0)"
                class="transition-all duration-300 ease-in-out"
              />
            </svg>
            <div class="absolute inset-0 flex items-center justify-center">
              <span class="text-xs font-bold text-yellow-700 dark:text-yellow-300">
                {{ taskStatus?.progress || 0 }}%
              </span>
            </div>
          </div>
          <!-- 进度文字 -->
          <div class="text-center">
            <p class="text-[10px] font-medium text-yellow-800 dark:text-yellow-200 truncate w-16">
              {{ taskStatusDescription }}
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="image-list-container flex-1 min-h-0 overflow-y-auto overflow-x-hidden p-2 scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800">
      <div 
        v-for="(image, index) in processedUserImages" 
        :key="index"
        @click="selectUserImage(image)"
        :class="[
          'user-image-item mb-2.5 p-1 rounded-lg cursor-pointer border-2 transition-all duration-200 flex-shrink-0 overflow-hidden',
          selectedUserImage?.imagedata === image.imagedata
            ? 'border-green-500 dark:border-emerald-500 bg-green-50 dark:bg-emerald-900/40 ring-2 ring-green-200 dark:ring-emerald-800 ring-offset-1 dark:ring-offset-gray-900'
            : 'border-green-100 dark:border-emerald-900 hover:border-green-300 dark:hover:border-emerald-600 hover:shadow-md'
        ]"
      >
        <img 
          :src="image.processedImageUrl" 
          :alt="`User image ${index}`"
          class="w-full aspect-square object-cover rounded-md" 
        />
      </div>
      <div v-if="processedUserImages.length === 0 && !currentTaskId" class="flex flex-col items-center justify-center py-8 text-gray-400 dark:text-gray-500 text-xs">
        <span class="mb-1 opacity-60">暂无</span>
        <p class="text-[10px]">生成后显示</p>
      </div>
      <div v-else-if="processedUserImages.length === 0 && currentTaskId" class="flex flex-col items-center justify-center py-8 text-gray-400 dark:text-gray-500 text-xs">
        <span class="mb-1 opacity-60">生成中</span>
        <p class="text-[10px]">请等待任务完成</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

// Props
const props = defineProps<{
  userImages: any[];
  selectedUserImage: any | null;
  currentTaskId: string | null;
  taskStatus: any | null;
}>();

// Emits
const emit = defineEmits<{
  (e: 'select-image', image: any): void;
}>();

// 计算属性：获取当前任务状态描述
const taskStatusDescription = computed(() => {
  if (!props.taskStatus) return '';
  
  switch (props.taskStatus.status) {
    case 'pending':
      return '任务排队中...';
    case 'processing':
      return '正在生成图像...';
    case 'completed':
      return '生成完成';
    case 'failed':
      return '生成失败';
    case 'cancelled':
      return '任务已取消';
    default:
      return '生成中...';
  }
});

// 使用计算属性处理用户图像列表，优化性能
const processedUserImages = computed(() => {
  return props.userImages.map(image => ({
    ...image,
    processedImageUrl: getImageSrc(image)
  }));
});

// 用于缓存处理过的图像URL
const imageCache = new Map();

// 计算属性，处理返回的base64数据或URL
const getImageSrc = (image: any) => {
  if (!image) return '';
  
  // 生成缓存键
  const cacheKey = typeof image === 'object' ? JSON.stringify(image) : image;
  
  // 检查缓存中是否存在
  if (imageCache.has(cacheKey)) {
    return imageCache.get(cacheKey);
  }
  
  // 定义可能的图片URL字段，现在包含了实际的字段名imagedata
  const possibleFields = ['imagedata', 'image_data', 'url', 'imageUrl', 'src', 'picture', 'image_url'];
  
  for (const field of possibleFields) {
    if (image[field]) {
      let imageUrl = image[field];
      
      // 如果是base64编码的图片数据
      if (typeof imageUrl === 'string' && imageUrl.startsWith('data:image')) {
        imageCache.set(cacheKey, imageUrl);
        return imageUrl;
      }
      
      // 如果是相对路径，补全协议和主机名
      if (typeof imageUrl === 'string') {
        // 检查是否是完整的URL
        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
          imageCache.set(cacheKey, imageUrl);
          return imageUrl;
        }
        
        // 检查是否是绝对路径
        if (imageUrl.startsWith('/')) {
          // 检查是否是当前域名下的路径
          const currentOrigin = window.location.origin;
          const fullUrl = currentOrigin + imageUrl;
          imageCache.set(cacheKey, fullUrl);
          return fullUrl;
        }
        
        // 如果是相对路径，尝试构造完整URL
        const currentOrigin = window.location.origin;
        const fullUrl = currentOrigin + '/' + imageUrl;
        imageCache.set(cacheKey, fullUrl);
        return fullUrl;
      }
    }
  }
  
  // 如果没有找到有效的图片字段
  const emptyResult = '';
  imageCache.set(cacheKey, emptyResult);
  return emptyResult;
};

const selectUserImage = (image: any) => {
  emit('select-image', image);
};
</script>

<style scoped>
/* 与 Chat.vue 统一的布局 */
.middle-sidebar {
  min-height: 100vh;
}
</style>