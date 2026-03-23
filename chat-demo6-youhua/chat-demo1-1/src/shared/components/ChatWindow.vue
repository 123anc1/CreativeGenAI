<template>
  <div class="bg-white">
    <div ref="chatContainerRef" class="mx-auto w-full max-w-4xl space-y-8 px-6 sm:px-8 md:px-10 py-8">
      <div v-for="(m, idx) in messages" :key="idx" class="transition-all duration-500 ease-out fade-in">
        <div v-if="m.type === 'USER'" class="flex justify-end">
          <div class="max-w-[85%] rounded-xl px-5 py-4 bg-blue-500 text-white overflow-x-auto">
            <div class="space-y-4">
              <!-- 处理包含图片的混合内容 -->
              <template v-if="m.content && (m.content.includes('[图片URL]') || m.content.includes('图片 URL:'))">
                <div v-for="(item, index) in parseContentWithImages(m.content)" :key="index" class="image-url-container">
                  <div v-if="item.type === 'image'" class="image-wrapper" :class="{ 'loaded': isImageLoaded(getImageUrl(item.content)) }" :style="{ minHeight: isImageLoaded(getImageUrl(item.content)) ? 'auto' : '200px' }">
                    <img :src="getImageUrl(item.content)" alt="生成的图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
                  </div>
                  <div v-else-if="item.type === 'text'" class="whitespace-pre-wrap text-base leading-relaxed">{{ item.content }}</div>
                </div>
              </template>
              <!-- 直接处理图片URL（如果没有[图片URL]前缀） -->
              <div v-else-if="m.content && isImageUrl(m.content)" class="image-url-container">
                <div class="image-wrapper" :class="{ 'loaded': isImageLoaded(getImageUrl(m.content)) }" :style="{ minHeight: isImageLoaded(getImageUrl(m.content)) ? 'auto' : '200px' }">
                  <img :src="getImageUrl(m.content)" alt="图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
                </div>
              </div>
              <!-- 处理普通文本 -->
              <div v-else class="whitespace-pre-wrap text-base leading-relaxed">{{ m.content || '' }}</div>
            </div>
          </div>
        </div>
        <div v-else-if="m.type === 'ASSISTANT'" class="flex justify-start">
          <div
            class="max-w-[95%] ml-0 px-6 py-5 bg-white text-gray-800 overflow-x-auto">

            <div class="space-y-4">
              <!-- 处理包含图片的混合内容 -->
              <template v-if="m.content && (m.content.includes('[图片URL]') || m.content.includes('图片 URL:'))">
                <div v-for="(item, index) in parseContentWithImages(m.content)" :key="index" class="image-url-container">
                  <div v-if="item.type === 'image'" class="image-wrapper" :class="{ 'loaded': isImageLoaded(getImageUrl(item.content)) }" :style="{ minHeight: isImageLoaded(getImageUrl(item.content)) ? 'auto' : '200px' }">
                    <img :src="getImageUrl(item.content)" alt="生成的图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
                  </div>
                  <div v-else-if="item.type === 'text'" class="markdown-content" v-html="renderMarkdown(item.content)"></div>
                </div>
              </template>
              <!-- 直接处理图片URL（如果没有[图片URL]前缀） -->
              <div v-else-if="m.content && isImageUrl(m.content)" class="image-url-container">
                <div class="image-wrapper" :class="{ 'loaded': isImageLoaded(getImageUrl(m.content)) }" :style="{ minHeight: isImageLoaded(getImageUrl(m.content)) ? 'auto' : '200px' }">
                  <img :src="getImageUrl(m.content)" alt="图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
                </div>
              </div>
              <!-- 处理文档内容 -->
              <div v-else-if="m.content && m.content.startsWith('[文档内容]')" class="document-content-container">
                <div class="font-semibold text-blue-600 mb-2">文档内容摘要：</div>
                <div class="markdown-content" v-html="renderMarkdown(m.content.replace('[文档内容] ', ''))"></div>
              </div>
              <!-- 处理知识库检索结果 -->
              <div v-else-if="m.content && m.content.startsWith('[知识库检索结果]')" class="kb-result-container">
                <div class="font-semibold text-blue-600 mb-2">知识库检索结果：</div>
                <div class="markdown-content" v-html="renderMarkdown(m.content.replace('[知识库检索结果] ', ''))"></div>
              </div>
              <!-- 处理普通文本 -->
              <div v-else class="markdown-content" v-html="renderMarkdown(m.content || '')"></div>
            </div>
          </div>
        </div>
        <div v-else class="flex justify-start">
          <div
            class="max-w-[95%] ml-0 px-6 py-5 bg-white text-gray-600 overflow-x-auto">
            <div class="flex items-center gap-2 mb-2">
              <div class="w-2 h-2 bg-gray-400 rounded-full animate-pulse"></div>
              <span class="font-medium text-gray-500 text-sm">Thinking...</span>
              <div class="ml-auto flex space-x-1">
                <div class="w-2 h-2 bg-gray-300 rounded-full animate-pulse"></div>
                <div class="w-2 h-2 bg-gray-400 rounded-full animate-pulse delay-75"></div>
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-pulse delay-150"></div>
              </div>
            </div>
            <!-- 处理包含图片的混合内容 -->
            <template v-if="m.content && (m.content.includes('[图片URL]') || m.content.includes('图片 URL:'))">
              <div v-for="(item, index) in parseContentWithImages(m.content)" :key="index" class="image-url-container">
                <div v-if="item.type === 'image'" class="image-wrapper" :class="{ 'loaded': isImageLoaded(item.content) }" :style="{ minHeight: isImageLoaded(item.content) ? 'auto' : '200px' }">
                  <img :src="item.content" alt="生成的图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
                </div>
                <div v-else-if="item.type === 'text'" class="markdown-content opacity-80" v-html="renderMarkdown(item.content)"></div>
              </div>
            </template>
            <!-- 直接处理图片URL（如果没有[图片URL]前缀） -->
            <div v-else-if="m.content && isImageUrl(m.content)" class="image-url-container">
              <div class="image-wrapper" :class="{ 'loaded': isImageLoaded(m.content) }" :style="{ minHeight: isImageLoaded(m.content) ? 'auto' : '200px' }">
                <img :src="m.content" alt="图片" class="max-w-full rounded-lg shadow-sm hover:shadow-md transition-shadow" @load="onImageLoad" @error="onImageError" />
              </div>
            </div>
            <!-- 处理文档内容 -->
            <div v-else-if="m.content && m.content.startsWith('[文档内容]')" class="document-content-container">
              <div class="font-semibold text-blue-600 mb-2">文档内容摘要：</div>
              <div class="markdown-content opacity-80" v-html="renderMarkdown(m.content.replace('[文档内容] ', ''))"></div>
            </div>
            <!-- 处理知识库检索结果 -->
            <div v-else-if="m.content && m.content.startsWith('[知识库检索结果]')" class="kb-result-container">
              <div class="font-semibold text-blue-600 mb-2">知识库检索结果：</div>
              <div class="markdown-content opacity-80" v-html="renderMarkdown(m.content.replace('[知识库检索结果] ', ''))"></div>
            </div>
            <!-- 处理普通文本 -->
            <div v-else class="markdown-content opacity-80" v-html="renderMarkdown(m.content || '')"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, watch, onUnmounted, ComponentPublicInstance, shallowRef } from 'vue'
