<template>
  <div class="flex h-screen relative overflow-hidden">
    <!-- 背景装饰元素 -->
    <div class="absolute inset-0 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-gray-900 dark:to-gray-800 z-0">
    </div>
    <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>

    <!-- 动态装饰元素 -->
    <div
      class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-green-200 to-emerald-200 rounded-full blur-3xl opacity-20 animate-pulse">
    </div>
    <div
      class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-emerald-200 to-teal-200 rounded-full blur-3xl opacity-20 animate-pulse">
    </div>
    <!-- 左侧边栏 -->
    <UserSidebar
      mode="full"
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      :highlight-id="highlightId"
      :current-route="currentRoute"
      @switch-conversation="switchConversation"
      @create-temporary-conversation="createTemporaryConversation"
      @rename="handleRename"
      @remove="handleRemove"
    />

    <!-- 主聊天区域 -->
    <div class="flex-1 flex flex-col relative z-10">

      <!-- 未登录提示 -->
      <div v-if="!isAuthed"
        class="px-6 py-3 bg-gradient-to-r from-amber-50 to-yellow-50 dark:from-amber-900/20 dark:to-yellow-900/20 border-b border-amber-200 dark:border-amber-800/30 relative overflow-hidden">
        <div class="absolute top-0 left-0 w-2 h-full bg-gradient-to-b from-amber-400 to-yellow-400"></div>
        <div class="flex items-center justify-between ml-4">
          <div class="flex items-center">
            <div class="w-6 h-6 rounded-full bg-amber-100 dark:bg-amber-900 flex items-center justify-center mr-3">
              <svg class="w-3 h-3 text-amber-600 dark:text-amber-400" fill="none" stroke="currentColor"
                viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.998-.833-2.732 0L4.282 16.5c-.77.833.192 2.5 1.732 2.5z">
                </path>
              </svg>
            </div>
            <span class="text-sm text-amber-800 dark:text-amber-300">
              未检测到登录信息，请先登录
            </span>
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

      <!-- 聊天窗口 -->
      <div class="flex-1 overflow-y-auto px-4 py-6 relative bg-white" ref="chatAreaRef" :class="[
        'scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50',
        'dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800'
      ]">
        <!-- 欢迎背景 -->
        <div v-if="currentMessages.length === 0"
          class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <div class="text-center">
            <div class="text-2xl font-bold text-gray-600 mb-8">有什么可以帮忙的？</div>
          </div>
        </div>

        <ChatWindow :messages="currentMessages" />
      </div>

      <!-- 消息输入区域 -->
      <div
        class="border-t border-green-100 dark:border-emerald-900 px-4 py-4 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm shadow-lg">
        <MessageInput :messages="currentMessages" :session-id="currentConversationId || ''"
          :conversations="conversations" :current-conversation-id="currentConversationId"
          :is-temporary-conversation="isTemporaryConversation" :update-conversation-state="updateConversationState"
          :add-message-to-conversation="addMessageToConversation" :scroll-to-bottom="scrollToBottom"
          @send="sendMessage" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { CSSProperties } from 'vue'
import UserSidebar from "@/shared/components/UserSidebar.vue";
import { deleteSessionRequest } from "@/modules/auth/services/session";
import ChatWindow from "@/shared/components/ChatWindow.vue";
import MessageInput from '@/shared/components/MessageInput.vue'
import { Conversation, Message, RenamePayload, RemovePayload, LoginResponse } from '@/shared/types/type'

// 路由
const router = useRouter()

// 响应式数据 - 保持原有逻辑不变
const showDropdown = ref(false)
const currentRoute = ref('chat')
const conversations = ref<Conversation[]>([])
const currentConversationId = ref<string | null>(null)
const highlightId = ref<string | null>(null)
const chatAreaRef = ref<HTMLElement | null>(null)
const isTemporaryConversation = ref(false)

// 添加一个标志来控制是否应该执行API请求
const shouldLoadData = ref(true);

// 添加一个标志来标识组件是否已卸载
let isComponentMounted = true;

// 添加AbortController来取消正在进行的请求
let abortController: AbortController | null = null;

// 添加消息缓存，减少重复请求
const messageCache = ref<Record<string, Message[]>>({});

// 路由订阅变量
let unsubscribe: (() => void) | null = null;

// 创建一个函数来取消正在进行的请求
function cancelOngoingRequests() {
  if (abortController) {
    abortController.abort();
  }
  abortController = new AbortController();
}


// 添加一个ref来跟踪下拉菜单样式
const dropdownStyles = ref<CSSProperties>({
  position: 'fixed',
  top: '0',
  right: '0',
  width: '16rem',
  zIndex: 9999
});

// 更新下拉菜单位置的方法
const updateDropdownPosition = () => {
  // 获取头像按钮的位置
  const button = document.getElementById('avatar-button');
  if (!button) {
    return;
  }

  const rect = button.getBoundingClientRect();

  dropdownStyles.value = {
    position: 'fixed',
    top: `${rect.bottom + window.scrollY}px`,
    right: `${window.innerWidth - rect.right}px`,
    width: '16rem',
    zIndex: 9999
  };
};

