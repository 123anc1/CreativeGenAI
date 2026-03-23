<template>
  <div class="bg-gradient-to-br from-white to-gray-50 rounded-2xl p-6 border border-gray-100 shadow-sm">
    <h3 class="text-xl font-semibold flex items-center text-gray-800 mb-5">
      <div class="w-8 h-8 rounded-lg bg-cyan-100 flex items-center justify-center mr-3">
        <svg class="w-5 h-5 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
      </div>
      评论区（{{ totalCount }}条）
    </h3>

    <!-- 评论输入框 -->
    <div class="mb-6 pb-6 border-b border-gray-200">
      <div class="flex items-start gap-3">
        <div class="flex-shrink-0 w-8 h-8 rounded-full bg-gradient-to-r from-cyan-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
          {{ userInitial }}
        </div>
        <div class="flex-1">
          <textarea
            v-model="newComment"
            @keydown.ctrl.enter="submitComment"
            placeholder="发表评论... (Ctrl+Enter 发送)"
            class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-transparent resize-none text-sm"
            rows="3"
          ></textarea>
          <div class="mt-2 flex items-center justify-end gap-2">
            <span class="text-xs text-gray-500">{{ newComment.length }}/500</span>
            <button
              @click="submitComment"
              :disabled="!newComment.trim() || submitting"
              class="px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-600 hover:to-blue-700 disabled:from-gray-400 disabled:to-gray-500 text-white rounded-lg transition-all duration-300 text-sm font-medium hover:scale-105 disabled:scale-100"
            >
              {{ submitting ? '发送中...' : '发送评论' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="space-y-4">
      <!-- 加载状态 -->
      <div v-if="commentsLoading" class="flex items-center justify-center py-8">
        <div class="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-cyan-500 mr-3"></div>
        <span class="text-gray-500 text-sm">加载评论中...</span>
      </div>

      <!-- 空状态 -->
      <div v-else-if="comments.length === 0" class="text-center py-8">
        <svg class="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
        <p class="text-gray-500 text-sm">暂无评论，来发表第一条评论吧</p>
      </div>

      <!-- 评论项目 -->
      <div v-for="comment in comments" :key="comment.id" class="group bg-white p-4 rounded-xl border border-gray-100 hover:border-cyan-200 transition-all duration-300">
        <div class="flex gap-3">
          <!-- 用户头像 -->
          <div class="flex-shrink-0">
            <div class="w-8 h-8 rounded-full bg-gradient-to-r from-purple-400 to-pink-500 flex items-center justify-center text-white text-xs font-bold">
              {{ getUserInitial(comment.userName) }}
            </div>
          </div>

          <!-- 评论内容 -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-2">
                <span class="font-semibold text-gray-800 text-sm">{{ comment.userName || '匿名用户' }}</span>
                <span class="text-xs text-gray-400">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <!-- 删除按钮（仅自己的评论） -->
              <button
                v-if="isOwnComment(comment)"
                @click="deleteCommentConfirm(comment.id!)"
                class="text-gray-400 hover:text-red-600 transition-colors text-xs px-2 py-1 rounded hover:bg-red-50"
                title="删除评论"
              >
                删除
              </button>
            </div>
            <p class="text-gray-700 text-sm leading-relaxed break-words">{{ comment.content }}</p>

            <!-- 评论操作 -->
            <div class="mt-3 flex items-center gap-3 text-xs">
              <button
                @click="toggleLike(comment)"
                class="flex items-center gap-1 text-gray-500 hover:text-cyan-600 transition-colors"
                :disabled="isLikingComment === comment.id"
              >
                <div v-if="isLikingComment === comment.id" class="animate-spin rounded-full h-4 w-4 border-2 border-cyan-500 border-t-transparent"></div>
                <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                </svg>
                <span>{{ comment.likeCount || 0 }}</span>
              </button>
              <button @click="toggleReplyInput(comment.id ?? null)" class="text-gray-500 hover:text-cyan-600 transition-colors">
                回复
              </button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="mt-4 flex items-start gap-2">
              <div class="flex-1">
                <textarea
                  v-model="replyContent"
                  @keydown.ctrl.enter="submitReply(comment.id!)"
                  placeholder="输入回复内容... (Ctrl+Enter 发送)"
                  class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-transparent resize-none text-xs"
                  rows="2"
                ></textarea>
                <div class="mt-2 flex items-center justify-end gap-2">
                  <button
                    @click="toggleReplyInput(null)"
                    class="px-3 py-1 text-gray-600 hover:text-gray-800 text-xs"
                  >
                    取消
                  </button>
                  <button
                    @click="submitReply(comment.id!)"
                    :disabled="!replyContent.trim() || submitting"
                    class="px-3 py-1 bg-cyan-500 hover:bg-cyan-600 disabled:bg-gray-400 text-white rounded text-xs transition-colors"
                  >
                    回复
                  </button>
                </div>
              </div>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="mt-4 space-y-2 pl-4 border-l-2 border-cyan-200">
              <div
                v-for="reply in comment.replies"
                :key="reply.id"
                class="text-xs bg-gray-50 p-3 rounded-lg"
              >
                <div class="flex items-center justify-between mb-1">
                  <span class="font-semibold text-gray-700">{{ reply.userName || '匿名用户' }}</span>
                  <span class="text-gray-400">{{ formatTime(reply.createdAt) }}</span>
                </div>
                <p class="text-gray-600">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多按钮 -->
      <div v-if="hasMore && !commentsLoading" class="text-center pt-4">
        <button
          @click="loadMoreComments"
          class="px-4 py-2 text-cyan-600 hover:text-cyan-700 border border-cyan-300 hover:border-cyan-400 rounded-lg transition-colors text-sm font-medium"
        >
          加载更多评论
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import imageCommentService, { ImageComment } from '@/modules/user/services/imagecomment';

// 组件props定义
interface Props {
  postId?: number;
  visible?: boolean;
}

const props = defineProps<Props>();

// 组件事件
interface Emits {
  (e: 'comment-count-changed', count: number): void;
}

const emit = defineEmits<Emits>();

// 响应式状态
const comments = ref<ImageComment[]>([]);
const allRecords = ref<ImageComment[]>([]);
const newComment = ref('');
const replyContent = ref('');
const replyingTo = ref<number | null>(null);
const commentsLoading = ref(false);
const submitting = ref(false);
const currentPage = ref(1);
const pageSize = 10;
const totalCount = ref(0);
const hasMore = ref(false);
const isLikingComment = ref<number | null>(null);
const likeCommentDebounceTimer = ref<ReturnType<typeof setTimeout> | null>(null);

/**
 * 计算用户初始字符
 */
const userInitial = computed(() => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo);
      return (user.name || '用户')[0].toUpperCase();
    } catch {
      return 'U';
    }
  }
  return 'U';
});