import { Message } from '@/shared/types/type'
import { marked } from 'marked'

// 图片URL缓存，避免重复请求
const imageUrlCache = new Map<string, string>()

// 标记是否是首次加载
const isInitialLoad = ref(true)
// 跟踪图片加载状态
const pendingImages = ref(0)

/**
 * 获取加密的图片URL
 */
async function getEncryptedImageUrl(originalUrl: string): Promise<string> {
  console.log('开始获取加密图片URL:', originalUrl)
  
  // 检查缓存
  if (imageUrlCache.has(originalUrl)) {
    const cachedUrl = imageUrlCache.get(originalUrl) || originalUrl
    console.log('使用缓存的加密图片URL:', cachedUrl)
    return cachedUrl
  }
  
  // 对于带有http开头的URL，直接返回，不需要调用后端API
  if (originalUrl.startsWith('http://') || originalUrl.startsWith('https://')) {
    console.log('直接返回HTTP开头的URL:', originalUrl)
    imageUrlCache.set(originalUrl, originalUrl)
    return originalUrl
  }
  
  try {
    // 调用后端接口获取加密URL
    // 注意：后端接口路径应该是 /multimodal/result/image，使用POST方法
    const apiUrl = '/multimodal/result/image'
    console.log('请求后端API:', apiUrl)
    
    // 构建请求参数
    const formData = new FormData()
    formData.append('imageUrl', originalUrl)
    
    // 从localStorage获取认证令牌
    const token = localStorage.getItem('token')
    
    // 构建请求头
    const headers: Record<string, string> = {}
    if (token) {
      // 使用与request.ts相同的认证头格式
      headers['token'] = token
    }
    
    const response = await fetch(apiUrl, {
      method: 'POST',
      body: formData,
      headers: headers
    })
    console.log('后端响应状态:', response.status, response.statusText)
    
    if (response.ok) {
      const encryptedUrl = await response.text()
      console.log('获取到加密图片URL:', encryptedUrl)
      // 缓存结果
      imageUrlCache.set(originalUrl, encryptedUrl)
      return encryptedUrl
    } else {
      console.error('获取加密图片URL失败:', response.statusText)
      return originalUrl // 失败时返回原始URL
    }
  } catch (error) {
    console.error('获取加密图片URL出错:', error)
    return originalUrl // 出错时返回原始URL
  }
}

