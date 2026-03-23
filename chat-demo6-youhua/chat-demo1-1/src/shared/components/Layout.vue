<template>
  <div class="flex h-screen relative overflow-hidden">
    <!-- 背景装饰元素 -->
    <div class="absolute inset-0 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-gray-900 dark:to-gray-800 z-0"></div>
    <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>

    <!-- 动态装饰元素 -->
    <div class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-green-200 to-emerald-200 rounded-full blur-3xl opacity-20 animate-pulse"></div>
    <div class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-emerald-200 to-teal-200 rounded-full blur-3xl opacity-20 animate-pulse"></div>

    <!-- 左侧边栏 -->
    <div class="flex w-64 flex-col border-r border-green-100 dark:border-emerald-900 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm relative z-10 shadow-lg">
      <Sidebar />
    </div>

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col relative z-10">
      <!-- 顶部标题栏 -->
      <div class="flex items-center border-b px-6 py-4 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm border-green-100 dark:border-emerald-900 shadow-sm">
        <h1 class="text-lg font-semibold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
          {{ currentTitle }}
        </h1>
        <div class="ml-auto flex items-center gap-4 relative">
          <!-- 用户下拉菜单 -->
          <div class="relative user-dropdown-container" ref="dropdownContainer">
            <button id="avatar-button"
              class="flex items-center justify-center w-10 h-10 rounded-full text-white font-semibold transition-all duration-300 transform hover:scale-110 hover:shadow-lg focus:outline-none relative group"
              :style="{ background: avatarGradient }" @click.stop="toggleDropdown" aria-haspopup="true"
              :aria-expanded="showDropdown">
              {{ getUserInitial() }}
              <span class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-400 border-2 border-white dark:border-gray-900 rounded-full animate-pulse"></span>
            </button>

            <Teleport to="body">
              <div v-if="showDropdown"
                class="user-dropdown-menu bg-white dark:bg-gray-800 border border-green-100 dark:border-emerald-900 rounded-xl shadow-2xl shadow-green-200/30 dark:shadow-emerald-900/30 backdrop-blur-sm z-50 overflow-visible"
                :style="dropdownStyles" @click.stop>
                <div class="p-4 bg-gradient-to-r from-green-50/50 to-emerald-50/50 dark:from-emerald-900/30 dark:to-green-900/30 border-b border-green-100 dark:border-emerald-900">
                  <div class="flex items-center space-x-3">
                    <div class="w-12 h-12 rounded-full flex items-center justify-center text-white font-bold text-lg" :style="{ background: avatarGradient }">
                      {{ getUserInitial() }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-semibold text-gray-900 dark:text-white truncate">{{ getUsername() }}</p>
                      <p class="text-xs text-gray-500 dark:text-gray-400 truncate">{{ getEmail() }}</p>
                    </div>
                  </div>
                </div>

                <div class="py-2 px-1">
                  <button @click="goToProfile"
                    class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-green-200/50 dark:hover:shadow-emerald-900/50 hover:pl-5 cursor-pointer"
                    :class="[
                      'hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50',
                      'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
                      'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white',
                      'hover:ring-1 hover:ring-green-200 dark:hover:ring-emerald-800'
                    ]">
                    <svg class="w-4 h-4 mr-3 text-green-500 group-hover:text-green-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
                    个人中心
                  </button>

                  <button @click="goToFavorites"
                    class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-green-200/50 dark:hover:shadow-emerald-900/50 hover:mt-1 hover:pl-5 cursor-pointer"
                    :class="[
                      'hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50',
                      'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
                      'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white',
                      'hover:ring-1 hover:ring-green-200 dark:hover:ring-emerald-800'
                    ]">
                    <svg class="w-4 h-4 mr-3 text-green-500 group-hover:text-green-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                    </svg>
                    我的收藏
                  </button>

                  <button @click="logout"
                    class="flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md hover:shadow-red-200/50 dark:hover:shadow-pink-900/50 hover:mt-1 hover:pl-5 cursor-pointer"
                    :class="[
                      'hover:bg-gradient-to-r hover:from-red-50 hover:to-pink-50',
                      'dark:hover:from-red-900/30 dark:hover:to-pink-900/30',
                      'text-gray-700 dark:text-gray-300 hover:text-red-600 dark:hover:text-red-400',
                      'hover:ring-1 hover:ring-red-200 dark:hover:ring-pink-800'
                    ]">
                    <svg class="w-4 h-4 mr-3 text-red-500 group-hover:text-red-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                    </svg>
                    退出登录
                  </button>
                </div>
              </div>
            </Teleport>
          </div>
        </div>
      </div>

      <!-- 未登录提示 -->
      <div v-if="!isAuthed"
        class="px-6 py-3 bg-gradient-to-r from-amber-50 to-yellow-50 dark:from-amber-900/20 dark:to-yellow-900/20 border-b border-amber-200 dark:border-amber-800/30 relative overflow-hidden">
        <div class="absolute top-0 left-0 w-2 h-full bg-gradient-to-b from-amber-400 to-yellow-400"></div>
        <div class="flex items-center justify-between ml-4">
          <div class="flex items-center">
            <div class="w-6 h-6 rounded-full bg-amber-100 dark:bg-amber-900 flex items-center justify-center mr-3">
              <svg class="w-3 h-3 text-amber-600 dark:text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.998-.833-2.732 0L4.282 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
              </svg>
            </div>
            <span class="text-sm text-amber-800 dark:text-amber-300">未检测到登录信息，请先登录</span>
          </div>
          <button @click="goLogin"
            class="px-4 py-1.5 text-sm font-medium rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95"
            :class="[
              'bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600',
              'text-white shadow-md shadow-green-200/50 dark:shadow-emerald-900/50'
            ]">
            去登录
          </button>
        </div>
      </div>

      <!-- 主要内容 -->
      <main class="flex-1 overflow-y-auto p-6">
        <router-view :key="$route.fullPath" />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CSSProperties } from 'vue'
