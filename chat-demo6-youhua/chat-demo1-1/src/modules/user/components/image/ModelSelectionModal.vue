<template>
  <div v-if="visible" class="modal-overlay" @click="closeModal">
    <div class="modal-content bg-white dark:bg-gray-800 border border-green-100 dark:border-emerald-900 rounded-xl shadow-2xl shadow-green-200/30 dark:shadow-emerald-900/30 backdrop-blur-sm" @click.stop>
      <div class="modal-header border-b border-green-100 dark:border-emerald-900 bg-gradient-to-r from-green-50/50 to-emerald-50/50 dark:from-emerald-900/30 dark:to-green-900/30">
        <h3 class="text-lg font-bold text-gray-800 dark:text-gray-200">{{ title }}</h3>
        <button @click="closeModal" class="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 rounded-lg p-1 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      
      <div class="modal-body flex">
        <!-- 左侧模型列表（网格，一行约 3～4 个） -->
        <div class="model-list w-2/5 pr-4 border-r border-green-100 dark:border-emerald-900 flex flex-col min-w-0">
          <h4 class="font-bold mb-3 text-gray-800 dark:text-gray-200 flex-shrink-0">{{ modelType === 'base' ? '基础模型' : '风格模型' }}</h4>
          <div class="grid grid-cols-3 gap-3 flex-1 min-h-0 overflow-y-auto scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800 content-start">
            <div 
              v-for="model in models" 
              :key="modelType + '-' + (model.id || model.name)"
              @click="selectModel(model)"
              :class="[
                modelType === 'base' ? 'base-model-card p-3 border-2 rounded-xl cursor-pointer transition-all duration-200 flex flex-col items-center justify-center min-h-[72px] text-center' : 'style-model-card rounded-xl border-2 cursor-pointer transition-all duration-200 overflow-hidden flex flex-col',
                selectedModel?.id === model.id || selectedModel?.name === model.name 
                  ? 'border-green-500 dark:border-emerald-500 bg-green-50 dark:bg-emerald-900/40 ring-2 ring-green-200 dark:ring-emerald-800' 
                  : 'border-green-200 dark:border-emerald-800 hover:border-green-400 dark:hover:border-emerald-600 hover:bg-green-50/50 dark:hover:bg-emerald-900/20'
              ]"
            >
              <template v-if="modelType === 'style'">
                <div class="aspect-square w-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
                  <img v-if="model.previewImage" :src="model.previewImage" :alt="model.id" class="w-full h-full object-cover" />
                  <span v-else class="text-gray-400 dark:text-gray-500 text-xs">无预览</span>
                </div>
                <div class="p-2 text-center flex-shrink-0">
                  <span class="font-medium text-xs text-gray-800 dark:text-gray-200 line-clamp-1">{{ model.id }}</span>
                </div>
              </template>
              <template v-else>
                <span class="font-medium text-sm text-gray-800 dark:text-gray-200 line-clamp-2">{{ model.name }}</span>
              </template>
            </div>
            
            <!-- 清除选择选项（仅风格模型） -->
            <div 
              v-if="modelType === 'style'"
              class="style-model-card rounded-xl border-2 border-dashed border-green-300 dark:border-emerald-700 cursor-pointer flex flex-col items-center justify-center min-h-[100px] text-gray-500 dark:text-gray-400 hover:bg-green-50/50 dark:hover:bg-emerald-900/20 transition-colors"
              @click="clearSelection"
            >
              <span class="text-xs font-medium">不使用风格模型</span>
            </div>
            
            <div v-if="models.length === 0" class="col-span-3 text-center text-gray-500 dark:text-gray-400 py-8">
              <p>暂无{{ modelType === 'base' ? '基础' : '风格' }}模型</p>
              <button @click="loadModels" class="mt-2 text-green-600 dark:text-emerald-400 hover:underline">点击加载</button>
            </div>
          </div>
        </div>
        
        <!-- 右侧模型参数详情 -->
        <div class="model-details flex-1 min-w-0 pl-4 overflow-y-auto scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800">
          <h4 class="font-bold mb-3 text-gray-800 dark:text-gray-200">模型参数</h4>
          <div v-if="selectedModel" class="model-detail-content rounded-xl border border-green-100 dark:border-emerald-900 bg-green-50/50 dark:bg-emerald-900/20 p-4">
            <h5 class="font-medium mb-2 text-gray-800 dark:text-gray-200">{{ selectedModel.name || selectedModel.id }}</h5>
            <p class="text-sm text-gray-600 dark:text-gray-400 mb-4">{{ selectedModel.description }}</p>
            
            <!-- 基础模型参数 -->
            <div v-if="modelType === 'base'">
              <div class="mb-4">
                <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">推荐迭代步数</label>
                <input type="number" 
                  v-model.number="recommendedSteps" 
                  min="1"
                  max="50"
                  class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-2 px-3 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700" 
                  placeholder="输入推荐的迭代步数">
              </div>
              
              <div class="mb-4">
                <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">推荐CFG Scale</label>
                <input type="number" 
                  v-model.number="recommendedCfgScale" 
                  class="w-full border border-green-200 dark:border-emerald-800 rounded-lg py-2 px-3 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700" 
                  placeholder="输入推荐的CFG Scale">
              </div>
              
              <div class="mb-4">
                <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">支持的最大分辨率</label>
                <div class="grid grid-cols-3 gap-2">
                  <label class="inline-flex items-center text-gray-700 dark:text-gray-300">
                    <input type="radio" v-model="recommendedMaxResolution" value="1024x1024" class="form-radio text-green-500 dark:text-emerald-500">
                    <span class="ml-2">1024x1024</span>
                  </label>
                  <label class="inline-flex items-center text-gray-700 dark:text-gray-300">
                    <input type="radio" v-model="recommendedMaxResolution" value="1440x1440" class="form-radio text-green-500 dark:text-emerald-500">
                    <span class="ml-2">1440x1440</span>
                  </label>
                  <label class="inline-flex items-center text-gray-700 dark:text-gray-300">
                    <input type="radio" v-model="recommendedMaxResolution" value="2048x2048" class="form-radio text-green-500 dark:text-emerald-500">
                    <span class="ml-2">2048x2048</span>
                  </label>
                </div>
              </div>
            </div>
            
            <!-- 风格模型参数 -->
            <div v-else>
              <div class="mb-4">
                <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">风格强度</label>
                <input type="range" min="0.1" max="1" step="0.1" 
                  v-model.number="styleStrength" 
                  class="w-full accent-green-500 dark:accent-emerald-500" />
                <span class="text-sm text-gray-500 dark:text-gray-400">{{ styleStrength }}</span>
              </div>
              
              <div class="mb-4">
                <label class="block text-gray-700 dark:text-gray-300 text-sm font-bold mb-2">适用场景</label>
                <ul class="list-disc pl-5 text-sm text-gray-600 dark:text-gray-400">
                  <li v-for="(scene, idx) in selectedModel.applicableScenes || []" :key="idx">{{ scene }}</li>
                  <li v-if="!selectedModel.applicableScenes || selectedModel.applicableScenes.length === 0">暂无指定场景</li>
                </ul>
              </div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 dark:text-gray-400 py-10">
            <p>请选择一个{{ modelType === 'base' ? '基础' : '风格' }}模型查看参数</p>
          </div>
        </div>
      </div>
      
      <div class="modal-footer p-4 flex justify-end border-t border-green-100 dark:border-emerald-900">
        <button 
          @click="confirmSelection" 
          :disabled="modelType === 'base' && !selectedModel"
          class="rounded-lg px-6 py-2.5 text-sm font-medium transition-all duration-300 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white shadow-md shadow-green-200/50 dark:shadow-emerald-900/50 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-green-300 dark:focus:ring-emerald-700"
          :class="{ 'opacity-50 cursor-not-allowed': modelType === 'base' && !selectedModel }">
          确认选择
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Props
const props = defineProps<{
  visible: boolean;
  modelType: 'base' | 'style';
  models: any[];
  selectedModel: any | null;
  title: string;
}>();

