// 任务相关类型定义

export type TaskStatus = 'pending' | 'processing' | 'completed' | 'failed' | 'cancelled' | 'running';

export interface TaskInfo {
  taskId: string;
  startTime: number;
  prompt: string;
  model: string;
}

export interface TaskStatusData {
  status: TaskStatus;
  progress?: number;
  errorMessage?: string;
  error?: string;
  resultUrl?: string;
  imageUrl?: string;
}

export interface GenerationParams {
  prompt: string;
  negativePrompt: string;
  steps: number;
  cfgScale: number;
  width: string;
  height: string;
  selectedFile?: File | null;
  uploadedImageUrl?: string | null;
}
