<template>
  <div class="conversation-list">
    <div 
      v-for="conversation in conversations" 
      :key="conversation.sessionid" 
      class="conversation-item"
      :class="{ 
        'highlight-animation': highlightId === String(conversation.sessionid),
        'active': currentId === String(conversation.sessionid) 
      }"
    >
      <div class="conversation-content">
        <button 
          @click="selectConversation(conversation.sessionid)" 
          :class="getButtonClass(conversation)"
          class="conversation-button"
          :aria-current="currentId === conversation.sessionid ? 'page' : undefined"
        >
          <span class="conversation-title">{{ conversation.title || '新对话' }}</span>
        </button>

        <!-- 操作菜单 -->
        <div 
          class="menu-container" 
          @mouseenter="startHover(conversation.sessionid)"
          @mouseleave="clearHover(conversation.sessionid)"
          :ref="el => setMenuRef(el, conversation.sessionid)"
        >
          <button 
            @click.stop="toggleMenu(conversation.sessionid)"
            :aria-expanded="activeMenu === conversation.sessionid"
            :aria-haspopup="true" 
            class="menu-toggle"
            :title="activeMenu === conversation.sessionid ? '关闭菜单' : '更多操作'"
          >
            ⋯
          </button>

          <div 
            v-if="activeMenu === conversation.sessionid" 
            class="menu-dropdown" 
            role="menu" 
            @keydown.esc="closeMenu"
          >
            <button @click.stop="openRenameModal(conversation)" role="menuitem" class="menu-item">
              重命名
            </button>
            <button @click.stop="openDeleteConfirm(conversation)" role="menuitem" class="menu-item danger">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 重命名模态框 -->
    <Teleport to="body">
      <div v-if="showRenameModal" class="modal-overlay" @click="closeRenameModal">
        <div class="modal-content" @click.stop @keyup.esc="closeRenameModal">
          <h3>重命名对话</h3>
          <input 
            type="text" 
            v-model="renameInput" 
            ref="renameInputRef" 
            placeholder="输入新名称" 
            class="rename-input"
            @keyup.enter="confirmRename" 
          />
          <div class="modal-actions">
            <button @click="closeRenameModal" class="btn-cancel">取消</button>
            <button @click="confirmRename" class="btn-confirm">确认</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 删除确认模态框 -->
    <Teleport to="body">
      <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
        <div class="modal-content" @click.stop>
          <h3>确认删除</h3>
          <p>您确定要删除 "<strong>{{ deleteConversation?.title }}</strong>" 吗？此操作不可撤销。</p>
          <div class="modal-actions">
            <button @click="closeDeleteConfirm" class="btn-cancel">取消</button>
            <button @click="confirmDelete" class="btn-danger">删除</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted, ComponentPublicInstance } from 'vue';
import { Conversation, RenamePayload, RemovePayload } from '@/shared/types/type';

// 组件props定义
interface Props {
  conversations: Conversation[];
  currentId: string | null;
  highlightId: string | null;
}

const props = withDefaults(defineProps<Props>(), {
  conversations: () => [],
  currentId: null,
  highlightId: null
});

const emit = defineEmits<{
  (e: 'select', id: string | number): void;
  (e: 'rename', payload: RenamePayload): void;
  (e: 'remove', payload: RemovePayload): void;
}>();

// 状态管理
const activeMenu = ref<string | number | null>(null);
const menuTimeout = ref<NodeJS.Timeout | null>(null);
const menuRefs = ref<Record<string, Element | ComponentPublicInstance | null>>({});
const showRenameModal = ref(false);
const renameConversation = ref<Conversation | null>(null);
const renameInput = ref('');
const renameInputRef = ref<HTMLInputElement | null>(null);
const showDeleteConfirm = ref(false);
const deleteConversation = ref<Conversation | null>(null);

/**
 * 菜单引用管理
 */
function setMenuRef(el: Element | ComponentPublicInstance | null, id: string | number) {
  if (el) {
    menuRefs.value[String(id)] = el;
  } else {
    delete menuRefs.value[String(id)];
  }
}

/**
 * 点击外部检测和处理
 */
function isClickOutside(event: Event, id: string | number) {
  const menuEl = menuRefs.value[String(id)];
  if (!menuEl) return false;

  if (menuEl instanceof Element) {
    return !menuEl.contains(event.target as Node);
  } else if (menuEl && '$el' in menuEl) {
    return !(menuEl.$el as HTMLElement).contains(event.target as Node);
  }
  return true;
}

function handleClickOutside(event: Event) {
  if (activeMenu.value && isClickOutside(event, activeMenu.value)) {
    closeMenu();
  }
}

/**
 * 悬停交互处理
 */
function startHover(id: string | number) {
  if (menuTimeout.value) {
    clearTimeout(menuTimeout.value);
    menuTimeout.value = null;
  }
  
  // 鼠标悬停3秒后显示菜单
  menuTimeout.value = setTimeout(() => {
    activeMenu.value = id;
  }, 3000);
}

function clearHover(id: string | number) {
  if (menuTimeout.value) {
    clearTimeout(menuTimeout.value);
    menuTimeout.value = null;
  }
  
  // 鼠标移开后1秒关闭菜单
  menuTimeout.value = setTimeout(() => {
    if (activeMenu.value === id) {
      activeMenu.value = null;
    }
  }, 1000);
}

/**
 * 菜单操作方法
 */
function toggleMenu(id: string | number) {
  activeMenu.value = activeMenu.value === id ? null : id;
}

function closeMenu() {
  activeMenu.value = null;
}

