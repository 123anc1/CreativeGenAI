<template>
  <div class="flex min-h-screen bg-gradient-to-b from-emerald-50 to-white">
    <!-- 左侧导航栏 -->
    <div class="fixed top-0 left-0 h-screen z-20">
      <UserSidebar
        :conversations="conversations"
        :current-conversation-id="currentConversationId"
        :current-route="'profile'"
        @switch-conversation="switchConversation"
        @create-temporary-conversation="createTemporaryConversation"
      />
    </div>
    <div class="flex-1 ml-56 overflow-auto">


    <!-- 头部区域 -->
    <div class="header-section bg-gradient-to-r from-emerald-600 to-green-500 text-white shadow-lg">
      <div class="max-w-7xl mx-auto px-6 py-8">
        <h1 class="text-3xl font-bold tracking-tight">个人中心</h1>
        <p class="text-emerald-100 mt-2 text-lg">管理您的账户信息和生成的图片</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="profile-content max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 用户信息卡片 -->
      <div
        class="user-info-card bg-white rounded-2xl shadow-lg p-8 mb-10 border border-emerald-100 transform transition-all duration-300 hover:shadow-xl">
        <div class="flex flex-col sm:flex-row items-center sm:items-start gap-6">
          <!-- 头像区域 -->
          <div class="relative">
            <div class="w-20 h-20 rounded-full flex items-center justify-center text-white font-bold text-3xl shadow-lg"
              :style="{ background: avatarGradient }">
              {{ getUserInitial() }}
            </div>
            <div
              class="absolute -bottom-2 -right-2 w-8 h-8 bg-emerald-500 rounded-full flex items-center justify-center">
              <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clip-rule="evenodd" />
              </svg>
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1 text-center sm:text-left">
            <h2 class="text-2xl font-bold text-gray-800">{{ getUsername() }}</h2>
            <p class="text-gray-600 mt-1">{{ getEmail() }}</p>
            <div class="mt-4 flex flex-wrap gap-4">
              <div class="flex items-center text-sm text-gray-500">
                <svg class="w-4 h-4 mr-1 text-emerald-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd"
                    d="M4 5a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V7a2 2 0 00-2-2h-1.586a1 1 0 01-.707-.293l-1.121-1.121A2 2 0 0011.172 3H8.828a2 2 0 00-1.414.586L6.293 4.707A1 1 0 015.586 5H4zm6 9a3 3 0 100-6 3 3 0 000 6z"
                    clip-rule="evenodd" />
                </svg>
                <span>{{ userImages.length }} 张图片</span>
              </div>
              <div class="flex items-center text-sm text-gray-500">
                <svg class="w-4 h-4 mr-1 text-emerald-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd"
                    d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z"
                    clip-rule="evenodd" />
                </svg>
                <span>最近活跃</span>
              </div>
            </div>
          </div>

          <!-- 我的发布按钮 -->
          <button @click="goToMyPosts"
            class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl shadow-lg text-white bg-gradient-to-r from-indigo-600 to-purple-500 hover:from-indigo-700 hover:to-purple-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transform transition-all duration-200 hover:scale-105">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            我的发布
          </button>
        </div>
      </div>

      <!-- 图片列表区域 -->
      <div class="images-section">
        <!-- 标题区域 -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-10">
          <div>
            <h2 class="text-2xl font-bold text-gray-800 flex items-center">
              <div class="w-10 h-10 rounded-full bg-emerald-100 flex items-center justify-center mr-3">
                <svg class="w-6 h-6 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              我的图片
            </h2>
            <p class="text-gray-600 mt-2 sm:ml-13">浏览和管理您生成的所有图片</p>
          </div>

          <!-- 筛选和排序选项 -->
          <div class="mt-4 sm:mt-0 flex items-center space-x-4">
            <select v-model="sortBy" @change="sortImages"
              class="block w-40 rounded-lg border-gray-300 shadow-sm focus:border-emerald-500 focus:ring-emerald-500 text-sm">
              <option value="newest">最新生成</option>
              <option value="oldest">最早生成</option>
            </select>
            <div class="text-sm text-gray-500 bg-emerald-50 px-3 py-1 rounded-full">
              共 {{ userImages.length }} 张
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="flex flex-col items-center justify-center py-20">
          <div class="relative">
            <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-emerald-500"></div>
            <div class="absolute inset-0 flex items-center justify-center">
              <div class="h-8 w-8 rounded-full bg-emerald-100"></div>
            </div>
          </div>
          <p class="mt-4 text-gray-600 font-medium">正在加载您的图片...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="userImages.length === 0"
          class="text-center py-20 bg-gradient-to-br from-emerald-50 to-white rounded-2xl shadow-inner border border-emerald-100">
          <div
            class="mx-auto w-32 h-32 bg-gradient-to-br from-emerald-100 to-green-50 rounded-full flex items-center justify-center mb-6 shadow-lg">
            <svg class="w-16 h-16 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <h3 class="text-2xl font-bold text-gray-800 mb-2">还没有图片</h3>
          <p class="text-gray-600 max-w-md mx-auto mb-8">开始您的创作之旅，生成第一张AI图片吧！</p>
          <button @click="goToImageGeneration"
            class="inline-flex items-center px-8 py-3 border border-transparent text-base font-medium rounded-xl shadow-lg text-white bg-gradient-to-r from-emerald-600 to-green-500 hover:from-emerald-700 hover:to-green-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transform transition-all duration-200 hover:scale-105">
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            开始生成图片
          </button>
        </div>

        <!-- 瀑布流图片网格 -->
        <div v-else class="masonry-container">
          <div v-for="(image, index) in userImages" :key="image.id ?? index"
            class="masonry-item image-card-wrapper group">
            <!-- 图片卡片 -->
            <div
              class="image-card bg-white rounded-2xl shadow-md overflow-hidden border border-emerald-100/80 transition-all duration-300 hover:shadow-xl hover:shadow-emerald-100/50 hover:border-emerald-200 flex flex-col">
              <!-- 图片容器：悬停时显示放大预览 -->
              <div class="relative overflow-hidden flex-grow image-area"
                @mouseenter="(e: MouseEvent) => showZoomPreview(image.url, e)"
                @mousemove="(e: MouseEvent) => moveZoomPreview(e)" @mouseleave="hideZoomPreview">
                <img :src="image.url" :alt="image.prompt || 'Generated image'"
                  class="w-full h-auto object-contain masonry-img zoom-img" loading="lazy" @load="onImageLoad(index)"
                  @error="onImageError(index)" />

                <!-- 悬停遮罩层：操作按钮 -->
                <div
                  class="absolute inset-0 overlay-mask opacity-0 group-hover:opacity-100 transition-all duration-300 flex flex-col justify-end p-4 pointer-events-none">
                  <!-- 提示词预览 -->
                  <div class="mb-3">
                    <p class="text-white text-sm font-medium line-clamp-3 drop-shadow-sm" :title="image.prompt || '无提示词'">{{ image.prompt || '无提示词' }}
                    </p>
                  </div>
                  <!-- 操作按钮（恢复点击） -->
                  <div class="flex items-center justify-between pointer-events-auto">
                    <div class="flex items-center gap-2">
                      <button @click.stop="previewImage(image)"
                        class="action-btn flex items-center justify-center w-9 h-9 rounded-full bg-white/20 backdrop-blur-sm hover:bg-white/35 transition-colors duration-200"
                        title="预览">
                        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      </button>
                      <button @click.stop="downloadImage(image.url)"
                        class="action-btn flex items-center justify-center w-9 h-9 rounded-full bg-white/20 backdrop-blur-sm hover:bg-white/35 transition-colors duration-200"
                        title="下载">
                        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                        </svg>
                      </button>
                      <button @click.stop="confirmPublish(image)"
                        class="action-btn flex items-center justify-center w-9 h-9 rounded-full bg-blue-500/25 backdrop-blur-sm hover:bg-blue-500/40 transition-colors duration-200"
                        title="发布">
                        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">

                          <path data-follow-fill="currentColor"
                            d="M17.523 8.332a1 1 0 0 1-1.415 0L13 5.223v9.357a1 1 0 1 1-2 0V5.223L7.892 8.332a1 1 0 1 1-1.415-1.415l4.816-4.815a1 1 0 0 1 1.414 0l4.816 4.816a1 1 0 0 1 0 1.414ZM4.439 14.58a1 1 0 1 0-2 0v2.35a4 4 0 0 0 4 4h11.122a4 4 0 0 0 4-4v-2.35a1 1 0 0 0-2 0v2.35a2 2 0 0 1-2 2H6.439a2 2 0 0 1-2-2v-2.35Z"
                            clip-rule="evenodd" fill-rule="evenodd" fill="currentColor"></path>

                        </svg>
                      </button>
                    </div>
                    <button @click.stop="deleteImage(image.id)"
                      class="action-btn flex items-center justify-center w-9 h-9 rounded-full bg-red-500/25 backdrop-blur-sm hover:bg-red-500/40 transition-colors duration-200"
                      title="删除">
                      <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- 角标 -->
                <div class="absolute top-3 left-3">
                  <span
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                    AI生成
                  </span>
                </div>
              </div>

              <!-- 卡片底部信息 -->
              <div class="p-4">
                <p class="text-sm text-gray-600 line-clamp-2 mb-3 min-h-[2.5rem]" :title="image.prompt || '无提示词'">
                  {{ truncatePrompt(image.prompt) || '无提示词' }}
                </p>
                <div class="flex items-center justify-between pt-3 border-t border-gray-100">
                  <div class="flex items-center text-xs text-gray-500">
                    <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd"
                        d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z"
                        clip-rule="evenodd" />
                    </svg>
                    {{ formatDate(image.timestamp) }}
                  </div>
                  <div v-if="image.imagesize" class="text-xs text-gray-500">
                    {{ image.imagesize }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>


      </div>
    </div>

    <!-- Toast提示 -->
    <div v-if="toastMessage"
      class="toast fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-4 bg-emerald-600 text-white rounded-lg shadow-lg flex items-center space-x-2 z-50">
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span>{{ toastMessage }}</span>
    </div>

    <!-- 悬停放大快照：跟随鼠标的放大图 -->
    <div v-if="zoomPreview.url" class="zoom-preview-box"
      :style="{ left: zoomPreview.x + 'px', top: zoomPreview.y + 'px' }">
      <div class="zoom-preview-inner">
        <img :src="zoomPreview.url" alt="放大预览" class="zoom-preview-img" />
      </div>
    </div>

    <!-- 图片预览模态框 -->
    <div v-if="previewImageUrl" class="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4"
      @click.self="closePreview">
      <div class="relative max-w-4xl max-h-[90vh]">
        <button @click="closePreview"
          class="absolute -top-12 right-0 text-white hover:text-emerald-300 transition-colors">
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <img :src="previewImageUrl" alt="预览" class="max-w-full max-h-[85vh] rounded-lg shadow-2xl" />
        <div class="mt-4 text-white text-center">
          <p class="text-lg font-medium">{{ previewImagePrompt }}</p>
        </div>
      </div>
    </div>

    <!-- 发布确认弹窗 -->
    <div v-if="showPublishConfirm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-lg font-semibold mb-4">确认发布</h3>
        <p class="mb-4">确定要将这张图片发布到社区吗？</p>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">图片标题</label>
          <input v-model="imageTitle" type="text" placeholder="请输入图片标题" 
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            :disabled="publishing">
        </div>
        <div class="flex justify-end space-x-3">
          <button @click="cancelPublish" class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100"
            :disabled="publishing">
            取消
          </button>
          <button @click="publishImage"
            class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:opacity-50"
            :disabled="publishing">
            {{ publishing ? '发布中...' : '确定发布' }}
          </button>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { processImageSource } from '@/shared/utils/imageUtils'; // 导入图像处理工具
import imagepostService from '@/modules/user/services/imagepost';
import { useAuthStore } from '@/stores/auth';
import UserSidebar from '@/shared/components/UserSidebar.vue';
import { Conversation } from '@/shared/types/type';

const router = useRouter();
const authStore = useAuthStore();
const userImages = ref<any[]>([]);

// 导航栏相关状态
const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);
const loading = ref(true);
const sortBy = ref('newest');
const previewImageUrl = ref('');
const previewImagePrompt = ref('');
const showPublishConfirm = ref(false);
const imageToPublish = ref<any>(null);
const publishing = ref(false);
const imageTitle = ref('');

