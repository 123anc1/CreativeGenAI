<template>
  <div
    class="flex w-56 h-full flex-col bg-white relative z-10 shadow-lg">
    <div class="p-4 flex items-center">
      <div class="w-8 h-8 rounded-lg overflow-hidden">
        <img src="/imgAi.png" alt="Logo" class="w-full h-full object-contain" />
      </div>
    </div>
    <!-- 顶部新建对话按钮 -->
    <div class="px-4 pb-4">
      <button @click="createTemporaryConversation"
        class="w-full rounded-lg px-4 py-3 text-sm font-medium transition-all duration-300 transform hover:scale-[1.02] active:scale-95 shadow-md relative overflow-hidden group"
        :class="[
          'bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600',
          'text-white shadow-green-200/50 dark:shadow-emerald-900/50'
        ]">
        <span class="relative z-10 flex items-center justify-center">
          <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
          </svg>
          新建对话
        </span>
        <span
          class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-green-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></span>
      </button>
    </div>

    <!-- 功能链接 -->
    <div class="px-2 py-1 bg-white">
      <button v-for="item in navigationItems" :key="item.id" @click="handleNavigate(item)"
        class="w-full text-left px-3 py-2.5 text-sm transition-all duration-300 rounded-lg group hover:pl-4 relative overflow-hidden"
        :class="[
          isActiveRoute(item)
            ? 'bg-gradient-to-r from-emerald-50 to-green-50 text-emerald-700 border-emerald-200'
            : 'hover:bg-gradient-to-r hover:from-green-50/50 hover:to-emerald-50/50',
          'border border-transparent hover:border-green-200',
          'text-gray-700 hover:text-gray-900'
        ]">
        <span class="relative z-10 flex items-center">
          <span class="mr-2 text-base">{{ item.emoji }}</span>
          {{ item.label }}
          <svg
            class="w-3 h-3 ml-auto opacity-0 group-hover:opacity-100 transform group-hover:translate-x-1 transition-all duration-300"
            fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
          </svg>
        </span>
        <span
          class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-green-400 to-emerald-400 group-hover:w-full transition-all duration-500"></span>
      </button>
    </div>

    <!-- 对话列表 -->
    <div
      class="flex-1 overflow-y-auto px-2 scrollbar-thin scrollbar-thumb-green-300 scrollbar-track-green-50 dark:scrollbar-thumb-emerald-700 dark:scrollbar-track-gray-800">
      <ConversationList :conversations="conversations ?? []" :current-id="currentConversationId ?? null"
        :highlight-id="highlightId ?? null" @select="switchConversation" @rename="handleRename"
        @remove="handleRemove" />
    </div>
    
    <!-- 底部用户下拉菜单 -->
    <div class="p-3 bg-white">
      <div class="flex items-center">
        <UserDropdown />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'
import { useRouter } from 'vue-router'
import ConversationList from "./ConversationList.vue"
import UserDropdown from "./UserDropdown.vue"
import { Conversation, RenamePayload, RemovePayload } from '@/shared/types/type'

const router = useRouter()

const props = defineProps<{
  conversations?: Conversation[]
  currentConversationId?: string | null
  highlightId?: string | null
  currentRoute?: string
}>()

const emit = defineEmits<{
  switchConversation: [sessionid: string | number]
  createTemporaryConversation: []
  rename: [payload: RenamePayload]
  remove: [payload: RemovePayload]
}>()

const navigationItems = [
  {
    id: 'chat',
    label: '对话',
    emoji: '💬',
    route: 'chat',
    path: '/chat',
    icon: () => h('svg', {
      fill: 'none',
      stroke: 'currentColor',
      viewBox: '0 0 24 24',
      class: 'w-5 h-5'
    }, [
      h('path', {
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        d: 'M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z'
      })
    ])
  },
  {
    id: 'knowledge-base',
    label: '收藏夹',
    emoji: '📚',
    route: 'knowledge-base',
    path: '/knowledge-base',
    icon: () => h('svg', {
      fill: 'none',
      stroke: 'currentColor',
      viewBox: '0 0 24 24',
      class: 'w-5 h-5'
    }, [
      h('path', {
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        d: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253'
      })
    ])
  },
  {
    id: 'image-generation',
    label: '生成',
    emoji: '🖼️',
    route: 'image-generation',
    path: '/image-generation',
    icon: () => h('svg', {
      fill: 'none',
      stroke: 'currentColor',
      viewBox: '0 0 24 24',
      class: 'w-5 h-5'
    }, [
      h('path', {
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        d: 'M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z'
      })
    ])
  },
  {
    id: 'image-sharing',
    label: '灵感',
    emoji: '📷',
    route: 'image-sharing',
    path: '/user/image-sharing',
    icon: () => h('svg', {
      fill: 'none',
      stroke: 'currentColor',
      viewBox: '0 0 24 24',
      class: 'w-5 h-5'
    }, [
      h('path', {
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        d: 'M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z'
      })
    ])
  },
  {
    id:"canvas",
    label: '画布',
    emoji: '🎨',
    route: 'canvas',
    path: '/canvas',
    icon: () => h('svg', {
      fill: 'none',
      stroke: 'currentColor',
      viewBox: '0 0 24 24',
      class: 'w-5 h-5'
    }, [
      h('path', {
        'stroke-linecap': 'round',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        d: 'M9.75 17L9 20l-1.75-2.25M21 3.75v.01m-.75-3.75H3.75m0 0h-.01M7.5 21v-3.75m0 0H9m-3.75 3.75h3.75'
      })
    ])
  }
]

const isActiveRoute = (item: typeof navigationItems[0]) => {
  if (!props.currentRoute) return false
  return props.currentRoute === item.route
}

const handleNavigate = (item: typeof navigationItems[0]) => {
  if (item.path) {
    router.push(item.path)
  }
}

const switchConversation = (sessionid: string | number) => {
  emit('switchConversation', sessionid)
}

const createTemporaryConversation = () => {
  emit('createTemporaryConversation')
}

const handleRename = async (payload: RenamePayload) => {
  emit('rename', payload)
}

const handleRemove = async (payload: RemovePayload) => {
  emit('remove', payload)
}
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}

.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}

.scrollbar-thin::-webkit-scrollbar-thumb {
  border-radius: 3px;
}
</style>
