ModelManagement.vue
<template>
  <div class="model-management-container">
    <div class="mb-6">
      <h2 class="text-lg font-medium text-gray-900 mb-4">模型管理</h2>
      
      <!-- 基础模型区域 -->
      <div class="mb-8">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-md font-medium text-gray-700">基础模型</h3>
          <button 
            @click="showAddBaseModelModal = true"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            添加基础模型
          </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div 
            v-for="model in baseModels" 
            :key="model.id"
            class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
          >
            <div class="flex justify-between items-start">
              <div>
                <h4 class="font-medium text-gray-900">{{ model.name }}</h4>
                <p class="text-sm text-gray-500 mt-1">{{ model.modelType }}</p>
                <p class="text-xs text-gray-400 mt-2">{{ model.description }}</p>
              </div>
              <span 
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                :class="model.isEnabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ model.isEnabled ? '启用' : '禁用' }}
              </span>
            </div>
            <div class="mt-3 flex space-x-2">
              <button 
                @click="selectBaseModel(model)"
                class="text-xs px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
              >
                选择
              </button>
              <button 
                @click="editBaseModel()"
                class="text-xs px-2 py-1 bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
              >
                编辑
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 风格模型区域 -->
      <div>
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-md font-medium text-gray-700">风格模型</h3>
          <button 
            @click="showAddStyleModelModal = true"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            添加风格模型
          </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div 
            v-for="model in styleModels" 
            :key="model.id"
            class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
          >
            <div class="flex justify-between items-start">
              <div>
                <h4 class="font-medium text-gray-900">{{ model.name }}</h4>
                <p class="text-sm text-gray-500 mt-1">{{ model.loraType }}</p>
                <p class="text-xs text-gray-400 mt-2">{{ model.description }}</p>
              </div>
              <div v-if="model.previewImage" class="w-10 h-10 rounded overflow-hidden">
                <img :src="model.previewImage" :alt="model.name" class="w-full h-full object-cover">
              </div>
              <div v-else class="w-10 h-10 rounded overflow-hidden bg-gray-200 flex items-center justify-center">
                <span class="text-gray-500 text-xs">无预览</span>
              </div>
            </div>
            <div class="mt-3 flex space-x-2">
              <button 
                @click="selectStyleModel(model)"
                class="text-xs px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
              >
                选择
              </button>
              <button 
                @click="editStyleModel()"
                class="text-xs px-2 py-1 bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
              >
                编辑
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加基础模型模态框 -->
    <div v-if="showAddBaseModelModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75" @click="showAddBaseModelModal = false"></div>
        <div class="relative bg-white rounded-lg max-w-lg w-full mx-4 p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900">添加基础模型</h3>
            <button @click="showAddBaseModelModal = false" class="text-gray-400 hover:text-gray-500">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <form @submit.prevent="addBaseModel">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">名称 *</label>
                <input 
                  v-model="newBaseModel.name" 
                  type="text" 
                  required
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">模型类型 *</label>
                <input 
                  v-model="newBaseModel.modelType" 
                  type="text" 
                  required
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">描述</label>
                <textarea 
                  v-model="newBaseModel.description"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                ></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">
                  <input 
                    v-model="newBaseModel.isEnabled" 
                    type="checkbox"
                    class="rounded"
                  >
                  启用模型
                </label>
              </div>
            </div>
            
            <div class="mt-6 flex justify-end space-x-3">
              <button 
                type="button" 
                @click="showAddBaseModelModal = false"
                class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                取消
              </button>
              <button 
                type="submit"
                class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
              >
                添加
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 添加风格模型模态框 -->
    <div v-if="showAddStyleModelModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75" @click="showAddStyleModelModal = false"></div>
        <div class="relative bg-white rounded-lg max-w-lg w-full mx-4 p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900">添加风格模型</h3>
            <button @click="showAddStyleModelModal = false" class="text-gray-400 hover:text-gray-500">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <form @submit.prevent="addStyleModel">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">名称 *</label>
                <input 
                  v-model="newStyleModel.name" 
                  type="text" 
                  required
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">类型 *</label>
                <input 
                  v-model="newStyleModel.loraType" 
                  type="text" 
                  required
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">关联基础模型ID *</label>
                <input 
                  v-model="newStyleModel.baseModelId" 
                  type="text" 
                  required
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">描述</label>
                <textarea 
                  v-model="newStyleModel.description"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                ></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">预览图URL</label>
                <input 
                  v-model="newStyleModel.previewImage" 
                  type="text"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                >
              </div>
            </div>
            
            <div class="mt-6 flex justify-end space-x-3">
              <button 
                type="button" 
                @click="showAddStyleModelModal = false"
                class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                取消
              </button>
              <button 
                type="submit"
                class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
              >
                添加
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { 
  getBaseModels as apiGetBaseModels, 
  getStyleModels as apiGetStyleModels,
  selectBaseModel as apiSelectBaseModel,
  selectStyleModel as apiSelectStyleModel,
  addBaseModel as apiAddBaseModel,
  addStyleModel as apiAddStyleModel,
} from '@/modules/admin/services/model';

