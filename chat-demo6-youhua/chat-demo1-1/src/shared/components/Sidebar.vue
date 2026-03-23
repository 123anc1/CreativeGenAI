<template>
  <div class="flex flex-col h-full">
    <!-- Logo区域 -->
    <div class="flex items-center px-4 py-6 border-b border-gray-200">
      <div class="flex-shrink-0">
        <div class="h-10 w-10 rounded-full bg-gradient-to-r from-blue-500 to-emerald-500 flex items-center justify-center">
          <span class="text-white font-bold text-lg">A</span>
        </div>
      </div>
      <div class="ml-3">
        <h1 class="text-xl font-bold text-gray-900">{{ isAdmin ? '管理员' : 'AI助手' }}</h1>
        <p class="text-xs text-gray-500">{{ isAdmin ? '控制台' : '对话中心' }}</p>
      </div>
    </div>

    <!-- 管理员导航菜单 -->
    <template v-if="isAdmin">
      <nav class="mt-6 flex-1 px-2 space-y-1">
        <router-link
          v-for="item in adminNavigation"
          :key="item.name"
          :to="item.href"
          :class="[
            item.current
              ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
              : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900',
            'group flex items-center px-4 py-3 text-sm font-medium rounded-md'
          ]"
        >
          <component
            :is="item.icon"
            :class="[
              item.current ? 'text-blue-600' : 'text-gray-500 group-hover:text-gray-600',
              'mr-3 flex-shrink-0 h-6 w-6'
            ]"
            aria-hidden="true"
          />
          <span class="truncate">{{ item.name }}</span>
        </router-link>
      </nav>
    </template>

    <!-- 用户导航菜单 -->
    <template v-else>
      <!-- 顶部新建对话按钮 -->
      <div class="p-4">
        <button @click="createTemporaryConversation"
          class="w-full rounded-lg px-4 py-3 text-sm font-medium shadow-md relative overflow-hidden"
          :class="[
            'bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600',
            'text-white shadow-green-200/50 dark:shadow-emerald-900/50'
          ]">
          <span class="flex items-center justify-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            新建对话
          </span>
        </button>
      </div>

      <!-- 对话列表 -->
      <div
        class="flex-1 overflow-y-auto px-2 scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800">
        <ConversationList :conversations="conversations" :current-id="currentConversationId" :highlight-id="highlightId"
          @select="switchConversation" @rename="handleRename" @remove="handleRemove" />
      </div>

      <!-- 功能链接 -->
      <div
        class="p-2 border-t border-green-100 dark:border-emerald-900 bg-white/60 dark:bg-gray-900/60 backdrop-blur-sm">
        <router-link to="/knowledge-base"
          class="w-full text-left px-3 py-2.5 text-sm rounded-lg relative overflow-hidden block"
          :class="[
            'hover:bg-gradient-to-r hover:from-green-50/50 hover:to-emerald-50/50',
            'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
            'border border-transparent hover:border-green-200 dark:hover:border-emerald-800',
            'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
          ]">
          <span class="flex items-center">
            <span class="mr-2 text-base">📚</span>
            我的知识库
          </span>
        </router-link>

        <router-link to="/image-generation"
          class="w-full text-left px-3 py-2.5 text-sm rounded-lg relative overflow-hidden mt-1 block"
          :class="[
            'hover:bg-gradient-to-r hover:from-green-50/50 hover:to-emerald-50/50',
            'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
            'border border-transparent hover:border-green-200 dark:hover:border-emerald-800',
            'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
          ]">
          <span class="flex items-center">
            <span class="mr-2 text-base">🖼️</span>
            文生图模型
          </span>
        </router-link>

        <router-link to="/canvas"
          class="w-full text-left px-3 py-2.5 text-sm rounded-lg relative overflow-hidden mt-1 block"
          :class="[
            'hover:bg-gradient-to-r hover:from-green-50/50 hover:to-emerald-50/50',
            'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
            'border border-transparent hover:border-green-200 dark:hover:border-emerald-800',
            'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
          ]">
          <span class="flex items-center">
            <span class="mr-2 text-base">🎨</span>
            AI画布
          </span>
        </router-link>

        <router-link to="/"
          class="w-full text-left px-3 py-2.5 text-sm rounded-lg relative overflow-hidden mt-1 block"
          :class="[
            'hover:bg-gradient-to-r hover:from-green-50/50 hover:to-emerald-50/50',
            'dark:hover:from-emerald-900/30 dark:hover:to-green-900/30',
            'border border-transparent hover:border-green-200 dark:hover:border-emerald-800',
            'text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
          ]">
          <span class="flex items-center">
            <span class="mr-2 text-base">📷</span>
            图像分享
          </span>
        </router-link>
      </div>
    </template>

    <!-- 底部用户信息/系统信息 -->
    <div class="px-4 py-6 border-t border-gray-200 mt-auto">
      <template v-if="isAdmin">
        <div class="text-xs text-gray-500">
          <p>系统版本: v1.0.0</p>
          <p class="mt-1">运行时间: 24天 14小时</p>
        </div>
      </template>
      <template v-else>
        <div class="flex items-center">
          <div class="w-8 h-8 rounded-full flex items-center justify-center text-white font-semibold text-sm" :style="{ background: avatarGradient }">
            {{ getUserInitial() }}
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-900">{{ getUsername() }}</p>
            <p class="text-xs text-gray-500">{{ getEmail() }}</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import ConversationList from "./ConversationList.vue";
