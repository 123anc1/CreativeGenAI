<template>
  <div
    ref="itemRef"
    class="masonry-item group relative bg-white rounded-md hover:shadow-sm transition-shadow overflow-hidden cursor-pointer"
    :class="{ 'item-visible': imageLoaded && layoutComplete }"
    @click="handleClick"
  >
    <div class="image-area overflow-hidden" :style="imageAreaStyle">
      <!-- 图片加载占位 -->
      <div class="absolute inset-0 flex items-center justify-center bg-gray-100" v-if="!imageLoaded">
        <div class="w-8 h-8 animate-spin rounded-full border-t-2 border-b-2 border-emerald-500"></div>
      </div>
      <img 
        :data-src="imageSrc" 
        :alt="imageTitle" 
        class="w-full h-auto object-contain masonry-img lazy-load" 
        @load="handleImageLoad" 
        @error="handleImageError" 
        referrerpolicy="no-referrer" 
      />
    </div>
    
    <!-- 悬停遮罩 -->
    <div 
      class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center"
    >
      <div class="text-white text-center">
        <svg class="w-6 h-6 mx-auto mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
        <p class="text-xs">查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

// 定义props
const props = defineProps<{
  post: any;
  columnWidth: number;
  layoutComplete: boolean;
  getImageSrc: (post: any) => string;
}>();

// 定义emits
const emit = defineEmits<{
  (e: 'click', post: any): void;
  (e: 'image-loaded'): void;
}>();

// 引用
const itemRef = ref<HTMLElement | null>(null);
const imageLoaded = ref(false);

// 计算属性
const imageTitle = computed(() => props.post.image_title || props.post.title || 'AI图像');

const imageSrc = computed(() => {
  return props.getImageSrc(props.post);
});

const imageAreaStyle = computed(() => {
  // 如果有宽高信息，计算并返回合适的高度
  if (props.post.width && props.post.height && props.post.width > 0 && props.post.height > 0) {
    // 计算宽高比
    const aspectRatio = props.post.width / props.post.height;
    // 根据列宽计算高度
    const calculatedHeight = props.columnWidth / aspectRatio;
    // 确保高度合理，避免过小或过大
    const finalHeight = Math.max(150, Math.min(calculatedHeight, 800));
    return {
      height: `${finalHeight}px`,
      minHeight: '150px',
      maxHeight: '800px'
    };
  }
  // 没有宽高信息时使用默认最小高度
  return {
    minHeight: '150px',
    maxHeight: '800px'
  };
});

// 方法
const handleClick = () => {
  emit('click', props.post);
};

const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.classList.add('loaded');
  imageLoaded.value = true;
  emit('image-loaded');
};

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  // 不设置占位图，保持空白
  img.alt = '图片加载失败';
  imageLoaded.value = true;
};

// 暴露引用
defineExpose({
  itemRef
});
</script>

<style scoped>
.image-area {
  position: relative;
  min-height: 150px;
  background-color: #fafafa;
  overflow: hidden;
  transition: background-color 0.3s ease;
  border-radius: 0;
}

.masonry-img {
  width: 100%;
  height: auto;
  object-fit: cover;
  transition: transform 0.3s ease;
  background-color: #ffffff;
  opacity: 0;
  position: relative;
  z-index: 2;
  border-radius: 0;
}

.lazy-load {
  opacity: 0;
}

.image-area .masonry-img.loaded {
  opacity: 1;
}

.masonry-item {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.masonry-item.item-visible {
  opacity: 1;
}

.masonry-item:hover .masonry-img {
  transform: scale(1.03);
}
</style>