/**
 * 异步加载状态管理 Hook
 * 
 * 功能: 统一管理异步操作的加载状态、错误处理
 * 避免重复的 loading 和 error 状态声明
 * 
 * @example
 * ```typescript
 * const { loading, error, execute } = useAsync(async () => {
 *   return await api.fetchData()
 * })
 * 
 * watch(() => state.value, () => {
 *   // 当数据加载完成后...
 * })
 * ```
 */

import { ref, readonly, Ref } from 'vue'

/**
 * 使用异步操作 Hook
 * @template T - 异步结果的类型
 * @param asyncFn - 异步函数
 * @returns 加载状态、错误信息和执行函数
 */
export const useAsync = <T = any>(
  asyncFn: () => Promise<T>
) => {
  const loading = ref(false)
  const error = ref<Error | null>(null)
  const data = ref<T | null>(null)

  /**
   * 执行异步函数
   * @returns Promise<T | null>
   */
  const execute = async (): Promise<T | null> => {
    loading.value = true
    error.value = null

    try {
      const result = await asyncFn()
      data.value = result
      return result
    } catch (err) {
      error.value = err instanceof Error ? err : new Error(String(err))
      return null
    } finally {
      loading.value = false
    }
  }

  return {
    loading: readonly(loading),
    error: readonly(error),
    data: readonly(data as Ref<T | null>),
    execute
  }
}

export default useAsync
