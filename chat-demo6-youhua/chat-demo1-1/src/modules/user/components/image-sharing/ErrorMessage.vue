<template>
  <div v-if="error" class="flex items-center gap-2 text-red-600 text-sm p-4 bg-red-50">
    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
    <span>{{ error }}</span>
    <button @click="handleRetry" class="ml-2 text-xs text-blue-600 hover:text-blue-800">重试</button>
  </div>
</template>

<script setup lang="ts">
// 定义props
const props = defineProps<{
  error: string | null;
}>();

// 定义emits
const emit = defineEmits<{
  (e: 'retry'): void;
}>();

// 方法
const handleRetry = () => {
  emit('retry');
};
</script>

<style scoped>
/* 错误信息样式已在模板中通过Tailwind类定义 */
</style>