import { deleteSessionRequest } from "@/modules/auth/services/session";
import { Conversation, Message, RenamePayload, RemovePayload, LoginResponse } from '@/shared/types/type'

const route = useRoute()

const conversations = ref<Conversation[]>([])
const currentConversationId = ref<string | null>(null)
const highlightId = ref<string | null>(null)
const isTemporaryConversation = ref(false)
const shouldLoadData = ref(true);
let isComponentMounted = true;
let abortController: AbortController | null = null;

const isAdmin = computed(() => {
  return route.path.startsWith('/admin')
})

const adminNavigation = computed(() => [
  {
    name: '仪表盘',
    href: '/admin',
    icon: {
      name: 'dashboard-icon',
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>`
    },
    current: route.path === '/admin' || route.path === '/admin/'
  },
  {
    name: '用户管理',
    href: '/admin/users',
    icon: {
      name: 'users-icon',
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>`
    },
    current: route.path === '/admin/users'
  },
  {
    name: '模型管理',
    href: '/admin/models',
    icon: {
      name: 'model-icon',
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>`
    },
    current: route.path === '/admin/models'
  },
  {
    name: '内容管理',
    href: '/admin/content',
    icon: {
      name: 'content-icon',
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>`
    },
    current: route.path === '/admin/content'
  },
  {
    name: '日志管理',
    href: '/admin/logs',
    icon: {
      name: 'logs-icon',
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" /></svg>`
    },
    current: route.path === '/admin/logs'
  }
]);

// 移除updateCurrentRoute函数，因为现在使用computed自动更新

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

function cancelOngoingRequests() {
  if (abortController) {
    abortController.abort();
  }
  abortController = new AbortController();
}

function parseJwt(token: string | null): Record<string, any> | null {
  if (!token) return null
  try {
    const payload = token.split('.')[1]
    const decoded = atob(payload)
    return JSON.parse(decoded)
  } catch (e) {
    console.error('解析 token 失败:', e)
    return null
  }
}

function getUserId(): string | null {
  const token = localStorage.getItem('token')
  const payload = parseJwt(token)
  if (payload?.id) return payload.id
  if (payload?.userId) return payload.userId
  if (payload?.userid) return payload.userid
  try {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const user: LoginResponse = JSON.parse(userInfo)
      return user?.id || null
    }
  } catch {}
  return null
}

const currentConversation = computed<Conversation | null>(() => {
  return conversations.value.find(c => c.sessionid === currentConversationId.value) || null
})

