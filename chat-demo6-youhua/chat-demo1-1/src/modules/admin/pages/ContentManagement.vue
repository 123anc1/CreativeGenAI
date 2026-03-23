<template>
  <div class="content-management">
    <!-- 页面标题和操作栏 -->
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">内容管理</h1>
          <p class="mt-1 text-sm text-gray-600">管理用户上传的所有图片内容</p>
        </div>
        <div class="flex space-x-3">
          <button
            @click="refreshData"
            :disabled="loading"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <svg v-if="!loading" class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            <svg v-else class="mr-2 h-4 w-4 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            刷新
          </button>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="bg-white rounded-lg shadow mb-6 p-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">搜索标题</label>
          <input
            v-model="searchTitle"
            @keyup.enter="handleSearch"
            type="text"
            placeholder="输入图片标题搜索..."
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户ID</label>
          <input
            v-model="searchUserId"
            @keyup.enter="handleSearch"
            type="number"
            placeholder="输入用户ID..."
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          />
        </div>
        <div class="flex items-end">
          <button
            @click="handleSearch"
            class="w-full md:w-auto px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            搜索
          </button>
        </div>
        <div class="flex items-end">
          <button
            @click="resetSearch"
            class="w-full md:w-auto px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          >
            重置
          </button>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-blue-100 rounded-md p-3">
              <svg class="h-6 w-6 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">总图片数</dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-gray-900">{{ totalImages }}</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-green-100 rounded-md p-3">
              <svg class="h-6 w-6 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">已发布</dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-gray-900">{{ publishedImages }}</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-yellow-100 rounded-md p-3">
              <svg class="h-6 w-6 text-yellow-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">待审核</dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-gray-900">{{ pendingImages }}</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-purple-100 rounded-md p-3">
              <svg class="h-6 w-6 text-purple-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">活跃用户</dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-gray-900">{{ uniqueUsers }}</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片列表表格 -->
    <div class="bg-white shadow rounded-lg">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900">图片列表</h2>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="py-12">
        <div class="flex justify-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
        <p class="text-center mt-4 text-gray-500">加载中...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="py-12">
        <div class="text-center">
          <svg class="mx-auto h-12 w-12 text-red-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">加载失败</h3>
          <p class="mt-1 text-sm text-gray-500">{{ error }}</p>
          <div class="mt-6">
            <button
              @click="loadImageData"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              重新加载
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="imagePosts.length === 0" class="py-12">
        <div class="text-center">
          <svg class="mx-auto h-12 w-12 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">暂无图片内容</h3>
          <p class="mt-1 text-sm text-gray-500">还没有用户上传任何图片内容</p>
        </div>
      </div>

      <!-- 图片列表 -->
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">图片</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标题</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">用户</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">浏览量</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">点赞数</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">上传时间</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="post in imagePosts" :key="post.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex-shrink-0 h-12 w-12">
                  <img 
                    v-if="post.image_url" 
                    :src="post.image_url" 
                    :alt="post.image_title || '图片'"
                    class="h-12 w-12 rounded-md object-cover cursor-pointer hover:opacity-75 transition-opacity"
                    @click="showImageDetail(post)"
                  />
                  <div v-else class="h-12 w-12 rounded-md bg-gray-200 flex items-center justify-center">
                    <svg class="h-6 w-6 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm font-medium text-gray-900 max-w-xs truncate">
                  {{ post.image_title || '未命名' }}
                </div>
                <div v-if="post.image_description" class="text-sm text-gray-500 max-w-xs truncate">
                  {{ post.image_description }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ post.user_name || '未知用户' }}</div>
                <div class="text-sm text-gray-500">ID: {{ post.userid }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="[
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                  post.status === 1 ? 'bg-green-100 text-green-800' : 
                  post.status === 0 ? 'bg-yellow-100 text-yellow-800' : 
                  'bg-red-100 text-red-800'
                ]">
                  {{ getStatusText(post.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ post.viewcount || 0 }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ post.likeCount || 0 }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(post.created_at) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button
                    @click="showImageDetail(post)"
                    class="text-blue-600 hover:text-blue-900"
                  >
                    查看
                  </button>
                  <button
                    @click="editImage(post)"
                    class="text-indigo-600 hover:text-indigo-900"
                  >
                    编辑
                  </button>
                  <button
                    @click="confirmDelete(post)"
                    class="text-red-600 hover:text-red-900"
                  >
                    删除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="bg-white px-6 py-4 border-t border-gray-200 flex items-center justify-between">
        <div class="flex-1 flex justify-between sm:hidden">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            上一页
          </button>
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            下一页
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              显示第 <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span> 到 <span class="font-medium">{{ Math.min(currentPage * pageSize, totalImages) }}</span> 条，
              共 <span class="font-medium">{{ totalImages }}</span> 条记录
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
              <button
                @click="prevPage"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
              >
                <span class="sr-only">上一页</span>
                <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
                </svg>
              </button>
              
              <template v-for="page in visiblePages" :key="page">
                <button
                  v-if="page !== '...'"
                  @click="goToPage(page as number)"
                  :class="[
                    'relative inline-flex items-center px-4 py-2 border text-sm font-medium',
                    page === currentPage
                      ? 'z-10 bg-blue-50 border-blue-500 text-blue-600'
                      : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                  ]"
                >
                  {{ page }}
                </button>
                <span v-else class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                  ...
                </span>
              </template>
              
              <button
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
              >
                <span class="sr-only">下一页</span>
                <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片详情模态框 -->
    <div v-if="showDetailModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-lg font-medium text-gray-900">图片详情</h3>
            <button @click="closeDetailModal" class="text-gray-400 hover:text-gray-600">
              <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div v-if="selectedImage" class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <img 
                :src="selectedImage.image_url" 
                :alt="selectedImage.image_title"
                class="w-full h-auto rounded-lg shadow-md"
              />
            </div>
            <div class="space-y-4">
              <div>
                <h4 class="text-lg font-medium text-gray-900">{{ selectedImage.image_title || '未命名' }}</h4>
                <p class="mt-1 text-sm text-gray-500">{{ selectedImage.image_description }}</p>
              </div>
              
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <p class="text-sm text-gray-500">用户</p>
                  <p class="font-medium">{{ selectedImage.user_name }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">用户ID</p>
                  <p class="font-medium">{{ selectedImage.userid }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">状态</p>
                  <span :class="[
                    'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                    selectedImage.status === 1 ? 'bg-green-100 text-green-800' : 
                    selectedImage.status === 0 ? 'bg-yellow-100 text-yellow-800' : 
                    'bg-red-100 text-red-800'
                  ]">
                    {{ getStatusText(selectedImage.status) }}
                  </span>
                </div>
                <div>
                  <p class="text-sm text-gray-500">上传时间</p>
                  <p class="font-medium">{{ formatDate(selectedImage.created_at) }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">浏览量</p>
                  <p class="font-medium">{{ selectedImage.viewcount || 0 }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">点赞数</p>
                  <p class="font-medium">{{ selectedImage.likeCount || 0 }}</p>
                </div>
              </div>
              
              <div v-if="selectedImage.modelName || selectedImage.style">
                <h5 class="text-sm font-medium text-gray-900 mb-2">生成参数</h5>
                <div class="grid grid-cols-2 gap-2 text-sm">
                  <div v-if="selectedImage.modelName">
                    <span class="text-gray-500">模型:</span>
                    <span class="ml-1">{{ selectedImage.modelName }}</span>
                  </div>
                  <div v-if="selectedImage.style">
                    <span class="text-gray-500">风格:</span>
                    <span class="ml-1">{{ selectedImage.style }}</span>
                  </div>
                  <div v-if="selectedImage.steps">
                    <span class="text-gray-500">步数:</span>
                    <span class="ml-1">{{ selectedImage.steps }}</span>
                  </div>
                  <div v-if="selectedImage.cfgScale">
                    <span class="text-gray-500">CFG:</span>
                    <span class="ml-1">{{ selectedImage.cfgScale }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑图片模态框 -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-lg font-medium text-gray-900">编辑图片</h3>
            <button @click="closeEditModal" class="text-gray-400 hover:text-gray-600">
              <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <form @submit.prevent="saveImage" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input
                v-model="editForm.title"
                type="text"
                required
                readonly
                class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <div
                class="w-full rounded-md border border-gray-300 p-3 shadow-sm bg-gray-50 min-h-[120px] whitespace-pre-wrap"
              >
                {{ editForm.description || '无描述' }}
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
              <select
                v-model="editForm.status"
                class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              >
                <option :value="1">已发布</option>
                <option :value="0">待审核</option>
                <option :value="2">已拒绝</option>
              </select>
            </div>
            
            <div class="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                @click="closeEditModal"
                class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                取消
              </button>
              <button
                type="submit"
                :disabled="saving"
                class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
              >
                <span v-if="saving">保存中...</span>
                <span v-else>保存</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-md w-full">
        <div class="p-6">
          <div class="flex items-center mb-4">
            <div class="flex-shrink-0">
              <svg class="h-6 w-6 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-lg font-medium text-gray-900">确认删除</h3>
            </div>
          </div>
          <div class="mt-2">
            <p class="text-sm text-gray-500">
              确定要删除这张图片吗？此操作不可撤销。
            </p>
          </div>
          <div class="mt-4 flex justify-end space-x-3">
            <button
              @click="cancelDelete"
              class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              取消
            </button>
            <button
              @click="deleteImage"
              :disabled="deleting"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50"
            >
              <span v-if="deleting">删除中...</span>
              <span v-else>确认删除</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue';
import { Imagepost, Imagepostparam } from '@/shared/types';
import imagepostService from '@/modules/user/services/imagepost';

// 响应式数据
const loading = ref(false);
const error = ref<string | null>(null);
const imagePosts = ref<Imagepost[]>([]);
const totalImages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索参数
const searchTitle = ref('');
const searchUserId = ref<number | null>(null);

// 模态框状态
const showDetailModal = ref(false);
const showEditModal = ref(false);
const showDeleteConfirm = ref(false);
const selectedImage = ref<Imagepost | null>(null);

// 表单数据
const editForm = reactive({
  title: '',
  description: '',
  status: 1
});



// 操作状态
const saving = ref(false);
const deleting = ref(false);

// 计算属性
const totalPages = computed(() => Math.ceil(totalImages.value / pageSize.value));

const publishedImages = computed(() => 
  imagePosts.value.filter(post => post.status === 1).length
);

const pendingImages = computed(() => 
  imagePosts.value.filter(post => post.status === 0).length
);

const uniqueUsers = computed(() => 
  [...new Set(imagePosts.value.map(post => post.userid))].length
);

// 分页相关计算属性
const visiblePages = computed(() => {
  const pages: (number | '...')[] = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  if (total <= 7) {
    // 如果总页数小于等于7，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    // 显示前3页
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i);
      }
      pages.push('...');
      pages.push(total);
    }
    // 显示后3页
    else if (current >= total - 3) {
      pages.push(1);
      pages.push('...');
      for (let i = total - 4; i <= total; i++) {
        pages.push(i);
      }
    }
    // 显示中间页
    else {
      pages.push(1);
      pages.push('...');
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i);
      }
      pages.push('...');
      pages.push(total);
    }
  }
  
  return pages;
});

