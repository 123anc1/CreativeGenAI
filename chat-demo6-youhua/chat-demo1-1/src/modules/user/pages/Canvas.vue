<template>
  <div class="flex h-screen relative overflow-hidden canvas-container">
    <UserSidebar
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      :current-route="'canvas'"
      @switch-conversation="switchConversation"
      @create-temporary-conversation="createTemporaryConversation"
      @remove="handleRemove"
    />
    
    <div class="flex-1 flex flex-col overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-br from-indigo-50 to-purple-50 dark:from-gray-900 dark:to-gray-800 z-0"></div>
      <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>
      <div class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-indigo-200 to-purple-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>
      <div class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-purple-200 to-indigo-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>

      <div class="main-content flex flex-1 relative z-10 min-h-0">
        <div class="canvas-area flex-1 flex flex-col min-w-0 relative">
          <div class="toolbar flex items-center gap-2 p-3 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm border-b border-indigo-100 dark:border-indigo-900 z-10">
            <div class="flex items-center gap-3">
              <button 
                @click="importImage"
                class="p-2 rounded-lg transition-all duration-200 bg-gradient-to-r from-indigo-500 to-purple-500 text-white shadow-md hover:shadow-lg"
                title="导入图片"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                </svg>
              </button>
              
              <button 
                @click="openAssetsModal"
                class="p-2 rounded-lg transition-all duration-200 bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-md hover:shadow-lg"
                title="导入资产"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                </svg>
              </button>
              
              <button 
                @click="addEmptyLayer"
                class="p-2 rounded-lg transition-all duration-200 bg-gradient-to-r from-emerald-500 to-green-500 text-white shadow-md hover:shadow-lg"
                title="添加空白图层"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
              </button>
            </div>

            <div class="flex-1 flex items-center">
              <div class="flex items-center gap-2">
                <h3 class="text-lg font-medium text-gray-800 dark:text-gray-200">{{ sessionName }}</h3>
                <button 
                  @click="renameSession"
                  class="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors duration-200"
                  title="重命名项目"
                >
                  <svg class="w-4 h-4 text-gray-500 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                  </svg>
                </button>
              </div>
            </div>
            
            <div class="flex items-center gap-2">
              <button 
                @click="zoomIn"
                class="p-2 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-200"
                title="放大"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                </svg>
              </button>
              <button 
                @click="zoomOut"
                class="p-2 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-200"
                title="缩小"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </button>
              <button 
                @click="resetView"
                class="p-2 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-200"
                title="重置视图"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
              </button>
            </div>
          </div>

          <div 
            class="canvas-wrapper flex-1 overflow-hidden relative"
            :class="{ 'space-pressed': canvasState.isSpacePressed.value }"
            @mousedown="handleMouseDown"
            @mousemove="handleMouseMove"
            @mouseup="handleMouseUp"
            @mouseleave="handleMouseUp"
            @wheel="handleWheel"
            @touchstart="handleTouchStart"
            @touchmove="handleTouchMove"
            @touchend="handleTouchEnd"
            @click="handleCanvasClickEvent"
          >
            <div 
              class="canvas-container absolute"
              :style="canvasContainerStyle"
              ref="localRedrawState.canvasContainer.value"
            >
              <div 
                v-for="(layer, index) in layerState.layers.value" 
                :key="layer.id"
                class="layer absolute"
                :style="layerStyle(layer)"
              >
                <div :class="['layer-content relative', layerState.selectedLayerId.value === layer.id ? 'selected' : '']">
                  <img 
                    v-if="layer.imageUrl" 
                    :src="layer.imageUrl" 
                    :alt="`Layer ${index + 1}`"
                    class="max-w-none max-h-none cursor-move"
                    :style="{
                      width: `${layer.width}px`,
                      height: `${layer.height}px`,
                      opacity: layer.opacity / 100
                    }"
                    @mousedown="!localRedrawState.isLocalRedrawMode.value && startLayerDragEvent($event, layer.id)"
                    @touchstart="!localRedrawState.isLocalRedrawMode.value && startLayerDragEvent($event, layer.id)"
                    @click="handleImageClickEvent(layer)"
                  />
                  <div 
                    v-else 
                    class="empty-layer bg-gray-100/50 border border-dashed border-gray-300 dark:border-gray-700 cursor-move"
                    :style="{ width: '200px', height: '200px' }"
                    @mousedown="startLayerDragEvent($event, layer.id)"
                    @touchstart="startLayerDragEvent($event, layer.id)"
                    @click="selectLayer(layer.id)"
                  ></div>
                  
                  <ImageToolbar
                    v-if="layerState.selectedLayerId.value === layer.id && layer.imageUrl"
                    :layer="layer"
                    @add-to-chat="addImageToInput"
                    @expand-image="handleExpandImage"
                    @enhance-image="handleEnhanceImage"
                    @remove-background="handleRemoveBackground"
                    @inpaint="handleInpaint"
                    @local-redraw="handleLocalRedraw"
                    @text-redraw="handleTextRedraw"
                    @image-adjust="handleImageAdjust"
                    @generate-video="handleGenerateVideo"
                    @rotate="handleRotate"
                    @reset="handleResetLayer"
                    @download="handleDownloadLayer"
                  />
                </div>
              </div>
              
              <LocalRedraw
                ref="localRedrawRef"
                :is-active="localRedrawState.isLocalRedrawMode.value"
                :brush-size="localRedrawState.brushSize.value"
                :canvas-container-ref="localRedrawState.canvasContainer.value"
                :layer-id="localRedrawState.localRedrawLayerId.value"
                :layer-image-url="getSelectedLayerImageUrl(localRedrawState.localRedrawLayerId.value)"
                :canvas-scale="canvasState.scale.value"
                @update:is-active="localRedrawState.isLocalRedrawMode.value = $event"
                @update:draw-points="localRedrawState.drawPoints.value = $event"
                @complete="handleLocalRedrawComplete"
                @cancel="cancelLocalRedraw"
              />
            </div>

            <div v-if="layerState.layers.value.length === 0" class="welcome-screen absolute inset-0 flex flex-col items-center justify-center z-20">
              <div class="bg-white/90 dark:bg-gray-900/90 backdrop-blur-sm rounded-2xl p-8 max-w-md w-full text-center shadow-xl">
                <div class="text-6xl mb-4">🎨</div>
                <h2 class="text-2xl font-bold text-gray-800 dark:text-gray-200 mb-3">欢迎使用AI画布</h2>
                <p class="text-gray-600 dark:text-gray-400 mb-6">上传图片到画布中，开始您的创作之旅</p>
                
                <div class="space-y-4">
                  <button 
                    @click="importImage"
                    class="w-full rounded-lg px-6 py-3.5 text-base font-medium transition-all duration-300 transform hover:scale-[1.02] active:scale-95 bg-gradient-to-r from-indigo-500 to-purple-500 hover:from-indigo-600 hover:to-purple-600 text-white shadow-md shadow-indigo-200/50 dark:shadow-indigo-900/50"
                  >
                    <span class="flex items-center justify-center">
                      <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                      </svg>
                      导入本地图像
                    </span>
                  </button>
                  
                  <div class="text-sm text-gray-500 dark:text-gray-500">或</div>
                  
                  <button 
                    @click="openAssetsModal"
                    class="w-full rounded-lg px-6 py-3.5 text-base font-medium transition-all duration-300 transform hover:scale-[1.02] active:scale-95 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white shadow-md shadow-purple-200/50 dark:shadow-purple-900/50"
                  >
                    <span class="flex items-center justify-center">
                      <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                      </svg>
                      导入资产
                    </span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <CanvasInput 
            :selected-image-urls="selectedImageUrls"
            :session-id="sessionId"
            @remove-selected-image="removeSelectedImage"
            @send-inpainting="handleSendInpainting"
          />
        </div>

        <AssetModal 
          :visible="assetState.showAssetsModal.value"
          :assets="assetState.userAssets.value"
          :loading="assetState.loadingAssets.value"
          :selected-assets="assetState.selectedAssets.value"
          @close="closeAssetsModal"
          @toggle-selection="toggleAssetSelection"
          @import-selected="importSelectedAssets"
        />
        
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue';
import UserSidebar from '@/shared/components/UserSidebar.vue';
import ImageToolbar from '@/modules/user/components/canvas/ImageToolbar.vue';
import AssetModal from '@/modules/user/components/canvas/AssetModal.vue';
import CanvasInput from '@/modules/user/components/canvas/CanvasInput.vue';
import LocalRedraw from '@/modules/user/components/canvas/LocalRedraw.vue';
import { Conversation } from '@/shared/types/type';
import type { Layer } from '@/modules/user/types/canvas';

