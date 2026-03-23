<template>
  <div class="flex h-screen bg-gray-50">
    <!-- 左侧导航栏 -->
    <UserSidebar
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      :current-route="'knowledge-base'"
      @switch-conversation="switchConversation"
      @create-temporary-conversation="createTemporaryConversation"
    />
    <div class="flex-1 flex flex-col overflow-hidden">
    <!-- 顶部区域：标题 + 搜索 + 导入按钮 -->
    <div class="bg-white border-b border-gray-200 p-4">
      <div class="max-w-7xl mx-auto">
        <div class="flex items-center justify-between mb-4">
          <h1 class="text-xl font-semibold text-gray-800">收藏夹</h1>
          <div class="flex items-center space-x-3">
            <!-- 搜索框 -->
            <div class="relative">
              <input 
                v-model="searchQuery" 
                type="text" 
                placeholder="搜索" 
                class="w-64 px-4 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              />
              <span class="absolute right-3 top-2.5 text-gray-400">🔍</span>
            </div>
            <!-- 导入按钮 -->
            <button @click="triggerFileSelect" :disabled="uploading"
              class="px-4 py-2 bg-gray-800 hover:bg-gray-900 text-white rounded-lg flex items-center disabled:opacity-50 transition-colors duration-200">
              <span class="mr-2">+</span>
              <span v-if="!uploading">收藏</span>
              <span v-else>上传中...</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 overflow-y-auto p-6">
      <div class="max-w-7xl mx-auto">
        <!-- 空状态 -->
        <div v-if="filteredDocuments.length === 0" class="flex flex-col items-center justify-center h-64">
          <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <span class="text-gray-400 text-2xl">📁</span>
          </div>
          <p class="text-gray-500 text-sm">暂无文档</p>
          <p class="text-gray-400 text-xs mt-2">点击右上角的「收藏」按钮添加文档</p>
        </div>

        <!-- 文件卡片网格 -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          <div v-for="doc in filteredDocuments" :key="doc.id"
            class="bg-white rounded-lg border border-gray-200 hover:border-green-300 hover:shadow-md transition-all duration-200 group cursor-pointer"
          >
            <div class="p-4">
              <!-- 文件标题和内容预览 -->
              <div class="mb-4">
                <h3 class="text-sm font-medium text-gray-800 mb-2 line-clamp-2">{{ doc.filename }}</h3>
                <div class="bg-gray-50 rounded-lg p-3 mb-3">
                  <div class="flex items-center mb-2">
                    <div class="w-8 h-8 bg-blue-100 rounded flex items-center justify-center mr-2">
                      <span class="text-blue-600 text-xs font-bold">W</span>
                    </div>
                  </div>
                </div>
                <p class="text-xs text-gray-400 line-clamp-2">暂无内容预览</p>
              </div>
              
              <!-- 底部信息 -->
              <div class="flex items-center justify-between pt-3 border-t border-gray-100">
                <div class="flex items-center text-xs text-gray-400">
                  <span class="mr-2">📄</span>
                  <span>Doc</span>
                  <span class="mx-2">•</span>
                  <span>{{ new Date().toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }) }}</span>
                </div>
                <button @click.stop="deleteDocument(doc.filename, doc.userid)" 
                  class="text-gray-300 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all duration-200"
                  title="删除文档"
                >
                  🗑️
                </button>
              </div>
            </div>
          </div>
          
          <!-- 添加文件卡片 -->
          <div @click="triggerFileSelect"
            class="bg-white rounded-lg border border-dashed border-gray-300 hover:border-green-400 hover:bg-green-50 transition-all duration-200 cursor-pointer flex flex-col items-center justify-center p-8 min-h-[200px]"
          >
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mb-3 group-hover:bg-green-100">
              <span class="text-gray-400 text-xl group-hover:text-green-600">+</span>
            </div>
            <p class="text-sm text-gray-500">添加文件</p>
          </div>
        </div>
      </div>
    </div>
    
    <input id="file-import" ref="fileInputRef" type="file" multiple @change="handleFileImport" class="hidden" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { uploadGlobalDocument } from '@/modules/user/services/document'  // 导入新的API函数
import { getKnowledgeBaseList, deleteKnowledgeBaseDocument, WorldGlobal } from '@/modules/user/services/knowledgeBase' // 导入知识库API
import UserSidebar from '@/shared/components/UserSidebar.vue';
import { Conversation } from '@/shared/types/type';