/**
 * 获取用户名首字母
 */
const getUserInitial = (userName?: string) => {
  return (userName || '用户')[0].toUpperCase();
};

/**
 * 格式化时间显示
 */
const formatTime = (dateString?: string) => {
  if (!dateString) return '刚刚';
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;

  return date.toLocaleDateString();
};

/**
 * 检查评论是否属于当前用户
 */
const isOwnComment = (comment: ImageComment) => {
  const userInfo = localStorage.getItem('userInfo');
  if (!userInfo) return false;
  try {
    const user = JSON.parse(userInfo);
    return comment.userId === user.id;
  } catch {
    return false;
  }
};

/**
 * 加载评论列表
 */
const loadComments = async () => {
  if (!props.postId) return;

  commentsLoading.value = true;
  try {
    const result = await imageCommentService.getComments(
      props.postId,
      currentPage.value,
      pageSize
    );

    const records = result.records || [];

    const hasNestedReplies = records.some(r => Array.isArray((r as any).replies));
    if (hasNestedReplies) {
      if (currentPage.value === 1) {
        comments.value = records as ImageComment[];
      } else {
        comments.value = (comments.value || []).concat(records as ImageComment[]);
      }

      const newCount = result.total || comments.value.length || 0;
      if (newCount !== totalCount.value) {
        totalCount.value = newCount;
        emit('comment-count-changed', newCount);
      }
      hasMore.value = (records.length || 0) >= pageSize && comments.value.length < (result.total || Infinity);
    } else {
      if (currentPage.value === 1) {
        allRecords.value = records.slice();
      } else {
        allRecords.value = allRecords.value.concat(records);
      }

      const nested = (() => {
        const map = new Map<number, ImageComment & { replies?: ImageComment[] }>();
        allRecords.value.forEach(r => {
          map.set(r.id as number, { ...(r as any), replies: [] });
        });

        const roots: (ImageComment & { replies?: ImageComment[] })[] = [];
        map.forEach(c => {
          if (c.parentId === null || c.parentId === undefined) {
            roots.push(c);
          } else {
            const parent = map.get(c.parentId as number);
            if (parent) {
              parent.replies = parent.replies || [];
              parent.replies.push(c);
            } else {
              roots.push(c);
            }
          }
        });

        roots.sort((a, b) => (b.createdAt ? Date.parse(b.createdAt) : 0) - (a.createdAt ? Date.parse(a.createdAt) : 0));
        return roots;
      })();

      comments.value = nested as ImageComment[];

      const newCount = result.total || allRecords.value.length || 0;
      if (newCount !== totalCount.value) {
        totalCount.value = newCount;
        emit('comment-count-changed', newCount);
      }
      hasMore.value = (records.length || 0) >= pageSize && allRecords.value.length < (result.total || Infinity);
    }
  } catch (error) {
    console.error('加载评论失败:', error);
  } finally {
    commentsLoading.value = false;
  }
};