// 方法
const loadImageData = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    const params: Imagepostparam = {
      page: currentPage.value,
      size: pageSize.value,
      title: searchTitle.value || undefined,
      userid: searchUserId.value || undefined
    };
    
    const result = await imagepostService.getImagepostListAdmin(params);
    imagePosts.value = result.records;
    totalImages.value = result.total;
    
    console.log('加载图片数据成功:', {
      total: result.total,
      currentPage: currentPage.value,
      records: result.records.length
    });
  } catch (err: any) {
    console.error('加载图片数据失败:', err);
    error.value = err.message || '加载数据失败';
    imagePosts.value = [];
    totalImages.value = 0;
  } finally {
    loading.value = false;
  }
};

const refreshData = () => {
  currentPage.value = 1;
  loadImageData();
};

const handleSearch = () => {
  currentPage.value = 1;
  loadImageData();
};

const resetSearch = () => {
  searchTitle.value = '';
  searchUserId.value = null;
  currentPage.value = 1;
  loadImageData();
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    loadImageData();
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    loadImageData();
  }
};

const goToPage = (page: number) => {
  if (page !== currentPage.value && page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadImageData();
  }
};

const showImageDetail = (post: Imagepost) => {
  selectedImage.value = post;
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedImage.value = null;
};

const editImage = (post: Imagepost) => {
  selectedImage.value = post;
  editForm.title = post.image_title || '';
  editForm.description = post.image_description || '';
  editForm.status = post.status || 1;
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
  selectedImage.value = null;
};

