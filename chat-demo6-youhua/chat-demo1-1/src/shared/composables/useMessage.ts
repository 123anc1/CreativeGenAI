/**
 * 消息提示 Composition Hook
 * 
 * 功能: 统一的消息提示管理，包括成功、错误、警告等
 * 避免在每个组件中重复实现消息逻辑
 * 
 * @example
 * ```typescript
 * const { message, showMessage, hideMessage } = useMessage()
 * 
 * const handleSubmit = async () => {
 *   try {
 *     await api.submit()
 *     showMessage('提交成功！', 'success')
 *   } catch (error) {
 *     showMessage('提交失败：' + error.message, 'error')
 *   }
 * }
 * ```
 */

import { reactive } from 'vue'

/**
 * 消息状态接口
 */
interface MessageState {
  text: string
  type: 'success' | 'error' | 'warning' | 'info'
  show: boolean
  timer?: number
}

/**
 * 使用消息提示 Hook
 * @param duration - 消息显示时长（毫秒），默认3000
 * @returns 消息状态和控制方法
 */
export const useMessage = (duration: number = 3000) => {
  const message = reactive<MessageState>({
    text: '',
    type: 'success',
    show: false,
    timer: undefined
  })

  /**
   * 显示消息
   * @param text - 消息文本
   * @param type - 消息类型
   * @param customDuration - 自定义显示时长
   */
  const showMessage = (
    text: string,
    type: 'success' | 'error' | 'warning' | 'info' = 'success',
    customDuration?: number
  ) => {
    message.text = text
    message.type = type
    message.show = true

    // 清除之前的定时器
    if (message.timer) {
      clearTimeout(message.timer)
    }

    // 设置自动隐藏
    message.timer = window.setTimeout(() => {
      hideMessage()
    }, customDuration ?? duration)
  }

  /**
   * 隐藏消息
   * @param delay - 延迟隐藏时长（毫秒），用于动画
   */
  const hideMessage = (delay: number = 0) => {
    if (delay > 0) {
      setTimeout(() => {
        message.show = false
        setTimeout(() => {
          message.text = ''
        }, 300)
      }, delay)
    } else {
      message.show = false
      setTimeout(() => {
        message.text = ''
      }, 300)
    }
  }

  /**
   * 清除定时器（组件卸载时调用）
   */
  const clearTimer = () => {
    if (message.timer) {
      clearTimeout(message.timer)
    }
  }

  return {
    message,
    showMessage,
    hideMessage,
    clearTimer
  }
}

export default useMessage
