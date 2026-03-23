<template>
  <div class="flex h-screen relative overflow-hidden image-generation-container">
    <!-- 左侧导航栏 -->
    <UserSidebar
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      :current-route="'image-generation'"
      @switch-conversation="switchConversation"
      @create-temporary-conversation="createTemporaryConversation"
      @remove="handleRemove"
    />
    <div class="flex-1 flex flex-col overflow-hidden">
    <!-- 背景装饰（与 Chat 一致） -->
    <div class="absolute inset-0 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-gray-900 dark:to-gray-800 z-0"></div>
    <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>
    <div class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-green-200 to-emerald-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>
    <div class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-emerald-200 to-teal-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>

    <div class="main-content flex flex-1 relative z-10 min-h-0">
      <!-- 左侧 - 当前生成结果显示（可上下滚动） -->
      <ImageResultPanel 
        :latest-generated-image="latestGeneratedImage"
        :loading="loading"
        @set-as-reference="setAsPromptReference"
      />

      <!-- 中间 - 用户图像列表（可上下滚动） -->
      <ImageListPanel 
        :user-images="userImages"
        :selected-user-image="selectedUserImage"
        :current-task-id="currentTaskId"
        :task-status="taskStatus"
        @select-image="selectUserImage"
      />

      <!-- 右侧 - 参数设置（可上下滚动） -->
      <ImageSettingsPanel 
        :selected-base-model="selectedBaseModel"
        :selected-style-model="selectedStyleModel"
        :loading="loading"
        :processing-reference="processingReference"
        :current-task-id="currentTaskId"
        :task-status="taskStatus"
        :selected-image="selectedImage"
        :initial-params="sameStyleParams"
        @open-base-model="openBaseModelSelection"
        @open-style-model="openStyleModelSelection"
        @generate="generateImage"
        @file-upload="handleFileUpload"
        @remove-image="removeSelectedImage"
      />
    </div>

    <!-- 基础模型选择弹窗 -->
    <ModelSelectionModal 
      :visible="showBaseModelSelection"
      model-type="base"
      :models="baseModels"
      :selected-model="selectedBaseModel"
      title="基础模型选择"
      @close="closeBaseModelModal"
      @select="selectBaseModel"
      @confirm="confirmBaseModelSelection"
      @load="loadBaseModels"
    />

    <!-- 风格模型选择弹窗 -->
    <ModelSelectionModal 
      :visible="showStyleModelSelection"
      model-type="style"
      :models="styleModels"
      :selected-model="selectedStyleModel"
      title="风格模型选择"
      @close="closeStyleModelModal"
      @select="selectStyleModel"
      @clear="clearStyleSelection"
      @confirm="confirmStyleModelSelection"
      @load="loadStyleModels"
    />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import UserSidebar from '@/shared/components/UserSidebar.vue';
import ImageResultPanel from '@/modules/user/components/image/ImageResultPanel.vue';
import ImageListPanel from '@/modules/user/components/image/ImageListPanel.vue';
import ImageSettingsPanel from '@/modules/user/components/image/ImageSettingsPanel.vue';
import ModelSelectionModal from '@/modules/user/components/image/ModelSelectionModal.vue';
import { Conversation } from '@/shared/types/type';
import { getUserId } from '@/utils/auth';
import { getImageSrc, convertUrlToFile } from '@/utils/image';
import { modelApi, imageApi } from '@/services/api';
import type { GeneratedImage, UserImage } from '@/types/image';
import type { BaseModel, StyleModel } from '@/types/model';
import type { TaskStatusData, GenerationParams } from '@/types/task';


// 导航栏相关状态
const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);

// 状态管理
const selectedFile = ref<File | null>(null);
const selectedImage = ref<string | null>(null);
const uploadedImageUrl = ref<string | null>(null);
const loading = ref(false);
const processingReference = ref(false);
const selectedBaseModel = ref<BaseModel | null>(null);
const selectedStyleModel = ref<StyleModel | null>(null);

// 画同款参数
const sameStyleParams = ref<{
  prompt?: string;
  negativePrompt?: string;
  steps?: number;
  cfgScale?: number;
  width?: number;
  height?: number;
} | null>(null);