const saveImage = async () => {
  if (!selectedImage.value) return;
  
  saving.value = true;
  try {
    const updatedPost: Imagepost = {
      ...selectedImage.value,
      status: editForm.status
    };
    
    await imagepostService.updateImagepost(updatedPost);
    
    // 更新本地数据
    const index = imagePosts.value.findIndex(p => p.id === selectedImage.value?.id);
    if (index !== -1) {
      imagePosts.value[index] = { ...updatedPost };
    }
    
    closeEditModal();
    
    // 显示成功消息
    alert('图片状态更新成功');
  } catch (err: any) {
    console.error('更新图片失败:', err);
    alert('更新失败: ' + (err.message || '未知错误'));
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (post: Imagepost) => {
  selectedImage.value = post;
  showDeleteConfirm.value = true;
};

const cancelDelete = () => {
  showDeleteConfirm.value = false;
  selectedImage.value = null;
};

const deleteImage = async () => {
  if (!selectedImage.value) return;
  
  deleting.value = true;
  try {
    await imagepostService.deleteImagepost(selectedImage.value);
    
    // 从本地数据中移除
    const index = imagePosts.value.findIndex(p => p.id === selectedImage.value?.id);
    if (index !== -1) {
      imagePosts.value.splice(index, 1);
      totalImages.value--;
    }
    
    cancelDelete();
    
    // 显示成功消息
    alert('图片删除成功');
  } catch (err: any) {
    console.error('删除图片失败:', err);
    alert('删除失败: ' + (err.message || '未知错误'));
  } finally {
    deleting.value = false;
  }
};

const getStatusText = (status?: number): string => {
  switch (status) {
    case 1: return '已发布';
    case 0: return '待审核';
    case 2: return '已拒绝';
    default: return '未知';
  }
};

const formatDate = (date?: Date): string => {
  if (!date) return '未知';
  return new Date(date).toLocaleString('zh-CN');
};

// 生命周期钩子
onMounted(() => {
  loadImageData();
});
</script>

<style scoped>
.content-management {
  padding: 1rem;
}

/* 滚动条样式 */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 #f7fafc;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f7fafc;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #cbd5e0;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background-color: #a0aec0;
}
</style>