import { useCanvas } from '@/modules/user/composables/useCanvas';
import { useLayer } from '@/modules/user/composables/useLayer';
import { useAsset } from '@/modules/user/composables/useAsset';
import { useSession } from '@/modules/user/composables/useSession';

const {
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
  handleGlobalKeyUp
} = useCanvas();

const {
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
} = useLayer(canvasState.scale, layerState, dragState, canvasState);

const { assetState, openAssetsModal, closeAssetsModal, toggleAssetSelection, saveImagesToServer: saveImages } = useAsset();

const { sessionId, sessionName, initSession: initSessionData } = useSession();

const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);
const selectedImageUrls = ref<string[]>([]);
const maskBase64 = ref<string>('');
const localRedrawRef = ref<any>(null);

const switchConversation = (id: string | number) => {
  currentConversationId.value = String(id);
};

const createTemporaryConversation = () => {};
const handleRemove = (_payload: { sessionid: string | number }) => {};
const renameSession = () => {};

const handleLocalRedrawComplete = async (maskBlob: Blob) => {
  if (!localRedrawState.localRedrawLayerId.value) return;
  
  const maskUrl = URL.createObjectURL(maskBlob);
  const a = document.createElement('a');
  a.href = maskUrl;
  a.download = 'mask.png'; //下载mask图片
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(maskUrl);
  
  const reader = new FileReader();
  reader.onload = (e) => {
    const base64String = e.target?.result as string;
    if (base64String) {
      maskBase64.value = base64String;
    }
  };
  reader.readAsDataURL(maskBlob);
  cancelLocalRedraw();
};