import Sidebar from './Sidebar.vue'
import { LoginResponse } from '@/shared/types/type'

const router = useRouter()
const route = useRoute()
const showDropdown = ref(false)
const dropdownStyles = ref<CSSProperties>({ position: 'fixed', top: '0', right: '0', width: '16rem', zIndex: 9999 })
const dropdownContainer = ref<HTMLElement | null>(null)

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

const isAdmin = computed(() => route.path.startsWith('/admin'))

const currentTitle = computed<string>(() => {
  if (isAdmin.value) {
    const adminTitles: Record<string, string> = {
      '/admin': '仪表盘', '/admin/users': '用户管理', '/admin/models': '模型管理',
      '/admin/content': '内容管理', '/admin/logs': '日志管理'
    }
    return adminTitles[route.path] || '管理员控制台'
  }
  const userTitles: Record<string, string> = {
    '/': '图像分享', '/chat': 'AI对话', '/knowledge-base': '知识库',
    '/image-generation': '文生图', '/profile': '个人中心', '/my-posts': '我的帖子'
  }
  return userTitles[route.path] || 'AI助手'
})

const isAuthed = computed<boolean>(() => !!localStorage.getItem('token'))

const getUserInitial = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '?'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.name ? user.name.charAt(0).toUpperCase() : '?'
  } catch { return '?' }
}

const getUsername = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '未知用户'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.name || '未知用户'
  } catch { return '未知用户' }
}

const getEmail = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return '未提供邮箱'
  try {
    const user: LoginResponse = JSON.parse(userInfo)
    return user?.email || '未提供邮箱'
  } catch { return '未提供邮箱' }
}

const updateDropdownPosition = () => {
  const button = document.getElementById('avatar-button');
  if (!button) return;
  const rect = button.getBoundingClientRect();
  dropdownStyles.value = { position: 'fixed', top: `${rect.bottom + window.scrollY}px`, right: `${window.innerWidth - rect.right}px`, width: '16rem', zIndex: 9999 };
};

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
  if (showDropdown.value) { nextTick(() => { updateDropdownPosition(); }); }
};

const handleResize = () => { if (showDropdown.value) updateDropdownPosition(); };

onMounted(() => {
  window.addEventListener('resize', handleResize);
  window.addEventListener('scroll', handleResize);
  document.addEventListener('click', handleClickOutside);
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  window.removeEventListener('scroll', handleResize);
  document.removeEventListener('click', handleClickOutside);
})

const goToProfile = async () => {
  try { showDropdown.value = false; await router.push('/profile'); }
  catch (error) { console.error('跳转到个人中心失败:', error); }
};

const goToFavorites = () => { showDropdown.value = false; router.push('/favorites'); };
function goLogin() { router.push('/login'); }
function logout() { showDropdown.value = false; localStorage.removeItem('token'); localStorage.removeItem('userInfo'); router.push('/login'); }
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target as Node)) { showDropdown.value = false; }
};
</script>

<style scoped>
.bg-grid-pattern {
  background-image: linear-gradient(to right, rgba(16, 185, 129, 0.05) 1px, transparent 1px), linear-gradient(to bottom, rgba(16, 185, 129, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}
@media (max-width: 768px) {
  .flex.h-screen { flex-direction: column; }
  .w-64 { width: 100%; height: auto; }
  .flex-1 { flex: 1; }
}
</style>