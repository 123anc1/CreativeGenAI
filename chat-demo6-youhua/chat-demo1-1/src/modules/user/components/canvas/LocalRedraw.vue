<template>
  <div v-if="isActive" class="local-redraw-container absolute inset-0">
    <!-- maskCanvas - 绘制mask -->
    <canvas
      ref="maskCanvas"
      class="mask-canvas absolute"
      :style="canvasStyle"
      @mousedown="startDrawing"
      @mousemove="handleMouseMove"
      @mouseup="stopDrawing"
      @mouseleave="stopDrawing"
      @touchstart="startDrawing"
      @touchmove="handleTouchMove"
      @touchend="stopDrawing"
    ></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue';

interface DrawPoint {
  x: number;
  y: number;
}

const props = withDefaults(defineProps<{
  isActive: boolean;
  brushSize?: number;
  canvasContainerRef: HTMLElement | null;
  layerId: string | null;
  layerImageUrl: string | null;
  drawPoints?: DrawPoint[];
  canvasScale?: number;
}>(), {
  brushSize: 20,
  drawPoints: () => [],
  canvasScale: 1
});

const emit = defineEmits<{
  'update:isActive': [value: boolean];
  'update:drawPoints': [points: DrawPoint[]];
  'complete': [maskBlob: Blob];
  'cancel': [];
}>();

const maskCanvas = ref<HTMLCanvasElement | null>(null);

const maskCtx = ref<CanvasRenderingContext2D | null>(null);

const isDrawing = ref(false);
const currentDrawPoints = ref<DrawPoint[]>([]);
const localBrushSize = computed(() => props.brushSize);
const imagePosition = ref({ x: 0, y: 0, width: 0, height: 0 });
const lastStrokePoint = ref<DrawPoint | null>(null);
const drawHistory = ref<DrawPoint[][]>([]);

const canvasStyle = computed(() => {
  const style: Record<string, string | number> = {
    left: `${imagePosition.value.x}px`,
    top: `${imagePosition.value.y}px`,
    width: `${imagePosition.value.width}px`,
    height: `${imagePosition.value.height}px`,
    pointerEvents: 'auto' as const
  };
  return style;
});

const currentImageSize = ref({ width: 0, height: 0 });
const previousImageSize = ref({ width: 0, height: 0 });

const checkImageSizeChanged = (): boolean => {
  const layerElement = document.querySelector(`.layer-content.selected`) as HTMLElement | null;
  if (!layerElement) return false;

  const rect = layerElement.getBoundingClientRect();
  const displayWidth = rect.width / props.canvasScale;
  const displayHeight = rect.height / props.canvasScale;

  const widthChanged = displayWidth !== currentImageSize.value.width;
  const heightChanged = displayHeight !== currentImageSize.value.height;

  console.log('checkImageSizeChanged - 当前显示尺寸:', rect.width, 'x', rect.height);
  console.log('checkImageSizeChanged - 原始尺寸:', displayWidth, 'x', displayHeight);
  console.log('checkImageSizeChanged - canvasScale:', props.canvasScale);

  if (widthChanged || heightChanged) {
    previousImageSize.value = { width: currentImageSize.value.width, height: currentImageSize.value.height };
    currentImageSize.value = { width: displayWidth, height: displayHeight };
    console.log('checkImageSizeChanged - 尺寸发生变化');
    return true;
  }
  return false;
};

const remapDrawPoints = () => {
  const scaleX = previousImageSize.value.width > 0
    ? currentImageSize.value.width / previousImageSize.value.width
    : 1;
  const scaleY = previousImageSize.value.height > 0
    ? currentImageSize.value.height / previousImageSize.value.height
    : 1;

  currentDrawPoints.value = currentDrawPoints.value.map(point => ({
    x: point.x * scaleX,
    y: point.y * scaleY
  }));

  drawHistory.value = drawHistory.value.map(stroke =>
    stroke.map(point => ({
      x: point.x * scaleX,
      y: point.y * scaleY
    }))
  );

  if (lastStrokePoint.value) {
    lastStrokePoint.value = {
      x: lastStrokePoint.value.x * scaleX,
      y: lastStrokePoint.value.y * scaleY
    };
  }

  emit('update:drawPoints', currentDrawPoints.value);
};

