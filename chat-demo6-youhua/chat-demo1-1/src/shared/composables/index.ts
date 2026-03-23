/**
 * Composables 统一导出文件
 * 
 * 所有可复用的 Composition API hooks 都在这里统一导出
 * 简化组件中的import语句
 * 
 * @example
 * ```typescript
 * // 不需要这样做：
 * import { useMessage } from '@/composables/useMessage'
 * import { useAuth } from '@/composables/useAuth'
 * 
 * // 而是可以这样：
 * import { useMessage, useAuth } from '@/composables'
 * ```
 */

export { useMessage } from './useMessage'
export { useAsync } from './useAsync'
export { useFormValidate } from './useFormValidate'
export { usePagination } from './usePagination'
export { useAuth } from './useAuth'