// 悬停放大快照（跟随鼠标的放大图）
const zoomPreview = ref<{ url: string; x: number; y: number }>({ url: '', x: 0, y: 0 });
const ZOOM_OFFSET = 16;

const showZoomPreview = (url: string, e: MouseEvent) => {
  zoomPreview.value = { url, x: e.clientX + ZOOM_OFFSET, y: e.clientY + ZOOM_OFFSET };
};
const moveZoomPreview = (e: MouseEvent) => {
  if (zoomPreview.value.url) {
    zoomPreview.value = { ...zoomPreview.value, x: e.clientX + ZOOM_OFFSET, y: e.clientY + ZOOM_OFFSET };
  }
};
const hideZoomPreview = () => {
  zoomPreview.value = { url: '', x: 0, y: 0 };
};

// 获取用户信息
const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    try {
      return JSON.parse(userInfo);
    } catch {
      return null;
    }
  }
  return null;
};

const getUserInitial = () => {
  const userInfo = getUserInfo();
  if (!userInfo || !userInfo.name) return '?';
  return userInfo.name.charAt(0).toUpperCase();
};

const getUsername = () => {
  const userInfo = getUserInfo();
  if (!userInfo || !userInfo.name) return '未知用户';
  return userInfo.name;
};

const getEmail = () => {
  const userInfo = getUserInfo();
  if (!userInfo || !userInfo.email) return '未提供邮箱';
  return userInfo.email;
};