// 定义组件props接口
interface Props {
  messages: Message[]
}

const props = defineProps<Props>()

// 响应式引用和状态管理
const chatContainerRef = ref<HTMLElement | null>(null)
const editorRefs = shallowRef<Record<string, Element | ComponentPublicInstance | null>>({})
const editorInstances = shallowRef<Record<string, EditorInstance>>({})
const editors = ref<Record<number, boolean>>({})

// 编辑器实例类型定义
interface EditorInstance {
  dispose: () => void
}

// 配置Markdown渲染选项
marked.setOptions({
  breaks: true,
  gfm: true
})

/**
 * 检查是否是图片URL
 */
function isImageUrl(url: string): boolean {
  if (!url) return false;
  
  const validExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.svg'];
  
  try {
    // 尝试解析为绝对URL
    const parsedUrl = new URL(url);
    
    // 检查URL是否以图片扩展名结尾
    const hasValidExtension = validExtensions.some(ext => 
      parsedUrl.pathname.toLowerCase().endsWith(ext)
    );
    
    // 检查URL是否包含图片相关的路径或查询参数
    const hasImagePath = parsedUrl.pathname.toLowerCase().includes('image') || 
                       parsedUrl.pathname.toLowerCase().includes('img');
    
    // 检查URL是否来自常见的图片托管服务
    const imageHosts = ['imgur.com', 'unsplash.com', 'picsum.photos', 'cdn.jsdelivr.net', 'i.imgur.com'];
    const isFromImageHost = imageHosts.some(host => 
      parsedUrl.hostname.toLowerCase().includes(host)
    );
    
    return hasValidExtension || hasImagePath || isFromImageHost;
  } catch {
    // 处理相对路径
    const lowerUrl = url.toLowerCase();
    // 检查相对路径是否以图片扩展名结尾
    const hasValidExtension = validExtensions.some(ext => 
      lowerUrl.endsWith(ext)
    );
    
    return hasValidExtension;
  }
}
/**
 * 解析内容，按照原始顺序返回包含文本和图片的混合内容
 */