const redrawAllPoints = () => {
  if (!maskCtx.value) return;

  drawHistory.value.forEach(stroke => {
    if (stroke.length === 0) return;
    maskCtx.value!.beginPath();
    stroke.forEach((point, index) => {
      if (index === 0) {
        maskCtx.value?.moveTo(point.x, point.y);
      } else {
        maskCtx.value?.lineTo(point.x, point.y);
      }
    });
    maskCtx.value!.stroke();
  });

  if (currentDrawPoints.value.length > 0) {
    maskCtx.value.beginPath();
    currentDrawPoints.value.forEach((point, index) => {
      if (index === 0) {
        maskCtx.value?.moveTo(point.x, point.y);
      } else {
        maskCtx.value?.lineTo(point.x, point.y);
      }
    });
    maskCtx.value.stroke();
  }
};

const initCanvases = () => {
  console.log('initCanvases - 开始初始化canvas');
  console.log('initCanvases - canvasScale:', props.canvasScale);

  console.log('initCanvases - maskCanvas:', maskCanvas.value);
  
  if (!maskCanvas.value) {
    console.log('initCanvases - canvas元素不存在，退出');
    return;
  }
  
  const layerElement = document.querySelector(`.layer-content.selected`) as HTMLElement | null;
  console.log('initCanvases - 选中图层元素:', layerElement);
  
  if (layerElement) {
    const rect = layerElement.getBoundingClientRect();
    console.log('initCanvases - 图层显示尺寸:', rect.width, 'x', rect.height);
    
    // 将显示尺寸转换为原始尺寸（考虑canvas缩放）
    const originalWidth = rect.width / props.canvasScale;
    const originalHeight = rect.height / props.canvasScale;
    console.log('initCanvases - 图层原始尺寸:', originalWidth, 'x', originalHeight);
    
    // 获取容器元素
    const containerElement = document.querySelector('.local-redraw-container') as HTMLElement | null;
    let relativeX = 0;
    let relativeY = 0;
    
    if (containerElement) {
      const containerRect = containerElement.getBoundingClientRect();
      // 容器位置也需要转换为原始尺寸
      relativeX = (rect.left - containerRect.left) / props.canvasScale;
      relativeY = (rect.top - containerRect.top) / props.canvasScale;
    }
    
    // 使用原始尺寸更新imagePosition
    imagePosition.value = {
      x: relativeX,
      y: relativeY,
      width: originalWidth,
      height: originalHeight
    };
    console.log('initCanvases - 更新imagePosition (原始尺寸):', imagePosition.value);
    
    // 使用原始尺寸初始化maskCanvas
    maskCanvas.value.width = originalWidth;
    maskCanvas.value.height = originalHeight;
    maskCtx.value = maskCanvas.value.getContext('2d');
    console.log('initCanvases - maskCanvas尺寸 (原始尺寸):', maskCanvas.value.width, 'x', maskCanvas.value.height);
    console.log('initCanvases - maskCtx:', maskCtx.value);
    
    if (maskCtx.value) {
      maskCtx.value.lineCap = 'round';
      maskCtx.value.lineJoin = 'round';
      maskCtx.value.lineWidth = props.brushSize;
      maskCtx.value.globalAlpha = 0.5;
      maskCtx.value.strokeStyle = '#6366f1';
      maskCtx.value.fillStyle = '#6366f1';
      console.log('initCanvases - maskCtx配置完成，brushSize:', props.brushSize);
    }
  
    // 调试：检查canvas的实际位置和大小
    nextTick(() => {
      if (maskCanvas.value) {
        const maskRect = maskCanvas.value.getBoundingClientRect();
        console.log('initCanvases - maskCanvas实际位置:', maskRect);
      }
    });
  } else {
    console.log('initCanvases - 未找到选中图层元素');
  }
};

