// 图像相关类型定义

export interface GeneratedImage {
  url: string;
  timestamp: string;
  prompt: string;
  model: string;
  style?: string;
  taskId?: string | null;
  imagedata?: string;
  image_data?: string;
  imageUrl?: string;
  src?: string;
  picture?: string;
  image_url?: string;
  createTime?: string;
  time?: string;
}

export interface UserImage {
  id?: string;
  imagedata: string;
  timestamp: string;
  prompt?: string;
  createTime?: string;
  time?: string;
}

export interface ProcessedUserImage extends UserImage {
  processedImageUrl: string;
}