const importImage = () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = async (e) => {
    const target = e.target as HTMLInputElement;
    if (target.files && target.files[0]) {
      const file = target.files[0];
      const reader = new FileReader();
      reader.onload = async (event) => {
        const imageBase64 = event.target?.result as string;
        
        const img = new Image();
        img.onload = () => {
          addLayer(imageBase64, img.width, img.height);
        };
        img.src = imageBase64;
        
        const savedImageUrls = await saveImages([{ imageBase64 }], sessionId);
        
        if (savedImageUrls.length > 0) {
          const lastLayer = layerState.layers.value[layerState.layers.value.length - 1];
          if (lastLayer) {
            lastLayer.imageUrl = savedImageUrls[0];
          }
        }
      };
      reader.readAsDataURL(file);
    }
  };
  input.click();
};

const startLayerDragEvent = (e: MouseEvent | TouchEvent, layerId: string) => {
  startLayerDrag(e, layerId, getClientCoordinates);
};

const handleTouchStart = (e: TouchEvent) => {
  if (localRedrawState.isLocalRedrawMode.value) {
    const touch = e.touches[0];
    updateCursorPosition(touch as unknown as MouseEvent);
  } else {
    startDrag(e);
  }
};

const handleTouchMove = (e: TouchEvent) => {
  if (localRedrawState.isLocalRedrawMode.value) {
    const touch = e.touches[0];
    updateCursorPosition(touch as unknown as MouseEvent);
  } else {
    if (layerState.isDraggingLayer.value) {
      dragLayer(e, getClientCoordinates);
    } else {
      drag(e);
    }
  }
};

const handleTouchEnd = () => {
  if (!localRedrawState.isLocalRedrawMode.value) {
    stopDrag(stopLayerDrag);
  }
};

const handleMouseDown = (e: MouseEvent) => {
  if (localRedrawState.isLocalRedrawMode.value) {
    updateCursorPosition(e);
  } else {
    startDrag(e);
  }
};

const handleMouseMove = (e: MouseEvent) => {
  if (localRedrawState.isLocalRedrawMode.value) {
    updateCursorPosition(e);
  } else {
    if (layerState.isDraggingLayer.value) {
      dragLayer(e, getClientCoordinates);
    } else {
      drag(e);
    }
  }
};

const handleMouseUp = () => {
  if (!localRedrawState.isLocalRedrawMode.value) {
    stopDrag(stopLayerDrag);
  }
};

