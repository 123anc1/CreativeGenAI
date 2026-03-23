// 模型相关类型定义

export interface BaseModel {
  id: string;
  name: string;
  description?: string;
  recommendedSteps?: number;
  recommendedCfgScale?: number;
  recommendedMaxResolution?: string;
}

export interface StyleModel {
  id: string;
  name?: string;
  description?: string;
  previewImage?: string;
  filePath?: string;
  applicableScenes?: string[];
}

export interface ModelSelection {
  baseModel: BaseModel | null;
  styleModel: StyleModel | null;
  styleStrength: number;
}
