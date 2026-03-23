/**
 * 系统消息常量
 * 
 * 统一管理项目中的各种提示信息和错误消息
 * 便于维护和国际化处理
 * 
 * @example
 * ```typescript
 * import { messages } from '@/shared/constants/messages'
 * 
 * showMessage(messages.success.login)
 * showMessage(messages.error.networkError, 'error')
 * ```
 */

/**
 * 成功消息
 */
export const successMessages = {
  login: '登录成功，正在跳转...',
  logout: '已成功退出登录',
  register: '注册成功，请登录',
  update: '更新成功',
  delete: '删除成功',
  create: '创建成功',
  upload: '上传成功',
  submit: '提交成功',
  save: '保存成功',
  copy: '已复制到剪贴板',
  action: '操作成功',
}

/**
 * 错误消息
 */
export const errorMessages = {
  networkError: '网络错误，请检查网络连接',
  timeout: '请求超时，请重试',
  notFound: '请求的资源不存在',
  forbidden: '您没有权限执行此操作',
  unauthorized: '请先登录',
  loginFailed: '登录失败，请检查用户名或密码',
  registerFailed: '注册失败，请稍后重试',
  uploadFailed: '上传失败，请重试',
  deleteFailed: '删除失败，请重试',
  updateFailed: '更新失败，请重试',
  serverError: '服务器错误，请稍后重试',
  unknown: '发生错误，请稍后重试',
}

/**
 * 验证消息
 */
export const validationMessages = {
  required: (field: string) => `${field}不能为空`,
  minLength: (field: string, min: number) => `${field}长度至少为${min}`,
  maxLength: (field: string, max: number) => `${field}长度不能超过${max}`,
  invalidEmail: '邮箱格式不正确',
  invalidPhone: '电话格式不正确',
  invalidUsername: '用户名格式不正确（3-20个字符，仅限字母、数字、下划线）',
  passwordTooWeak: '密码强度不够（至少8位，需包含大小写字母和数字）',
  passwordMismatch: '两次输入的密码不一致',
  usernameExists: '用户名已存在',
  emailExists: '邮箱已被注册',
}

/**
 * 确认消息
 */
export const confirmMessages = {
  deleteUser: '确定要删除这个用户吗？此操作无法撤销。',
  deleteComment: '确定要删除这条评论吗？',
  deletePost: '确定要删除这篇文章吗？此操作无法撤销。',
  logout: '确定要退出登录吗？',
  clearData: '这将清除所有数据，确定继续吗？',
}

/**
 * 提示消息
 */
export const infoMessages = {
  loading: '加载中...',
  processing: '处理中...',
  noData: '暂无数据',
  noResults: '没有找到匹配的结果',
  empty: '列表为空',
  noMoreData: '没有更多数据了',
  selectOption: '请选择',
  selectDate: '请选择日期',
}

/**
 * 警告消息
 */
export const warningMessages = {
  unsavedChanges: '您有未保存的更改，确定要离开吗？',
  lowDiskSpace: '磁盘空间即将满，请清理一些文件',
  sessionExpired: '登录已过期，请重新登录',
  maintenanceMode: '系统正在维护中，请稍后再试',
  dataSync: '数据同步中，请勿刷新页面',
}

/**
 * 统一导出
 */
export const messages = {
  success: successMessages,
  error: errorMessages,
  validation: validationMessages,
  confirm: confirmMessages,
  info: infoMessages,
  warning: warningMessages,
}

/**
 * 快速访问常见错误消息
 */
export const getErrorMessage = (errorCode: number | string): string => {
  const errorMap: Record<string, string> = {
    '400': '请求参数错误',
    '401': '未授权，请登录',
    '403': '禁止访问',
    '404': '资源不存在',
    '500': '服务器内部错误',
    '502': '网关错误',
    '503': '服务不可用',
    'NETWORK_ERROR': errorMessages.networkError,
    'TIMEOUT': errorMessages.timeout,
    'NOT_FOUND': errorMessages.notFound,
    'FORBIDDEN': errorMessages.forbidden,
    'UNAUTHORIZED': errorMessages.unauthorized,
  }
  
  return errorMap[errorCode] || errorMessages.unknown
}

export default messages
