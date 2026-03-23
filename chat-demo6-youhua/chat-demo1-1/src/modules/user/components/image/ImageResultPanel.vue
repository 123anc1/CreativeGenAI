<template>
  <div class="left-panel flex-1 min-w-0 min-h-0 p-6 overflow-y-auto overflow-x-hidden scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800 bg-white/60 dark:bg-gray-900/60 backdrop-blur-sm border-r border-green-100 dark:border-emerald-900">
    <div class="current-result-container bg-white/80 dark:bg-gray-800/80 rounded-xl shadow-lg border border-green-100 dark:border-emerald-900 p-4 flex flex-col min-h-[70vh]">
      <!-- 有图：图片区 + 信息区 -->
      <template v-if="latestGeneratedImage">
        <div class="current-image-display flex-1 min-h-0 flex items-center justify-center p-2">
          <img 
            :src="getImageSrc(latestGeneratedImage)" 
            :alt="`Latest generated image`"
            class="max-w-full max-h-[55vh] w-auto object-contain rounded-lg shadow-md ring-1 ring-green-100 dark:ring-emerald-900/50" 
          />
        </div>
        <div class="image-info flex-shrink-0 w-full mt-4 space-y-3 border-t border-green-100 dark:border-emerald-900/50 pt-4">
          <p class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5 text-green-500 dark:text-emerald-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            生成时间：{{ formatDate(latestGeneratedImage.timestamp) }}
          </p>
          <div class="prompt-card rounded-lg border border-green-100 dark:border-emerald-900/50 bg-green-50/50 dark:bg-emerald-900/20 overflow-hidden">
            <div class="px-3 py-2 border-b border-green-100 dark:border-emerald-900/50 bg-white/50 dark:bg-gray-800/50">
              <span class="text-xs font-semibold text-green-700 dark:text-emerald-300">提示词 (Prompt)</span>
            </div>
            <div class="prompt-text px-3 py-2.5 text-sm text-gray-700 dark:text-gray-300 leading-relaxed break-words max-h-24 overflow-y-auto scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-transparent">
              {{ latestGeneratedImage.prompt || '—' }}
            </div>
          </div>
          <div class="actions flex flex-wrap gap-2">
            <button @click="downloadImage(latestGeneratedImage)" class="inline-flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200 text-green-700 dark:text-emerald-300 bg-green-50 dark:bg-emerald-900/30 hover:bg-green-100 dark:hover:bg-emerald-900/50 border border-green-200 dark:border-emerald-800 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
              下载
            </button>
            <button @click="setAsPromptReference" class="inline-flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200 text-emerald-700 dark:text-green-300 bg-emerald-50 dark:bg-green-900/30 hover:bg-emerald-100 dark:hover:bg-green-900/50 border border-emerald-200 dark:border-green-800 focus:outline-none focus:ring-2 focus:ring-emerald-300 dark:focus:ring-green-700">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
              设为参考图
            </button>
          </div>
        </div>
      </template>
      
      <!-- 加载中 -->
      <div v-else-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[50vh]">
        <div class="spinner"></div>
        <p class="mt-4 text-lg text-gray-600 dark:text-gray-400">正在生成图像中...</p>
        <p class="text-sm text-gray-500 dark:text-gray-500 mt-2">请耐心等待，这可能需要一些时间</p>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="flex-1 flex flex-col items-center justify-center min-h-[50vh] text-gray-500 dark:text-gray-400">
        <div class="w-16 h-16 rounded-full bg-green-100/50 dark:bg-emerald-900/30 flex items-center justify-center mb-4">
          <svg class="w-8 h-8 text-green-400 dark:text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14"/></svg>
        </div>
        <p class="font-medium">暂无生成的图像</p>
        <p class="text-sm mt-2 text-gray-400 dark:text-gray-500">请在右侧设置参数并生成图像</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

// Props
const props = defineProps<{
  latestGeneratedImage: any | null;
  loading: boolean;
}>();

// Emits
const emit = defineEmits<{
  (e: 'set-as-reference', image: any): void;
}>();

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

const formatDate = (timestamp: string) => {
  return new Date(timestamp).toLocaleString('zh-CN');
};

const downloadImage = (image: any) => {
  if (!image.url) return;

  // 创建一个临时的a标签用于下载
  const a = document.createElement('a');
  
  a.href = image.url;
  const fileName = image.url.split('/').pop()?.split('?')[0] || `generated-image-${Date.now()}.png`;
  a.download = fileName;
  
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};

const setAsPromptReference = () => {
  if (props.latestGeneratedImage) {
    emit('set-as-reference', props.latestGeneratedImage);
  }
};
</script>

<style scoped>
/* 与 Chat.vue 统一的布局 */
.left-panel {
  min-height: 100vh;
}

/* 加载动画 */
.spinner {
  width: 48px;
  height: 48px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 小型加载动画 */
.spinner-small {
  width: 24px;
  height: 24px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
</style>