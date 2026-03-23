<template>
  <div class="message-input-container">
    <!-- 文件显示区域 - 横向排列在对话框上方 -->
    <div v-if="selectedFiles.length > 0 || documents.length > 0 || imageUrlInput || imageBase64" class="files-display-area">
      <!-- 已选择的文件（待上传） -->
      <div v-for="(file, index) in selectedFiles" :key="'selected-' + index" class="file-chip">
        <div class="file-chip-icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"></path>
            <polyline points="13 2 13 9 20 9"></polyline>
          </svg>
        </div>
        <span class="file-chip-name">{{ file.name }}</span>
        <button @click="removeFile(index)" class="file-chip-remove" title="删除">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
      
      <!-- 已上传的会话文档 -->
      <div v-for="doc in documents" :key="'doc-' + doc.fileName" class="file-chip file-chip-uploaded">
        <div class="file-chip-icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
          </svg>
        </div>
        <span class="file-chip-name">{{ doc.fileName }}</span>
        <span v-if="doc.fileSize" class="file-chip-size">{{ formatFileSize(doc.fileSize) }}</span>
        <button @click="confirmDelete(doc.fileName)" class="file-chip-remove" title="删除文档">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
      
      <!-- 图片URL -->
      <div v-if="imageUrlInput" class="file-chip file-chip-uploaded">
        <div class="file-chip-icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
        </div>
        <span class="file-chip-name">图片URL</span>
        <button @click="clearImageUrl" class="file-chip-remove" title="删除">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
      
      <!-- Base64图片 -->
      <div v-if="imageBase64" class="file-chip file-chip-uploaded">
        <div class="file-chip-icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
        </div>
        <span class="file-chip-name">Base64图片</span>
        <button @click="clearImageBase64" class="file-chip-remove" title="删除">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
    </div>

    <div class="input-wrapper">
      <label for="file-upload" class="input-left-icon" title="上传文件">
        +
        <input 
          id="file-upload" 
          type="file" 
          ref="fileInputRef"
          multiple 
          @change="handleFileChange"
          class="file-input" 
        />
      </label>
      <textarea ref="textareaRef" v-model="inputText" @keydown.enter.exact.prevent="handleSend"
        @keydown.enter.shift="allowNewline" @input="adjustTextareaHeight" :placeholder="placeholder"
        :maxlength="maxLength" class="message-input" :class="{ 'is-invalid': isInvalid }" rows="1"></textarea>

      <div class="input-right-icons">
        <!-- 多模态输入选项 -->
        <div class="modal-input-menu-container">
          <button @click="toggleModalInputMenu" class="mic-button" title="多模态输入">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
          </button>
          <div v-if="showModalInputMenu" class="export-dropdown">
            <button @click="showImageUrlDialog = true" class="export-option">图片URL</button>
            <button @click="showImageUploadDialog = true" class="export-option">上传图片</button>
          </div>
        </div>
        
        <!-- 导出聊天记录下拉菜单 -->
        <div class="export-menu-container">
          <button @click="toggleExportMenu" class="mic-button" title="下载文档">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
          </button>
          <div v-if="showExportMenu" class="export-dropdown">
            <button @click="exportAsTxt" class="export-option">导出为 TXT</button>
            <button @click="exportAsExcel" class="export-option">导出为 Excel</button>
          </div>
        </div>
        <button @click="handleSend" :disabled="!canSend" class="send-button" :class="{ 'is-loading': sending }">
          <svg v-if="!sending" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="spinner">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"></path>
          </svg>
        </button>
      </div>
    </div>

    <div v-if="errorMessage" class="error-message">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      {{ errorMessage }}
    </div>
    <div v-if="uploadErrorMessage" class="error-message">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      {{ uploadErrorMessage }}
    </div>

    <!-- 图片URL输入弹窗 -->
    <Teleport to="body">
      <div v-if="showImageUrlDialog" class="dialog-overlay" @click="showImageUrlDialog = false">
        <div class="dialog-content" @click.stop>
          <div class="dialog-header">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
            <h4>输入图片URL</h4>
          </div>
          <div class="dialog-body">
            <input v-model="imageUrlInput" type="url" placeholder="请输入图片URL" class="dialog-input" />
          </div>
          <div class="dialog-actions">
            <button @click="showImageUrlDialog = false" class="btn-secondary">取消</button>
            <button @click="confirmImageUrl" class="btn-primary">确认</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 图片上传弹窗 -->
    <Teleport to="body">
      <div v-if="showImageUploadDialog" class="dialog-overlay" @click="showImageUploadDialog = false">
        <div class="dialog-content" @click.stop>
          <div class="dialog-header">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
            <h4>上传图片</h4>
          </div>
          <div class="dialog-body">
            <input type="file" accept="image/*" @change="handleImageUpload" class="dialog-input" />
          </div>
          <div class="dialog-actions">
            <button @click="showImageUploadDialog = false" class="btn-secondary">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 删除确认弹窗 -->
    <Teleport to="body">
      <div v-if="showConfirmDialog" class="dialog-overlay" @click="cancelDelete">
        <div class="dialog-content" @click.stop>
          <div class="dialog-header">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="warning-icon">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
              <line x1="12" y1="9" x2="12" y2="13"></line>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
            <h4>确认删除文档</h4>
          </div>
          <p>
            <strong>{{ truncateFileName(fileToDelete, 30) }}</strong><br>
            <span class="dialog-hint">删除后无法恢复，确定要继续吗？</span>
          </p>
          <div class="dialog-actions">
            <button @click="cancelDelete" class="btn-secondary">取消</button>
            <button @click="performDelete" :disabled="deleting" class="btn-danger">
              {{ deleting ? '删除中...' : '删除' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch } from 'vue'
import { uploadDocumentToSession, getSessionDocuments as fetchSessionDocuments, deleteSessionDocument } from '@/modules/user/services/document'

interface Props {
  placeholder?: string;
  maxLength?: number;
  showCharCounter?: boolean;
  validateInput?: (text: string) => boolean;
  // 添加sessionId属性，用于识别当前会话
  sessionId?: string;
  // 添加onCreateSession回调函数，用于创建会话
  onCreateSession?: (firstMessage: string) => Promise<string | null>;
  // 添加messages属性，用于导出聊天记录
  messages?: Array<{ type: string; content: string }>;
  // 添加当前对话相关的属性
  conversations?: Array<{sessionid: string, title: string, userid: string, messages: Array<{type: string, content: string}>}>;
  currentConversationId?: string | null;
  isTemporaryConversation?: boolean;
  updateConversationState?: (sessionId: string, isTemporary: boolean) => void;
  addMessageToConversation?: (sessionId: string, message: {type: string, content: string}) => void;
  scrollToBottom?: () => Promise<void>;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '给我发消息或布置任务',
  maxLength: Number.MAX_SAFE_INTEGER,
  showCharCounter: true,
  validateInput: (text: string) => text.trim().length > 0,
  sessionId: '',
  onCreateSession: () => (_firstMessage: string) => Promise.resolve(null),
  messages: () => [],
  conversations: () => [],
  currentConversationId: () => '',
  isTemporaryConversation: () => false,
  updateConversationState: () => () => {},
  addMessageToConversation: () => () => {},
  scrollToBottom: () => async () => {}
})