async function loadConversations(): Promise<void> {
  if (!shouldLoadData.value || !isComponentMounted) return;
  if (abortController) {
    abortController.abort();
  }
  abortController = new AbortController();
  const uid = getUserId()
  if (!uid) return
  try {
    const resp = await fetch(`/session/list?id=${uid}`, {
      headers: { token: localStorage.getItem('token') || '' },
      signal: abortController.signal
    })
    if (resp.ok) {
      const contentType = resp.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const data = await resp.json()
        const conversationsData: Conversation[] = data.map((item: any) => ({
          sessionid: String(item.sessionid),
          title: item.title || '',
          userid: item.userid || '',
          messages: item.messages || []
        }))
        conversations.value = conversationsData.sort((a, b) => {
          const idA = isNaN(Number(a.sessionid)) ? a.sessionid : Number(a.sessionid)
          const idB = isNaN(Number(b.sessionid)) ? b.sessionid : Number(b.sessionid)
          if (typeof idA === 'number' && typeof idB === 'number') {
            return idB - idA
          } else {
            return String(idB).localeCompare(String(idA))
          }
        })
        if (conversations.value.length && !currentConversationId.value) {
          currentConversationId.value = conversations.value[0].sessionid
          await loadMessages()
        }
      }
    }
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      console.log('Session list request was cancelled');
    } else {
      console.error('加载会话失败:', error);
    }
  }
}

async function loadMessages(): Promise<void> {
  if (!shouldLoadData.value || !isComponentMounted) return;
  if (!abortController) {
    abortController = new AbortController();
  }
  const uid = getUserId()
  if (!uid || !currentConversationId.value) return
  try {
    const resp = await fetch(
      `/ai/c_msg?uid=${uid}&sessionid=${currentConversationId.value}`,
      { headers: { token: localStorage.getItem('token') || '' }, signal: abortController.signal }
    )
    if (resp.ok) {
      const contentType = resp.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const msgs = await resp.json();
        const converted: Message[] = msgs.map((m: any) => {
          if (/^####\[推理\]####：/.test(m.content)) {
            return { type: 'ASSISTANT_REASONING', content: m.content.replace(/^####\[推理\]####：/, '') }
          } else if (/^####\[回复\]####：/.test(m.content)) {
            return { type: 'ASSISTANT_RESPONSE', content: m.content.replace(/^####\[回复\]####：/, '') }
          } else {
            return { type: m.type === 'USER' ? 'USER' : 'ASSISTANT', content: m.content }
          }
        })
        const conv = conversations.value.find(c => c.sessionid === currentConversationId.value!)
        if (conv) conv.messages = converted
      }
    }
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      console.log('Messages request was cancelled');
    } else {
      console.error('加载消息失败:', error)
    }
  }
}

function switchConversation(sessionid: string | number): void {
  const sessionIdStr = String(sessionid)
  currentConversationId.value = sessionIdStr
  isTemporaryConversation.value = false
  loadMessages()
}

function createTemporaryConversation(): void {
  if (currentConversation.value) {
    currentConversation.value.messages = []
  }
  isTemporaryConversation.value = true
  currentConversationId.value = null
}

async function handleRename(payload: RenamePayload): Promise<void> {
  try {
    const body = { sessionid: payload.sessionid, title: payload.title }
    const resp = await fetch('/session/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', token: localStorage.getItem('token') || '' },
      body: JSON.stringify(body)
    })
    if (!resp.ok) {
      console.error('重命名失败', resp.status)
      return
    }
    const conv = conversations.value.find(c => c.sessionid === payload.sessionid)
    if (conv) {
      conv.title = payload.title
      conversations.value = [...conversations.value]
    }
  } catch (e) {
    console.error('handleRename error', e)
  }
}

async function handleRemove(payload: RemovePayload): Promise<void> {
  try {
    const uid = getUserId()
    if (!uid) return
    await deleteSessionRequest({ userid: uid, sessionid: payload.sessionid })
    const idx = conversations.value.findIndex(c => c.sessionid === payload.sessionid)
    if (idx >= 0) conversations.value.splice(idx, 1)
    if (currentConversationId.value === payload.sessionid) {
      currentConversationId.value = conversations.value.length ? conversations.value[0].sessionid : null
      if (currentConversationId.value) await loadMessages()
    }
  } catch (e) {
    console.error('handleRemove error', e)
  }
}

onMounted(() => {
  isComponentMounted = true;
  abortController = new AbortController();
  if (shouldLoadData.value && !isAdmin.value) {
    loadConversations();
  }
})

// 移除路由变化监听器，因为现在使用computed自动更新

onUnmounted(() => {
  shouldLoadData.value = false;
  isComponentMounted = false;
  cancelOngoingRequests();
})
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar { width: 6px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { border-radius: 3px; }
a { transition: all 0.2s ease; }
</style>