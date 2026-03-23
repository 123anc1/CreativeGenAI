import { defineStore } from 'pinia';
import type { GeneratedImage, UserImage } from '@/types/image';
import type { BaseModel, StyleModel } from '@/types/model';
import type { TaskStatusData } from '@/types/task';

export const useImageStore = defineStore('image', {
  state: () => ({
    // 状态管理
    selectedFile: null as File | null,
    selectedImage: null as string | null,
    uploadedImageUrl: null as string | null,
    loading: false,
    processingReference: false,
    selectedBaseModel: null as BaseModel | null,
    selectedStyleModel: null as StyleModel | null,
    
    // 异步任务相关状态
    currentTaskId: null as string | null,
    taskStatus: null as TaskStatusData | null,
    isGenerating: false,
    
    // 最新生成的图像
    latestGeneratedImage: null as GeneratedImage | null,
    
    // 用户图像列表
    userImages: [] as UserImage[],
    selectedUserImage: null as UserImage | null,
    
    // 基础模型弹窗相关
    showBaseModelSelection: false,
    baseModels: [] as BaseModel[],
    
    // 风格模型弹窗相关
    showStyleModelSelection: false,
    styleModels: [] as StyleModel[],
  }),
  
  getters: {
    canSubmit: (state) => {
      const hasBaseModel = !!state.selectedBaseModel;
      const isLoading = state.loading;
      const isProcessingReference = state.processingReference;
      const hasTaskRunning = !!state.currentTaskId;
      
      return hasBaseModel && !isLoading && !isProcessingReference && !hasTaskRunning;
    },
    
    taskStatusDescription: (state) => {
      if (!state.taskStatus) return '';
      
      switch (state.taskStatus.status) {
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
    },
  },
  
  actions: {
    // 设置选中的文件
    setSelectedFile(file: File | null) {
      this.selectedFile = file;
    },
    
    // 设置选中的图像
    setSelectedImage(image: string | null) {
      this.selectedImage = image;
    },
    
    // 设置上传的图像URL
    setUploadedImageUrl(url: string | null) {
      this.uploadedImageUrl = url;
    },
    
    // 设置加载状态
    setLoading(loading: boolean) {
      this.loading = loading;
    },
    
    // 设置处理参考图状态
    setProcessingReference(processing: boolean) {
      this.processingReference = processing;
    },
    
    // 设置选中的基础模型
    setSelectedBaseModel(model: BaseModel | null) {
      this.selectedBaseModel = model;
    },
    
    // 设置选中的风格模型
    setSelectedStyleModel(model: StyleModel | null) {
      this.selectedStyleModel = model;
    },
    
    // 设置当前任务ID
    setCurrentTaskId(taskId: string | null) {
      this.currentTaskId = taskId;
    },
    
    // 设置任务状态
    setTaskStatus(status: TaskStatusData | null) {
      this.taskStatus = status;
    },
    
    // 设置生成状态
    setIsGenerating(generating: boolean) {
      this.isGenerating = generating;
    },
    
    // 设置最新生成的图像
    setLatestGeneratedImage(image: GeneratedImage | null) {
      this.latestGeneratedImage = image;
    },
    
    // 设置用户图像列表
    setUserImages(images: UserImage[]) {
      this.userImages = images;
    },
    
    // 设置选中的用户图像
    setSelectedUserImage(image: UserImage | null) {
      this.selectedUserImage = image;
    },
    
    // 设置基础模型选择弹窗显示状态
    setShowBaseModelSelection(show: boolean) {
      this.showBaseModelSelection = show;
    },
    
    // 设置基础模型列表
    setBaseModels(models: BaseModel[]) {
      this.baseModels = models;
    },
    
    // 设置风格模型选择弹窗显示状态
    setShowStyleModelSelection(show: boolean) {
      this.showStyleModelSelection = show;
    },
    
    // 设置风格模型列表
    setStyleModels(models: StyleModel[]) {
      this.styleModels = models;
    },
    
    // 重置生成状态
    resetGenerationState() {
      this.isGenerating = false;
      this.loading = false;
      this.currentTaskId = null;
      this.taskStatus = null;
      this.selectedFile = null;
      this.selectedImage = null;
      this.uploadedImageUrl = null;
    },
  },
});