const avatarGradient = ref('linear-gradient(135deg, #10B981 0%, #059669 100%)');

// 计算头像渐变颜色
const calculateAvatarGradient = () => {
  const userInfo = getUserInfo();
  const name = userInfo?.name || 'User';
  const gradients = [
    'linear-gradient(135deg, #10B981 0%, #059669 100%)',
    'linear-gradient(135deg, #34D399 0%, #10B981 100%)',
    'linear-gradient(135deg, #059669 0%, #047857 100%)',
    'linear-gradient(135deg, #34D399 0%, #059669 100%)',
    'linear-gradient(135deg, #10B981 0%, #047857 100%)',
  ];

  const index = name.charCodeAt(0) % gradients.length;
  avatarGradient.value = gradients[index];
};

/**
 * 将接口返回的日期规范为时间戳（毫秒），用于排序和显示。
 * 重要：后端 "YYYY-MM-DD HH:mm:ss" 或 "YYYY-MM-DD" 按本地时间解析，
 * 避免被 JS 当作 UTC 导致在 UTC+8 显示成"昨天"。
 */
const normalizeApiDate = (val: unknown): number => {
  if (val == null || val === '') return Date.now();
  if (typeof val === 'number') {
    return val < 1e12 ? val * 1000 : val;
  }
  if (typeof val === 'string') {
    const s = val.trim();
    if (!s) return Date.now();
    const n = Number(s);
    if (!Number.isNaN(n) && String(Number(s)) === s) {
      return n < 1e12 ? n * 1000 : n;
    }
    const asLocal = parseDateStringAsLocal(s);
    if (asLocal != null) return asLocal;
    const t = new Date(s).getTime();
    return Number.isNaN(t) ? Date.now() : t;
  }
  return Date.now();
};