const clearCanvases = () => {

  if (maskCanvas.value && maskCtx.value) {
    const canvas = maskCanvas.value;
    maskCtx.value.clearRect(0, 0, canvas.width, canvas.height);
  }

  currentDrawPoints.value = [];
  lastStrokePoint.value = null;
  drawHistory.value = [];
  imagePosition.value = { x: 0, y: 0, width: 0, height: 0 };
  currentImageSize.value = { width: 0, height: 0 };
  previousImageSize.value = { width: 0, height: 0 };
  emit('update:drawPoints', []);
};

const updateCursorPosition = (e: MouseEvent | TouchEvent) => {
  let clientX: number, clientY: number;
  
  if (e instanceof TouchEvent) {
    clientX = e.touches[0].clientX;
    clientY = e.touches[0].clientY;
  } else {
    clientX = e.clientX;
    clientY = e.clientY;
  }
  
  // 更新CSS变量以显示自定义光标
  const canvasWrapper = document.querySelector('.canvas-wrapper') as HTMLElement | null;
  if (canvasWrapper) {
    canvasWrapper.style.setProperty('--mouse-x', `${clientX}px`);
    canvasWrapper.style.setProperty('--mouse-y', `${clientY}px`);
  }
};

const startDrawing = (e: MouseEvent | TouchEvent) => {
  console.log('startDrawing - 开始绘制');
  console.log('startDrawing - isDrawing:', isDrawing.value);
  console.log('startDrawing - imagePosition:', imagePosition.value);
  console.log('startDrawing - canvasScale:', props.canvasScale);

  e.preventDefault();
  e.stopPropagation();

  isDrawing.value = true;

  if (checkImageSizeChanged()) {
    console.log('startDrawing - 图像尺寸发生变化，重新映射坐标并重绘');
    remapDrawPoints();
    initCanvases();
    redrawAllPoints();
  } else if (!maskCtx.value) {
    initCanvases();
  }

  updateCursorPosition(e);

  let clientX: number, clientY: number;

  if (e instanceof TouchEvent) {
    clientX = e.touches[0].clientX;
    clientY = e.touches[0].clientY;
  } else {
    clientX = e.clientX;
    clientY = e.clientY;
  }

  console.log('startDrawing - 鼠标坐标:', clientX, clientY);

  let x = 0, y = 0;
  if (maskCanvas.value) {
    const rect = maskCanvas.value.getBoundingClientRect();
    x = (clientX - rect.left) / props.canvasScale;
    y = (clientY - rect.top) / props.canvasScale;
  }

  console.log('startDrawing - 相对canvas坐标:', x, y);
  console.log('startDrawing - 图像范围检查:', x >= 0 && x <= imagePosition.value.width && y >= 0 && y <= imagePosition.value.height);

  x = Math.max(0, Math.min(x, imagePosition.value.width));
  y = Math.max(0, Math.min(y, imagePosition.value.height));

  const newPoint = { x, y };

  currentDrawPoints.value = [newPoint];
  lastStrokePoint.value = newPoint;
  emit('update:drawPoints', currentDrawPoints.value);
  console.log('startDrawing - 开始绘制路径，点数:', currentDrawPoints.value.length);

  if (maskCtx.value) {
    maskCtx.value.beginPath();
    maskCtx.value.moveTo(x, y);
    console.log('startDrawing - maskCtx路径已创建');
  } else {
    console.log('startDrawing - maskCtx不存在');
  }
};