const emit = defineEmits<{
  'send': [text: string]
}>()

const inputText = ref('')
const sending = ref(false)
const uploading = ref(false)
const textareaRef = ref<HTMLTextAreaElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFiles = ref<File[]>([])
const uploadErrorMessage = ref('')

// 多模态输入相关状态
const imageUrlInput = ref('')
const imageBase64 = ref('')
const showModalInputMenu = ref(false)
const showImageUrlDialog = ref(false)
const showImageUploadDialog = ref(false)

// 文档列表相关状态
const loading = ref(false);
const deleting = ref(false);
const documents = ref<any[]>([]);
const showConfirmDialog = ref(false);
const fileToDelete = ref('');

// 计算属性
const canSend = computed(() => {
  return !sending.value &&
    !uploading.value &&
    (inputText.value.trim().length > 0 || selectedFiles.value.length > 0 || imageUrlInput.value || imageBase64.value) &&
    inputText.value.length <= props.maxLength &&
    props.validateInput(inputText.value)
})

const isInvalid = computed(() => {
  return inputText.value.length > 0 && !props.validateInput(inputText.value)
})

const errorMessage = computed(() => {
  if (inputText.value.length > props.maxLength) {
    return `输入内容超出最大长度限制 (${props.maxLength} 字符)`
  }
  if (inputText.value.length > 0 && !props.validateInput(inputText.value)) {
    return '输入内容不符合要求'
  }
  return null
})