/** 将 "YYYY-MM-DD HH:mm:ss" / "YYYY-MM-DD" 按本地时间解析为时间戳 */
function parseDateStringAsLocal(s: string): number | null {
  const dateOnly = /^(\d{4})-(\d{1,2})-(\d{1,2})$/;
  const dateTime = /^(\d{4})-(\d{1,2})-(\d{1,2})[\sT](\d{1,2}):(\d{1,2})(?::(\d{1,2})(?:\.(\d+))?)?/;
  let m = s.match(dateTime);
  if (m) {
    const y = parseInt(m[1], 10);
    const mon = parseInt(m[2], 10) - 1;
    const d = parseInt(m[3], 10);
    const h = parseInt(m[4], 10) || 0;
    const min = parseInt(m[5], 10) || 0;
    const sec = parseInt(m[6], 10) || 0;
    const ms = m[7] ? parseInt(m[7].slice(0, 3).padEnd(3, '0'), 10) : 0;
    const t = new Date(y, mon, d, h, min, sec, ms).getTime();
    return Number.isNaN(t) ? null : t;
  }
  m = s.match(dateOnly);
  if (m) {
    const y = parseInt(m[1], 10);
    const mon = parseInt(m[2], 10) - 1;
    const d = parseInt(m[3], 10);
    const t = new Date(y, mon, d, 0, 0, 0, 0).getTime();
    return Number.isNaN(t) ? null : t;
  }
  return null;
}