// 异步任务相关状态
const currentTaskId = ref<string | null>(null);
const taskStatus = ref<TaskStatusData | null>(null);
const taskPollingTimer = ref<number | null>(null);
const isGenerating = ref(false);

// 最新生成的图像
const latestGeneratedImage = ref<GeneratedImage | null>(null);

// 用户图像列表
const userImages = ref<UserImage[]>([]);
const selectedUserImage = ref<UserImage | null>(null);

// 基础模型弹窗相关
const showBaseModelSelection = ref(false);
const baseModels = ref<BaseModel[]>([]);

// 风格模型弹窗相关
const showStyleModelSelection = ref(false);
const styleModels = ref<StyleModel[]>([]);

// 模型参数缓存（key: 模型名称/id，value: 模型参数）
const baseModelParamsCache = ref<Map<string, any>>(new Map());
const styleModelParamsCache = ref<Map<string, any>>(new Map());

// 当前正在加载参数的模型ID（用于显示loading状态）
const loadingModelId = ref<string | null>(null);

// 参考图预览区域 ref，用于设为参考图后滚动到该位置
const referencePreviewRef = ref<HTMLElement | null>(null);

// 轮询计数器和超时保护
const pollCount = ref(0);
const MAX_POLL_COUNT = 3600; // 最大轮询次数 (2小时，每2秒一次)
const START_TIME = ref<number>(0);

// 获取用户图像列表
const loadUserImages = async () => {
  try {
    const userId = getUserId();
    
    if (!userId) {
      console.error('无法获取用户ID');
      return;
    }

    const result = await imageApi.getUserImages(userId);

    if (Array.isArray(result)) {
      userImages.value = result.map(img => {
        return {
          ...img,
          timestamp: img.createTime || img.time || img.timestamp || new Date().toISOString()
        };
      });
      console.log('获取用户图像列表成功:', userImages.value);
    } else {
      console.error('返回的数据不是数组格式:', result);
    }
  } catch (error: any) {
    console.error('加载用户图像列表失败:', error);
    showToast(`加载图像列表失败：${error.message || '未知错误'}`, 'error');
  }
};

// 选择用户图像
const selectUserImage = (image: UserImage) => {
  selectedUserImage.value = image;
  // 将选中的图像设置为当前显示的图像
  latestGeneratedImage.value = {
    ...image,
    url: getImageSrc(image),
    model: '未知模型',
    prompt: image.prompt || ''
  };
};