const handleCanvasClickEvent = (e: MouseEvent) => {
  handleCanvasClick(e, localRedrawState.isLocalRedrawMode);
};

const handleImageClickEvent = (layer: Layer) => {
  handleImageClick(layer, localRedrawState.isLocalRedrawMode, cancelLocalRedraw);
};

const addImageToInput = (imageUrl: string | undefined) => {
  if (imageUrl && !selectedImageUrls.value.includes(imageUrl)) {
    selectedImageUrls.value.push(imageUrl);
  }
};

const removeSelectedImage = (index: number) => {
  selectedImageUrls.value.splice(index, 1);
};

const handleExpandImage = (_layer: Layer) => {};
const handleEnhanceImage = (_layer: Layer) => {};
const handleRemoveBackground = (_layer: Layer) => {};
const handleInpaint = (_layer: Layer) => {};

const handleLocalRedraw = (layer: Layer) => {
  localRedrawState.isLocalRedrawMode.value = true;
  localRedrawState.localRedrawLayerId.value = layer.id;
  
  if (layer.imageUrl) {
    selectedImageUrls.value = [layer.imageUrl];
  }
  
  const canvasWrapper = document.querySelector('.canvas-wrapper') as HTMLElement;
  if (canvasWrapper) {
    canvasWrapper.classList.add('local-redraw-mode');
    const rect = canvasWrapper.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    canvasWrapper.style.setProperty('--mouse-x', `${centerX}px`);
    canvasWrapper.style.setProperty('--mouse-y', `${centerY}px`);
  }
};

const handleTextRedraw = (_layer: Layer) => {};
const handleImageAdjust = (_layer: Layer) => {};
const handleGenerateVideo = (_layer: Layer) => {};
const handleRotate = (_layer: Layer) => {};
const handleResetLayer = (_layer: Layer) => {};

const handleDownloadLayer = (layer: Layer) => {
  if (layer.imageUrl) {
    const link = document.createElement('a');
    link.href = layer.imageUrl;
    link.download = `image-${layer.id}.png`;
    link.click();
  }
};