// 监听sessionId变化，自动加载文档列表
watch(
  () => props.sessionId,
  async (newSessionId) => {
    if (newSessionId) {
      await loadDocuments();
    } else {
      documents.value = [];
    }
  },
  { immediate: true }
);

// 加载文档列表
async function loadDocuments() {
  if (!props.sessionId) return;
  
  loading.value = true;
  try {
    const docs = await fetchSessionDocuments(props.sessionId);
    console.log('获取到的文档数据:', docs); // 添加调试日志
    
    documents.value = docs.map((doc: any) => {
      console.log('处理文档项:', doc); // 添加调试日志
      
      // 根据后端实际返回的WorldSession实体进行映射
      return {
        fileName: doc.filename || doc.fileName || doc.name || '未知文件',
        fileSize: doc.fileSize || doc.size || doc.file_size,
        uploadTime: doc.uploadTime || doc.time || doc.upload_time,
        // 添加其他可能的字段
        id: doc.id,
        userid: doc.userid,
        sessionid: doc.sessionid,
        md5: doc.md5,
        chunkcount: doc.chunkcount
      };
    });
    
    console.log('处理后的文档数据:', documents.value); // 添加调试日志
  } catch (error) {
    console.error('加载文档列表失败:', error);
    documents.value = [];
  } finally {
    loading.value = false;
  }
}

// 删除文档
async function confirmDelete(fileName: string) {
  fileToDelete.value = fileName;
  showConfirmDialog.value = true;
}

function cancelDelete() {
  showConfirmDialog.value = false;
  fileToDelete.value = '';
}

async function performDelete() {
  if (!props.sessionId || !getUserId() || !fileToDelete.value) return;
  
  deleting.value = true;
  try {
    await deleteSessionDocument(fileToDelete.value, getUserId(), props.sessionId);
    
    // 从列表中移除已删除的文档
    documents.value = documents.value.filter(doc => doc.fileName !== fileToDelete.value);
  } catch (error) {
    console.error('删除文档失败:', error);
  } finally {
    deleting.value = false;
    showConfirmDialog.value = false;
    fileToDelete.value = '';
  }
}

// 格式化文件大小
function formatFileSize(bytes?: number): string {
  if (bytes === undefined || bytes === null) return '';
  
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 截断文件名
function truncateFileName(fileName: string, maxLength: number): string {
  if (fileName.length <= maxLength) return fileName;
  const ext = fileName.split('.').pop();
  const nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
  if (nameWithoutExt.length > maxLength - 5) {
    return nameWithoutExt.substring(0, maxLength - 5) + '...' + (ext ? '.' + ext : '');
  }
  return fileName;
}

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

// 处理文件选择
function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const newFiles = Array.from(target.files)
    selectedFiles.value = [...selectedFiles.value, ...newFiles]
    
    // 清空input值，以便可以选择相同的文件
    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }
  }
}

// 移除选定的文件
function removeFile(index: number) {
  selectedFiles.value.splice(index, 1)
  selectedFiles.value = [...selectedFiles.value] // 触发响应式更新
}

// 导出功能相关
const showExportMenu = ref(false)

function toggleExportMenu() {
  showExportMenu.value = !showExportMenu.value
}

function closeExportMenu() {
  showExportMenu.value = false
}