// 设置为参考图（不弹窗，改为右侧参数区滚动到预览位置）
const setAsPromptReference = async (image: GeneratedImage) => {
  if (image) {
    processingReference.value = true; // 设置处理状态
    const imageUrl = getImageSrc(image);
    selectedImage.value = imageUrl;
    
    // 将图像URL存储到uploadedImageUrl参数中
    uploadedImageUrl.value = imageUrl;
    
    // 将图片URL转换为File对象以便上传
    const file = await convertUrlToFile(imageUrl);
    if (file) {
      selectedFile.value = file;
    }
    
    processingReference.value = false; // 重置处理状态
    nextTick(() => {
      referencePreviewRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  }
};

// 打开基础模型选择弹窗
const openBaseModelSelection = async () => {
  showBaseModelSelection.value = true;
  if (baseModels.value.length === 0) {
    await loadBaseModels();
  }
};

// 打开风格模型选择弹窗
const openStyleModelSelection = async () => {
  showStyleModelSelection.value = true;
  if (styleModels.value.length === 0) {
    await loadStyleModels();
  }
};

// 加载基础模型列表
const loadBaseModels = async () => {
  try {
    const result = await modelApi.getBaseModels();
    console.log('获取基础模型列表响应结果:', result);
    
    if (result.code === 1) {
      baseModels.value = Array.isArray(result.data) ? result.data : [];
      console.log('获取基础模型列表成功:', baseModels.value);
      
      // 预缓存所有基础模型的参数
      for (const model of baseModels.value) {
        if (model.name && !baseModelParamsCache.value.has(model.name)) {
          try {
            const params = await modelApi.getBaseModelParams(model.name);
            if (params && params.code === 1) {
              baseModelParamsCache.value.set(model.name, params.data);
            }
          } catch (e) {
            console.warn(`预缓存基础模型 ${model.name} 参数失败:`, e);
          }
        }
      }
      console.log('基础模型参数缓存完成:', baseModelParamsCache.value.size);
    } else {
      showToast(`获取基础模型列表失败: ${result.msg}`, 'error');
    }
  } catch (error: any) {
    showToast(`加载基础模型失败：${error.message || '未知错误'}`, 'error');
  }
};

// 加载风格模型列表
const loadStyleModels = async () => {
  try {
    const result = await modelApi.getStyleModels();
    
    if (result.code === 1) {
      styleModels.value = Array.isArray(result.data) ? result.data : [];
      console.log('获取风格模型列表成功:', styleModels.value);
      console.log('开始预缓存风格模型参数，共', styleModels.value.length, '个');
      
      // 预缓存所有风格模型的参数
      let cachedCount = 0;
      for (const model of styleModels.value) {
        if (model.id && !styleModelParamsCache.value.has(model.id)) {
          try {
            console.log('预缓存风格模型:', model.id);
            const params = await modelApi.getStyleModelParams(model.id);
            if (params && params.code === 1) {
              styleModelParamsCache.value.set(model.id, params.data);
              cachedCount++;
            } else {
              console.warn('风格模型参数返回异常:', model.id, params);
            }
          } catch (e) {
            console.warn(`预缓存风格模型 ${model.id} 参数失败:`, e);
          }
        } else {
          console.log('风格模型已缓存，跳过:', model.id);
        }
      }
      console.log('风格模型参数缓存完成: 成功', cachedCount, '/ 总数', styleModels.value.length, ' | 缓存Map大小:', styleModelParamsCache.value.size);
    } else {
      showToast(`获取风格模型列表失败: ${result.msg}`, 'error');
    }
  } catch (error: any) {
    showToast(`加载风格模型失败：${error.message || '未知错误'}`, 'error');
  }
};

// 选择基础模型（使用缓存，无网络延迟）
const selectBaseModel = (model: BaseModel) => {
  const cachedParams = baseModelParamsCache.value.get(model.name);
  if (cachedParams) {
    selectedBaseModel.value = {...model, ...cachedParams};
  } else {
    selectedBaseModel.value = model;
  }
};

// 选择风格模型（使用缓存，无网络延迟）
const selectStyleModel = (model: StyleModel) => {
  console.log('选择风格模型:', model.id);
  console.log('缓存Map内容:', Array.from(styleModelParamsCache.value.keys()));
  const cachedParams = styleModelParamsCache.value.get(model.id);
  console.log('从缓存获取参数:', cachedParams);
  if (cachedParams) {
    selectedStyleModel.value = {...model, ...cachedParams};
    console.log('选择风格模型，使用缓存参数:', selectedStyleModel.value);
  } else {
    console.warn('风格模型参数未在缓存中找到:', model.id);
    selectedStyleModel.value = model;
  }
};

// 清除风格模型选择
const clearStyleSelection = () => {
  selectedStyleModel.value = null;
};

// 确认基础模型选择
const confirmBaseModelSelection = (data: any) => {
  if (data.recommendedSteps) {
    // 更新参数
  }
  if (data.recommendedCfgScale) {
    // 更新参数
  }
  
  if (data.recommendedMaxResolution) {
    // 更新分辨率
  }
  
  showBaseModelSelection.value = false;
};

// 确认风格模型选择
const confirmStyleModelSelection = () => {
  showStyleModelSelection.value = false;
};

// 关闭基础模型弹窗
const closeBaseModelModal = () => {
  showBaseModelSelection.value = false;
};

// 关闭风格模型弹窗
const closeStyleModelModal = () => {
  showStyleModelSelection.value = false;
};

// 调用后端API生成图像
const generateImage = async (params: GenerationParams) => {
  isGenerating.value = true;
  loading.value = true;

  try {
    const userId = getUserId();
    const formData = new FormData();

    // 添加文件参数（如果有）
    if (params.selectedFile) {
      formData.append('image_file', params.selectedFile);
    }

    // 添加其他参数字段
    formData.append('userid', userId.toString());
    formData.append('prompt', params.prompt);
    formData.append('negativePrompt', params.negativePrompt);
    formData.append('model_name', selectedBaseModel.value?.id || '');
    if (selectedStyleModel.value) {
      formData.append('style', selectedStyleModel.value.filePath || '');
    }
    formData.append('steps', params.steps.toString());
    formData.append('cfg_scale', params.cfgScale.toString());
    formData.append('width', params.width);
    formData.append('height', params.height);

    if (params.uploadedImageUrl) {
      formData.append('image_url', params.uploadedImageUrl);
    }

    console.log('发送异步文生图请求到 /image/async_text_to_image，参数:', {
      userid: userId,
      prompt: params.prompt,
      negativePrompt: params.negativePrompt,
      model_name: selectedBaseModel.value?.id || '',
      style: selectedStyleModel.value?.filePath || '',
      steps: params.steps,
      cfg_scale: params.cfgScale,
      width: params.width,
      height: params.height,
      image_url: params.uploadedImageUrl,
      image_file: params.selectedFile ? '已上传' : '未上传'
    });

    const result = await imageApi.generateImage(formData);
    console.log('异步任务启动成功，任务ID:', result);

    // 保存任务ID并开始轮询
    currentTaskId.value = typeof result === 'string' ? result : result.data;
    // 持久化任务信息到localStorage
    if (currentTaskId.value) {
      localStorage.setItem('image_generation_task', currentTaskId.value);
      localStorage.setItem('image_generation_task_info', JSON.stringify({
        taskId: currentTaskId.value,
        startTime: Date.now(),
        prompt: params.prompt,
        model: selectedBaseModel.value?.name || ''
      }));
    }
    startTaskPolling();

    // 显示任务启动成功的提示
    showToast('图像生成任务已启动，正在后台处理中...', 'success');

  } catch (error: any) {
    console.error('启动异步图像生成失败:', error);
    showToast(error.message || '启动图像生成失败，请稍后重试', 'error');
    isGenerating.value = false;
    loading.value = false;
  }
};

// 检查任务状态并处理相应逻辑
const checkTaskStatus = (statusData: TaskStatusData): boolean => {
  if (!statusData || !statusData.status) {
    console.warn('任务状态数据不完整:', statusData);
    return false;
  }

  const status = statusData.status;
  const progress = statusData.progress || 0;
  const errorMessage = statusData.errorMessage || statusData.error || '';
  console.log('检查任务状态:', { status, progress, errorMessage });
  
  if (progress >= 100) {
    console.log('任务进度已达100%，停止轮询');
    handleTaskCompleted();
    return true;
  }
  
  if (errorMessage) {
    console.log('检测到任务错误:', errorMessage);
    handleTaskFailed();
    return true;
  }
  switch (status) {
    case 'completed':
      console.log('任务状态为completed，停止轮询');
      handleTaskCompleted();
      return true;
    case 'failed':
      console.log('任务状态为failed，停止轮询');
      handleTaskFailed();
      return true;
    case 'cancelled':
      console.log('任务状态为cancelled，停止轮询');
      handleTaskCancelled();
      return true;
    case 'running':
    case 'processing':
    case 'pending':
      console.log('任务进行中，继续轮询');
      return false;
    default:
      console.log('未知任务状态:', status, '但进度未达100%，继续轮询');
      return progress < 100;
  }
};

// 开始任务轮询
const startTaskPolling = () => {
  if (taskPollingTimer.value) {
    clearInterval(taskPollingTimer.value);
  }
  
  // 重置计数器
  pollCount.value = 0;
  START_TIME.value = Date.now();

  console.log('开始任务轮询，任务ID:', currentTaskId.value);

  taskPollingTimer.value = window.setInterval(async () => {
    // 增加轮询计数
    pollCount.value++;
    
    console.log(`第${pollCount.value}次轮询任务状态，任务ID: ${currentTaskId.value}`);
    
    // 检查是否超过最大轮询次数
    if (pollCount.value > MAX_POLL_COUNT) {
      console.warn('轮询次数超限，自动停止');
      stopTaskPolling();
      resetGenerationState();
      clearTaskStorage();
      showToast('任务执行时间过长，已自动停止监控', 'info');
      return;
    }
    
    // 检查是否超过2小时
    const elapsedTime = Date.now() - START_TIME.value;
    if (elapsedTime > 2 * 60 * 60 * 1000) { // 2小时
      console.warn('轮询时间超限，自动停止');
      stopTaskPolling();
      resetGenerationState();
      clearTaskStorage();
      showToast('任务执行时间过长，已自动停止监控', 'info');
      return;
    }

    if (!currentTaskId.value) {
      console.log('任务ID为空，停止轮询');
      stopTaskPolling();
      return;
    }

    try {
      const result = await imageApi.getTaskStatus(currentTaskId.value);
      console.log('收到任务状态响应:', result);
      
      if (result.code === 1 && result.data) {
        taskStatus.value = result.data;
        console.log(`任务状态更新 (${pollCount.value}次):`, taskStatus.value);
        
        // 关键安全检查：在任何情况下，进度100%都立即停止轮询
        const progress = result.data.progress || 0;
        console.log('当前进度:', progress);
        if (progress >= 100) {
          console.log('进度已达100%，强制停止轮询');
          handleTaskCompleted();
          return;
        }
        
        // 检查是否有业务层面的错误
        if (result.data.errorMessage || result.data.error) {
          console.log('检测到业务错误:', result.data.errorMessage || result.data.error);
          handleTaskFailed();
          return;
        }
        
        // 使用统一的状态检查函数（但进度100%已经在上面处理过了）
        const shouldStopPolling = checkTaskStatus(result.data);
        if (shouldStopPolling) {
          console.log('状态检查返回停止轮询');
          return;
        }
      } else {
        console.error('获取任务状态失败:', result.msg || '未知错误');
        
        // 如果返回错误码不是1，认为是业务错误
        if (result.code !== 1) {
          console.log('业务错误，停止轮询');
          handleTaskFailed();
          return;
        }
        
        const errorMsg = result.msg || '';
        if (errorMsg.includes('不存在') || errorMsg.includes('not found')) {
          console.log('任务不存在，停止轮询');
          stopTaskPolling();
          resetGenerationState();
          setTimeout(() => {
            clearTaskStorage();
          }, 5000);
          showToast('任务已完成或已过期', 'info');
          return;
        }
      }
    } catch (error) {
      console.error('轮询任务状态时发生网络错误:', error);
      
      // 网络错误处理 - 可以选择继续轮询或停止
      // 这里选择继续轮询，因为可能是临时网络问题
      console.warn('网络错误，将在下次轮询时重试');
    }
  }, 2000); // 每2秒轮询一次
};

// 停止任务轮询
const stopTaskPolling = () => {
  console.log('停止任务轮询被调用');
  
  if (taskPollingTimer.value) {
    console.log('清除轮询定时器:', taskPollingTimer.value);
    clearInterval(taskPollingTimer.value);
    taskPollingTimer.value = null;
  } else {
    console.log('没有活动的轮询定时器');
  }
  
  console.log('轮询停止完成');
};

// 处理任务完成
const handleTaskCompleted = () => {
  console.log('开始处理任务完成逻辑');
  
  stopTaskPolling();
  console.log('轮询已停止');
  
  // 安全地创建最新的生成图像对象
  const newImage: GeneratedImage = {
    url: taskStatus.value?.resultUrl || taskStatus.value?.imageUrl || '',
    timestamp: new Date().toISOString(),
    prompt: '', // 从参数中获取
    model: selectedBaseModel.value?.name || '未知模型',
    style: selectedStyleModel.value?.name || '',
    taskId: currentTaskId.value
  };

  console.log('创建的新图像对象:', newImage);

  // 设置为最新生成的图像
  latestGeneratedImage.value = newImage;
  console.log('最新生成图像已设置');

  // 刷新用户图像列表
  setTimeout(() => {
    loadUserImages();
  }, 1000);

  // 清空输入和状态，并清除存储
  resetGenerationState();
  clearTaskStorage();
  
  showToast('图像生成完成！', 'success');
  console.log('任务完成处理完毕');
};

// 处理任务失败
const handleTaskFailed = () => {
  stopTaskPolling();
  resetGenerationState();
  localStorage.removeItem('image_generation_task');
  localStorage.removeItem('image_generation_task_info');
  const errorMsg = taskStatus.value?.errorMessage || '图像生成失败';
  showToast(`生成失败: ${errorMsg}`, 'error');
};

// 处理任务取消
const handleTaskCancelled = () => {
  stopTaskPolling();
  resetGenerationState();
  localStorage.removeItem('image_generation_task');
  localStorage.removeItem('image_generation_task_info');
  showToast('任务已被取消', 'info');
};

// 任务管理函数
const clearTaskStorage = () => {
  localStorage.removeItem('image_generation_task');
  localStorage.removeItem('image_generation_task_info');
  console.log('任务存储信息已清除');
};

// 重置生成状态
const resetGenerationState = () => {
  console.log('重置生成状态');
  isGenerating.value = false;
  loading.value = false;
  currentTaskId.value = null;
  taskStatus.value = null;
  selectedFile.value = null;
  selectedImage.value = null;
  uploadedImageUrl.value = null;
  console.log('生成状态重置完成');
};

const handleFileUpload = (file: File) => {
  selectedFile.value = file;

  // 创建预览图片
  const reader = new FileReader();
  reader.onload = (e) => {
    selectedImage.value = e.target?.result as string;
  };
  reader.readAsDataURL(file);
};

// 删除选中的图片
const removeSelectedImage = () => {
  selectedFile.value = null;
  selectedImage.value = null;
  uploadedImageUrl.value = null;
};

// 导航栏相关方法
const switchConversation = (sessionid: string | number) => {
  currentConversationId.value = String(sessionid);
  // 可以在这里添加切换对话的逻辑
};

const createTemporaryConversation = () => {
  // 创建临时对话的逻辑
};

const handleRemove = (payload: { sessionid: string | number; userid?: string }) => {
  // 删除对话的逻辑
  console.log('删除对话:', payload.sessionid, payload.userid);
};

// 组件卸载时清理定时器
onUnmounted(() => {
  stopTaskPolling();
});

// 添加通用的Toast通知函数
const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  // 创建toast元素
  const toast = document.createElement('div');
  toast.className = `fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 ${
    type === 'success' ? 'bg-green-500 text-white' :
    type === 'error' ? 'bg-red-500 text-white' :
    'bg-blue-500 text-white'
  }`;
  toast.textContent = message;
  
  document.body.appendChild(toast);
  
  // 动画显示
  setTimeout(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translate(-50%, 0)';
  }, 10);
  
  // 根据类型设置不同的显示时间
  const displayTime = type === 'error' ? 4000 : 2500;
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translate(-50%, -20px)';
    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
      }
    }, 300);
  }, displayTime);
};

