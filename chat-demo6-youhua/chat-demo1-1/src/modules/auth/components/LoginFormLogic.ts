/**
 * 登录表单逻辑
 * 处理用户输入、表单验证、提交逻辑
 */

import { reactive } from 'vue'
import type { Credentials } from '@/shared/types'
import { useFormValidate, useMessage } from '@/shared/composables'

export interface LoginFormProps {
  loading?: boolean
}

export interface LoginFormEmits {
  login: [credentials: Credentials]
}
const emit = defineEmits<LoginFormEmits>()

const credentials = reactive<Credentials>({
  username: '',
  password: ''
})

// 表单验证规则
const { validate, errors } = useFormValidate(credentials, {
  username: { required: true, minLength: 3 },
  password: { required: true, minLength: 6 }
})

const { showMessage } = useMessage()

const handleSubmit = async () => {
  if (!validate()) {
    showMessage('请填写正确的用户名和密码', 'error')
    return
  }

  emit('login', {
    username: credentials.username.trim(),
    password: credentials.password
  })
}

const getFieldError = (field: keyof Credentials): string | null => {
  return errors[field] || null
}

export {
  credentials,
  errors,
  validate,
  handleSubmit,
  getFieldError
}