// 添加事件监听器
const handleResize = () => {
  if (showDropdown.value) {
    updateDropdownPosition();
  }
};

// 在组件挂载后添加事件监听器
onMounted(() => {
  // 设置组件为已挂载状态
  isComponentMounted = true;
  // 初始化AbortController
  abortController = new AbortController();

  // 添加窗口大小改变和滚动事件监听器
  window.addEventListener('resize', handleResize);
  window.addEventListener('scroll', handleResize);

  // 添加点击外部区域关闭下拉菜单的事件监听
  document.addEventListener('click', handleClickOutside);

  // 监听路由变化
  unsubscribe = router.beforeEach((to, from, next) => {
    // 如果是跳转到其他页面，停止执行当前页面的API请求
    if (to.path !== from.path) {
      shouldLoadData.value = false;
      cancelOngoingRequests(); // 取消正在进行的请求
    }
    next();
  });

  // 初始化加载会话列表，仅在shouldLoadData为true时执行
  if (shouldLoadData.value) {
    loadConversations();
  }
})

// 在组件销毁前清理监听器
onUnmounted(() => {
  shouldLoadData.value = false; // 组件卸载时停止执行API请求
  isComponentMounted = false;  // 标记组件已卸载
  cancelOngoingRequests(); // 取消正在进行的请求
  // 移除事件监听器
  window.removeEventListener('resize', handleResize);
  window.removeEventListener('scroll', handleResize);
  document.removeEventListener('click', handleClickOutside);
  if (unsubscribe) {
    unsubscribe();
  }
});

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
  } catch {
    // ignore
  }
  return null
}

const currentConversation = computed<Conversation | null>(() => {
  return conversations.value.find(c => c.sessionid === currentConversationId.value) || null
})

const currentMessages = computed<Message[]>(() => {
  return currentConversation.value?.messages || []
})

const isAuthed = computed<boolean>(() => {
  return !!localStorage.getItem('token')
})



async function loadConversations(): Promise<void> {
  // 添加检查，如果不需要加载数据或组件已卸载则直接返回
  if (!shouldLoadData.value || !isComponentMounted) return;

  // 取消之前的请求控制器（如果存在）
  if (abortController) {
    abortController.abort();
  }
  // 创建新的AbortController
  abortController = new AbortController();

  const uid = getUserId()
  if (!uid) return

  try {
    const resp = await fetch(`/session/list?id=${uid}`, {
      headers: {
        token: localStorage.getItem('token') || ''
      },
      signal: abortController.signal  // 添加信号以支持取消请求
    })

    if (resp.ok) {
      // 检查响应是否为JSON类型
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
        
      } else {
        // 如果响应不是JSON类型，则输出警告信息
        const textResponse = await resp.text();
        console.warn('非JSON响应:', textResponse);
      }
    } else {
      // 处理HTTP错误状态
      console.error(`请求失败，状态码: ${resp.status}`);
      if (resp.status === 401) {
        // 如果是未授权错误，可能是token过期，跳转到登录页
        router.push('/login');
      }
    }
  } catch (error) {
    // 检查是否是因为请求被取消导致的错误
    if (error instanceof DOMException && error.name === 'AbortError') {
      console.log('Session list request was cancelled');
    } else {
      console.error('加载会话失败:', error);
    }
  }
}

