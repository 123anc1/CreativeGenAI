<template>
  <div class="image-toolbar">
    <button class="toolbar-button" title="添加到对话" @click="emit('add-to-chat', layer?.imageUrl ?? undefined)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
      </svg>
      <span>添加到对话</span>
    </button>
    <button class="toolbar-button" title="扩图" @click="emit('expand-image', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8-4"></path>
      </svg>
      <span>扩图</span>
    </button>
    <button class="toolbar-button" title="超清" @click="emit('enhance-image', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
      </svg>
      <span>超清</span>
    </button>
    <button class="toolbar-button" title="抠图" @click="emit('remove-background', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
      </svg>
      <span>抠图</span>
    </button>
    <button class="toolbar-button" title="消除笔" @click="emit('inpaint', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
      </svg>
      <span>消除笔</span>
    </button>
    <div class="dropdown">
      <button class="toolbar-button" title="更多" @click="toggleDropdown">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"></path>
        </svg>
        <span>更多</span>
      </button>
      <Teleport to="body">
        <div v-if="showDropdown" class="dropdown-menu" :style="dropdownMenuStyle">
          <button class="dropdown-item" @click="emit('local-redraw', layer)">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
            </svg>
            <span>局部重绘</span>
          </button>
          <button class="dropdown-item" @click="emit('text-redraw', layer)">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z"></path>
            </svg>
            <span>文字重绘</span>
          </button>
          <button class="dropdown-item" @click="emit('image-adjust', layer)">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c-.94 1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <span>画面微调</span>
          </button>
          <button class="dropdown-item" @click="emit('generate-video', layer)">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>生成视频</span>
          </button>
        </div>
      </Teleport>
    </div>
    <button class="toolbar-button" title="旋转" @click="emit('rotate', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
      </svg>
    </button>
    <button class="toolbar-button" title="重置" @click="emit('reset', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
      </svg>
    </button>
    <button class="toolbar-button" title="下载" @click="emit('download', layer)">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';

const props = defineProps<{
  layer?: {
    id: string;
    imageUrl: string | null;
    imageId: string | number | null;
    x: number;
    y: number;
    width: number;
    height: number;
    opacity: number;
    visible: boolean;
    zIndex: number;
  };
}>();

const layer = props.layer;
const showDropdown = ref(false);
const dropdownMenuStyle = ref({});

const toggleDropdown = async () => {
  showDropdown.value = !showDropdown.value;
  
  if (showDropdown.value) {
    await nextTick();
    updateDropdownPosition();
  }
};

const updateDropdownPosition = () => {
  const dropdownButton = document.querySelector('.dropdown .toolbar-button');
  const dropdownMenu = document.querySelector('.dropdown-menu');
  
  if (dropdownButton && dropdownMenu) {
    const buttonRect = dropdownButton.getBoundingClientRect();
    const menuRect = dropdownMenu.getBoundingClientRect();
    
    // 计算下拉菜单位置
    const top = buttonRect.bottom + 6;
    let left = buttonRect.right - menuRect.width;
    
    // 确保菜单不会超出屏幕右边界
    if (left + menuRect.width > window.innerWidth) {
      left = window.innerWidth - menuRect.width - 10;
    }
    
    // 确保菜单不会超出屏幕左边界
    if (left < 10) {
      left = 10;
    }
    
    // 确保菜单不会超出屏幕底部
    if (top + menuRect.height > window.innerHeight) {
      dropdownMenuStyle.value = {
        position: 'fixed',
        top: `${buttonRect.top - menuRect.height - 6}px`,
        left: `${left}px`,
        zIndex: '9999',
        boxShadow: '0 4px 16px rgba(0, 0, 0, 0.12)',
        border: '1px solid rgba(0, 0, 0, 0.1)',
        borderRadius: '12px',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        backdropFilter: 'blur(10px)'
      };
    } else {
      dropdownMenuStyle.value = {
        position: 'fixed',
        top: `${top}px`,
        left: `${left}px`,
        zIndex: '9999',
        boxShadow: '0 4px 16px rgba(0, 0, 0, 0.12)',
        border: '1px solid rgba(0, 0, 0, 0.1)',
        borderRadius: '12px',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        backdropFilter: 'blur(10px)'
      };
    }
  }
};

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (!target.closest('.dropdown')) {
    showDropdown.value = false;
  }
};

// 监听点击事件
document.addEventListener('click', handleClickOutside);

// 组件卸载时移除事件监听器
import { onUnmounted } from 'vue';
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

const emit = defineEmits<{
  'add-to-chat': [imageUrl: string | undefined]
  'expand-image': [layer: any]
  'enhance-image': [layer: any]
  'remove-background': [layer: any]
  'inpaint': [layer: any]
  'local-redraw': [layer: any]
  'text-redraw': [layer: any]
  'image-adjust': [layer: any]
  'generate-video': [layer: any]
  'rotate': [layer: any]
  'reset': [layer: any]
  'download': [layer: any]
}>();
</script>

<style scoped>
.image-toolbar {
  position: absolute;
  top: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 0, 0, 0.08);
  max-width: 90vw;
  flex-wrap: nowrap;
  justify-content: center;
  z-index: 1000;
  overflow-x: auto;
  overflow-y: visible;
}

.dropdown {
  position: relative;
  z-index: 9999;
}

.dropdown-menu {
  position: fixed;
  top: auto;
  left: auto;
  right: auto;
  margin-top: 6px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 9999;
  min-width: 140px;
  overflow: hidden;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  width: 100%;
  border: none;
  background: transparent;
  text-align: left;
  color: #333;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
}

.dropdown-item svg {
  stroke: #666;
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.dropdown-item:hover svg {
  stroke: #6366f1;
}

.toolbar-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: none;
  background: transparent;
  border-radius: 10px;
  color: #333;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.toolbar-button:hover {
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
}

.toolbar-button svg {
  stroke: #666;
  width: 18px;
  height: 18px;
}

.toolbar-button:hover svg {
  stroke: #6366f1;
}

/* 更多按钮样式 */
.toolbar-button:last-child:nth-child(4),
.toolbar-button:nth-last-child(4) {
  background: rgba(0, 0, 0, 0.05);
}

.toolbar-button:last-child:nth-child(4):hover,
.toolbar-button:nth-last-child(4):hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.toolbar-button:last-child:nth-child(4):hover svg,
.toolbar-button:nth-last-child(4):hover svg {
  stroke: #333;
}

/* 水平滚动条样式 */
.image-toolbar::-webkit-scrollbar {
  height: 4px;
}

.image-toolbar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

.image-toolbar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
}

.image-toolbar::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .toolbar-button {
    padding: 6px 8px;
    font-size: 11px;
  }
  
  .toolbar-button svg {
    width: 14px;
    height: 14px;
  }
}

@media (max-width: 480px) {
  .toolbar-button span {
    display: none;
  }
  
  .toolbar-button {
    padding: 6px;
  }
  
  .image-toolbar {
    gap: 3px;
  }
}
</style>
