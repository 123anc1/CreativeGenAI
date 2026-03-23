/**
 * 分页管理 Hook
 * 
 * 功能: 统一的分页逻辑管理，支持页码、页面大小、总数控制
 * 减少列表页面中的分页代码重复
 * 
 * @example
 * ```typescript
 * const { pageNum, pageSize, total, totalPages, next, prev, goTo } = usePagination(1, 10)
 * 
 * const loadData = async () => {
 *   const { data, total: responseTotal } = await api.getList({
 *     pageNum: pageNum.value,
 *     pageSize: pageSize.value
 *   })
 *   items.value = data
 *   total.value = responseTotal
 * }
 * ```
 */

import { ref, computed } from 'vue'

/**
 * 使用分页 Hook
 * @param initialPage - 初始页码，默认1
 * @param initialSize - 初始每页数量，默认10
 * @returns 分页状态和操作方法
 */
export const usePagination = (
  initialPage: number = 1,
  initialSize: number = 10
) => {
  const pageNum = ref(initialPage)
  const pageSize = ref(initialSize)
  const total = ref(0)

  /**
   * 总页数（计算属性）
   */
  const totalPages = computed(() => 
    Math.ceil(total.value / pageSize.value)
  )

  /**
   * 是否为第一页
   */
  const isFirstPage = computed(() => pageNum.value === 1)

  /**
   * 是否为最后一页
   */
  const isLastPage = computed(() => 
    pageNum.value >= totalPages.value
  )

  /**
   * 开始位置（用于显示当前位置）
   */
  const startIndex = computed(() => 
    (pageNum.value - 1) * pageSize.value + 1
  )

  /**
   * 结束位置（用于显示当前位置）
   */
  const endIndex = computed(() => 
    Math.min(pageNum.value * pageSize.value, total.value)
  )

  /**
   * 前进到下一页
   * @returns 是否成功
   */
  const next = (): boolean => {
    if (!isLastPage.value) {
      pageNum.value++
      return true
    }
    return false
  }

  /**
   * 返回上一页
   * @returns 是否成功
   */
  const prev = (): boolean => {
    if (!isFirstPage.value) {
      pageNum.value--
      return true
    }
    return false
  }

  /**
   * 跳转到指定页
   * @param page - 目标页码
   * @returns 是否成功
   */
  const goTo = (page: number): boolean => {
    if (page >= 1 && page <= totalPages.value) {
      pageNum.value = page
      return true
    }
    return false
  }

  /**
   * 设置每页数量（并重置页码）
   * @param size - 新的每页数量
   */
  const setPageSize = (size: number) => {
    pageSize.value = size
    pageNum.value = 1
  }

  /**
   * 重置分页
   */
  const reset = () => {
    pageNum.value = initialPage
    pageSize.value = initialSize
    total.value = 0
  }

  return {
    pageNum,
    pageSize,
    total,
    totalPages,
    isFirstPage,
    isLastPage,
    startIndex,
    endIndex,
    next,
    prev,
    goTo,
    setPageSize,
    reset
  }
}

export default usePagination