// 响应式数据
const baseModels = ref<any[]>([]);
const styleModels = ref<any[]>([]);
const showAddBaseModelModal = ref(false);
const showAddStyleModelModal = ref(false);

// 新模型数据
const newBaseModel = ref({
  name: '',
  modelType: '',
  description: '',
  isEnabled: true
});

const newStyleModel = ref({
  name: '',
  loraType: '',
  baseModelId: '',
  description: '',
  previewImage: ''
});

// 获取模型数据
const loadModels = async () => {
  try {
    console.log('开始加载模型数据...');
    // 获取基础模型
    const baseResponse: any = await apiGetBaseModels();
    console.log('基础模型响应:', baseResponse);
    if (baseResponse && baseResponse.data) {
      baseModels.value = Array.isArray(baseResponse.data) ? baseResponse.data : [];
    } else {
      baseModels.value = baseResponse || [];
    }

    // 获取风格模型
    const styleResponse: any = await apiGetStyleModels();
    console.log('风格模型响应:', styleResponse);
    if (styleResponse && styleResponse.data) {
      styleModels.value = Array.isArray(styleResponse.data) ? styleResponse.data : [];
    } else {
      styleModels.value = styleResponse || [];
    }
    
    console.log('模型数据加载完成:', {
      baseModels: baseModels.value.length,
      styleModels: styleModels.value.length
    });
  } catch (error: any) {
    console.error('加载模型失败:', error);
    alert(`加载模型失败: ${error.message || error}`);
  }
};

// 选择基础模型
const selectBaseModel = async (model: any) => {
  try {
    await apiSelectBaseModel(model.id);
    alert(`已选择基础模型: ${model.name}`);
  } catch (error: any) {
    alert(`选择基础模型失败: ${error.message || error}`);
  }
};

// 选择风格模型
const selectStyleModel = async (model: any) => {
  try {
    await apiSelectStyleModel(model.id);
    alert(`已选择风格模型: ${model.name}`);
  } catch (error: any) {
    alert(`选择风格模型失败: ${error.message || error}`);
  }
};

// 编辑基础模型
const editBaseModel = () => {
  alert('编辑功能将在后续版本中实现');
};

// 编辑风格模型
const editStyleModel = () => {
  alert('编辑功能将在后续版本中实现');
};

// 添加基础模型
const addBaseModel = async () => {
  try {
    await apiAddBaseModel(newBaseModel.value);
    alert('基础模型添加成功');
    showAddBaseModelModal.value = false;
    // 重置表单
    newBaseModel.value = {
      name: '',
      modelType: '',
      description: '',
      isEnabled: true
    };
    // 重新加载数据
    await loadModels();
  } catch (error: any) {
    alert(`添加基础模型失败: ${error.message || error}`);
  }
};

// 添加风格模型
const addStyleModel = async () => {
  try {
    await apiAddStyleModel(newStyleModel.value);
    alert('风格模型添加成功');
    showAddStyleModelModal.value = false;
    // 重置表单
    newStyleModel.value = {
      name: '',
      loraType: '',
      baseModelId: '',
      description: '',
      previewImage: ''
    };
    // 重新加载数据
    await loadModels();
  } catch (error: any) {
    alert(`添加风格模型失败: ${error.message || error}`);
  }
};

// 监听路由变化
const route = useRoute();

watch(
  () => route.path,
  (newPath: string) => {
    if (newPath === '/admin/models') {
      loadModels();
    }
  }
);

onMounted(() => {
  loadModels();
});
</script>

<style scoped>
.model-management-container {
  padding: 1rem;
}
</style>