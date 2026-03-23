import { ref, computed} from 'vue';
import { CANVAS_CONFIG, type CanvasState, type LayerState, type DragState, type LocalRedrawState, type Layer } from '../types/canvas';

const {
  CANVAS_SIZE,
  CANVAS_CENTER,
  MIN_SCALE,
  MAX_SCALE,
  ZOOM_IN_FACTOR,
  ZOOM_OUT_FACTOR,
  WHEEL_ZOOM_FACTOR
} = CANVAS_CONFIG;

export function useCanvas() {
  const canvasState: CanvasState = {
    isDragging: ref<boolean>(false),
    lastX: ref<number>(0),
    lastY: ref<number>(0),
    offsetX: ref<number>(0),
    offsetY: ref<number>(0),
    scale: ref<number>(1),
    isSpacePressed: ref<boolean>(false)
  };

  const layerState: LayerState = {
    layers: ref<Layer[]>([]),
    selectedLayerId: ref<string | null>(null),
    nextLayerId: ref<number>(1),
    isDraggingLayer: ref<boolean>(false),
    draggingLayerId: ref<string | null>(null),
    layerStartX: ref<number>(0),
    layerStartY: ref<number>(0),
    previousSelectedLayerId: ref<string | null>(null)
  };

  const dragState: DragState = {
    isDraggingImage: ref<boolean>(false),
    dragStartTime: ref<number>(0),
    dragStartPos: ref<{ x: number; y: number }>({ x: 0, y: 0 })
  };

  const localRedrawState: LocalRedrawState = {
    isLocalRedrawMode: ref<boolean>(false),
    localRedrawLayerId: ref<string | null>(null),
    drawPoints: ref<{ x: number; y: number }[]>([]),
    brushSize: ref<number>(20),
    canvasContainer: ref<HTMLElement | null>(null)
  };

  const canvasContainerStyle = computed(() => ({
    transform: `translate(${canvasState.offsetX.value}px, ${canvasState.offsetY.value}px) scale(${canvasState.scale.value})`,
    transformOrigin: 'center center',
    transition: canvasState.isDragging.value ? 'none' : 'transform 0.2s ease',
    width: `${CANVAS_SIZE}px`,
    height: `${CANVAS_SIZE}px`,
    left: '50%',
    top: '50%',
    marginLeft: `-${CANVAS_CENTER}px`,
    marginTop: `-${CANVAS_CENTER}px`
  }));

  const getClientCoordinates = (e: MouseEvent | TouchEvent) => {
    if (e instanceof MouseEvent) {
      return { x: e.clientX, y: e.clientY };
    }
    return { x: e.touches[0].clientX, y: e.touches[0].clientY };
  };

  const startDrag = (e: MouseEvent | TouchEvent) => {
    if (layerState.isDraggingLayer.value || !canvasState.isSpacePressed.value) return;
    
    canvasState.isDragging.value = true;
    e.preventDefault();
    const { x, y } = getClientCoordinates(e);
    canvasState.lastX.value = x;
    canvasState.lastY.value = y;
  };

  const drag = (e: MouseEvent | TouchEvent, dragLayerFn?: (e: MouseEvent | TouchEvent) => void) => {
    if (layerState.isDraggingLayer.value && dragLayerFn) {
      dragLayerFn(e);
      return;
    }
    
    if (!canvasState.isDragging.value) return;
    
    e.preventDefault();
    const { x, y } = getClientCoordinates(e);
    canvasState.offsetX.value += x - canvasState.lastX.value;
    canvasState.offsetY.value += y - canvasState.lastY.value;
    canvasState.lastX.value = x;
    canvasState.lastY.value = y;
  };

  const stopDrag = (stopLayerDragFn?: () => void) => {
    canvasState.isDragging.value = false;
    if (stopLayerDragFn) stopLayerDragFn();
  };

  const handleWheel = (e: WheelEvent) => {
    e.preventDefault();
    
    const oldScale = canvasState.scale.value;
    const delta = e.deltaY > 0 ? WHEEL_ZOOM_FACTOR : (1 / WHEEL_ZOOM_FACTOR);
    const newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, oldScale * delta));
    
    if (newScale === oldScale) return;
    
    zoomToCenter(newScale);
  };

  const zoomIn = () => {
    const oldScale = canvasState.scale.value;
    const newScale = Math.min(MAX_SCALE, oldScale * ZOOM_IN_FACTOR);
    
    if (newScale === oldScale) return;
    
    zoomToCenter(newScale);
  };

  const zoomOut = () => {
    const oldScale = canvasState.scale.value;
    const newScale = Math.max(MIN_SCALE, oldScale * ZOOM_OUT_FACTOR);
    
    if (newScale === oldScale) return;
    
    zoomToCenter(newScale);
  };
   
  const zoomToCenter = (newScale: number) => {
    const canvasContainer = document.querySelector('.canvas-container') as HTMLElement | null;
    if (!canvasContainer) {
      canvasState.scale.value = newScale;
      return;
    }
    
    // 获取canvas-container的中心点（相对于canvas-wrapper）
    const rect = canvasContainer.getBoundingClientRect();
    const wrapper = document.querySelector('.canvas-wrapper') as HTMLElement | null;
    if (!wrapper) {
      canvasState.scale.value = newScale;
      return;
    }
    
    const wrapperRect = wrapper.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2 - wrapperRect.left;
    const centerY = rect.top + rect.height / 2 - wrapperRect.top;
    
    const oldScale = canvasState.scale.value;
    const oldOffsetX = canvasState.offsetX.value;
    const oldOffsetY = canvasState.offsetY.value;
    
    const scaleRatio = newScale / oldScale;
    canvasState.offsetX.value = centerX - (centerX - oldOffsetX) * scaleRatio;
    canvasState.offsetY.value = centerY - (centerY - oldOffsetY) * scaleRatio;
    
    canvasState.scale.value = newScale;
  };

  const resetView = () => {
    canvasState.offsetX.value = 0;
    canvasState.offsetY.value = 0;
    canvasState.scale.value = 1;
  };

  const updateCursorPosition = (e: MouseEvent) => {
    const canvasWrapper = document.querySelector('.canvas-wrapper') as HTMLElement | null;
    if (canvasWrapper) {
      canvasWrapper.style.setProperty('--mouse-x', `${e.clientX}px`);
      canvasWrapper.style.setProperty('--mouse-y', `${e.clientY}px`);
    }
  };

  const cancelLocalRedraw = () => {
    localRedrawState.isLocalRedrawMode.value = false;
    localRedrawState.localRedrawLayerId.value = null;
    localRedrawState.drawPoints.value = [];
    
    const canvasWrapper = document.querySelector('.canvas-wrapper') as HTMLElement | null;
    if (canvasWrapper) {
      canvasWrapper.classList.remove('local-redraw-mode');
      canvasWrapper.classList.remove('drawing');
      canvasWrapper.style.removeProperty('--mouse-x');
      canvasWrapper.style.removeProperty('--mouse-y');
    }
  };

  const handleGlobalKeyDown = (event: KeyboardEvent, deleteSelectedLayerFn?: () => void) => {
    if (event.key === 'Escape' && localRedrawState.isLocalRedrawMode.value) {
      cancelLocalRedraw();
    } else if (event.key === ' ') {
      canvasState.isSpacePressed.value = true;
    } else if ((event.key === 'Delete' || event.key === 'Backspace') && deleteSelectedLayerFn) {
      deleteSelectedLayerFn();
    }
  };

  const handleGlobalKeyUp = (event: KeyboardEvent) => {
    if (event.key === ' ') {
      canvasState.isSpacePressed.value = false;
    }
  };

  return {
    canvasState,
    layerState,
    dragState,
    localRedrawState,
    canvasContainerStyle,
    getClientCoordinates,
    startDrag,
    drag,
    stopDrag,
    handleWheel,
    zoomIn,
    zoomOut,
    resetView,
    updateCursorPosition,
    cancelLocalRedraw,
    handleGlobalKeyDown,
    handleGlobalKeyUp,
    CANVAS_CONFIG
  };
}