const handleSendInpainting = async (text: string, imageUrls: string[]) => {
  if (imageUrls.length === 0 || !text.trim() || !sessionId.value) {
    return;
  }

  try {
    if (localRedrawState.isLocalRedrawMode.value && localRedrawRef.value) {
      await localRedrawRef.value.handleComplete();
      await new Promise(resolve => setTimeout(resolve, 500));
    }

    const userInfo = localStorage.getItem('userInfo');
    const userId = userInfo ? JSON.parse(userInfo)?.id : null;
    
    const requestData = {
      uid: userId,
      sessionId: sessionId.value,
      imageUrl: imageUrls,
      prompt: text,
      imageMaskBase64: maskBase64.value ? [maskBase64.value] : []
    };
    
    const response = await fetch('/canvas/inpainting', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify(requestData)
    });

    const result = await response.json();
    
    if (result.code === 1) {
      let imageUrl = '';
      
      if (result.data && Array.isArray(result.data) && result.data.length > 0) {
        imageUrl = result.data[0];
        if (imageUrl) {
          imageUrl = imageUrl.trim().replace(/[`]/g, '');
        }
      } else if (result.data && result.data.imageBackUrl) {
        imageUrl = result.data.imageBackUrl;
      }
      
      if (imageUrl) {
        selectedImageUrls.value = [];
        
        const img = new Image();
        img.onload = () => {
          addLayer(imageUrl, img.width, img.height);
        };
        img.src = imageUrl;
      }
    }
  } catch {}
};

const importSelectedAssets = async () => {
  const imagesToSave = assetState.selectedAssets.value.map(asset => ({
    imageUrl: asset.url
  }));
  
  const savedImageUrls = await saveImages(imagesToSave, sessionId);
  
  assetState.selectedAssets.value.forEach((asset, index) => {
    const imageUrl = savedImageUrls[index] || asset.url;
    const img = new Image();
    img.onload = () => {
      addLayer(imageUrl, img.width, img.height, asset.id);
    };
    img.src = imageUrl;
  });
  
  closeAssetsModal();
};

const handleKeyDown = (event: KeyboardEvent) => {
  handleGlobalKeyDown(event, deleteSelectedLayer);
};

onMounted(() => {
  initSessionData(addLayer);
  window.addEventListener('keydown', handleKeyDown);
  window.addEventListener('keyup', handleGlobalKeyUp);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown);
  window.removeEventListener('keyup', handleGlobalKeyUp);
});

watch(layerState.layers, (newLayers) => {
  newLayers.sort((a, b) => a.zIndex - b.zIndex);
}, { deep: true });
</script>

<style scoped>
.bg-grid-pattern {
  background-image: linear-gradient(to right, rgba(99, 102, 241, 0.05) 1px, transparent 1px), linear-gradient(to bottom, rgba(99, 102, 241, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

.canvas-wrapper {
  cursor: default;
}

.canvas-wrapper.space-pressed {
  cursor: grab;
}

.canvas-wrapper.space-pressed:active {
  cursor: grabbing;
}

.canvas-wrapper.local-redraw-mode {
  cursor: none;
}

.canvas-wrapper.local-redraw-mode .layer {
  opacity: 0.7;
}

.canvas-wrapper.local-redraw-mode .layer-content.selected {
  opacity: 1;
  cursor: none;
}

.canvas-wrapper.local-redraw-mode::before {
  content: '';
  position: fixed;
  width: 20px;
  height: 20px;
  border: 2px solid #6366f1;
  border-radius: 50%;
  pointer-events: none;
  z-index: 9999;
  left: var(--mouse-x, 0);
  top: var(--mouse-y, 0);
  transform: translate(-50%, -50%);
  transition: opacity 0.1s ease;
}

.canvas-wrapper.local-redraw-mode.drawing::before {
  background: rgba(99, 102, 241, 0.3);
}

.canvas-wrapper.local-redraw-mode:hover::before {
  background: rgba(99, 102, 241, 0.2);
}

.canvas-wrapper.local-redraw-mode .layer-content:not(.selected) {
  pointer-events: auto;
  cursor: default;
}

.canvas-wrapper.local-redraw-mode .layer-content:not(.selected):hover {
  cursor: pointer;
}

.canvas-wrapper.local-redraw-mode .layer-content.selected {
  pointer-events: none;
}

.layer {
  user-select: none;
}

.layer-content {
  position: relative;
  transition: all 0.2s ease;
}

.local-redraw-selection {
  border: 2px solid #6366f1;
  background: linear-gradient(45deg, rgba(99, 102, 241, 0.3) 25%, rgba(99, 102, 241, 0.1) 25%, rgba(99, 102, 241, 0.1) 50%, rgba(99, 102, 241, 0.3) 50%, rgba(99, 102, 241, 0.3) 75%, rgba(99, 102, 241, 0.1) 75%, rgba(99, 102, 241, 0.1) 100%);
  background-size: 20px 20px;
  z-index: 100;
  pointer-events: none;
  box-shadow: 0 0 10px rgba(99, 102, 241, 0.5);
  transition: all 0.2s ease;
}

.local-redraw-canvas {
  z-index: 9998;
  pointer-events: none;
  overflow: visible;
}

.canvas-wrapper.local-redraw-mode .layer-content.selected {
  position: relative;
  z-index: 50;
}

.canvas-wrapper.local-redraw-mode .cursor-move {
  cursor: crosshair !important;
}

.layer-content {
  position: relative;
  transition: all 0.2s ease;
}

.layer:hover .layer-content:not(.selected) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.layer-content.selected {
  box-shadow: 0 0 0 2px #6366f1, 0 4px 12px rgba(99, 102, 241, 0.3);
  border-radius: 4px;
  transition: all 0.2s ease;
  z-index: 10;
}

.settings-panel {
  scrollbar-width: thin;
  scrollbar-color: #6366f1 #e0e7ff;
}

.settings-panel::-webkit-scrollbar {
  width: 6px;
}

.settings-panel::-webkit-scrollbar-track {
  background: #e0e7ff;
  border-radius: 3px;
}

.settings-panel::-webkit-scrollbar-thumb {
  background: #6366f1;
  border-radius: 3px;
}

@media (max-width: 768px) {
  .settings-panel {
    position: fixed;
    right: 0;
    top: 0;
    height: 100%;
    z-index: 50;
    transform: translateX(100%);
    transition: transform 0.3s ease;
  }
  
  .settings-panel.open {
    transform: translateX(0);
  }
}
</style>
