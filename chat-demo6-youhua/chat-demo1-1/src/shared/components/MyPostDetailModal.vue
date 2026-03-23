<template>
  <div v-if="visible" class="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
      <!-- 头部 -->
      <div class="p-4 border-b flex justify-between items-center">
        <h2 class="text-xl font-semibold">{{ imageData?.image_title || '图片详情' }}</h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- 主体内容 -->
      <div class="flex-1 overflow-y-auto p-6">
        <!-- 图片区域 -->
        <div class="mb-6">
          <img 
            :src="imageData?.image_url" 
            :alt="imageData?.image_title || '图片'"
            class="w-full h-auto max-h-96 object-contain mx-auto"
          />
        </div>

        <!-- 发布状态 -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-3">发布状态</h3>
          <div class="flex items-center">
            <span 
              :class="[
                'px-3 py-1 rounded-full text-sm font-medium',
                imageData?.status === 1 ? 'bg-green-100 text-green-800' :
                imageData?.status === 0 ? 'bg-yellow-100 text-yellow-800' :
                imageData?.status === 2 ? 'bg-red-100 text-red-800' :
                'bg-gray-100 text-gray-800'
              ]"
            >
              {{ getStatusText(imageData?.status) }}
            </span>
          </div>
        </div>

        <!-- 图片信息 -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-3">图片信息</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-600">标题</p>
              <p class="font-medium">{{ imageData?.image_title || '未命名' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">创建时间</p>
              <p class="font-medium">{{ formatDate(imageData?.created_at) }}</p>
            </div>
            <div class="md:col-span-2">
              <p class="text-sm text-gray-600">描述</p>
              <p class="font-medium">{{ imageData?.image_description || imageData?.description || '无描述' }}</p>
            </div>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-3">统计信息</h3>
          <div class="flex space-x-4">
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <span>{{ imageData?.likeCount || 0 }} 点赞</span>
            </div>
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <span>{{ imageData?.commentCount || 0 }} 评论</span>
            </div>
          </div>
        </div>
      </div>

      
    </div>
  </div>
</template>

<script setup lang="ts">
import { ImagePost } from '@/shared/types';

const props = defineProps<{
  visible: boolean;
  imageData: ImagePost | null;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'delete'): void;
}>();

const getStatusText = (status?: string | number): string => {
  // 处理数字类型的状态值
  if (typeof status === 'number') {
    switch (status) {
      case 0:
        return '待审核';
      case 1:
        return '通过审核';
      case 2:
        return '拒绝';
      default:
        return '未知状态';
    }
  }
  // 处理字符串类型的状态值
  switch (status) {
    case 'published':
      return '已发布';
    case 'pending':
      return '审核中';
    case 'rejected':
      return '审核拒绝';
    default:
      return '未知状态';
  }
};

const formatDate = (dateString: string | Date | undefined): string => {
  if (!dateString) return '未知';
  try {
    let date: Date;
    if (dateString instanceof Date) {
      date = dateString;
    } else if (typeof dateString === 'string') {
      date = new Date(dateString);
    } else {
      return '未知';
    }
    if (isNaN(date.getTime())) return '未知';
    return date.toLocaleString('zh-CN');
  } catch {
    return '未知';
  }
};
</script>

<style scoped>
/* 自定义样式 */
.max-h-96 {
  max-height: 24rem;
}
</style>