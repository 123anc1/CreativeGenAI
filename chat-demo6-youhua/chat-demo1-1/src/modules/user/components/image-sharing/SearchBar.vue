<template>
  <div class="flex items-center space-x-3">
    <div class="relative">
      <input
        v-model="localSearchTitle"
        @keyup.enter="handleSearch"
        placeholder="搜索图片标题..."
        class="pl-9 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent text-sm w-64 bg-gray-50 hover:bg-white transition-colors"
      />
      <svg class="w-4 h-4 absolute left-3 top-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
    </div>
    <button 
      @click="handleSearch"
      class="px-4 py-2 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg text-sm transition-colors"
    >
      搜索
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

// 定义props
const props = defineProps<{
  searchTitle: string;
}>();

// 定义emits
const emit = defineEmits<{
  (e: 'search', searchTitle: string): void;
}>();

// 本地状态
const localSearchTitle = ref(props.searchTitle);

// 监听props变化
watch(() => props.searchTitle, (newValue) => {
  localSearchTitle.value = newValue;
});

// 方法
const handleSearch = () => {
  emit('search', localSearchTitle.value);
};
</script>

<style scoped>
/* 搜索栏样式已在模板中通过Tailwind类定义 */
</style>