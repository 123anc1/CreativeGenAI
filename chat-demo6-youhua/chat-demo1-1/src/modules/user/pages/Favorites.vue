<template>
  <div class="flex h-screen bg-gray-50">
    <!-- 左侧导航栏 -->
    <UserSidebar mode="compact" :current-route="currentRoute" />

    <!-- 主内容区域 -->
    <div class="flex-1 overflow-hidden flex flex-col min-w-0">
      <div class="container mx-auto px-4 py-6 flex-1 overflow-y-auto">
        <!-- 页面标题 -->
        <div class="mb-6">
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
            <svg class="w-6 h-6 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
            </svg>
            我的收藏
          </h1>
          <p class="text-gray-600 dark:text-gray-400 mt-2">查看和管理您收藏的图片</p>
        </div>

        <!-- 图片列表 -->
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div 
            v-for="post in favoritePosts" 
            :key="post.id"
            class="image-card group"
            @click="showImageDetail(post)"
          >
            <div class="relative overflow-hidden rounded-lg shadow-md group-hover:shadow-lg transition-shadow">
              <img 
                :src="post.image_url || post.imageUrl" 
                :alt="post.image_title || post.title" 
                class="w-full h-64 object-cover transition-transform duration-300 group-hover:scale-105"
                loading="lazy"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-4">
                <h3 class="text-white font-medium text-sm truncate">{{ post.image_title || post.title }}</h3>
                <div class="flex items-center gap-3 mt-2 text-white/90 text-xs">
                  <span class="flex items-center gap-1">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                    </svg>
                    {{ post.likeCount || 0 }}
                  </span>
                  <span class="flex items-center gap-1">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                    </svg>
                    {{ post.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-green-500"></div>
        </div>

        <!-- 空状态 -->
        <div v-if="!isLoading && favoritePosts.length === 0" class="text-center py-16">
          <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
          </svg>
          <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">暂无收藏</h3>
          <p class="text-gray-600 dark:text-gray-400">浏览灵感页面，收藏您喜欢的图片</p>
          <button @click="goToImageSharing" class="mt-4 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors">
            去灵感页面
          </button>
        </div>

        <!-- 加载更多 -->
        <div v-if="!isLoading && favoritePosts.length > 0 && hasMore" class="text-center py-8">
          <button @click="loadMore" class="px-6 py-2 border border-green-500 text-green-500 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 transition-colors">
            加载更多
          </button>
        </div>
      </div>
    </div>

    <!-- 图片详情模态框 -->
    <ImageDetailModal 
      :visible="showDetailModal" 
      :image-data="selectedImage"
      :is-liked="isLiked"
      :is-collected="isCollected"
      :get-image-src="getSingleImageSrc"
      @close="closeDetailModal"
      @like="handleLike"
      @favorite="handleFavorite"
      @draw-same-style="handleDrawSameStyle"
      @use-as-reference="handleUseAsReference"
      @download="handleDownload"
      @share="handleShare"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import UserSidebar from '@/shared/components/UserSidebar.vue'
import ImageDetailModal from '@/shared/components/ImageDetailModal.vue'
import { fetchFavoritePosts, toggleFavorite, checkLikeAndCollectionStatus } from '@/modules/user/services/imagepost'
import imagepostService from '@/modules/user/services/imagepost'
import type { ImagePost } from '@/shared/types'

const router = useRouter()

// 当前路由状态
const currentRoute = ref('favorites')

// 响应式数据
const favoritePosts = ref<ImagePost[]>([])
const selectedImage = ref<ImagePost | null>(null)
const showDetailModal = ref(false)
const isLoading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const isLiked = ref(false)
const isCollected = ref(true) // 收藏页面默认是已收藏状态

// 页面加载时获取收藏列表
onMounted(async () => {
  currentRoute.value = 'favorites'
  await fetchFavoritePostsList()
})

// 获取收藏列表
const fetchFavoritePostsList = async (page: number = 1, isLoadMore: boolean = false) => {
  if (isLoading.value) return
  
  isLoading.value = true
  
  try {
    const userInfo = localStorage.getItem('userInfo')
    let userId = 0
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        userId = user.id || 0
      } catch (e) {
        userId = 0
      }
    }
    
    if (userId === 0) {
      console.error('用户未登录')
      router.push('/login')
      return
    }
    
    const response = await fetchFavoritePosts(userId)
    
    if (response.success) {
      const newPosts = response.data || []
      
      if (isLoadMore) {
        favoritePosts.value = [...favoritePosts.value, ...newPosts]
      } else {
        favoritePosts.value = newPosts
      }
      
      // 检查是否还有更多数据
      hasMore.value = newPosts.length === 10 // 假设每页10条
      currentPage.value = page
    } else {
      console.error('获取收藏列表失败:', response.message)
    }
  } catch (error) {
    console.error('获取收藏列表出错:', error)
  } finally {
    isLoading.value = false
  }
}

