import { type Ref } from 'vue';
import { CANVAS_CONFIG, type Layer, type LayerState, type DragState, type CanvasState } from '../types/canvas';

const {
  CANVAS_CENTER,
  LAYER_SPACING,
  DEFAULT_LAYER_WIDTH,
  DEFAULT_LAYER_HEIGHT,
  MAX_LAYER_SIZE
} = CANVAS_CONFIG;

export function useLayer(canvasScale: Ref<number>, layerState: LayerState, dragState: DragState, canvasState: CanvasState) {
  const calculateLayerPosition = () => {
    let lastLayerRight = CANVAS_CENTER;
    
    if (layerState.layers.value.length > 0) {
      const lastLayer = layerState.layers.value[layerState.layers.value.length - 1];
      lastLayerRight = lastLayer.x + lastLayer.width + LAYER_SPACING;
    }
    
    const layerX = layerState.layers.value.length === 0 
      ? CANVAS_CENTER 
      : lastLayerRight;
    
    return layerX;
  };

  const addLayer = (imageUrl: string, width: number, height: number, imageId: string | number | null = null) => {
    let adjustedWidth = width;
    let adjustedHeight = height;
    
    if (width > MAX_LAYER_SIZE || height > MAX_LAYER_SIZE) {
      const aspectRatio = width / height;
      if (width > height) {
        adjustedWidth = MAX_LAYER_SIZE;
        adjustedHeight = MAX_LAYER_SIZE / aspectRatio;
      } else {
        adjustedHeight = MAX_LAYER_SIZE;
        adjustedWidth = MAX_LAYER_SIZE * aspectRatio;
      }
    }
    
    const layerX = calculateLayerPosition();
    const layerY = CANVAS_CENTER - adjustedHeight / 2;
    
    const newLayer: Layer = {
      id: `layer-${layerState.nextLayerId.value++}`,
      imageId,
      imageUrl,
      x: layerX,
      y: layerY,
      width: adjustedWidth,
      height: adjustedHeight,
      opacity: 100,
      visible: true,
      zIndex: layerState.layers.value.length + 1,
    };
    
    layerState.layers.value.push(newLayer);
    if (canvasState?.offsetX) {
      canvasState.offsetX.value = 0;
      canvasState.offsetY.value = 0;
    }
  };

  const addEmptyLayer = () => {
    const layerX = calculateLayerPosition();
    const layerY = CANVAS_CENTER - DEFAULT_LAYER_HEIGHT / 2;
    
    const newLayer: Layer = {
      id: `layer-${layerState.nextLayerId.value++}`,
      imageId: null,
      imageUrl: null,
      x: layerX,
      y: layerY,
      width: DEFAULT_LAYER_WIDTH,
      height: DEFAULT_LAYER_HEIGHT,
      opacity: 100,
      visible: true,
      zIndex: layerState.layers.value.length + 1,
    };
    
    layerState.layers.value.push(newLayer);
    if (canvasState?.offsetX) {
      canvasState.offsetX.value = 0;
      canvasState.offsetY.value = 0;
    }
  };

  const layerStyle = (layer: Layer): Record<string, any> => {
    const style: Record<string, any> = {
      left: `${layer.x}px`,
      top: `${layer.y}px`,
      zIndex: layer.zIndex,
      opacity: layer.visible ? 1 : 0.3,
    };
    if (!layer.visible) {
      style.pointerEvents = 'none';
    }
    return style;
  };

  const selectLayer = (layerId: string) => {
    if (layerState.selectedLayerId.value === layerId) return;
    layerState.selectedLayerId.value = layerId;
    const layer = layerState.layers.value.find(l => l.id === layerId);
    if (layer) {
      const maxZIndex = Math.max(...layerState.layers.value.map(l => l.zIndex), 0);
      layer.zIndex = maxZIndex + 1;
    }
  };

  const getSelectedLayerImageUrl = (localRedrawLayerId: string | null): string | null => {
    if (!localRedrawLayerId) return null;
    const layer = layerState.layers.value.find(l => l.id === localRedrawLayerId);
    return layer?.imageUrl || null;
  };

  const startLayerDrag = (e: MouseEvent | TouchEvent, layerId: string, getClientCoordinates: (e: MouseEvent | TouchEvent) => { x: number; y: number }) => {
    e.stopPropagation();
    
    dragState.isDraggingImage.value = false;
    dragState.dragStartTime.value = Date.now();
    const { x, y } = getClientCoordinates(e);
    dragState.dragStartPos.value = { x, y };
    
    if (layerState.selectedLayerId.value !== layerId) {
      selectLayer(layerId);
      return;
    }
    
    layerState.previousSelectedLayerId.value = layerState.selectedLayerId.value;
    layerState.selectedLayerId.value = null;
    layerState.isDraggingLayer.value = true;
    layerState.draggingLayerId.value = layerId;
    
    e.preventDefault();
    layerState.layerStartX.value = x;
    layerState.layerStartY.value = y;
  };

  const dragLayer = (e: MouseEvent | TouchEvent, getClientCoordinates: (e: MouseEvent | TouchEvent) => { x: number; y: number }) => {
    if (!layerState.isDraggingLayer.value || !layerState.draggingLayerId.value) return;
    
    e.preventDefault();
    dragState.isDraggingImage.value = true;
    
    const { x, y } = getClientCoordinates(e);
    const deltaX = (x - layerState.layerStartX.value) / canvasScale.value;
    const deltaY = (y - layerState.layerStartY.value) / canvasScale.value;
    
    const layer = layerState.layers.value.find(l => l.id === layerState.draggingLayerId.value);
    if (layer) {
      layer.x += deltaX;
      layer.y += deltaY;
    }
    
    layerState.layerStartX.value = x;
    layerState.layerStartY.value = y;
  };

  const stopLayerDrag = () => {
    layerState.isDraggingLayer.value = false;
    
    if (layerState.previousSelectedLayerId.value) {
      layerState.selectedLayerId.value = layerState.previousSelectedLayerId.value;
      layerState.previousSelectedLayerId.value = null;
    }
    
    layerState.draggingLayerId.value = null;
  };

  const deleteSelectedLayer = async () => {
    if (!layerState.selectedLayerId.value) return;
    
    const layerIndex = layerState.layers.value.findIndex(layer => layer.id === layerState.selectedLayerId.value);
    if (layerIndex > -1) {
      const layer = layerState.layers.value[layerIndex];
      
      if (layer.imageId) {
        try {
          await fetch(`/canvas/image/delete/${encodeURIComponent(String(layer.imageId))}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
              token: localStorage.getItem('token') || ''
            }
          });
        } catch (error) {
          console.error('删除图片失败:', error);
        }
      }
      
      layerState.layers.value.splice(layerIndex, 1);
      layerState.selectedLayerId.value = null;
    }
  };

  const handleImageClick = (layer: any, isLocalRedrawMode: Ref<boolean>, cancelLocalRedraw: () => void) => {
    if (isLocalRedrawMode.value) {
      cancelLocalRedraw();
      selectLayer(layer.id);
      return;
    }
    
    if (dragState.isDraggingImage.value) {
      return;
    }
    
    const dragDuration = Date.now() - dragState.dragStartTime.value;
    if (dragDuration > 200) {
      return;
    }
    
    selectLayer(layer.id);
  };

  const handleCanvasClick = (event: MouseEvent, isLocalRedrawMode: Ref<boolean>) => {
    if (isLocalRedrawMode.value) {
      return;
    }
    
    const target = event.target as HTMLElement;
    const isLayerClick = target.closest('.layer') || target.closest('.layer-content') || target.closest('img');
    
    if (!isLayerClick) {
      layerState.selectedLayerId.value = null;
    }
  };

  return {
    addLayer,
    addEmptyLayer,
    layerStyle,
    selectLayer,
    getSelectedLayerImageUrl,
    startLayerDrag,
    dragLayer,
    stopLayerDrag,
    deleteSelectedLayer,
    handleImageClick,
    handleCanvasClick
  };
}
