import { ref, onMounted, onUnmounted, Ref } from 'vue';

export function useMasonryLayout(containerRef?: Ref<HTMLElement | null>) {
  // 瀑布流相关变量
  const masonryContainer = containerRef || ref<HTMLElement | null>(null);
  const masonryItems = ref<(Element | HTMLElement | any)[]>([]);
  const columnCount = ref(4);
  const columnWidth = ref(0);
  const columnHeights = ref<number[]>([]);
  const imageLoadCount = ref(0);
  const layoutComplete = ref(false);
  
  // 定时器
  let layoutTimer: ReturnType<typeof setTimeout> | null = null;
  let rafId: number | null = null;
  let resizeTimer: ReturnType<typeof setTimeout> | null = null;
  
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
      // 计算所有项目的高度和位置
      const itemPositions = items.map((item) => {
        // 找到高度最小的列
        let minHeight = columnHeights.value[0];
        let minIndex = 0;
        for (let i = 1; i < columnHeights.value.length; i++) {
          if (columnHeights.value[i] < minHeight) {
            minHeight = columnHeights.value[i];
            minIndex = i;
          }
        }
        
        // 计算位置
        const left = minIndex * (columnWidth.value + 16);
        const top = minHeight;
        
        // 更新列高度
        columnHeights.value[minIndex] = minHeight + item.offsetHeight + 16;
        
        return { item, left, top };
      });
      
      // 一次性应用所有位置，减少重排
      itemPositions.forEach(({ item, left, top }) => {
        item.style.position = 'absolute';
        item.style.width = `${columnWidth.value}px`;
        item.style.left = `${left}px`;
        item.style.top = `${top}px`;
      });
      
      // 设置容器高度
      if (columnHeights.value.length > 0) {
        // 计算最大高度
        let maxHeight = columnHeights.value[0];
        for (let i = 1; i < columnHeights.value.length; i++) {
          if (columnHeights.value[i] > maxHeight) {
            maxHeight = columnHeights.value[i];
          }
        }
        
        if (masonryContainer.value) {
          // 移除过渡动画，避免频繁高度变化导致闪烁
          masonryContainer.value.style.transition = 'none';
          masonryContainer.value.style.height = `${maxHeight}px`;
          
          // 强制触发滚动容器的高度更新
          const parent = masonryContainer.value.parentElement;
          if (parent) {
            parent.style.height = 'auto';
            parent.offsetHeight;
            parent.style.height = '';
          }
        }
      }
      
      // 布局完成后立即显示图片
      layoutComplete.value = true;
    });
  };
  
  // 初始化瀑布流
  const initMasonry = () => {
    if (!masonryContainer.value) return;
    calculateColumnCount();
    // 始终重置列高度，确保布局正确
    columnHeights.value = new Array(columnCount.value).fill(0);
    layoutMasonry();
  };
  
  // 图片加载完成回调 - 优化性能
  const onImageLoad = () => {
    imageLoadCount.value++;
    
    // 移除防抖，直接更新布局，避免白条闪烁
    if (layoutTimer) clearTimeout(layoutTimer);
    
    // 使用requestAnimationFrame来优化布局更新
    if (rafId) cancelAnimationFrame(rafId);
    rafId = requestAnimationFrame(() => {
      layoutMasonry();
    });
  };
  
  // 处理窗口大小变化 - 添加防抖
  const handleResize = () => {
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = setTimeout(() => {
      initMasonry();
    }, 200); // 200ms防抖
  };
  
  // 清理函数
  const cleanup = () => {
    // 清理定时器
    if (layoutTimer) {
      clearTimeout(layoutTimer);
      layoutTimer = null;
    }
    
    if (resizeTimer) {
      clearTimeout(resizeTimer);
      resizeTimer = null;
    }
    
    // 清理requestAnimationFrame
    if (rafId) {
      cancelAnimationFrame(rafId);
      rafId = null;
    }
    
    // 移除事件监听器
    window.removeEventListener('resize', handleResize);
  };
  
  // 生命周期钩子
  onMounted(() => {
    window.addEventListener('resize', handleResize);
  });
  
  onUnmounted(() => {
    cleanup();
  });
  
  return {
    masonryContainer,
    masonryItems,
    columnCount,
    columnWidth,
    layoutComplete,
    initMasonry,
    onImageLoad,
    handleResize,
    cleanup
  };
}