onMounted(() => {
  // 页面加载时自动获取模型列表
  loadBaseModels();
  loadStyleModels();
  
  // 检查是否有未完成的任务需要恢复
  const storedTaskId = localStorage.getItem('image_generation_task');
  const storedTaskInfo = localStorage.getItem('image_generation_task_info');
  
  if (storedTaskId && storedTaskInfo) {
    try {
      const taskInfo = JSON.parse(storedTaskInfo);
      console.log('发现未完成的任务，开始恢复:', { storedTaskId, taskInfo });
      
      // 恢复任务状态
      currentTaskId.value = storedTaskId;
      isGenerating.value = true;
      loading.value = true;
      
      // 开始轮询任务状态
      startTaskPolling();
      
      // 显示恢复提示
      setTimeout(() => {
        showToast('检测到未完成的图像生成任务，正在恢复状态...', 'info');
      }, 1000);
    } catch (error) {
      console.error('恢复任务失败:', error);
      localStorage.removeItem('image_generation_task');
      localStorage.removeItem('image_generation_task_info');
    }
  }
  
  // 检查是否有画同款参数 - 支持 sessionStorage 和 URL 查询参数
  let paramsToProcess = null;
  
  try {
    // 首先检查 sessionStorage - 画同款参数
    const sameStyleParamsStr = sessionStorage.getItem('sameStyleParams');
    if (sameStyleParamsStr) {
      try {
        paramsToProcess = JSON.parse(sameStyleParamsStr);
        // 清除sessionStorage中的参数
        sessionStorage.removeItem('sameStyleParams');
        console.log('从 sessionStorage 获取到画同款参数:', paramsToProcess);
      } catch (error) {
        console.error('解析 sessionStorage 参数失败:', error);
        sessionStorage.removeItem('sameStyleParams');
      }
    }
    
    // 检查是否有参考图参数（用做参考图功能）
    if (!paramsToProcess) {
      const referenceParamsStr = sessionStorage.getItem('referenceImageParams');
      if (referenceParamsStr) {
        try {
          const referenceParams = JSON.parse(referenceParamsStr);
          sessionStorage.removeItem('referenceImageParams');
          console.log('从 sessionStorage 获取到参考图参数:', referenceParams);
          
          // 只设置参考图，不设置其他参数
          if (referenceParams.referenceImageUrl) {
            selectedImage.value = referenceParams.referenceImageUrl;
            uploadedImageUrl.value = referenceParams.referenceImageUrl;
            
            // 直接使用图片URL，不再尝试转换为File对象（避免CORS错误）
            console.log('使用图片URL作为参考图:', referenceParams.referenceImageUrl);
            
            setTimeout(() => {
              showToast('已为您设置参考图', 'success');
            }, 500);
          }
        } catch (error) {
          console.error('解析参考图参数失败:', error);
          sessionStorage.removeItem('referenceImageParams');
        }
      }
    }
    
    // 如果 sessionStorage 中没有参数，检查 URL 查询参数
    if (!paramsToProcess) {
      const urlParams = new URLSearchParams(window.location.search);
      const imageUrl = urlParams.get('imageUrl');
      const imagePrompt = urlParams.get('imagePrompt');
      const imageTitle = urlParams.get('imageTitle');
      
      if (imageUrl || imagePrompt) {
        paramsToProcess = {
          prompt: imagePrompt || '',
          title: imageTitle || '',
          imageUrl: imageUrl || '',
          timestamp: Date.now()
        };
        
        // 清除URL参数
        window.history.replaceState({}, document.title, window.location.pathname);
        console.log('从 URL 参数获取到画同款参数:', paramsToProcess);
      }
    }
    
    // 处理参数
    if (paramsToProcess) {
      // 验证参数完整性
      if (!paramsToProcess.prompt && !paramsToProcess.imageUrl) {
        console.warn('画同款参数不完整，跳过自动填充');
        return;
      }
      
      try {
        // 保存画同款参数到状态，传递给 ImageSettingsPanel
        sameStyleParams.value = {
          prompt: paramsToProcess.prompt,
          negativePrompt: paramsToProcess.negativePrompt,
          steps: paramsToProcess.steps ? Number(paramsToProcess.steps) : undefined,
          cfgScale: paramsToProcess.cfgScale ? Number(paramsToProcess.cfgScale) : undefined,
          width: paramsToProcess.width ? Number(paramsToProcess.width) : undefined,
          height: paramsToProcess.height ? Number(paramsToProcess.height) : undefined
        };
        
        console.log('画同款参数已设置:', sameStyleParams.value);
        
        // 如果有图片URL，设置为参考图
        if (paramsToProcess.imageUrl) {
          selectedImage.value = paramsToProcess.imageUrl;
          uploadedImageUrl.value = paramsToProcess.imageUrl;
          
          // 直接使用图片URL，不再尝试转换为File对象（避免CORS错误）
          console.log('使用图片URL作为参考图:', paramsToProcess.imageUrl);
        }
        
        // 显示成功提示
        setTimeout(() => {
          showToast('已为您填充画同款参数，请选择模型后生成', 'success');
        }, 500);
        
      } catch (error) {
        console.error('处理画同款参数时出错:', error);
        showToast('参数填充过程中出现问题，请手动设置', 'error');
      }
    }
  } catch (error) {
    console.error('画同款参数处理整体流程出错:', error);
    showToast('画同款功能初始化失败', 'error');
  }
});
</script>

<style scoped>
/* 与 Chat.vue 统一的布局 */
.image-generation-container {
  min-height: 100vh;
}

.header-section {
  flex-shrink: 0;
}

/* 与 Chat.vue 一致的网格背景 */
.bg-grid-pattern {
  background-image:
    linear-gradient(to right, rgba(16, 185, 129, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(16, 185, 129, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

.main-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  min-height: 0;
}
</style>