// Emits
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'select', model: any): void;
  (e: 'clear'): void;
  (e: 'confirm', data: any): void;
  (e: 'load'): void;
}>();

// 响应式数据
const recommendedSteps = ref(30);
const recommendedCfgScale = ref(7);
const recommendedMaxResolution = ref('1024x1024');
const styleStrength = ref(0.5);

const closeModal = () => {
  emit('close');
};

const selectModel = (model: any) => {
  emit('select', model);
};

const clearSelection = () => {
  emit('clear');
};

const confirmSelection = () => {
  if (props.modelType === 'base' && !props.selectedModel) {
    return;
  }
  
  const data = props.modelType === 'base' ? {
    model: props.selectedModel,
    recommendedSteps: recommendedSteps.value,
    recommendedCfgScale: recommendedCfgScale.value,
    recommendedMaxResolution: recommendedMaxResolution.value
  } : {
    model: props.selectedModel,
    styleStrength: styleStrength.value
  };
  
  emit('confirm', data);
};

const loadModels = () => {
  emit('load');
};
</script>

<style scoped>
/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.modal-content {
  width: 90%;
  max-width: 900px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 16px;
  flex: 1;
  overflow: hidden;
}

.modal-footer {
  padding: 16px;
}

/* 模型卡片样式 */
.base-model-card {
  min-height: 72px;
}

.style-model-card {
  min-height: 100px;
}
</style>