// 格式化日期（用于显示生成/修改时间）
const formatDate = (timestamp: string | number) => {
  const date = new Date(timestamp);
  if (Number.isNaN(date.getTime())) return '—';
  const now = new Date();
  const diffTime = now.getTime() - date.getTime();
  const diffAbs = Math.abs(diffTime);
  const sameDay = date.getDate() === now.getDate() &&
    date.getMonth() === now.getMonth() && date.getFullYear() === now.getFullYear();
  if (sameDay) {
    if (diffAbs < 60 * 1000) return '刚刚';
    if (diffAbs < 60 * 60 * 1000) return `${Math.floor(diffAbs / (60 * 1000))}分钟前`;
    return `${Math.floor(diffAbs / (60 * 60 * 1000))}小时前`;
  }
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);
  const isYesterday = date.getDate() === yesterday.getDate() &&
    date.getMonth() === yesterday.getMonth() && date.getFullYear() === yesterday.getFullYear();
  if (isYesterday) return '昨天';
  const diffDays = Math.round(diffAbs / (1000 * 60 * 60 * 24));
  if (diffDays <= 7) return `${diffDays}天前`;
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

// 截断过长的提示词
const truncatePrompt = (prompt?: string) => {
  if (!prompt) return '';
  if (prompt.length <= 60) return prompt;
  return prompt.substring(0, 57) + '...';
};

// 加载用户图片
const loadUserImages = async () => {
  const userIdStr = getUserInfo()?.id;
  console.log('加载图片列表，用户ID:', userIdStr);
  if (!userIdStr) {
    console.error('用户未登录');
    loading.value = false;
    return;
  }

  try {
    const response = await fetch('/image/list', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        token: localStorage.getItem('token') || ''
      },
      body: `userid=${encodeURIComponent(userIdStr)}`
    });

    if (!response.ok) {
      throw new Error(`请求失败: ${response.status} ${response.statusText}`);
    }

    // 检查响应类型
    const contentType = response.headers.get('content-type');

    let result;
    if (contentType && contentType.includes('application/json')) {
      result = await response.json();
    } else {
      const textResponse = await response.text();
      if (!textResponse || textResponse.trim() === '') {
        console.warn('响应为空');
        userImages.value = [];
        return;
      }
      try {
        result = JSON.parse(textResponse);
      } catch (parseError) {
        console.error('解析响应失败:', parseError);
        userImages.value = [];
        return;
      }
    }

    // 处理图片数据
    const imagesData = Array.isArray(result) ? result : (result.images || []);

    userImages.value = imagesData.map((img: any) => ({
      id: img.id,
      userid: img.userid,
      url: img.imagedata,
      prompt: img.prompt || img.description || '无提示词',
      timestamp: normalizeApiDate(img.updatedat ?? img.createdat),
      imagetype: img.imagetype,
      imagesize: img.imagesize,
      originalData: img // 保存原始数据用于调试
    }));
    console.log('用户图片列表:', userImages.value);
    sortImages(); // 确保按最新排序
  } catch (error) {
    console.error('加载图片列表失败:', error);
    userImages.value = [];
  } finally {
    loading.value = false;
  }
};

// 图片加载完成
const onImageLoad = (index: number) => {
  const items = document.querySelectorAll('.masonry-item');
  const item = items[index];
  if (item) item.classList.add('loaded');
};

// 图片加载失败
const onImageError = (index: number) => {
  console.error(`图片加载失败，索引: ${index}`);
  console.log('失败的图片数据:', userImages.value[index]);

  // 使用图像处理工具处理备用方案
  const imageData = userImages.value[index];
  const fallbackUrl = imageData?.image_url || '/placeholder-image.png';
  userImages.value[index].url = processImageSource(fallbackUrl);
  console.log('使用备用图片:', fallbackUrl);
};