// 多模态输入功能相关
function toggleModalInputMenu() {
  showModalInputMenu.value = !showModalInputMenu.value
}

function confirmImageUrl() {
  if (imageUrlInput.value) {
    showImageUrlDialog.value = false
    showModalInputMenu.value = false
  }
}

function clearImageUrl() {
  imageUrlInput.value = ''
}

function clearImageBase64() {
  imageBase64.value = ''
}

function handleImageUpload(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    
    // 检查文件大小
    if (file.size > 10 * 1024 * 1024) { // 10MB限制
      uploadErrorMessage.value = '图片大小不能超过10MB';
      return;
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      uploadErrorMessage.value = '请上传图片文件';
      return;
    }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      const result = e.target?.result as string
      if (result) {
        imageBase64.value = result
        showImageUploadDialog.value = false
        showModalInputMenu.value = false
        uploadErrorMessage.value = ''; // 清除错误信息
      }
    }
    reader.onerror = () => {
      uploadErrorMessage.value = '图片读取失败，请重试';
    }
    reader.readAsDataURL(file)
  }
}

async function exportAsTxt() {
  if (!props.messages || props.messages.length === 0) {
    console.log("没有消息可以导出");
    closeExportMenu();
    return;
  }

  // 按照项目规范，导出最简问答格式
  let content = '';
  for (let i = 0; i < props.messages.length; i += 2) {
    const question = props.messages[i]?.content || '';
    const answer = i + 1 < props.messages.length ? props.messages[i + 1]?.content : '';
    
    if (question) {
      content += '问：' + question + '\n';
      if (answer) {
        content += '答：' + answer + '\n';
      }
      content += '\n'; // 空行分隔
    }
  }

  // 创建并下载文件
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `chat_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.txt`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);

  closeExportMenu();
}

async function exportAsExcel() {
  if (!props.messages || props.messages.length === 0) {
    console.log("没有消息可以导出");
    closeExportMenu();
    return;
  }

  // 动态导入xlsx库
  const { utils, writeFile } = await import('xlsx');

  // 按照项目规范，整理为问答对格式
  const data = [];
  for (let i = 0; i < props.messages.length; i += 2) {
    const question = props.messages[i]?.content || '';
    const answer = i + 1 < props.messages.length ? props.messages[i + 1]?.content : '';
    
    if (question) {
      data.push({
        '问题': question,
        '答案': answer
      });
    }
  }

  // 创建工作簿
  const ws = utils.json_to_sheet(data);
  const wb = utils.book_new();
  utils.book_append_sheet(wb, ws, '聊天记录');

  // 导出文件
  writeFile(wb, `chat_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.xlsx`);

  closeExportMenu();
}

// 创建新对话并使用指定文本作为标题（用于首次发送消息时）
async function createRealConversationWithText(firstMessage: string): Promise<string | null> {
  const uid = getUserId()
  if (!uid) return null;

  // 使用用户输入的消息作为标题
  let title = firstMessage.substring(0, 10); // 取前10个字符作为标题
  if (firstMessage.length > 10) {
    title += '...'; // 如果超过10个字符，添加省略号
  }

  console.log('创建会话标题:', title);
  try {
    const body = { uid: uid, title: title }  // 修改：将 userid 改为 uid 以匹配后端ChatMsg类
    const resp = await fetch('/session/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify(body)
    })

    if (resp.ok) {
      const result = await resp.json()
      // 确保返回的会话数据符合类型定义
      const newConversation = {
        sessionid: String(result.sessionid),
        title: title, // 强制使用我们设置的标题，而不是后端返回的
        userid: String(result.userid),
        messages: []
      }
      
      // 将新会话添加到列表顶部
      if (props.conversations) {
        props.conversations.unshift(newConversation);
      }
      
      // 更新当前会话ID
      if (props.updateConversationState) {
        props.updateConversationState(newConversation.sessionid, false);
      }
      
      return newConversation.sessionid;
    } else {
      console.error('创建会话失败:', resp.status);
      // 如果API返回错误，仍然创建一个本地会话记录
      const localConversation = {
        sessionid: `local_${Date.now()}`,
        title: title,
        userid: uid,
        messages: []
      };
      
      if (props.conversations) {
        props.conversations.unshift(localConversation);
      }
      if (props.updateConversationState) {
        props.updateConversationState(localConversation.sessionid, false);
      }
      
      return localConversation.sessionid;
    }
  } catch (error) {
    console.error('创建会话失败:', error)
    // 在异常情况下，仍然创建一个本地会话记录
    const localConversation = {
        sessionid: `local_${Date.now()}`,
        title: title,
        userid: uid,
        messages: []
      };
    
    if (props.conversations) {
      props.conversations.unshift(localConversation);
    }
    if (props.updateConversationState) {
      props.updateConversationState(localConversation.sessionid, false);
    }
    
    return localConversation.sessionid;
  }
}

