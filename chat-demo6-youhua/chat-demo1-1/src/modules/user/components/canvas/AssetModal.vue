<template>
  <div v-if="visible" class="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4">
    <div class="bg-white dark:bg-gray-900 rounded-2xl w-full max-w-4xl max-h-[80vh] flex flex-col shadow-2xl">
      <!-- 弹窗头部 -->
      <div class="p-6 border-b border-gray-200 dark:border-gray-800 flex items-center justify-between">
        <h3 class="text-xl font-bold text-gray-800 dark:text-gray-200">导入资产</h3>
        <button @click="close" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>
      
      <!-- 弹窗内容 -->
      <div class="flex-1 overflow-auto p-6">
        <div v-if="loading" class="flex items-center justify-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-purple-500"></div>
        </div>
        
        <div v-else-if="assets.length === 0" class="text-center py-12">
          <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          <h4 class="text-lg font-medium text-gray-800 dark:text-gray-200 mb-2">暂无资产</h4>
          <p class="text-gray-600 dark:text-gray-400">您还没有生成或上传任何图片</p>
        </div>
        
        <div v-else class="grid grid-cols-4 gap-4">
          <div v-for="(asset, index) in assets" :key="asset.id || index" class="relative group">
            <div class="aspect-square rounded-lg overflow-hidden border border-gray-200 dark:border-gray-800 hover:border-purple-500 transition-colors duration-200 cursor-pointer">
              <img 
                    :src="asset.url" 
                    :alt="asset.prompt || 'Asset'" 
                    class="w-full h-full object-cover"
                    @click="toggleSelection(asset)"
                  />
                  <!-- 选择框 -->
                  <div class="absolute top-2 right-2">
                    <div 
                      :class="['w-6 h-6 rounded-full flex items-center justify-center transition-all duration-200', 
                        props.selectedAssets.includes(asset) 
                          ? 'bg-purple-600 text-white' 
                          : 'bg-white/80 dark:bg-gray-900/80 text-gray-400 border border-gray-300 dark:border-gray-700']"
                    >
                      <svg v-if="props.selectedAssets.includes(asset)" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                      </svg>
                    </div>
                  </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 弹窗底部 -->
      <div class="p-6 border-t border-gray-200 dark:border-gray-800 flex items-center justify-between">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          已选择 {{ props.selectedAssets.length }} 项
        </div>
        <div class="flex gap-3">
          <button 
            @click="close"
            class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors duration-200"
          >
            取消
          </button>
          <button 
            @click="importSelected"
            :disabled="props.selectedAssets.length === 0"
            class="px-4 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            导入选中
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Asset } from '@/modules/user/types/canvas';

const props = defineProps<{
  visible: boolean;
  assets: Asset[];
  loading: boolean;
  selectedAssets: Asset[];
}>();

const emit = defineEmits<{
  close: [];
  toggleSelection: [asset: Asset];
  importSelected: [];
}>();

const close = () => {
  emit('close');
};

const toggleSelection = (asset: Asset) => {
  emit('toggleSelection', asset);
};

const importSelected = () => {
  emit('importSelected');
};
</script>

<style scoped>
/* 组件样式已包含在主样式中 */
</style>