// 加载更多
const loadMore = async () => {
  if (hasMore.value && !isLoading.value) {
    await fetchFavoritePostsList(currentPage.value + 1, true)
  }
}

// 显示图片详情
const showImageDetail = async (post: ImagePost) => {
  selectedImage.value = post
  showDetailModal.value = true
  
  // 检查点赞和收藏状态
  if (post.id) {
    await checkLikeAndCollectionStatus(post.id)
      .then(({ isLiked: liked, isCollected: collected }) => {
        isLiked.value = liked
        isCollected.value = collected
      })
  }
}

// 关闭详情模态框
const closeDetailModal = () => {
  showDetailModal.value = false
  selectedImage.value = null
}

// 处理点赞
const handleLike = async () => {
  if (!selectedImage.value) return
  
  const userInfo = localStorage.getItem('userInfo')
  let userId = 0
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo)
      userId = user.id || 0
    } catch (e) {
      userId = 0
    }
  }
  
  if (userId === 0) {
    router.push('/login')
    return
  }
  
  try {
    if (isLiked.value) {
      // 取消点赞
      await imagepostService.removeLike(selectedImage.value.id, userId)
      isLiked.value = false
    } else {
      // 点赞
      await imagepostService.addLike(selectedImage.value.id, userId)
      isLiked.value = true
    }
    
    // 更新 selectedImage 中的点赞数
    if (selectedImage.value) {
      const currentCount = selectedImage.value.likeCount || 0
      selectedImage.value = {
        ...selectedImage.value,
        likeCount: isLiked.value ? currentCount + 1 : Math.max(0, currentCount - 1)
      }
    }
    
    // 更新列表中的点赞数
    const index = favoritePosts.value.findIndex((post: ImagePost) => post.id === selectedImage.value?.id)
    if (index !== -1) {
      favoritePosts.value[index] = {
        ...favoritePosts.value[index],
        likeCount: selectedImage.value?.likeCount || 0
      }
    }
  } catch (error) {
    console.error('处理点赞失败:', error)
  }
}

// 处理收藏
const handleFavorite = async () => {
  if (!selectedImage.value) return
  
  const userInfo = localStorage.getItem('userInfo')
  let userId = 0
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo)
      userId = user.id || 0
    } catch (e) {
      userId = 0
    }
  }
  
  if (userId === 0) {
    router.push('/login')
    return
  }
  
  try {
    // 调用取消收藏接口
    await toggleFavorite(selectedImage.value.id, userId, true)
    
    // 从列表中移除
    favoritePosts.value = favoritePosts.value.filter((post: ImagePost) => post.id !== selectedImage.value?.id)
    closeDetailModal()
  } catch (error) {
    console.error('处理收藏失败:', error)
  }
}

// 处理画同款功能
const handleDrawSameStyle = async (imageData: any) => {
  // 防止重复点击
  if (isNavigating.value) {
    return
  }

  isNavigating.value = true

  try {
    // 关闭当前弹窗
    closeDetailModal()

    // 验证必要数据
    if (!imageData) {
      throw new Error('图片数据为空')
    }

    // 将图片参数存储到 sessionStorage 中，供 ImageGeneration 组件读取
    const generationParams = {
      prompt: imageData.image_description || imageData.image_title || imageData.title || '',
      title: imageData.image_title || imageData.title || '画同款作品',
      imageUrl: getSingleImageSrc(imageData) || imageData.image_url || imageData.imageUrl || '',
      timestamp: Date.now(),
      // 添加新参数
      negativePrompt: imageData.negativePrompt,
      steps: imageData.steps,
      cfgScale: imageData.cfgScale,
      modelName: imageData.modelName,
      style: imageData.style,
      width: imageData.width,
      height: imageData.height
    }

    // 验证图片URL是否有效
    if (!generationParams.imageUrl) {
      throw new Error('无法获取图片数据')
    }

    sessionStorage.setItem('sameStyleParams', JSON.stringify(generationParams))

    // 检查认证状态
    const token = localStorage.getItem('token')

    if (!token) {
      // 显示友好的提示信息
      showToast('请先登录后再使用此功能', 'error')
      await router.push('/login')
      return
    }

    // 执行路由跳转
    await router.push('/image-generation')

    // 显示成功提示
    showToast('已跳转到图像生成页面，参数已自动填充', 'success')

  } catch (error) {
    console.error('画同款功能执行失败:', error)

    // 显示用户友好的错误提示
    const errorMessage = error instanceof Error ? error.message : '操作失败，请稍后重试'
    showToast(errorMessage, 'error')

  } finally {
    // 确保状态重置
    isNavigating.value = false
  }
}