function parseContentWithImages(content: string): Array<{ type: 'text' | 'image'; content: string }> {
  if (!content) return [];
  
  const result: Array<{ type: 'text' | 'image'; content: string }> = [];
  let remainingContent = content;
  
  // 定义所有图片URL模式的正则表达式
  const imagePatterns = [
    // [图片URL] `URL` 格式
    { regex: /\[图片URL\]\s*`([^`]+)`/, extract: (match: RegExpMatchArray) => match[1] },
    // [图片URL] URL 格式（无反引号）
    { regex: /\[图片URL\]\s*((?:https?:\/\/[^\s]+))/, extract: (match: RegExpMatchArray) => match[1] },
    // 图片 URL: `URL` 格式
    { regex: /图片 URL:\s*`([^`]+)`/, extract: (match: RegExpMatchArray) => match[1] },
    // 图片 URL: URL 格式（无反引号）
    { regex: /图片 URL:\s*((?:https?:\/\/[^\s]+|[^\s]+\.(?:jpg|jpeg|png|gif|webp|svg)))/, extract: (match: RegExpMatchArray) => match[1] }
  ];
  
  while (remainingContent) {
    let earliestMatch: { index: number; url: string; length: number } | null = null;
    
    // 找到最早出现的图片URL
    for (const pattern of imagePatterns) {
      const match = pattern.regex.exec(remainingContent);
      if (match && (!earliestMatch || match.index < earliestMatch.index)) {
        earliestMatch = {
          index: match.index,
          url: pattern.extract(match),
          length: match[0].length
        };
      }
    }
    
    if (earliestMatch) {
      // 处理图片前的文本
      if (earliestMatch.index > 0) {
        const text = remainingContent.substring(0, earliestMatch.index);
        if (text.trim()) {
          result.push({ type: 'text', content: text });
        }
      }
      
      // 处理图片
      result.push({ type: 'image', content: earliestMatch.url });
      
      // 剩余内容
      remainingContent = remainingContent.substring(earliestMatch.index + earliestMatch.length);
    } else {
      // 没有更多图片，处理剩余文本
      if (remainingContent.trim()) {
        result.push({ type: 'text', content: remainingContent });
      }
      break;
    }
  }
  
  return result;
}

/**
 * 渲染Markdown内容
 */
function renderMarkdown(md: string): string {
  try {
    if (!md) return ''
    return marked.parse(md) as string
  } catch (error) {
    console.error('Markdown解析错误:', error)
    return md || ''
  }
}

// 响应式存储图片URL映射
const imageUrlMap = ref<Record<string, string>>({})

// 跟踪已加载的图片
const loadedImages = ref<Set<string>>(new Set())

/**
 * 获取图片URL（处理加密）
 */
function getImageUrl(originalUrl: string): string {
  // 检查是否已经有加密URL
  if (imageUrlMap.value[originalUrl]) {
    return imageUrlMap.value[originalUrl]
  }
  
  // 异步获取加密URL
  getEncryptedImageUrl(originalUrl).then(encryptedUrl => {
    imageUrlMap.value[originalUrl] = encryptedUrl
  })
  
  // 暂时返回原始URL，等待加密URL获取完成后会自动更新
  return originalUrl
}

/**
 * 图片加载完成回调
 */
const onImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  const src = img?.src
  if (src) {
    loadedImages.value.add(src)
  }
  pendingImages.value = Math.max(0, pendingImages.value - 1)
  // 图片加载完成后滚动到底部
  // 使用双重 requestAnimationFrame 确保布局已完成
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      scrollToBottomImmediate()
    })
  })
}

/**
 * 图片加载失败回调
 */
const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  const src = img?.src
  if (src) {
    loadedImages.value.add(src) // 即使失败也标记为已处理
  }
  pendingImages.value = Math.max(0, pendingImages.value - 1)
  // 即使图片加载失败也滚动到底部
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      scrollToBottomImmediate()
    })
  })
}

/**
 * 检查图片是否已加载
 */
const isImageLoaded = (src: string): boolean => {
  return loadedImages.value.has(src)
}

/**
 * 滚动到聊天窗口底部
 * @param smooth 是否使用平滑滚动（首次加载时建议不使用）
 */
const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTo({
        top: chatContainerRef.value.scrollHeight,
        behavior: smooth ? 'smooth' : 'auto'
      })
    }
  })
}

/**
 * 强制滚动到底部（无动画，用于初始加载）
 */
const scrollToBottomImmediate = () => {
  nextTick(() => {
    requestAnimationFrame(() => {
      if (chatContainerRef.value) {
        const scrollHeight = chatContainerRef.value.scrollHeight
        const clientHeight = chatContainerRef.value.clientHeight
        
        // 只有当内容高度大于容器高度时才需要滚动
        if (scrollHeight > clientHeight) {
          chatContainerRef.value.scrollTop = scrollHeight
        }
      }
    })
  })
}

