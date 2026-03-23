/**
 * 表单数据校验 Hook
 * 
 * 功能: 统一的表单验证逻辑，支持多种验证规则
 * 减少组件中的重复验证代码
 * 
 * @example
 * ```typescript
 * const { form, errors, validate, reset } = useFormValidate({
 *   username: { required: true, minLength: 3 },
 *   email: { required: true, pattern: 'email' },
 *   password: { required: true, minLength: 6 }
 * })
 * 
 * const handleSubmit = async () => {
 *   if (!validate()) return
 *   await api.submit(form)
 * }
 * ```
 */

import { reactive, computed, readonly } from 'vue'

/**
 * 验证规则类型
 */
interface ValidationRule {
  required?: boolean
  minLength?: number
  maxLength?: number
  min?: number
  max?: number
  pattern?: string | RegExp
  custom?: (value: any) => boolean | string
}

/**
 * 验证规则集
 */
interface ValidationRules {
  [fieldName: string]: ValidationRule | ValidationRule[]
}

/**
 * 预定义的验证模式
 */
const patterns = {
  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  url: /^https?:\/\/.+/,
  phone: /^1[3-9]\d{9}$/,
  username: /^[a-zA-Z0-9_-]{3,20}$/
}

/**
 * 使用表单验证 Hook
 * @param initialForm - 初始表单数据
 * @param rules - 验证规则
 * @returns 表单数据、错误信息和验证方法
 */
export const useFormValidate = <T extends Record<string, any>>(
  initialForm: T,
  rules: ValidationRules = {}
) => {
  const form = reactive<T>({ ...initialForm })
  const errors = reactive<Record<string, string>>({})

  /**
   * 验证单个字段
   * @param fieldName - 字段名
   * @returns 是否通过验证
   */
  const validateField = (fieldName: string): boolean => {
    const rule = rules[fieldName]
    const value = (form as Record<string, any>)[fieldName] as T[keyof T]

    if (!rule) return true

    const ruleArray = Array.isArray(rule) ? rule : [rule]

    for (const r of ruleArray) {
      // 必填验证
      if (r.required && !value) {
        errors[fieldName] = `${fieldName} 不能为空`
        return false
      }

      if (!value) continue

      // 最小长度
      if (r.minLength && String(value).length < r.minLength) {
        errors[fieldName] = `${fieldName} 长度至少为 ${r.minLength}`
        return false
      }

      // 最大长度
      if (r.maxLength && String(value).length > r.maxLength) {
        errors[fieldName] = `${fieldName} 长度不能超过 ${r.maxLength}`
        return false
      }

      // 最小值
      if (r.min !== undefined && Number(value) < r.min) {
        errors[fieldName] = `${fieldName} 最小值为 ${r.min}`
        return false
      }

      // 最大值
      if (r.max !== undefined && Number(value) > r.max) {
        errors[fieldName] = `${fieldName} 最大值为 ${r.max}`
        return false
      }

      // 正则表达式验证
      if (r.pattern) {
        const regex = typeof r.pattern === 'string' 
          ? patterns[r.pattern as keyof typeof patterns] || new RegExp(r.pattern)
          : r.pattern
        
        if (!regex.test(String(value))) {
          errors[fieldName] = `${fieldName} 格式不正确`
          return false
        }
      }

      // 自定义验证
      if (r.custom) {
        const result = r.custom(value)
        if (result !== true) {
          errors[fieldName] = typeof result === 'string' ? result : `${fieldName} 验证失败`
          return false
        }
      }
    }

    delete errors[fieldName]
    return true
  }

  /**
   * 验证所有字段
   * @returns 是否全部通过验证
   */
  const validate = (): boolean => {
    let isValid = true
    Object.keys(rules).forEach(fieldName => {
      if (!validateField(fieldName)) {
        isValid = false
      }
    })
    return isValid
  }

  /**
   * 重置表单
   * @param newData - 新的初始数据
   */
  const reset = (newData?: Partial<T>) => {
    const data = newData || initialForm
    Object.keys(form).forEach(key => {
      (form as Record<string, any>)[key] = (data as Record<string, any>)[key] ?? (initialForm as Record<string, any>)[key]
    })
    Object.keys(errors).forEach(key => {
      delete errors[key]
    })
  }

  /**
   * 获取字段错误信息
   */
  const getError = (fieldName: string): string | null => {
    return errors[fieldName] || null
  }

  /**
   * 检查是否有错误
   */
  const hasError = computed(() => Object.keys(errors).length > 0)

  return {
    form,
    errors: readonly(errors),
    validate,
    validateField,
    reset,
    getError,
    hasError
  }
}

export default useFormValidate