/**
 * 对话选择处理
 */
function selectConversation(id: string | number) {
  emit('select', id);
  closeMenu();
}

/**
 * 获取按钮样式类
 */
function getButtonClass(conversation: Conversation) {
  return {
    'conversation-button': true,
    'active': props.currentId === String(conversation.sessionid)
  };
}

/**
 * 重命名对话功能
 */
function openRenameModal(conversation: Conversation) {
  renameConversation.value = conversation;
  renameInput.value = conversation.title;
  showRenameModal.value = true;
  
  nextTick(() => {
    if (renameInputRef.value) {
      renameInputRef.value.focus();
    }
  });
}

function closeRenameModal() {
  showRenameModal.value = false;
  renameConversation.value = null;
  renameInput.value = '';
}

function confirmRename() {
  if (renameConversation.value && renameInput.value.trim()) {
    const payload: RenamePayload = {
      sessionid: renameConversation.value.sessionid,
      title: renameInput.value.trim()
    };
    
    emit('rename', payload);
    closeRenameModal();
  }
}

/**
 * 删除对话功能
 */
function openDeleteConfirm(conversation: Conversation) {
  deleteConversation.value = conversation;
  showDeleteConfirm.value = true;
}

function closeDeleteConfirm() {
  showDeleteConfirm.value = false;
  deleteConversation.value = null;
}

function confirmDelete() {
  if (deleteConversation.value) {
    const payload: RemovePayload = {
      sessionid: deleteConversation.value.sessionid,
      userid: deleteConversation.value.userid || ''
    };
    
    emit('remove', payload);
    closeDeleteConfirm();
  }
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside);
  if (menuTimeout.value) {
    clearTimeout(menuTimeout.value);
  }
});
</script>

<style scoped>
/* 对话列表容器 */
.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  height: 100%;
}

.conversation-item {
  padding: 0.25rem 0;
  transition: all 0.3s ease;
}

.conversation-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* 对话按钮样式 */
.conversation-button {
  flex: 1;
  text-align: left;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  color: #4b5563;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.875rem;
  transition: background-color 0.2s;
}

.conversation-button:hover { background-color: #f3f4f6; }
.conversation-button.active { background-color: #e5e7eb; color: #1f2937; font-weight: 500; }

.dark .conversation-button { color: #d1d5db; }
.dark .conversation-button:hover { background-color: #374151; }
.dark .conversation-button.active { background-color: #374151; color: #f9fafb; }

.conversation-title {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 菜单相关样式 */
.menu-container { position: relative; display: flex; align-items: center; }

.menu-toggle {
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  transition: background-color 0.2s;
}

.menu-toggle:hover { background-color: #e5e7eb; color: #374151; }
.dark .menu-toggle:hover { background-color: #4b5563; color: #f9fafb; }

.menu-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  min-width: 120px;
  z-index: 10;
  padding: 0.25rem 0;
}

.dark .menu-dropdown { background: #1f2937; border-color: #374151; }

.menu-item {
  width: 100%;
  text-align: left;
  padding: 0.5rem 1rem;
  border: none;
  background: transparent;
  color: #4b5563;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background-color 0.2s;
}

.menu-item:hover { background-color: #f3f4f6; }
.dark .menu-item { color: #d1d5db; }
.dark .menu-item:hover { background-color: #374151; }

.menu-item.danger { color: #ef4444; }
.menu-item.danger:hover { background-color: #fee2e2; color: #b91c1c; }
.dark .menu-item.danger:hover { background-color: #3f3f46; color: #f87171; }

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.dark .modal-content { background: #1f2937; }

.modal-content h3 {
  margin: 0 0 1rem 0;
  color: #1f2937;
  font-size: 1.25rem;
  font-weight: 600;
}

.dark .modal-content h3 { color: #f9fafb; }

.modal-content p { margin: 0 0 1.5rem 0; color: #6b7280; }
.dark .modal-content p { color: #d1d5db; }

.rename-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  box-sizing: border-box;
}

.dark .rename-input { background: #374151; border-color: #4b5563; color: #f9fafb; }

.modal-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.btn-cancel,
.btn-confirm,
.btn-danger {
  padding: 0.625rem 1.25rem;
  border-radius: 0.5rem;
  border: none;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-cancel:hover,
.btn-confirm:hover,
.btn-danger:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn-cancel:active,
.btn-confirm:active,
.btn-danger:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-cancel { 
  background-color: #e5e7eb; 
  color: #374151; 
}
.btn-cancel:hover { 
  background-color: #d1d5db; 
}

.btn-confirm { 
  background-color: #3b82f6; 
  color: white; 
}
.btn-confirm:hover { 
  background-color: #2563eb; 
}

.btn-danger { 
  background-color: #ef4444; 
  color: white; 
}
.btn-danger:hover { 
  background-color: #dc2626; 
}

/* 暗黑模式按钮样式 */
.dark .btn-cancel { 
  background-color: #374151; 
  color: #f9fafb; 
}
.dark .btn-cancel:hover { 
  background-color: #4b5563; 
}

.dark .btn-confirm { 
  background-color: #2563eb; 
  color: white; 
}
.dark .btn-confirm:hover { 
  background-color: #1d4ed8; 
}

.dark .btn-danger { 
  background-color: #dc2626; 
  color: white; 
}
.dark .btn-danger:hover { 
  background-color: #b91c1c; 
}

/* 动画效果 */
.highlight-animation { animation: highlightPulse 1.5s ease-in-out; }

@keyframes highlightPulse {
  0% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
  100% { transform: translateY(0); }
}
</style>