const fileInputRef = ref<HTMLInputElement | null>(null)
const documents = ref<WorldGlobal[]>([]) // 修改类型为WorldGlobal
const uploading = ref(false)
const searchQuery = ref('')
const activeTag = ref('all')

// 导航栏相关状态
const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);

// 标签数据 - 与图片一致
const tags = ref([
  { id: 'all', name: '全部', count: 0 },
  { id: 'untagged', name: '无标签', count: 0 },
  { id: 'learning', name: '学习文档', count: 0 },
  { id: 'knowledge', name: '文档知识库', count: 0 }
])

// 从 token 中解析用户信息
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

// 获取用户ID
function getUserId(): string {
  const token = localStorage.getItem('token')
  const payload = parseJwt(token)

  // 优先从token中获取用户ID
  if (payload?.id) return payload.id
  if (payload?.userId) return payload.userId
  if (payload?.userid) return payload.userid

  // 其次从userInfo中获取
  try {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const user: any = JSON.parse(userInfo)
      return user?.id || ''
    }
  } catch {
    // ignore
  }
  return ''
}

// 触发文件选择
function triggerFileSelect() {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 处理文件导入
async function handleFileImport(event: Event) {
  const target = event.target as HTMLInputElement
  if (!target.files || target.files.length === 0) return

  const files = Array.from(target.files)

  uploading.value = true

  try {
    // 使用新的API函数上传文件
    await uploadGlobalDocument(files)

    // 上传成功后重新加载文档列表
    await loadDocuments()
    alert(`成功导入 ${files.length} 个文件！`)
  } catch (error: any) {
    console.error('文件导入失败:', error)
    alert(`文件导入失败: ${error.message}`)
  } finally {
    uploading.value = false

    // 清空文件选择
    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }
  }
}

// 加载文档列表
async function loadDocuments() {
  try {
    const userId = getUserId();
    if (!userId) {
      throw new Error('用户未登录');
    }

    const result = await getKnowledgeBaseList(userId);
    documents.value = result;
    
    // 更新标签计数
    updateTagCounts()
  } catch (error: any) {
    console.error('加载文档列表失败:', error);
    alert(`加载文档列表失败: ${error.message}`);
    documents.value = []; // 出错时清空列表
    updateTagCounts()
  }
}

// 删除文档
async function deleteDocument(filename: string, userId: number) {
  if (!confirm(`确定要删除文档 "${filename}" 吗？此操作不可撤销。`)) {
    return;
  }

  try {
    const success = await deleteKnowledgeBaseDocument(filename, userId);
    if (success) {
      // 从本地列表中移除已删除的文档
      documents.value = documents.value.filter(doc => doc.filename !== filename);
      updateTagCounts()
      alert('文档删除成功！');
    }
  } catch (error: any) {
    console.error('删除文档失败:', error);
    alert(`删除文档失败: ${error.message}`);
  }
}



// 过滤文档
const filteredDocuments = computed(() => {
  let result = documents.value
  
  // 按标签过滤
  if (activeTag.value !== 'all') {
    // 这里根据实际情况实现标签过滤逻辑
    // 暂时返回所有文档
  }
  
  // 按搜索词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(doc => 
      doc.filename.toLowerCase().includes(query)
    )
  }
  
  return result
})

// 更新标签计数
function updateTagCounts() {
  tags.value.forEach(tag => {
    if (tag.id === 'all') {
      tag.count = documents.value.length
    } else {
      // 这里根据实际情况实现标签计数逻辑
      // 暂时返回0
      tag.count = 0
    }
  })
}



// 导航栏相关方法
const switchConversation = (sessionid: string | number) => {
  currentConversationId.value = String(sessionid);
  // 可以在这里添加切换对话的逻辑
};

const createTemporaryConversation = () => {
  // 创建临时对话的逻辑
};



onMounted(() => {
  loadDocuments()
})
</script>

<style scoped>
/* 添加一些额外的绿色主题样式 */
:deep() {
  /* 自定义滚动条样式 */
  .overflow-y-auto::-webkit-scrollbar {
    width: 6px;
  }
  
  .overflow-y-auto::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
  }
  
  .overflow-y-auto::-webkit-scrollbar-thumb {
    background: #9ca3af;
    border-radius: 3px;
  }
  
  .overflow-y-auto::-webkit-scrollbar-thumb:hover {
    background: #6b7280;
  }
}

/* 悬停动画效果 */
.hover-lift {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hover-lift:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>