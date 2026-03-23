<template>
  <div class="input-area bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm border-t border-indigo-100 dark:border-indigo-900 p-4 z-10">
    <div class="flex flex-col gap-3">
      <div class="flex items-center gap-2">
        <span class="text-sm font-medium text-gray-600 dark:text-gray-400">已选择的图片:</span>
        <div class="flex flex-wrap gap-2">
          <span 
            v-for="(_, index) in selectedImageUrls" 
            :key="index"
            class="text-xs bg-indigo-100 dark:bg-indigo-900 text-indigo-800 dark:text-indigo-200 px-2 py-1 rounded-full flex items-center"
          >
            图片 {{ index + 1 }}
            <button 
              @click="removeSelectedImage(index)"
              class="ml-1 text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-300"
            >
              ×
            </button>
          </span>
        </div>
      </div>
      <div class="flex gap-2">
        <input 
          v-model="inputText"
          type="text"
          placeholder="输入提示词..."
          class="flex-1 px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          @keydown.delete.stop
          @keydown.backspace.stop
        />
        <button 
          @click="sendInpaintingRequest"
          :disabled="selectedImageUrls.length === 0 || !inputText.trim()"
          class="px-6 py-2 rounded-lg bg-gradient-to-r from-indigo-500 to-purple-500 text-white font-medium hover:from-indigo-600 hover:to-purple-600 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  selectedImageUrls: string[];
  sessionId: string | null;
}>();

const emit = defineEmits<{
  removeSelectedImage: [index: number];
  sendInpainting: [text: string, imageUrls: string[]];
}>();

const inputText = ref('');

const removeSelectedImage = (index: number) => {
  emit('removeSelectedImage', index);
};

const sendInpaintingRequest = async () => {
  if (props.selectedImageUrls.length === 0 || !inputText.value.trim() || !props.sessionId) {
    console.error('缺少必要参数');
    return;
  }

  emit('sendInpainting', inputText.value.trim(), props.selectedImageUrls);
  inputText.value = '';
};
</script>

<style scoped>
/* 组件样式已包含在主样式中 */
</style>