const draw = (e: MouseEvent | TouchEvent) => {
  console.log('draw - 绘制中');
  console.log('draw - isDrawing:', isDrawing.value);
  console.log('draw - canvasScale:', props.canvasScale);
  
  if (!isDrawing.value) {
    console.log('draw - 不在绘制状态，退出');
    return;
  }
  
  updateCursorPosition(e);
  
  let clientX: number, clientY: number;
  
  if (e instanceof TouchEvent) {
    clientX = e.touches[0].clientX;
    clientY = e.touches[0].clientY;
  } else {
    clientX = e.clientX;
    clientY = e.clientY;
  }
  
  console.log('draw - 鼠标坐标:', clientX, clientY);
  
  // 计算相对于canvas的坐标
  let x = 0, y = 0;
  if (maskCanvas.value) {
    const rect = maskCanvas.value.getBoundingClientRect();
    x = (clientX - rect.left) / props.canvasScale;
    y = (clientY - rect.top) / props.canvasScale;
  }
  
  console.log('draw - 相对canvas坐标:', x, y);
  
  // 确保坐标在图像范围内
  x = Math.max(0, Math.min(x, imagePosition.value.width));
  y = Math.max(0, Math.min(y, imagePosition.value.height));
  
  console.log('draw - 裁剪后坐标:', x, y);
  
  const newPoint = { x, y };
  
  // 叠加绘制：只绘制从上一个点到当前点的线段
  if (maskCtx.value && lastStrokePoint.value) {
    maskCtx.value.beginPath();
    maskCtx.value.moveTo(lastStrokePoint.value.x, lastStrokePoint.value.y);
    maskCtx.value.lineTo(x, y);
    maskCtx.value.stroke();
    console.log('draw - 叠加绘制线段');
  }
  
  // 更新上一个点为当前点
  lastStrokePoint.value = newPoint;
  
  // 记录所有点（用于最终生成mask）
  currentDrawPoints.value = [...currentDrawPoints.value, newPoint];
  emit('update:drawPoints', currentDrawPoints.value);
  console.log('draw - 当前绘制点数:', currentDrawPoints.value.length);
};

const handleMouseMove = (e: MouseEvent) => {
  console.log('handleMouseMove - 鼠标移动');
  updateCursorPosition(e);
  if (isDrawing.value) {
    let sizeChanged = false;
    if (checkImageSizeChanged()) {
      console.log('handleMouseMove - 图像尺寸发生变化，重新映射坐标并重绘');
      sizeChanged = true;
      remapDrawPoints();
      initCanvases();
      redrawAllPoints();
      let clientX: number, clientY: number;
      clientX = e.clientX;
      clientY = e.clientY;
      let x = 0, y = 0;
      if (maskCanvas.value) {
        const rect = maskCanvas.value.getBoundingClientRect();
        x = (clientX - rect.left) / props.canvasScale;
        y = (clientY - rect.top) / props.canvasScale;
      }
      x = Math.max(0, Math.min(x, imagePosition.value.width));
      y = Math.max(0, Math.min(y, imagePosition.value.height));
      lastStrokePoint.value = { x, y };
    }
    if (!sizeChanged) {
      console.log('handleMouseMove - 在绘制状态，调用draw');
      draw(e);
    }
  } else {
    console.log('handleMouseMove - 不在绘制状态');
  }
};

const handleTouchMove = (e: TouchEvent) => {
  console.log('handleTouchMove - 触摸移动');
  updateCursorPosition(e);
  if (isDrawing.value) {
    let sizeChanged = false;
    if (checkImageSizeChanged()) {
      console.log('handleTouchMove - 图像尺寸发生变化，重新映射坐标并重绘');
      sizeChanged = true;
      remapDrawPoints();
      initCanvases();
      redrawAllPoints();
      let clientX: number, clientY: number;
      clientX = e.touches[0].clientX;
      clientY = e.touches[0].clientY;
      let x = 0, y = 0;
      if (maskCanvas.value) {
        const rect = maskCanvas.value.getBoundingClientRect();
        x = (clientX - rect.left) / props.canvasScale;
        y = (clientY - rect.top) / props.canvasScale;
      }
      x = Math.max(0, Math.min(x, imagePosition.value.width));
      y = Math.max(0, Math.min(y, imagePosition.value.height));
      lastStrokePoint.value = { x, y };
    }
    if (!sizeChanged) {
      console.log('handleTouchMove - 在绘制状态，调用draw');
      draw(e);
    }
  } else {
    console.log('handleTouchMove - 不在绘制状态');
  }
};

