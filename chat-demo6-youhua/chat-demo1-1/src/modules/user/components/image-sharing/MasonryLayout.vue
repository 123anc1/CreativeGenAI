<template>
  <div ref="masonryContainer" class="masonry-container">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';

// 定义props
const props = defineProps<{
  items: any[];
}>();

// 定义emits
const emit = defineEmits<{
  (e: 'layout-complete'): void;
}>();

// 瀑布流相关变量
const masonryContainer = ref<HTMLElement | null>(null);
const masonryItems = ref<(Element | HTMLElement | any)[]>([]);
const columnCount = ref(4);
const columnWidth = ref(0);
const columnHeights = ref<number[]>([]);
const layoutComplete = ref(false);

// 计算列数
const calculateColumnCount = () => {
  if (!masonryContainer.value) return;
  
  const containerWidth = masonryContainer.value.offsetWidth;
  let count = 4; // 默认4列
  
  if (containerWidth < 640) {
    count = 1;
  } else if (containerWidth < 900) {
    count = 2;
  } else if (containerWidth < 1200) {
    count = 3;
  }
  
  columnCount.value = count;
  columnWidth.value = (containerWidth - (count - 1) * 16) / count; // 16px gap
};

// 布局瀑布流
const layoutMasonry = () => {
  if (!masonryContainer.value || !masonryItems.value || masonryItems.value.length === 0) return;
  
  // 过滤掉null值，只使用有效的元素并转换为HTMLElement
  const items = masonryItems.value
    .filter((item): item is Element => item !== null)
    .filter((item): item is HTMLElement => item instanceof HTMLElement);
  
  if (items.length === 0) return;
  
  // 重置列高度
  columnHeights.value = new Array(columnCount.value).fill(0);
  
  // 批量处理布局，减少重排
  requestAnimationFrame(() => {
    // 先计算所有项目的高度
    const itemHeights = items.map(item => {
      return item.offsetHeight;
    });
    
    // 然后进行布局
    items.forEach((item, index) => {
      // 找到高度最小的列
      const minHeight = Math.min(...columnHeights.value);
      const minIndex = columnHeights.value.indexOf(minHeight);
      
      // 设置项目位置，使用transform来提高性能
      item.style.position = 'absolute';
      item.style.width = `${columnWidth.value}px`;
      item.style.left = `${minIndex * (columnWidth.value + 16)}px`; // 16px gap
      item.style.top = `${minHeight}px`;
      
      // 更新列高度
      const itemHeight = itemHeights[index];
      columnHeights.value[minIndex] = minHeight + itemHeight + 16; // 16px gap
    });
    
    // 设置容器高度
    if (columnHeights.value.length > 0) {
      const maxHeight = Math.max(...columnHeights.value);
      if (masonryContainer.value) {
        masonryContainer.value.style.height = `${maxHeight}px`;
      }
    }
    
    // 布局完成后显示图片
    layoutComplete.value = true;
    emit('layout-complete');
  });
};

// 初始化瀑布流
const initMasonry = () => {
  if (!masonryContainer.value) return;
  calculateColumnCount();
  columnHeights.value = new Array(columnCount.value).fill(0);
  layoutMasonry();
};

// 处理窗口大小变化 - 添加防抖
let resizeTimer: ReturnType<typeof setTimeout> | null = null;
const handleResize = () => {
  if (resizeTimer) clearTimeout(resizeTimer);
  resizeTimer = setTimeout(() => {
    initMasonry();
  }, 200); // 200ms防抖
};

// 生命周期钩子
onMounted(() => {
  initMasonry();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  if (resizeTimer) {
    clearTimeout(resizeTimer);
    resizeTimer = null;
  }
});

// 监听数据变化
watch(() => props.items, () => {
  // 重置布局完成状态
  layoutComplete.value = false;
  // 重置masonryItems数组
  masonryItems.value = [];
  // 使用requestAnimationFrame确保DOM已更新
  requestAnimationFrame(() => {
    initMasonry();
  });
}, { deep: true });

// 暴露方法和状态
defineExpose({
  initMasonry,
  masonryItems,
  columnWidth,
  layoutComplete
});
</script>

<style scoped>
/* JavaScript瀑布流布局 */
.masonry-container {
  position: relative;
  width: 100%;
  padding: 0 0.5rem;
}

.masonry-item {
  position: absolute;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  opacity: 0;
  visibility: hidden;
}

/* 布局完成后显示图片 */
.masonry-item.opacity-100 {
  opacity: 1;
  visibility: visible;
  transition: opacity 0.3s ease-in-out, visibility 0.3s ease-in-out;
}

/* 悬停效果 */
.masonry-item:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}
</style>