// 处理用做参考图功能
const handleUseAsReference = async (imageData: any) => {
  // 防止重复点击
  if (isNavigating.value) {
    return
  }

  isNavigating.value = true

  try {
    // 关闭当前弹窗
    closeDetailModal()

    // 验证必要数据
    if (!imageData) {
      throw new Error('图片数据为空')
    }

    // 将图片参数存储到 sessionStorage 中，供 ImageGeneration 组件读取
    const referenceParams = {
      referenceImageUrl: getSingleImageSrc(imageData) || imageData.image_url || imageData.imageUrl || '',
      timestamp: Date.now()
    }

    // 验证图片URL是否有效
    if (!referenceParams.referenceImageUrl) {
      throw new Error('无法获取图片数据')
    }

    sessionStorage.setItem('referenceImageParams', JSON.stringify(referenceParams))

    // 检查认证状态
    const token = localStorage.getItem('token')

    if (!token) {
      // 显示友好的提示信息
      showToast('请先登录后再使用此功能', 'error')
      await router.push('/login')
      return
    }

    // 执行路由跳转
    await router.push('/image-generation')

    // 显示成功提示
    showToast('已跳转到图像生成页面，参考图已设置', 'success')

  } catch (error) {
    console.error('用做参考图功能执行失败:', error)

    // 显示用户友好的错误提示
    const errorMessage = error instanceof Error ? error.message : '操作失败，请稍后重试'
    showToast(errorMessage, 'error')

  } finally {
    // 确保状态重置
    isNavigating.value = false
  }
}

// 添加通用的Toast通知函数
const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  // 创建toast元素
  const toast = document.createElement('div')
  toast.className = `fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 ${type === 'success' ? 'bg-green-500 text-white' :
    type === 'error' ? 'bg-red-500 text-white' :
      'bg-blue-500 text-white'
    }`
  toast.textContent = message

  document.body.appendChild(toast)

  // 动画显示
  setTimeout(() => {
    toast.style.opacity = '1'
    toast.style.transform = 'translate(-50%, 0)'
  }, 10)

  // 2-3秒后自动消失
  setTimeout(() => {
    toast.style.opacity = '0'
    toast.style.transform = 'translate(-50%, -20px)'
    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast)
      }
    }, 300)
  }, type === 'error' ? 4000 : 2500)
}

// 添加路由跳转状态管理
const isNavigating = ref(false)

// 图像处理函数 - 自动选择最佳显示方式
const getSingleImageSrc = (post: any) => {
  let imageSrc = ''
  
  // 优先处理 base64 字符串数据
  if (post.imageData && typeof post.imageData === 'string' && (post.imageData as string).startsWith('iVBOR')) {
    try {
      // 如果是 base64 数据，直接创建 data URL
      imageSrc = `data:image/png;base64,${post.imageData}`
    } catch (error) {
      console.error('处理 base64 图像数据失败:', error)
    }
  } else if (post.imageData && Array.isArray(post.imageData)) {
    // 处理字节数组数据
    try {
      const uint8Array = new Uint8Array(post.imageData)
      const blob = new Blob([uint8Array], { type: 'image/png' })
      imageSrc = URL.createObjectURL(blob)
    } catch (error) {
      console.error('解码图像数据失败:', error)
    }
  } else if (post.image_url) {
    // 如果有 image_url，使用它作为后备
    imageSrc = post.image_url
  } else if (post.imageUrl) {
    // 如果有 imageUrl（驼峰命名），也使用它作为后备
    imageSrc = post.imageUrl
  }
  
  return imageSrc
}

// 处理下载
const handleDownload = () => {
  if (!selectedImage.value) return
  
  const imageUrl = selectedImage.value.image_url || selectedImage.value.imageUrl
  if (imageUrl) {
    const link = document.createElement('a')
    link.href = imageUrl
    link.download = selectedImage.value.image_title || selectedImage.value.title || 'image'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

// 处理分享
const handleShare = () => {
  if (!selectedImage.value) return
  
  const imageUrl = selectedImage.value.image_url || selectedImage.value.imageUrl
  if (imageUrl) {
    if (navigator.share) {
      navigator.share({
        title: selectedImage.value.image_title || selectedImage.value.title || '分享图片',
        text: selectedImage.value.image_description || selectedImage.value.image_title || '',
        url: imageUrl
      })
    } else {
      // 复制链接到剪贴板
      navigator.clipboard.writeText(imageUrl)
        .then(() => {
          alert('链接已复制到剪贴板')
        })
        .catch(err => {
          console.error('复制失败:', err)
        })
    }
  }
}

// 跳转到灵感页面
const goToImageSharing = () => {
  router.push('/')
}
</script>

<style scoped>
.image-card {
  cursor: pointer;
}
</style>