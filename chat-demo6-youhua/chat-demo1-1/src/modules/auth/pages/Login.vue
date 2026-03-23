<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-form">
        <div class="form-header">
          <div class="logo-container">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12,3C7.03,3 3,7.03 3,12C3,16.97 7.03,21 12,21C16.97,21 21,16.97 21,12C21,7.03 16.97,3 12,3M12,19C8.13,19 5,15.87 5,12C5,8.13 8.13,5 12,5C15.87,5 19,8.13 19,12C19,15.87 15.87,19 12,19M12,17C14.76,17 17,14.76 17,12C17,9.24 14.76,7 12,7C9.24,7 7,9.24 7,12C7,14.76 9.24,17 12,17M12,15C10.34,15 9,13.66 9,12C9,10.34 10.34,9 12,9C13.66,9 15,10.34 15,12C15,13.66 13.66,15 12,15Z" />
              </svg>
            </div>
            <h2>用户登录</h2>
            <div class="welcome-text">欢迎回来，请登录您的账户</div>
          </div>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" v-model="credentials.username" required autocomplete="username" placeholder="请输入用户名">
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="credentials.password" required autocomplete="current-password" placeholder="请输入密码">
          </div>

          <button type="submit" class="login-btn" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span>{{ loading ? '登录中...' : '登录' }}</span>
          </button>
        </form>

        <div v-if="message.text" class="message-container">
          <div class="message" :class="[message.type, message.show ? 'show' : '']" @click="hideMessage">
            <span class="message-text">{{ message.text }}</span>
          </div>
        </div>

        <div class="form-footer">
          <a href="#" class="footer-link">忘记密码?</a>
          <a href="#" class="footer-link" @click.prevent="goToRegister">注册新账户</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router';
import { isAdminRole } from '@/shared/constants/userRoles';
import { Credentials, MessageState, LoginResponse } from '@/shared/types/type'

interface ExtendedMessageState extends MessageState {
  timer?: number
}

const router = useRouter()
const credentials = reactive<Credentials>({
  username: '',
  password: ''
})

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

const goToRegister = () => {
  router.push('/register')
}

const handleLogin = async () => {
  if (!credentials.username.trim()) {
    showMessage('请输入用户名', 'error')
    return
  }
  if (!credentials.password) {
    showMessage('请输入密码', 'error')
    return
  }

  loading.value = true

  const response = await fetch('/api/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: credentials.username.trim(),
      password: credentials.password
    })
  })
  const results = await response.json()
  const result: LoginResponse = results.data

  console.log('返回信息', results.msg)

  if (results.code !== 1) {
    showMessage(results.msg, 'error')
    loading.value = false
    return
  }

  if (results.code === 1 && result.token) {
    localStorage.setItem('token', result.token)
    localStorage.setItem('userInfo', JSON.stringify({
      id: result.id,
      username: result.username,
      name: result.name,
      role: result.role || 'user'
    }))
    console.log('登录成功，用户ID:', result.id)
  }
  showMessage('登录成功！正在跳转...', 'success')

  setTimeout(() => {
    const storedUserInfo = localStorage.getItem('userInfo');
    const storedToken = localStorage.getItem('token');
    
    if (!storedUserInfo || !storedToken) {
      console.error('用户信息或token存储失败');
      showMessage('登录信息存储失败，请重试', 'error');
      return;
    }
    
    try {
      const userInfo = JSON.parse(storedUserInfo);
      if (userInfo.role && isAdminRole(userInfo.role)) {
        router.push('/admin');
      } else {
        router.push('/');
      }
    } catch (error) {
      console.error('解析用户信息失败:', error);
      showMessage('登录信息解析失败，请重试', 'error');
    }
  }, 1000)
}
</script>

<style scoped>
.login-wrapper {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-form {
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

.login-form h2 {
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

.login-btn {
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

.login-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
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
  justify-content: space-between;
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