/**
 * 发布新评论
 */
const submitComment = async () => {
  if (!newComment.value.trim() || !props.postId) return;

  submitting.value = true;
  try {
    await imageCommentService.addComment({
      postId: props.postId,
      content: newComment.value.trim()
    });

    newComment.value = '';
    currentPage.value = 1;
    await loadComments();
  } catch (error) {
    console.error('发布评论失败:', error);
    alert('发布评论失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

/**
 * 控制回复输入框显示
 */
const toggleReplyInput = (commentId: number | null) => {
  if (replyingTo.value === commentId) {
    replyingTo.value = null;
    replyContent.value = '';
  } else {
    replyingTo.value = commentId;
    replyContent.value = '';
  }
};

/**
 * 提交回复
 */
const submitReply = async (parentId: number) => {
  if (!replyContent.value.trim() || !props.postId) return;

  submitting.value = true;
  try {
    await imageCommentService.replyComment({
      postId: props.postId,
      content: replyContent.value.trim(),
      parentId: parentId
    });

    replyContent.value = '';
    replyingTo.value = null;
    currentPage.value = 1;
    await loadComments();
  } catch (error) {
    console.error('回复评论失败:', error);
    alert('回复失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

/**
 * 删除评论相关方法
 */
const deleteCommentConfirm = (commentId: number) => {
  if (!confirm('确定要删除这条评论吗？')) return;
  deleteComment(commentId);
};

const deleteComment = async (commentId: number) => {
  try {
    await imageCommentService.deleteComment(commentId);
    currentPage.value = 1;
    await loadComments();
  } catch (error) {
    console.error('删除评论失败:', error);
    alert('删除评论失败，请稍后重试');
  }
};

/**
 * 点赞功能
 */
const toggleLike = async (comment: ImageComment) => {
  if (!comment.id) return;

  // 防止重复点击
  if (isLikingComment.value === comment.id) return;

  // 清除之前的防抖定时器
  if (likeCommentDebounceTimer.value) {
    clearTimeout(likeCommentDebounceTimer.value);
    likeCommentDebounceTimer.value = null;
  }

  // 添加防抖，防止快速重复点击
  likeCommentDebounceTimer.value = setTimeout(async () => {
    const commentId = comment.id;
    if (!commentId) return;
    
    isLikingComment.value = commentId;
    try {
      const liked = comment.likeCount !== undefined && comment.likeCount > 0;
      if (liked) {
        await imageCommentService.unlikeComment(commentId);
        if (comment.likeCount) comment.likeCount--;
      } else {
        await imageCommentService.likeComment(commentId);
        if (comment.likeCount !== undefined) {
          comment.likeCount++;
        } else {
          comment.likeCount = 1;
        }
      }
    } catch (error) {
      console.error('点赞失败:', error);
    } finally {
      isLikingComment.value = null;
    }
  }, 200); // 200ms防抖
};

/**
 * 分页加载更多评论
 */
const loadMoreComments = () => {
  currentPage.value++;
  loadComments();
};

// 生命周期钩子
onMounted(() => {
  if (props.postId) {
    loadComments();
  }
});

// 暴露方法给父组件
const exposedReloadComments = () => {
  currentPage.value = 1;
  loadComments();
};

defineExpose({
  loadComments: exposedReloadComments
});
</script>

<style scoped>
textarea {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 禁用调整尺寸 */
textarea.resize-none {
  resize: none;
}
</style>
