<template>
  <div class="right-panel flex-shrink-0 w-1/4 min-h-0 min-w-0 border-l border-green-100 dark:border-emerald-900 p-6 overflow-y-auto overflow-x-hidden scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm">
    <div class="settings-section">
      <h2 class="text-lg font-medium mb-4 text-gray-800 dark:text-gray-200">参数设置</h2>

      <!-- 基础模型选择 -->
      <div class="model-selection mb-6">
        <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">
          基础模型 <span class="text-red-500">*</span>
        </label>
        <div class="flex space-x-3">
          <button 
            @click="openBaseModelSelection"
            class="w-full rounded-lg px-4 py-2.5 text-sm font-medium transition-all duration-300 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white shadow-md shadow-green-200/50 dark:shadow-emerald-900/50 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700">
            {{ selectedBaseModel ? selectedBaseModel.name : '选择基础模型' }}
          </button>
        </div>
      </div>

      <!-- 风格模型选择 -->
      <div class="model-selection mb-6">
        <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">
          风格模型 <span class="text-gray-500 dark:text-gray-400">(可选)</span>
        </label>
        <div class="flex space-x-3">
          <button 
            @click="openStyleModelSelection"
            class="w-full rounded-lg px-4 py-2.5 text-sm font-medium transition-all duration-300 bg-gradient-to-r from-teal-500 to-emerald-500 hover:from-teal-600 hover:to-emerald-600 text-white shadow-md shadow-emerald-200/50 dark:shadow-emerald-900/50 focus:outline-none focus:ring-2 focus:ring-emerald-300 dark:focus:ring-emerald-700">
            {{ selectedStyleModel ? selectedStyleModel.id : '选择风格模型' }}
          </button>
        </div>
      </div>

      <!-- 提示词输入 -->
      <div class="prompt-input mb-6">
        <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2" for="prompt">
          提示词 (Prompt)
        </label>
        <textarea 
          id="prompt" 
          v-model="prompt" 
          rows="4" 
          placeholder="描述您想要生成的图像..." 
          class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-2 px-3 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700 focus:border-green-400 dark:focus:border-emerald-600"
          ></textarea>
      </div>

      <!-- 负面提示词 -->
      <div class="negative-prompt mb-6">
        <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2" for="negative-prompt">
          负面提示词 (Negative Prompt)
        </label>
        <textarea 
          id="negative-prompt" 
          v-model="negativePrompt" 
          rows="3" 
          placeholder="描述您不希望出现在图像中的内容..."
          class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-2 px-3 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700 focus:border-green-400 dark:focus:border-emerald-600"></textarea>
      </div>

      <!-- 参数设置 -->
      <div class="parameters mb-6">
        <h3 class="text-md font-medium mb-3 text-gray-800 dark:text-gray-200">高级参数</h3>

        <div class="grid grid-cols-2 gap-4">
          <div class="steps-param">
            <label class="block text-gray-700 dark:text-gray-300 text-sm mb-1">迭代步数</label>
            <input type="range" min="1" max="50" v-model.number="steps" class="w-full accent-green-500 dark:accent-emerald-500" />
            <span class="text-sm text-gray-500 dark:text-gray-400">{{ steps }} 步</span>
          </div>

          <div class="cfg-scale-param">
            <label class="block text-gray-700 dark:text-gray-300 text-sm mb-1">CFG Scale</label>
            <input type="range" min="1" max="20" v-model.number="cfgScale" class="w-full accent-green-500 dark:accent-emerald-500" />
            <span class="text-sm text-gray-500 dark:text-gray-400">{{ cfgScale }}</span>
          </div>

          <div class="width-param">
            <label class="block text-gray-700 dark:text-gray-300 text-sm mb-1">宽度</label>
            <select v-model="width"
              class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-1 px-2 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700">
              <option value="1024">1024px</option>
              <option value="1440">1440px</option>
              <option value="2048">2048px</option>
            </select>
          </div>

          <div class="height-param">
            <label class="block text-gray-700 dark:text-gray-300 text-sm mb-1">高度</label>
            <select v-model="height"
              class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-1 px-2 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700">
              <option value="1024">1024px</option>
              <option value="1440">1440px</option>
              <option value="2048">2048px</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 参考图片上传 -->
      <div ref="referencePreviewRef" class="upload-group mb-6">
        <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2" for="image-upload">
          参考图片 (可选)
        </label>
        <div class="flex items-center">
          <div class="w-full px-1">
            <label for="image-upload"
              class="flex items-center justify-center rounded-lg px-4 py-2.5 text-sm font-medium transition-all duration-300 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white shadow-md shadow-green-200/50 dark:shadow-emerald-900/50 cursor-pointer w-full focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700">
              上传图片
              <input id="image-upload" ref="fileInputRef" type="file" accept="image/*" @change="handleFileUpload"
                class="hidden" />
            </label>
          </div>
        </div>
        
        <!-- 参考图片预览 -->
        <div v-if="props.selectedImage" class="mt-3 relative">
          <img :src="props.selectedImage" alt="参考图片" 
            class="w-full max-h-48 object-contain rounded-lg border border-gray-200" />
          <button @click="removeSelectedImage" 
            class="absolute top-2 right-2 bg-white/80 hover:bg-white rounded-full p-1 shadow-md">
            <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- 生成按钮 -->
      <div class="generate-btn-container mb-4">
        <!-- 任务状态显示 -->
        <div v-if="currentTaskId" class="task-status-container mb-4 p-4 rounded-lg bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="spinner-small"></div>
              <div>
                <p class="font-medium text-yellow-800 dark:text-yellow-200">任务进行中</p>
                <p class="text-sm text-yellow-600 dark:text-yellow-400">{{ taskStatusDescription }}</p>
                <p v-if="taskStatus?.progress" class="text-xs text-yellow-500 dark:text-yellow-300">
                  进度: {{ taskStatus.progress }}%
                </p>
              </div>
            </div>
          </div>
        </div>
        
        <button 
          @click="generateImage" 
          :disabled="loading || processingReference || !canSubmit"
          class="w-full rounded-lg px-4 py-3 text-sm font-medium transition-all duration-300 transform hover:scale-[1.02] active:scale-95 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white shadow-md shadow-green-200/50 dark:shadow-emerald-900/50 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          :class="{ 'opacity-50 cursor-not-allowed': loading || processingReference || !canSubmit }">
          {{ loading ? '生成中...' : processingReference ? '处理参考图中...' : currentTaskId ? '任务进行中...' : '生成图像' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';

// 初始参数类型
interface InitialParams {
  prompt?: string;
  negativePrompt?: string;
  steps?: number;
  cfgScale?: number;
  width?: number | string;
  height?: number | string;
}

// Props
const props = defineProps<{
  selectedBaseModel: any | null;
  selectedStyleModel: any | null;
  loading: boolean;
  processingReference: boolean;
  currentTaskId: string | null;
  taskStatus: any | null;
  selectedImage: string | null;
  initialParams?: InitialParams | null;
}>();

// Emits
const emit = defineEmits<{
  (e: 'open-base-model'): void;
  (e: 'open-style-model'): void;
  (e: 'generate', params: any): void;
  (e: 'file-upload', file: File): void;
  (e: 'remove-image'): void;
}>();

// 响应式数据
const prompt = ref('');
const negativePrompt = ref('');
const steps = ref(30);
const cfgScale = ref(7);
const width = ref('1024');
const height = ref('1024');
const selectedFile = ref<File | null>(null);
const uploadedImageUrl = ref<string | null>(null);

// 应用初始参数
const applyInitialParams = (params: InitialParams | null | undefined) => {
  if (!params) return;
  
  if (params.prompt !== undefined && params.prompt !== null) {
    prompt.value = params.prompt;
  }
  if (params.negativePrompt !== undefined && params.negativePrompt !== null) {
    negativePrompt.value = params.negativePrompt;
  }
  if (params.steps !== undefined && params.steps !== null) {
    steps.value = Number(params.steps);
  }
  if (params.cfgScale !== undefined && params.cfgScale !== null) {
    cfgScale.value = Number(params.cfgScale);
  }
  if (params.width !== undefined && params.width !== null) {
    width.value = String(params.width);
  }
  if (params.height !== undefined && params.height !== null) {
    height.value = String(params.height);
  }
};

// 监听 initialParams 变化
watch(() => props.initialParams, (newParams) => {
  applyInitialParams(newParams);
}, { immediate: true });

// 监听 selectedImage 变化，同步到 uploadedImageUrl
watch(() => props.selectedImage, (newImage) => {
  if (newImage) {
    uploadedImageUrl.value = newImage;
  }
}, { immediate: true });

// 计算属性：判断是否可以提交生成请求
const canSubmit = computed(() => {
  const hasBaseModel = !!props.selectedBaseModel;
  const isLoading = props.loading;
  const isProcessingReference = props.processingReference;
  const hasTaskRunning = !!props.currentTaskId;
  
  return hasBaseModel && !isLoading && !isProcessingReference && !hasTaskRunning;
});

// 计算属性：获取当前任务状态描述
const taskStatusDescription = computed(() => {
  if (!props.taskStatus) return '';
  
  switch (props.taskStatus.status) {
    case 'pending':
      return '任务排队中...';
    case 'processing':
      return '正在生成图像...';
    case 'completed':
      return '生成完成';
    case 'failed':
      return '生成失败';
    case 'cancelled':
      return '任务已取消';
    default:
      return '生成中...';
  }
});

const openBaseModelSelection = () => {
  emit('open-base-model');
};

const openStyleModelSelection = () => {
  emit('open-style-model');
};

const generateImage = () => {
  if (!prompt.value.trim()) {
    alert('请输入提示词');
    return;
  }

  if (!props.selectedBaseModel) {
    alert('请选择基础模型');
    return;
  }

  emit('generate', {
    prompt: prompt.value,
    negativePrompt: negativePrompt.value,
    steps: steps.value,
    cfgScale: cfgScale.value,
    width: width.value,
    height: height.value,
    selectedFile: selectedFile.value,
    uploadedImageUrl: uploadedImageUrl.value
  });
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files || !target.files[0]) {
    return;
  }

  selectedFile.value = target.files[0];
  emit('file-upload', target.files[0]);
};

const removeSelectedImage = () => {
  selectedFile.value = null;
  uploadedImageUrl.value = null;
  emit('remove-image');
};

</script>

<style scoped>
/* 与 Chat.vue 统一的布局 */
.right-panel {
  min-height: 100vh;
}

/* 加载动画 */
.spinner-small {
  width: 24px;
  height: 24px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>