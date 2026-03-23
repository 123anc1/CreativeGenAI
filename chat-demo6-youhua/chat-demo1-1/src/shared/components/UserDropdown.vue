<template>
  <div class="relative user-dropdown-container" ref="dropdownContainer">
    <!-- 用户头像 -->
    <button id="avatar-button"
      class="flex items-center justify-center w-10 h-10 rounded-full text-white font-semibold transition-all duration-300 transform hover:scale-110 hover:shadow-lg focus:outline-none relative group"
      :style="{ background: avatarGradient }" @click.stop="toggleDropdown" aria-haspopup="true"
      :aria-expanded="showDropdown">
      {{ getUserInitial() }}
      <!-- 在线状态指示器 -->
      <span
        class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-400 border-2 border-white dark:border-gray-900 rounded-full animate-pulse"></span>
    </button>

    <!-- 使用Teleport将下拉菜单移到body下 -->
    <Teleport to="body">
      <div v-if="showDropdown"
        class="user-dropdown-menu bg-white dark:bg-gray-800 border border-green-100 dark:border-emerald-900 rounded-xl shadow-2xl shadow-green-200/30 dark:shadow-emerald-900/30 backdrop-blur-sm z-50 overflow-visible"
        :style="dropdownStyles" @click.stop>
        <!-- 用户信息区域 -->
        <div
          class="p-4 bg-gradient-to-r from-green-50/50 to-emerald-50/50 dark:from-emerald-900/30 dark:to-green-900/30 border-b border-green-100 dark:border-emerald-900">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 rounded-full flex items-center justify-center text-white font-bold text-lg"
              :style="{ background: avatarGradient }">
              {{ getUserInitial() }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-900 dark:text-white truncate">
                {{ getUsername() }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-400 truncate">
                {{ getEmail() }}
              </p>
            </div>
          </div>
        </div>

        <!-- 菜单项 -->
        <div class="py-2 px-1">
          <button @click="goToProfile"
            class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-green-200/50 dark:hover:shadow-emerald-900/50 hover:pl-5 cursor-pointer"
            :class="[
              'hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50',
              'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
              'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white',
              'hover:ring-1 hover:ring-green-200 dark:hover:ring-emerald-800'
            ]" style="pointer-events: auto;">
            <svg class="w-4 h-4 mr-3 text-green-500 group-hover:text-green-600 transition-colors" fill="none"
              stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
            个人中心
            <svg
              class="w-3 h-3 ml-auto opacity-0 group-hover:opacity-100 transform group-hover:translate-x-1 transition-all duration-300"
              fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <!-- 收藏功能 -->
          <button @click="goToFavorites"
            class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-green-200/50 dark:hover:shadow-emerald-900/50 hover:mt-1 hover:pl-5 cursor-pointer"
            :class="[
              'hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50',
              'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
              'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white',
              'hover:ring-1 hover:ring-green-200 dark:hover:ring-emerald-800'
            ]" style="pointer-events: auto;">
            <svg class="w-4 h-4 mr-3 text-green-500 group-hover:text-green-600 transition-colors" fill="none"
              stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z">
              </path>
            </svg>
            我的收藏
            <svg
              class="w-3 h-3 ml-auto opacity-0 group-hover:opacity-100 transform group-hover:translate-x-1 transition-all duration-300"
              fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <button @click="logout"
            class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-red-200/50 dark:hover:shadow-pink-900/50 hover:mt-1 hover:pl-5 cursor-pointer"
            :class="[
              'hover:bg-gradient-to-r hover:from-red-50 hover:to-pink-50',
              'dark:hover:from-red-900/30 dark:hover:to-pink-900/30',
              'text-gray-700 dark:text-gray-300 hover:text-red-600 dark:hover:text-red-400',
              'hover:ring-1 hover:ring-red-200 dark:hover:ring-pink-800'
            ]" style="pointer-events: auto;">
            <svg class="w-4 h-4 mr-3 text-red-500 group-hover:text-red-600 transition-colors" fill="none"
              stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1">
              </path>
            </svg>
            退出登录
          </button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { CSSProperties } from 'vue'