// 下载图片
const downloadImage = (url: string) => {
  const link = document.createElement('a');
  link.href = url;
  link.download = `ai-generated-${Date.now()}.png`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

// 删除图片
const deleteImage = async (id: string | number) => {
  if (!confirm('确定要删除这张图片吗？')) return;
  console.log('准备删除图片:', id);

  console.log('准备发送删除请求，参数:', id);
  try {
    const token = localStorage.getItem('token') || '';

    const response = await fetch(`/image/delete/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        token
      }
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`删除失败: ${response.status} ${response.statusText} ${text}`);
    }

    // 成功后从列表中移除该图片
    userImages.value = userImages.value.filter((i: any) => i.id !== id);
    // 优化：改为使用toast提示而不是alert
    showToast('图片已删除成功');
  } catch (error) {
    console.error('删除图片失败:', error);
    showToast('删除图片失败，请重试');
  }
};

// 预览图片
const previewImage = (image: any) => {
  previewImageUrl.value = image.url;
  previewImagePrompt.value = image.prompt || '无提示词';
};

// 关闭预览
const closePreview = () => {
  previewImageUrl.value = '';
  previewImagePrompt.value = '';
};

// 排序图片（按 updatedat 修改时间，timestamp 已为毫秒时间戳）
const sortImages = () => {
  const toTime = (t: number | string) => typeof t === 'number' ? t : new Date(t).getTime();
  if (sortBy.value === 'newest') {
    userImages.value.sort((a, b) => toTime(b.timestamp) - toTime(a.timestamp));
  } else {
    userImages.value.sort((a, b) => toTime(a.timestamp) - toTime(b.timestamp));
  }
};

// 跳转到图片生成页面
const goToImageGeneration = () => {
  router.push('/image-generation');
};

// 跳转到我的发布页面
const goToMyPosts = () => {
  router.push('/my-posts');
};

// 添加一个简单的toast提示函数
const toastMessage = ref('');
const showToast = (message: string) => {
  toastMessage.value = message;
  setTimeout(() => {
    toastMessage.value = '';
  }, 3000); // 3秒后自动消失
};

// 发布图片确认
const confirmPublish = async (image: any) => {
  // 检查用户是否登录
  const userId = authStore.currentUser?.id;
  if (!userId) {
    showToast('请先登录');
    return;
  }

  // 检查图片是否已发布
  try {
    const checkResponse = await fetch(`/imagepost/image/${image.id}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (checkResponse.ok) {
      const checkResult = await checkResponse.json();
      if (checkResult.data) {
        showToast('图片已经发布，不能重复发布');
        return;
      }
    }

    // 图片未发布，显示确认弹窗
    imageToPublish.value = image;
    imageTitle.value = image.prompt?.substring(0, 50) || 'AI生成图片';
    showPublishConfirm.value = true;
  } catch (error) {
    console.error('检查图片发布状态失败:', error);
    showToast('检查发布状态失败，请重试');
  }
};

// 发布图片
const publishImage = async () => {
  if (!imageToPublish.value) return;

  // 验证标题
  if (!imageTitle.value.trim()) {
    showToast('请设置图片标题');
    return;
  }

  // 添加调试信息
  console.log('发布图片调试信息:', {
    currentUser: authStore.currentUser,
    token: localStorage.getItem('token'),
    userInfo: localStorage.getItem('userInfo')
  });

  const userId = authStore.currentUser?.id;
  if (!userId) {
    console.error('用户ID获取失败:', {
      currentUser: authStore.currentUser,
      tokenExists: !!localStorage.getItem('token'),
      userInfoExists: !!localStorage.getItem('userInfo')
    });
    showToast('请先登录');
    cancelPublish();
    return;
  }

  publishing.value = true;

  try {
    const imagepost = {
      userid: userId,
      image_url: imageToPublish.value.url,
      imageId: imageToPublish.value.id,
      image_title: imageTitle.value,
      image_description: imageToPublish.value.prompt || '无描述',
      created_at: new Date(),
      updated_at: new Date()
    };

    console.log('准备发布图片:', imagepost);
    const response = await imagepostService.upload(imagepost);

    if (response.ok) {
      showToast('图片发布成功！');
      // 可以在这里更新UI，比如添加发布状态标记
    } else {
      const errorText = await response.text();
      throw new Error(errorText || '发布失败');
    }
  } catch (error) {
    console.error('发布图片失败:', error);
    showToast('发布失败，请重试');
  } finally {
    publishing.value = false;
    cancelPublish();
  }
};

// 取消发布
const cancelPublish = () => {
  showPublishConfirm.value = false;
  imageToPublish.value = null;
  imageTitle.value = '';
};

// 导航栏相关方法
const switchConversation = (sessionid: string | number) => {
  currentConversationId.value = String(sessionid);
  // 可以在这里添加切换对话的逻辑
};

const createTemporaryConversation = () => {
  // 创建临时对话的逻辑
};



onMounted(() => {
  calculateAvatarGradient();
  authStore.loadUserInfo(); // 添加这行来加载用户信息
  loadUserImages();
});
</script>

<style scoped>
/* 真正的瀑布流布局 - 图片上下紧密贴合 */
.masonry-container {
  column-count: 4;
  /* 固定4列 */
  column-gap: 1rem;
  /* 列间距 */
  padding: 0 0.25rem;
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 1rem;
  /* 项目间距 */
  animation: fadeInUp 0.45s ease-out backwards;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .masonry-container {
    column-count: 3;
    /* 中等屏幕3列 */
  }
}

@media (max-width: 900px) {
  .masonry-container {
    column-count: 2;
    /* 小屏幕2列 */
  }
}

@media (max-width: 640px) {
  .masonry-container {
    column-count: 1;
    /* 移动端单列 */
    column-gap: 0.75rem;
  }

  .masonry-item {
    margin-bottom: 0.75rem;
  }

  .header-section {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .profile-content {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .user-info-card {
    padding: 1.5rem;
  }
}

/* 优化图片卡片的高度自适应 */
.image-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.image-area {
  flex: 1;
  min-height: 200px;
  position: relative;
}

/* 确保图片自适应 */
.zoom-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 悬停放大快照浮层优化 */
.zoom-preview-box {
  position: fixed;
  z-index: 100;
  width: 300px;
  height: 300px;
  pointer-events: none;
  transform: translate(0, 0);
  animation: zoomPreviewIn 0.15s ease-out;
  filter: drop-shadow(0 10px 25px rgba(0, 0, 0, 0.3));
}

.zoom-preview-inner {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid white;
  background: white;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.zoom-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.5);
  transform-origin: center center;
}

@keyframes zoomPreviewIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(10px);
  }

  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 图片卡片悬停效果优化 */
.group:hover .image-card {
  transform: translateY(-6px);
  box-shadow: 0 24px 48px -12px rgba(16, 185, 129, 0.25);
  border-color: rgba(16, 185, 129, 0.3);
}

/* 悬停遮罩优化 */
.overlay-mask {
  background: linear-gradient(to top,
      rgba(5, 46, 22, 0.95) 0%,
      rgba(6, 78, 59, 0.7) 40%,
      transparent 70%);
  backdrop-filter: blur(2px);
}

.action-btn {
  transition: all 0.2s ease;
  transform: translateY(0);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* 滚动条优化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #10b981, #059669);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #059669, #047857);
}

/* 加载动画优化 */
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }

  100% {
    background-position: 200% 0;
  }
}

.masonry-item:not(.loaded) .masonry-img {
  background: linear-gradient(90deg, #ecfdf5 25%, #d1fae5 50%, #ecfdf5 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

/* 卡片底部信息优化 */
.image-card .p-4 {
  flex-shrink: 0;
}

/* 按钮响应式优化 */
@media (max-width: 640px) {
  .user-info-card .flex-col.sm\\:flex-row {
    gap: 1rem;
  }

  .user-info-card button {
    width: 100%;
    justify-content: center;
  }
}

/* 图片加载骨架 */
.masonry-item:not(.loaded) .masonry-img {
  background: linear-gradient(90deg, #ecfdf5 25%, #d1fae5 50%, #ecfdf5 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
  min-height: 200px;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.masonry-item:nth-child(1) {
  animation-delay: 0.02s;
}

.masonry-item:nth-child(2) {
  animation-delay: 0.05s;
}

.masonry-item:nth-child(3) {
  animation-delay: 0.08s;
}

.masonry-item:nth-child(4) {
  animation-delay: 0.1s;
}

.masonry-item:nth-child(5) {
  animation-delay: 0.12s;
}

.masonry-item:nth-child(n+6) {
  animation-delay: 0.15s;
}
</style>