// 发送消息
async function sendMessage(text: string, sessionId?: string): Promise<void> {
  const uid = getUserId()
  if (!uid) return

  // 使用传入的会话ID或从props获取
  let actualSessionId = sessionId || props.sessionId;

  // 用户发送消息后滚动到底部
  await props.scrollToBottom();

  try {
    // 创建FormData对象来发送多模态数据
    const formData = new FormData();
    formData.append('text', text);
    formData.append('uid', uid); // FormData会自动将值转换为字符串，后端会解析为Integer
    formData.append('sessionid', actualSessionId);
    
    // 添加标题参数
    let title = text.substring(0, 10); // 取前10个字符作为标题
    if (text.length > 10) {
      title += '...'; // 如果超过10个字符，添加省略号
    }
    formData.append('title', title);
    
    // 添加图片URL
    if (imageUrlInput.value) {
      formData.append('imageUrl', imageUrlInput.value);
    }
    
    // 添加Base64图片
    if (imageBase64.value) {
      formData.append('imageBase64', imageBase64.value);
    }
    
    // 添加文件
    for (const file of selectedFiles.value) {
      formData.append('files', file);
    }

    const resp = await fetch('/multimodal/process', {
      method: 'POST',
      headers: {
        token: localStorage.getItem('token') || ''
      },
      body: formData
    })

    if (!resp.ok) {
      console.error('请求失败，状态码:', resp.status);
      uploadErrorMessage.value = `请求失败，状态码: ${resp.status}`;
      if (resp.status === 401) {
        // 如果是未授权错误，跳转到登录页
        window.location.href = '/login';
      }
      return;
    }
    
    const reader = resp.body?.getReader()
    if (!reader) return
    
    const decoder = new TextDecoder('utf-8')
    let full = ''
    let reasoningPart = ''
    let responsePart = ''
    let normalPart = '' // 添加一个变量来存储没有类型标记的内容
    let currentType = null
    try {
      while (true) {
        const { done, value } = await reader.read()

        if (done) break
        
        const chunk = decoder.decode(value, { stream: true })
        console.log('接收到块:', chunk);
        full += chunk

        // 首先将块按标记分割
        const parts = chunk.split(/(####\[推理\]####：|####\[回复\]####：)/g)
        
        for (let i = 0; i < parts.length; i++) {
          const part = parts[i]
          if (part === '####[推理]####：') {
            currentType = 'reasoning'
          } else if (part === '####\[回复\]####：') {
            currentType = 'response'
          } else if (part) { // 非空内容部分
            if (currentType === 'reasoning') {
              reasoningPart += part
              
              // 更新推理部分
              if (props.addMessageToConversation) {
                props.addMessageToConversation(actualSessionId, { 
                  type: 'ASSISTANT_REASONING', 
                  content: reasoningPart 
                });
              }
            } else if (currentType === 'response') {
              responsePart += part
              
              // 更新回复部分
              if (props.addMessageToConversation) {
                props.addMessageToConversation(actualSessionId, { 
                  type: 'ASSISTANT', 
                  content: responsePart 
                });
              }
            } else {
              // 处理没有类型标记的内容，累积到normalPart中
              normalPart += part
              
              // 更新普通内容部分
              if (props.addMessageToConversation) {
                props.addMessageToConversation(actualSessionId, { 
                  type: 'ASSISTANT', 
                  content: normalPart 
                });
              }
            }
          }
        }
      }
    } finally {
      reader.releaseLock()
    }
    
    // AI响应完成后，滚动到底部
    await props.scrollToBottom();
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

async function handleSend() {
  if (!canSend.value) return

  sending.value = true
  
  const text = inputText.value.trim();
  
  // 2. 如果当前没有会话ID但需要创建会话，则先创建会话
  let actualSessionId: string | null = props.sessionId;
  if (!actualSessionId){
    // 使用用户输入的内容作为标题创建会话
    actualSessionId = await createRealConversationWithText(text);
    console.log('创建会话成功，会话ID:', actualSessionId);
    if (!actualSessionId) {
      console.error('创建会话失败');
      sending.value = false;
      return;
    }
  }
  
  if (props.addMessageToConversation && actualSessionId ) {
    props.addMessageToConversation(actualSessionId , { type: 'USER', content: text });
    await props.scrollToBottom();
  }


  // 3. 上传文件（如果有的话），必须在获得有效sessionId之后
  if (selectedFiles.value.length > 0 && actualSessionId) {
    const uploadSuccess = await uploadFiles(actualSessionId);
    if (!uploadSuccess) {
      sending.value = false;
      return;
    }
  }

  // 4. 最后调用sendMessage发送消息到后端，传递实际的会话ID
  await sendMessage(text, actualSessionId);

  // 5. 发送消息后，刷新文档列表和滚动到底部
  if (actualSessionId) {
    await loadDocuments(); // 重新加载文档列表
  }

  // 重置状态
  setTimeout(() => {
    sending.value = false
    inputText.value = ''
    selectedFiles.value = []
    imageUrlInput.value = ''
    imageBase64.value = ''
    adjustTextareaHeight()
  }, 500)
}

// 上传文件到后端
async function uploadFiles(sessionId: string): Promise<boolean> {
  if (selectedFiles.value.length === 0) {
    return true // 没有文件需要上传，直接返回成功
  }

  uploading.value = true
  uploadErrorMessage.value = '' // 清空之前的错误信息

  try {
    // 会话内上传 - 每个文件单独上传
    for (const file of selectedFiles.value) {
      const uploadParams = {
        file: file,
        userId: getUserId(),
        sessionId: sessionId
      };

      await uploadDocumentToSession(uploadParams);
    }

    return true
  } catch (error: any) {
    console.error('文件上传失败:', error)
    uploadErrorMessage.value = error.message || '文件上传失败，请稍后重试'
    return false
  } finally {
    uploading.value = false
  }
}

function allowNewline(_e: KeyboardEvent) {
  return true
}

function adjustTextareaHeight() {
  if (!textareaRef.value) return

  textareaRef.value.style.height = 'auto'

  const minHeight = 44
  const adjustedHeight = Math.min(textareaRef.value.scrollHeight, 200)
  textareaRef.value.style.height = `${Math.max(adjustedHeight, minHeight)}px`
}

// 监听输入变化，调整高度
watch(inputText, async () => {
  await nextTick()
  adjustTextareaHeight()
})

</script>

<style scoped>
/* 主容器 */
.message-input-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 输入框包装器 */
.input-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.06);
  transition: border-color 0.2s, box-shadow 0.2s;
  min-height: 44px;
  max-height: 200px;
  overflow: hidden;
}

.input-wrapper:focus-within {
  border-color: #d1d5db;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07), 0 2px 4px rgba(0, 0, 0, 0.06);
}

/* 左侧加号图标 */
.input-left-icon {
  font-size: 18px;
  font-weight: 600;
  color: #9ca3af;
  flex-shrink: 0;
  margin-right: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 消息输入框 */
.message-input {
  flex: 1;
  resize: none;
  border: none;
  padding: 8px 0;
  background: transparent;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  font-size: 15px;
  line-height: 1.5;
  color: #1f2937;
  outline: none;
  min-width: 0;
}

.message-input::placeholder {
  color: #9ca3af;
}

.message-input.is-invalid {
  color: #ef4444;
}

/* 右侧图标容器 */
.input-right-icons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

/* 语音按钮 */
.mic-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.mic-button:hover {
  background: #e5e7eb;
  color: #374151;
  transform: scale(1.05);
}

.mic-button:active {
  transform: scale(0.95);
}

/* 发送按钮 */
.send-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  background: #9ca3af;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-button:hover:not(:disabled) {
  background: #6b7280;
  transform: scale(1.05);
}

.send-button:active:not(:disabled) {
  transform: scale(0.95);
}

.send-button:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* 加载动画 */
.spinner {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 文件显示区域 - 横向排列 */
.files-display-area {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 6px;
  padding: 8px 12px;
  margin: 0 16px 8px 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  max-height: 100px;
  overflow-y: auto;
}

/* 文件标签样式 */
.file-chip {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 4px 8px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 16px;
  font-size: 11px;
  color: #374151;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  flex-shrink: 0;
  max-width: 180px;
}

.file-chip:hover {
  border-color: #9ca3af;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 已上传文档标签 - 不同颜色区分 */
.file-chip-uploaded {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.file-chip-uploaded:hover {
  border-color: #93c5fd;
}

.file-chip-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  flex-shrink: 0;
}

.file-chip-uploaded .file-chip-icon {
  color: #3b82f6;
}

.file-chip-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
  max-width: 100px;
}

.file-chip-size {
  font-size: 9px;
  color: #9ca3af;
  flex-shrink: 0;
}

.file-chip-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  padding: 0;
  background: #e5e7eb;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
  color: #6b7280;
}

.file-chip-remove:hover {
  background: #ef4444;
  color: white;
  transform: scale(1.1);
}

.file-input {
  display: none;
}

/* 多模态输入菜单 */
.modal-input-menu-container {
  position: relative;
}

/* 导出下拉菜单 */
.export-menu-container {
  position: relative;
}

.export-dropdown {
  position: absolute;
  bottom: calc(100% + 8px);
  right: 0;
  z-index: 1000;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.06);
  min-width: 150px;
  overflow: hidden;
  animation: slideUp 0.15s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.export-option {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 10px 14px;
  background: none;
  border: none;
  text-align: left;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: background 0.15s;
}

.export-option:hover {
  background: #f9fafb;
}

/* 确认对话框 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.15s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  max-width: 380px;
  width: 90%;
  animation: slideUp 0.2s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.warning-icon {
  color: #f59e0b;
}

.dialog-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.dialog-content p {
  margin: 0 0 24px 0;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

.dialog-hint {
  font-size: 13px;
  color: #9ca3af;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-secondary {
  padding: 8px 20px;
  background: #f9fafb;
  color: #4b5563;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.btn-danger {
  padding: 8px 20px;
  background: #ef4444;
  color: white;
  border: 1px solid #ef4444;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-danger:hover {
  background: #dc2626;
  border-color: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(239, 68, 68, 0.2);
}

.btn-danger:disabled {
  background: #fecaca;
  border-color: #fecaca;
  color: #991b1b;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #ef4444;
  font-size: 13px;
  padding: 8px 12px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
}

/* 删除确认弹窗 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 提高z-index确保在最上层 */
  animation: fadeIn 0.15s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15), 0 10px 10px rgba(0, 0, 0, 0.1);
  animation: scaleIn 0.2s ease-out;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.warning-icon {
  color: #f59e0b;
}

.dialog-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.dialog-content p {
  margin: 0 0 24px 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-secondary,
.btn-danger {
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 弹窗输入框 */
.dialog-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 16px;
  transition: border-color 0.2s;
}

.dialog-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 主按钮样式 */
.btn-primary {
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.2);
}

/* 弹窗内容区域 */
.dialog-body {
  margin-bottom: 20px;
}

/* 滚动条样式优化 */
.documents-content::-webkit-scrollbar {
  width: 6px;
}

.documents-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.documents-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.documents-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