const stopDrawing = () => {
  if (isDrawing.value) {
    isDrawing.value = false;
    console.log('stopDrawing - 停止绘制');
    
    if (currentDrawPoints.value.length > 0) {
      drawHistory.value.push([...currentDrawPoints.value]);
    }
  }
};

const generateMask = async (): Promise<Blob> => {
  const allStrokes = [...drawHistory.value, currentDrawPoints.value];

  if (allStrokes.length === 0 || allStrokes.every(s => s.length === 0)) {
    throw new Error('缺少绘制点');
  }

  const canvas = document.createElement('canvas');

  let canvasWidth = imagePosition.value.width;
  let canvasHeight = imagePosition.value.height;

  const selectedLayerImg = document.querySelector(`.layer-content.selected img`) as HTMLImageElement | null;
  if (selectedLayerImg) {
    const layerImg = selectedLayerImg as HTMLImageElement;
    canvasWidth = layerImg.naturalWidth || canvasWidth;
    canvasHeight = layerImg.naturalHeight || canvasHeight;
  }

  canvas.width = canvasWidth;
  canvas.height = canvasHeight;

  const ctx = canvas.getContext('2d');

  if (!ctx) {
    throw new Error('无法创建Canvas上下文');
  }

  ctx.fillStyle = '#000000';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  ctx.strokeStyle = '#ffffff';
  ctx.lineWidth = localBrushSize.value * (canvas.width / imagePosition.value.width);
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';

  allStrokes.forEach(stroke => {
    if (stroke.length === 0) return;
    ctx.beginPath();
    stroke.forEach((point, index) => {
      const x = (point.x / imagePosition.value.width) * canvas.width;
      const y = (point.y / imagePosition.value.height) * canvas.height;
      if (index === 0) {
        ctx.moveTo(x, y);
      } else {
        ctx.lineTo(x, y);
      }
    });
    ctx.stroke();
  });

  return new Promise<Blob>((resolve, reject) => {
    canvas.toBlob((blob: Blob | null) => {
      if (blob) {
        resolve(blob);
      } else {
        reject(new Error('无法生成Blob'));
      }
    }, 'image/png');
  });
};

const handleComplete = async () => {
  if (currentDrawPoints.value.length === 0) return;
  
  try {
    const maskBlob = await generateMask();
    emit('complete', maskBlob);
  } catch (error) {
    console.error('生成mask失败:', error);
    emit('cancel');
  }
};

const cancel = () => {
  isDrawing.value = false;
  currentDrawPoints.value = [];
  drawHistory.value = [];
  clearCanvases();
  emit('update:isActive', false);
  emit('cancel');
};






  


watch(() => props.isActive, (newVal) => {
  if (!newVal) {
    clearCanvases();
    isDrawing.value = false;
  } else {
    nextTick(() => {
      initCanvases();
    });
  }
});

watch(() => props.canvasContainerRef, () => {
  if (props.isActive) {
    nextTick(() => {
      initCanvases();
    });
  }
});

// 监听图层ID和图像URL变化，确保canvas与当前图层匹配
watch(() => [props.layerId, props.layerImageUrl], () => {
  if (props.isActive) {
    nextTick(() => {
      initCanvases();
    });
  }
}, { deep: true });

onMounted(() => {
  if (props.isActive) {
    nextTick(() => {
      initCanvases();
    });
  }
});

defineExpose({
  initCanvases,
  clearCanvases,
  cancel,
  handleComplete
});
</script>

<style scoped>
.local-redraw-container {
  pointer-events: auto;
  z-index: 99999;
  cursor: none;
  background: transparent;
  border-radius: 8px;
}

.mask-canvas {
  pointer-events: auto;
  z-index: 2;
  cursor: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}
</style>