async function loadMessages(): Promise<void> {
  // 添加检查，如果不需要加载数据或组件已卸载则直接返回
  if (!shouldLoadData.value || !isComponentMounted) return;

  const uid = getUserId()
  if (!uid || !currentConversationId.value) return
  
  const sessionId = currentConversationId.value;
  
  // 检查缓存中是否有数据
  if (messageCache.value[sessionId]) {
    console.log('使用缓存的消息数据');
    const conv = conversations.value.find(c => c.sessionid === sessionId);
    if (conv) {
      conv.messages = messageCache.value[sessionId];
      await scrollToBottom();
      return;
    }
  }

  // 确保有有效的AbortController
  if (!abortController) {
    abortController = new AbortController();
  }

  try {
    const resp = await fetch(
      `/multimodal/result/session?uid=${uid}&sessionid=${sessionId}`,
      {
        headers: { token: localStorage.getItem('token') || '' },
        signal: abortController.signal  // 添加信号以支持取消请求
      }
    )

    if (resp.ok) {
      // 检查响应是否为JSON类型
      const contentType = resp.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const msgs = await resp.json();
        console.log('从后端获取的MultiModalResult数据:', msgs);
        
        // 转换MultiModalResult数据为Message数组
        const converted: Message[] = [];
        msgs.forEach((m: any) => {
          // 添加用户输入消息
          if (m.inputContent) {
            converted.push({
              type: 'USER',
              content: m.inputContent
            });
          }
          
          // 添加助手输出消息
          if (m.outputContent) {
            if (/^####\[推理\]####：/.test(m.outputContent)) {
              converted.push({
                type: 'ASSISTANT',
                content: m.outputContent.replace(/^####\[推理\]####：/, '')
              });
            } else if (/^####\[回复\]####：/.test(m.outputContent)) {
              converted.push({
                type: 'ASSISTANT',
                content: m.outputContent.replace(/^####\[回复\]####：/, '')
              });
            } else {
              converted.push({
                type: 'ASSISTANT',
                content: m.outputContent
              });
            }
          }
        });
        console.log('转换后的Message数据:', converted);
        
        // 缓存消息数据
        messageCache.value[sessionId] = converted;

        const conv = conversations.value.find(c => c.sessionid === sessionId);
        if (conv) {
          conv.messages = converted;
        }

        await scrollToBottom();
      } else {
        // 如果响应不是JSON类型，则输出警告信息
        const textResponse = await resp.text();
        console.warn('非JSON响应:', textResponse);
      }
    } else {
      // 处理HTTP错误状态
      console.error(`请求失败，状态码: ${resp.status}`);
      if (resp.status === 401) {
        // 如果是未授权错误，可能是token过期，跳转到登录页
        router.push('/login');
      }
    }
  } catch (error) {
    // 检查是否是因为请求被取消导致的错误
    if (error instanceof DOMException && error.name === 'AbortError') {
      console.log('Messages request was cancelled');
    } else {
      console.error('加载消息失败:', error);
    }
  }
}

const scrollToBottom = async (): Promise<void> => {
  await new Promise(resolve => setTimeout(resolve, 0))
  if (chatAreaRef.value) {
    chatAreaRef.value.scrollTop = chatAreaRef.value.scrollHeight
  }
}

function switchConversation(sessionid: string | number): void {
  const sessionIdStr = String(sessionid)
  currentConversationId.value = sessionIdStr
  isTemporaryConversation.value = false
  // 直接调用loadMessages，它会检查缓存
  loadMessages()
}

function createTemporaryConversation(): void {
  if (currentConversation.value) {
    currentConversation.value.messages = []
  }
  isTemporaryConversation.value = true
  currentConversationId.value = null
}

async function sendMessage(text: string): Promise<void> {
  console.log('Message sent:', text)
  // 消息发送逻辑已移至MessageInput组件中的handleSend函数
  // 这里保留空函数以保持兼容性
}

async function handleRename(payload: RenamePayload): Promise<void> {
  try {
    const body = { sessionid: payload.sessionid, title: payload.title }
    const resp = await fetch('/session/update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
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

    await deleteSessionRequest({
      userid: uid,
      sessionid: payload.sessionid
    })

    const idx = conversations.value.findIndex(c => c.sessionid === payload.sessionid)
    if (idx >= 0) conversations.value.splice(idx, 1)

    if (currentConversationId.value === payload.sessionid) {
      currentConversationId.value = conversations.value.length
        ? conversations.value[0].sessionid
        : null
      if (currentConversationId.value) await loadMessages()
    }
  } catch (e) {
    console.error('handleRemove error', e)
  }
}

function updateConversationState(sessionId: string, isTemporary: boolean) {
  currentConversationId.value = sessionId
  isTemporaryConversation.value = isTemporary
}

function addMessageToConversation(sessionId: string, message: { type: string, content: string }) {
  const conv = conversations.value.find(c => c.sessionid === sessionId)
  if (!conv) return

  const lastMessage = conv.messages[conv.messages.length - 1]
  if (lastMessage && lastMessage.type === message.type) {
    lastMessage.content = message.content
  } else {
    conv.messages.push({
      type: message.type as 'USER' | 'ASSISTANT',
      content: message.content
    })
  }
}

function goLogin() {
  shouldLoadData.value = false; // 设置标志，停止执行API请求
  cancelOngoingRequests(); // 取消正在进行的请求
  router.push('/login');
}

// 添加一个ref来引用下拉菜单容器
const dropdownContainer = ref<HTMLElement | null>(null)




// 添加点击外部区域关闭下拉菜单的事件监听
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target as Node)) {
    showDropdown.value = false;
  }
};

</script>

<style scoped>
/* 自定义样式补充 */
.bg-grid-pattern {
  background-image:
    linear-gradient(to right, rgba(16, 185, 129, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(16, 185, 129, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

/* 滚动条自定义 */
.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}

.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}

.scrollbar-thin::-webkit-scrollbar-thumb {
  border-radius: 3px;
}

/* 动画效果 */
@keyframes float {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-10px);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .flex.h-screen {
    flex-direction: column;
  }

  .w-64 {
    width: 100%;
    height: auto;
    max-height: 30vh;
  }

  .flex-1 {
    flex: 1;
  }

  /* 调整消息输入区域 */
  .border-t {
    border-top: 1px solid #e5e7eb;
  }

  /* 调整文件显示区域 */
  .files-display-area {
    max-height: 80px;
  }

  /* 调整输入框大小 */
  .message-input {
    font-size: 14px;
  }

  /* 调整按钮大小 */
  .send-button,
  .mic-button {
    width: 28px;
    height: 28px;
  }
}

/* 平板设备优化 */
@media (min-width: 769px) and (max-width: 1024px) {
  .w-64 {
    width: 200px;
  }

  .max-w-4xl {
    max-width: 3xl;
  }
}

/* 大屏设备优化 */
@media (min-width: 1025px) {
  .max-w-4xl {
    max-width: 4xl;
  }
}
</style>