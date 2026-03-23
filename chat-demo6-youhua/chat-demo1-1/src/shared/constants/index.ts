/**
 * 常量定义统一导出文件
 * 
 * 所有常量都在这里统一导出
 * 
 * @example
 * ```typescript
 * import { messages, tailwindClasses, buttonClasses } from '@/shared/constants'
 * ```
 */

export { USER_ROLES, ADMIN_ROLES, isAdminRole } from './userRoles'
export { messages, successMessages, errorMessages, validationMessages, getErrorMessage } from './messages'
export { 
  cardClasses, 
  buttonClasses, 
  inputClasses, 
  dropdownClasses, 
  avatarGradients,
  textClasses, 
  commonClasses 
} from './tailwind'

