/**
 * API 相关类型定义
 * 
 * 包含所有 API 请求/响应、错误处理相关的类型
 */

/**
 * 统一 API 响应包装类型
 * @template T - 数据类型
 */
export interface ApiResponse<T = any> {
  /** 响应状态码 */
  code: number
  /** 响应消息 */
  msg?: string
  message?: string
  /** 响应数据 */
  data?: T
  /** 响应时间戳 */
  timestamp?: number
  [key: string]: any
}

/**
 * 分页响应类型
 * @template T - 列表项类型
 */
export interface PageResponse<T> {
  /** 当前页码 */
  page?: number
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
  size?: number
  /** 总数 */
  total: number
  /** 总页数 */
  totalPages?: number
  pages?: number
  /** 列表数据 */
  records: T[]
  data?: T[]
  list?: T[]
}

/**
 * API 错误类型
 */
export interface ApiError extends Error {
  /** 错误代码 */
  code?: number
  /** 错误状态码 */
  status?: number
  /** 错误详情 */
  details?: any
}

/**
 * 登录相关类型
 */
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse extends ApiResponse {
  data?: {
    id: number | string
    username: string
    name: string
    email?: string
    role: string
    token: string
    [key: string]: any
  }
}

/**
 * 注册相关类型
 */
export interface RegisterRequest {
  username: string
  password: string
  email: string
  [key: string]: any
}

export interface RegisterResponse extends ApiResponse {
  success: boolean
}

/**
 * 凭证类型
 */
export interface Credentials {
  username: string
  password: string
}

/**
 * 用户相关类型
 */
export interface User {
  id: number
  username: string
  name: string
  email?: string
  role: string
  age?: number
  isActive?: boolean
  createdAt?: string
  updatedAt?: string
}

/**
 * 消息/对话相关类型
 */
export interface Message {
  type: 'USER' | 'ASSISTANT'
  content: string
  timestamp?: number
  id?: string
}

export interface Conversation {
  sessionid: string
  title: string
  messages: Message[]
  userid: string
  createdAt?: string
  updatedAt?: string
}

export interface RenamePayload {
  sessionid: string
  title: string
}

export interface RemovePayload {
  sessionid: string
  userid: string
}

/**
 * 会话删除相关类型
 */
export interface DeleteSessionRequestParams {
  userid: string
  sessionid: string
}

export interface DeleteSessionResponse extends ApiResponse {
  sessionid?: string
  userid?: string
  session?: {
    sessionid?: string
    userid?: string
  }
}

export interface DeleteSessionResult {
  sessionid: string
  userid: string
  raw?: DeleteSessionResponse
}

/**
 * 文档相关类型
 */
export interface DocumentInfo {
  id: string
  name: string
  fileName?: string
  size: number
  fileSize?: number
  uploadDate: string
  sessionId?: string
  userId: string
}

export interface UploadDocumentToSessionRequest {
  file: File
  userId: string
  sessionId: string
}

export interface UploadGlobalDocumentRequest {
  files: File[]
}

export interface GetDocumentsInSessionRequest {
  userId: string
  sessionId: string
}

/**
 * 日志相关类型
 */
export interface LogQueryParams {
  page?: number
  size?: number
  level?: string
  startDate?: string
  endDate?: string
  keyword?: string
  [key: string]: any
}

export interface LogEntry {
  id?: string | number
  timestamp: string
  level: 'DEBUG' | 'INFO' | 'WARN' | 'ERROR'
  message: string
  source?: string
  details?: any
}

export interface LogStatistics {
  totalLogs: number
  errorLogs: number
  warningLogs: number
  infoLogs: number
  debugLogs: number
  [key: string]: any
}

/**
 * 图片相关类型
 */
export interface ImagePost {
  id: number
  title: string
  image_title?: string
  image_url?: string
  imageUrl?: string
  description?: string
  userId?: number
  createdAt?: string
  updatedAt?: string
  commentCount?: number
  likeCount?: number
  status?: number
  [key: string]: any
}

/**
 * 图片帖子类型（与后端字段对应）
 */
export interface Imagepost {
  id: number
  userid: number
  userId?: number
  user_name?: string
  imageId?: number
  image_url?: string
  imageUrl?: string
  imageData?: number[] | string
  image_title?: string
  title?: string
  image_description?: string
  caption?: string
  status?: number
  published_at?: Date
  viewcount?: number
  likeCount?: number
  commentCount?: number
  created_at?: Date
  createdAt?: string
  updated_at?: Date
  updatedAt?: string
  imagesize?: string
  negativePrompt?: string
  steps?: string
  cfgScale?: string
  modelName?: string
  style?: string
  width?: number
  height?: number
}

/**
 * 图片帖子查询参数类型
 */
export interface Imagepostparam {
  page: number
  size: number
  title?: string
  userid?: number
}

/**
 * 评论相关类型
 */
export interface ImageComment {
  id?: number
  postId: number
  userId?: number
  userName?: string
  content: string
  parentId?: number | null
  likeCount?: number
  createdAt?: string
  updatedAt?: string
  replies?: ImageComment[]
}

export interface CommentPageResult extends PageResponse<ImageComment> {}

/**
 * 消息状态类型（用于 UI 消息提示）
 */
export interface MessageState {
  text: string
  type: 'success' | 'error' | 'warning' | 'info'
  show: boolean
  timer?: number
}

/**
 * 扩展的消息状态（包含定时器处理）
 */
export interface ExtendedMessageState extends MessageState {
  timer?: number
}