// 监听消息变化自动滚动
watch(() => props.messages, (newMessages, oldMessages) => {
  // 如果是首次加载（oldMessages为undefined）或消息数量大幅增加（加载历史记录）
  const isFirstLoad = !oldMessages || oldMessages.length === 0
  const isBulkLoad = oldMessages && newMessages.length - oldMessages.length > 5
  const hasMessages = newMessages && newMessages.length > 0
  
  if (!hasMessages) return
  
  if (isFirstLoad || isBulkLoad) {
    // 首次加载或批量加载时，使用立即滚动
    // 使用 requestAnimationFrame 确保 DOM 已更新
    requestAnimationFrame(() => {
      scrollToBottomImmediate()
    })
    isInitialLoad.value = false
  } else {
    // 正常消息更新使用平滑滚动
    scrollToBottom(true)
  }
}, { deep: true, immediate: true })

// 组件生命周期钩子
onMounted(() => {
  // 组件挂载时，如果已经有消息，立即滚动到底部
  if (props.messages && props.messages.length > 0) {
    scrollToBottomImmediate()
  }
})

// 专门监听消息从空到有的变化（处理父组件异步加载消息的情况）
watch(() => props.messages.length, (newLength, oldLength) => {
  // 当消息从空变为有内容时（父组件异步加载完成）
  if (newLength > 0 && oldLength === 0) {
    // 使用多重延迟确保渲染完成
    scrollToBottomImmediate()
    
    setTimeout(() => {
      scrollToBottomImmediate()
    }, 50)
    
    setTimeout(() => {
      scrollToBottomImmediate()
    }, 150)
    
    // 更长延迟处理图片加载
    setTimeout(() => {
      scrollToBottomImmediate()
    }, 500)
    
    setTimeout(() => {
      scrollToBottomImmediate()
    }, 1000)
  }
})

onUnmounted(() => {
  Object.values(editorInstances.value).forEach(instance => {
    if (instance && typeof instance.dispose === 'function') {
      instance.dispose()
    }
  })
  
  editorInstances.value = {}
  editorRefs.value = {}
  editors.value = {}
})
</script>

<style scoped>
/* Markdown内容基础样式 */
.markdown-content {
  line-height: 1.6;
  color: #333;
}

/* 标题层级样式 */
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  margin-top: 1.2em;
  margin-bottom: 0.8em;
  font-weight: bold;
  line-height: 1.4;
  color: #222;
}

.markdown-content :deep(h1) { font-size: 1.5em; margin: 0.67em 0; }
.markdown-content :deep(h2) { font-size: 1.3em; margin: 0.83em 0; }
.markdown-content :deep(h3) { font-size: 1.1em; margin: 1em 0; }
.markdown-content :deep(h4) { font-size: 1em; margin: 1.33em 0; }
.markdown-content :deep(h5) { font-size: 0.9em; margin: 1.67em 0; }
.markdown-content :deep(h6) { font-size: 0.8em; margin: 2.33em 0; }

@media (min-width: 640px) {
  .markdown-content :deep(h1) { font-size: 2em; }
  .markdown-content :deep(h2) { font-size: 1.5em; }
  .markdown-content :deep(h3) { font-size: 1.17em; }
  .markdown-content :deep(h4) { font-size: 1em; }
  .markdown-content :deep(h5) { font-size: 0.83em; }
  .markdown-content :deep(h6) { font-size: 0.67em; }
}

.markdown-content p { 
  margin-bottom: 1em;
  color: #333;
}

/* 列表样式 */
.markdown-content ul,
.markdown-content ol {
  margin-left: 1.5em;
  margin-bottom: 1em;
  padding-left: 1.5em;
}

.markdown-content li {
  margin-bottom: 0.5em;
  line-height: 1.6;
  color: #333;
}

