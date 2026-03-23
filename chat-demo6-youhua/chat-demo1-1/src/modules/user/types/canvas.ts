import { type Ref } from 'vue';

export interface Layer {
  id: string;
  imageId: string | number | null;
  imageUrl: string | null;
  x: number;
  y: number;
  width: number;
  height: number;
  opacity: number;
  visible: boolean;
  zIndex: number;
}

export interface Asset {
  id: string | number;
  userid: string | number;
  url: string;
  prompt: string;
  timestamp: string | number;
}

export interface ImageSaveRequest {
  imageBase64?: string;
  imageUrl?: string;
}

export interface SessionData {
  sessionId: string;
  text: string;
  layers: Layer[];
}

export interface CanvasState {
  isDragging: Ref<boolean>;
  lastX: Ref<number>;
  lastY: Ref<number>;
  offsetX: Ref<number>;
  offsetY: Ref<number>;
  scale: Ref<number>;
  isSpacePressed: Ref<boolean>;
}

export interface LayerState {
  layers: Ref<Layer[]>;
  selectedLayerId: Ref<string | null>;
  nextLayerId: Ref<number>;
  isDraggingLayer: Ref<boolean>;
  draggingLayerId: Ref<string | null>;
  layerStartX: Ref<number>;
  layerStartY: Ref<number>;
  previousSelectedLayerId: Ref<string | null>;
}

export interface DragState {
  isDraggingImage: Ref<boolean>;
  dragStartTime: Ref<number>;
  dragStartPos: Ref<{ x: number; y: number }>;
}

export interface LocalRedrawState {
  isLocalRedrawMode: Ref<boolean>;
  localRedrawLayerId: Ref<string | null>;
  drawPoints: Ref<{ x: number; y: number }[]>;
  brushSize: Ref<number>;
  canvasContainer: Ref<HTMLElement | null>;
}

export interface AssetState {
  showAssetsModal: Ref<boolean>;
  loadingAssets: Ref<boolean>;
  userAssets: Ref<Asset[]>;
  selectedAssets: Ref<Asset[]>;
}

export interface CanvasConfig {
  CANVAS_SIZE: number;
  CANVAS_CENTER: number;
  LAYER_SPACING: number;
  DEFAULT_LAYER_WIDTH: number;
  DEFAULT_LAYER_HEIGHT: number;
  MAX_LAYER_SIZE: number;
  MIN_SCALE: number;
  MAX_SCALE: number;
  ZOOM_IN_FACTOR: number;
  ZOOM_OUT_FACTOR: number;
  WHEEL_ZOOM_FACTOR: number;
}

export const CANVAS_CONFIG: CanvasConfig = {
  CANVAS_SIZE: 4000,
  CANVAS_CENTER: 2000,
  LAYER_SPACING: 20,
  DEFAULT_LAYER_WIDTH: 200,
  DEFAULT_LAYER_HEIGHT: 200,
  MAX_LAYER_SIZE: 256,
  MIN_SCALE: 0.1,
  MAX_SCALE: 5,
  ZOOM_IN_FACTOR: 1.2,
  ZOOM_OUT_FACTOR: 0.8,
  WHEEL_ZOOM_FACTOR: 0.9
};