import { LoginResponse } from '@/shared/types/type'

const router = useRouter()

// 响应式数据
const showDropdown = ref(false)

// 计算头像渐变颜色
const avatarGradient = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  const name = userInfo ? JSON.parse(userInfo)?.name : 'User'
  const colors = [
    'linear-gradient(135deg, #10B981 0%, #059669 100%)',
    'linear-gradient(135deg, #34D399 0%, #10B981 100%)',
    'linear-gradient(135deg, #059669 0%, #047857 100%)',
    'linear-gradient(135deg, #34D399 0%, #059669 100%)',
  ]

  if (!name) return colors[0]
  const index = name.charCodeAt(0) % colors.length
  return colors[index]
})

// 下拉菜单位置样式
const dropdownStyles = ref<CSSProperties>({
  position: 'fixed',
  top: '0',
  left: '0',
  width: '14rem',
  zIndex: 9999
})

// 更新下拉菜单位置
const updateDropdownPosition = () => {
  const button = document.getElementById('avatar-button')
  if (!button) return

  const rect = button.getBoundingClientRect()
  const menu = document.querySelector('.user-dropdown-menu')
  const menuWidth = 224 // 14rem = 224px
  const menuHeight = menu ? (menu as HTMLElement).offsetHeight : 300 // 预估高度，实际会根据内容调整
  
  // 计算菜单左侧位置（使菜单右边缘与头像右边缘对齐）
  let leftPosition = rect.right - menuWidth
  
  // 检查是否会超出屏幕左侧
  if (leftPosition < 0) {
    leftPosition = 0
  }
  
  // 计算菜单顶部位置（使菜单底部与头像顶部对齐）
  // 由于菜单使用fixed定位，直接使用rect.top即可，不需要加上window.scrollY
  // 因为getBoundingClientRect()返回的位置已经是相对于视口的
  const topPosition = rect.top - menuHeight
  
  dropdownStyles.value = {
    position: 'fixed',
    top: `${topPosition}px`,
    left: `${leftPosition}px`,
    width: '14rem',
    zIndex: 9999
  }
}

// 切换下拉菜单显示状态
const toggleDropdown = async () => {
  showDropdown.value = !showDropdown.value
  if (showDropdown.value) {
    // 等待菜单完全渲染
    await nextTick()
    // 延迟一点时间，确保菜单完全显示
    setTimeout(() => {
      updateDropdownPosition()
    }, 100)
  }
}

// 获取用户首字母
const getUserInitial = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '?'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.name ? user.name.charAt(0).toUpperCase() : '?'
  } catch {
    return '?'
  }
}

// 获取用户名
const getUsername = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '未知用户'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.name || '未知用户'
  } catch {
    return '未知用户'
  }
}

// 获取邮箱
const getEmail = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '未提供邮箱'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.email || '未提供邮箱'
  } catch {
    return '未提供邮箱'
  }
}

// 菜单操作方法
const goToProfile = async () => {
  try {
    showDropdown.value = false
    await router.push('/profile')
  } catch (error) {
    console.error('跳转到个人中心失败:', error)
  }
}

// 收藏功能
const goToFavorites = () => {
  showDropdown.value = false
  router.push('/favorites')
}

const logout = () => {
  showDropdown.value = false
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

// 事件处理
const handleResize = () => {
  if (showDropdown.value) {
    updateDropdownPosition()
  }
}

const handleClickOutside = (event: MouseEvent) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target as Node)) {
    showDropdown.value = false
  }
}

// refs
const dropdownContainer = ref<HTMLElement | null>(null)

// 生命周期
onMounted(() => {
  window.addEventListener('resize', handleResize)
  window.addEventListener('scroll', handleResize)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('scroll', handleResize)
  document.removeEventListener('click', handleClickOutside)
})

// 暴露方法供父组件使用
defineExpose({
  closeDropdown: () => {
    showDropdown.value = false
  }
})
</script>

<style scoped>
/* 组件样式已在模板中定义 */
</style>