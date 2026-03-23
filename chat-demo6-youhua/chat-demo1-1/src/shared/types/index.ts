/**
 * TypeScript 类型定义统一导出文件
 * 
 * 所有类型定义都在这里统一导出，简化组件、服务中的import语句
 * 
 * @example
 * ```typescript
 * import type { ApiResponse, User, Conversation } from '@/types'
 * ```
 */

// API 相关类型
export type {
  ApiResponse,
  PageResponse,
  ApiError,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  Credentials,
  User,
  Message,
  Conversation,
  RenamePayload,
  RemovePayload,
  DeleteSessionRequestParams,
  DeleteSessionResponse,
  DeleteSessionResult,
  DocumentInfo,
  UploadDocumentToSessionRequest,
  UploadGlobalDocumentRequest,
  GetDocumentsInSessionRequest,
  LogQueryParams,
  LogEntry,
  LogStatistics,
  ImagePost,
  Imagepost,
  Imagepostparam,
  ImageComment,
  CommentPageResult,
  MessageState,
  ExtendedMessageState
} from './api'

// Composables 相关类型
export type {
  IMessageState,
  IAsyncResult,
  IValidationRule,
  IPaginationInfo,
  IAuthUser
} from '@/shared/composables/types'


