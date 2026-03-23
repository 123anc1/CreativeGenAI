// 消息类型定义
export interface Message {
  type: "USER" | "ASSISTANT";
  content: string;
}

// 对话类型定义
export interface Conversation {
  sessionid: string;
  title: string;
  messages: Message[];
  userid: string;
}

// 重命名载荷类型定义
export interface RenamePayload {
  sessionid: string;
  title: string;
}

// 删除载荷类型定义
export interface RemovePayload {
  sessionid: string;
  userid: string;
}

// 登录请求类型定义
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应类型定义
export interface LoginResponse {
  id?: string;
  name?: string;
  email?: string;
  token?: string;
  [key: string]: any;
}

// 注册请求类型定义
export interface RegisterRequest {
  username: string;
  password: string;
  name: string;
  age: number;
}

// 注册响应类型定义
export interface RegisterResponse {
  success: boolean;
  message?: string;
  [key: string]: any;
}

// 凭证类型定义
export interface Credentials {
  username: string;
  password: string;
}

// 消息状态类型定义
export interface MessageState {
  text: string;
  type: "success" | "error";
  show: boolean;
}

// 删除会话请求参数类型定义
export interface DeleteSessionRequestParams {
  userid: string;
  sessionid: string;
}

// 删除会话响应类型定义
export interface DeleteSessionResponse {
  sessionid?: string;
  userid?: string;
  session?: {
    sessionid?: string;
    userid?: string;
  };
  [key: string]: any;
}

// 删除会话结果类型定义
export interface DeleteSessionResult {
  sessionid: string;
  userid: string;
  raw?: DeleteSessionResponse;
}

// 文档信息类型定义
export interface DocumentInfo {
  id: string;
  name: string;
  size: number;
  uploadDate: string;
  sessionId?: string;
  userId: string;
}

// 上传文档到会话请求类型定义
export interface UploadDocumentToSessionRequest {
  file: File;
  userId: string;
  sessionId: string;
}

// 全局上传文档请求类型定义
export interface UploadGlobalDocumentRequest {
  files: File[];
}

// 获取会话中文档请求类型定义
export interface GetDocumentsInSessionRequest {
  userId: string;
  sessionId: string;
}

// 日志查询参数类型定义
export interface LogQueryParams {
  page?: number;
  size?: number;
  level?: string;
  startDate?: string;
  endDate?: string;
  keyword?: string;
  [key: string]: any;
}

// 日志统计信息类型定义
export interface LogStatistics {
  totalLogs: number;
  errorLogs: number;
  warningLogs: number;
  infoLogs: number;
  debugLogs: number;
  [key: string]: any;
}
// 图片评论类型定义
export interface ImageComment {
  id?: number;
  postId: number;
  userId?: number;
  userName?: string;
  content: string;
  parentId?: number | null;
  likeCount?: number;
  createdAt?: string;
  updatedAt?: string;
  replies?: ImageComment[];
}

// 评论分页结果类型定义
export interface CommentPageResult {
  records: ImageComment[];
  total: number;
  page?: number;
  pageSize?: number;
}