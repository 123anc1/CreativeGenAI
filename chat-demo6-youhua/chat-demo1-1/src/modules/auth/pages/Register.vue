<template>
  <div class="register-wrapper">
    <div class="register-container">
      <div class="register-form">
        <div class="form-header">
          <div class="logo-container">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12,3C7.03,3 3,7.03 3,12C3,16.97 7.03,21 12,21C16.97,21 21,16.97 21,12C21,7.03 16.97,3 12,3M12,19C8.13,19 5,15.87 5,12C5,8.13 8.13,5 12,5C15.87,5 19,8.13 19,12C19,15.87 15.87,19 12,19M12,17C14.76,17 17,14.76 17,12C17,9.24 14.76,7 12,7C9.24,7 7,9.24 7,12C7,14.76 9.24,17 12,17M12,15C10.34,15 9,13.66 9,12C9,10.34 10.34,9 12,9C13.66,9 15,10.34 15,12C15,13.66 13.66,15 12,15Z" />
              </svg>
            </div>
            <h2>用户注册</h2>
            <div class="welcome-text">创建新账户，开始使用</div>
          </div>
        </div>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" v-model="registerData.username" required autocomplete="username" placeholder="请输入用户名">
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="registerData.password" required autocomplete="new-password" placeholder="请输入密码">
          </div>

          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input type="password" id="confirmPassword" v-model="confirmPassword" required autocomplete="new-password" placeholder="请再次输入密码">
          </div>

          <div class="form-group">
            <label for="name">姓名</label>
            <input type="text" id="name" v-model="registerData.name" required placeholder="请输入姓名">
          </div>

          <div class="form-group">
            <label for="age">年龄</label>
            <input type="number" id="age" v-model.number="registerData.age" required min="1" max="150" placeholder="请输入年龄">
          </div>

          <button type="submit" class="register-btn" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span>{{ loading ? '注册中...' : '注册' }}</span>
          </button>
        </form>

        <div v-if="message.text" class="message-container">
          <div class="message" :class="[message.type, message.show ? 'show' : '']" @click="hideMessage">
            <span class="message-text">{{ message.text }}</span>
          </div>
        </div>

        <div class="form-footer">
          <a href="#" class="footer-link" @click.prevent="goToLogin">已有账户？去登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router';
import { RegisterRequest, MessageState } from '@/shared/types/type'

interface ExtendedMessageState extends MessageState {
  timer?: number
}

const router = useRouter()
const registerData = reactive<RegisterRequest>({
  username: '',
  password: '',
  name: '',
  age: 0
})

const confirmPassword = ref('')
const loading = ref(false)

const message = ref<ExtendedMessageState>({
  text: '',
  type: 'success',
  show: false
})

const showMessage = (text: string, type: 'success' | 'error') => {
  message.value.text = text
  message.value.type = type
  message.value.show = true

  if (message.value.timer) {
    clearTimeout(message.value.timer)
  }
  message.value.timer = setTimeout(() => {
    message.value.show = false
    setTimeout(() => {
      message.value.text = ''
    }, 300)
  }, 3000) as unknown as number
}

const hideMessage = () => {
  message.value.show = false
  setTimeout(() => {
    message.value.text = ''
  }, 300)
}

const goToLogin = () => {
  router.push('/login')
}

const handleRegister = async () => {
  if (!registerData.username.trim()) {
    showMessage('请输入用户名', 'error')
    return
  }
  if (!registerData.password) {
    showMessage('请输入密码', 'error')
    return
  }
  if (!confirmPassword.value) {
    showMessage('请确认密码', 'error')
    return
  }
  if (registerData.password !== confirmPassword.value) {
    showMessage('两次输入的密码不一致', 'error')
    return
  }
  if (!registerData.name.trim()) {
    showMessage('请输入姓名', 'error')
    return
  }
  if (registerData.age <= 0 || registerData.age > 150) {
    showMessage('请输入有效的年龄', 'error')
    return
  }

  loading.value = true

  try {
    const response = await fetch('/api/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: registerData.username.trim(),
        password: registerData.password,
        name: registerData.name.trim(),
        age: registerData.age
      })
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const text = await response.text()
    if (!text) {
      throw new Error('Empty response from server')
    }

    const results = JSON.parse(text)

    console.log('注册返回信息', results)

    if (results.code !== 1) {
      showMessage(results.msg, 'error')
      loading.value = false
      return
    }

    showMessage('注册成功！正在跳转到登录页面...', 'success')

    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    console.error('注册失败:', error)
    if (error instanceof SyntaxError) {
      showMessage('服务器响应格式错误，请稍后重试', 'error')
    } else if (error instanceof Error) {
      showMessage(`注册失败: ${error.message}`, 'error')
    } else {
      showMessage('注册失败，请重试', 'error')
    }
    loading.value = false
  }
}
</script>

<style scoped>
.register-wrapper {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 400px;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #4CAF50, #2E7D32);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 28px;
  height: 28px;
  fill: white;
}

.register-form h2 {
  margin: 0;
  color: #2E7D32;
  font-size: 24px;
  font-weight: 600;
}

.welcome-text {
  color: #666;
  font-size: 14px;
  margin-top: 4px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.register-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #4CAF50, #2E7D32);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;
  margin-top: 10px;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.message-container {
  position: relative;
  margin-top: 16px;
  height: 50px;
  overflow: hidden;
}

.message {
  padding: 12px 16px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 10px;
  transform: translateY(-20px);
  opacity: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  position: absolute;
  width: 100%;
  top: 0;
  font-size: 14px;
}

.message.show {
  transform: translateY(0);
  opacity: 1;
}

.message.success {
  background: #E8F5E9;
  color: #2E7D32;
  border: 1px solid #C8E6C9;
}

.message.error {
  background: #FFEBEE;
  color: #C62828;
  border: 1px solid #FFCDD2;
}

.form-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-link {
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #2E7D32;
  text-decoration: underline;
}
</style>