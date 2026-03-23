/**
 * Composables 相关的 TypeScript 类型定义
 */

/**
 * 消息提示类型
 */
export interface IMessageState {
  text: string
  type: 'success' | 'error' | 'warning' | 'info'
  show: boolean
  timer?: number
}

/**
 * 异步操作结果类型
 */
export interface IAsyncResult<T> {
  loading: boolean
  error: Error | null
  data: T | null
}

/**
 * 表单验证规则类型
 */
export interface IValidationRule {
  required?: boolean
  minLength?: number
  maxLength?: number
  min?: number
  max?: number
  pattern?: string | RegExp
  custom?: (value: any) => boolean | string
}

/**
 * 分页信息类型
 */
export interface IPaginationInfo {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  startIndex: number
  endIndex: number
}

/**
 * 认证用户信息类型
 */
export interface IAuthUser {
  id: number
  username: string
  name: string
  role: string
  age?: number
  isActive?: boolean
}