.markdown-content li > ul,
.markdown-content li > ol {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

/* 代码块容器样式 */
.code-block-container {
  margin: 1.5em 0;
  overflow: hidden;
  font-family: 'Fira Code', 'Monaco', 'Menlo', 'Courier New', monospace;
  transition: all 0.3s ease;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  padding: 0.5rem 1rem;
  font-size: 0.8rem;
  color: #666;
}

.code-lang { 
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.copy-btn, .editor-btn {
  background-color: #f0f0f0;
  border: none;
  border-radius: 0.25rem;
  padding: 0.25rem 0.5rem;
  cursor: pointer;
  font-size: 0.7rem;
  font-weight: 500;
  transition: all 0.2s;
  margin-left: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.copy-btn:hover, .editor-btn:hover { 
  background-color: #e0e0e0;
  transform: translateY(-1px);
}

.editor-wrapper { height: 300px; overflow: hidden; }
.editor-container { width: 100%; height: 100%; }

.code-block-container pre {
  background-color: #ffffff;
  padding: 1rem;
  margin: 0;
  font-size: 0.85em;
  line-height: 1.5;
  overflow-x: auto;
  tab-size: 4;
}

.code-block-container code {
  color: #333;
  line-height: 1.6;
  overflow-wrap: normal;
  white-space: pre;
  display: block;
  font-family: 'Fira Code', 'Monaco', 'Menlo', 'Courier New', monospace;
}

/* 代码语法高亮 */
.code-block-container code .keyword { color: #666; font-weight: bold; }
.code-block-container code .string { color: #1a73e8; }
.code-block-container code .number { color: #006600; }
.code-block-container code .comment { color: #999; }
.code-block-container code .function { color: #333; font-weight: bold; }
.code-block-container code .operator { color: #666; }

/* 其他Markdown元素样式 */
.markdown-content blockquote {
  padding: 1rem 1.5rem;
  margin: 1.5em 0;
  color: #666;
  background-color: #ffffff;
  font-style: italic;
  transition: all 0.3s ease;
}

.markdown-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 1.5em 0;
}

.markdown-content th,
.markdown-content td {
  padding: 0.75em 1rem;
  text-align: left;
  transition: all 0.2s ease;
}

.markdown-content th { 
  background-color: #ffffff;
  font-weight: 600;
  color: #333;
}

.markdown-content tr:hover td {
  background-color: #f8f8f8;
}

.markdown-content img {
  max-width: 100%;
  border-radius: 6px;
  margin: 1.5em 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.markdown-content img:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.markdown-content a {
  color: #1a73e8;
  text-decoration: none;
  font-weight: 500;
  position: relative;
  transition: all 0.2s ease;
}

.markdown-content a:hover {
  color: #1557b0;
  text-decoration: underline;
}

.markdown-content a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background-color: #1a73e8;
  transition: width 0.2s ease;
}

.markdown-content a:hover::after {
  width: 100%;
}

/* 多模态输出样式 */
.image-url-container {
  margin: 1em 0;
}

/* 图片容器 - 预留给图片的最小高度，防止布局跳动 */
.image-wrapper {
  position: relative;
  overflow: hidden;
  background-color: #f3f4f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: min-height 0.3s ease, background-color 0.3s ease;
}

/* 图片加载后，容器高度自适应 */
.image-wrapper img {
  display: block;
  width: 100%;
  height: auto;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.image-wrapper img:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

/* 图片加载完成后的样式 */
.image-wrapper.loaded {
  min-height: auto !important;
  background-color: transparent;
}

.document-content-container,
.kb-result-container {
  margin: 1em 0;
  padding: 1em;
  background-color: #ffffff;
}

/* HTML列表样式覆盖 */
.markdown-content :deep(ul) {
  list-style-type: disc;
  margin-left: 1.5em;
  padding-left: 1.5em;
  margin-bottom: 1em;
}

.markdown-content :deep(ol) {
  list-style-type: decimal;
  margin-left: 1.5em;
  padding-left: 1.5em;
  margin-bottom: 1em;
}

.markdown-content :deep(li) {
  margin-bottom: 0.5em;
  line-height: 1.6;
  display: list-item;
}

.markdown-content :deep(li ul),
.markdown-content :deep(li ol) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  margin-left: 1.5em;
}

.markdown-content :deep(li p) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

/* 消息淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn 0.5s ease-out forwards;
}

</style>