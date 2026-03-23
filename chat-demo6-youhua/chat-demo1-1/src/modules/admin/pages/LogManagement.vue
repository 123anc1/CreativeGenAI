<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">日志管理</h1>
      <p class="mt-2 text-gray-600">系统操作日志查看和管理</p>
    </div>

    <!-- 多维度统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="rounded-full bg-blue-100 p-3">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">总日志数</p>
            <p class="text-2xl font-semibold text-gray-900">{{ getStatValue(statistics, ['totalLogs', 'total']) }}</p>

          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="rounded-full bg-green-100 p-3">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">成功请求</p>
            <p class="text-2xl font-semibold text-gray-900">{{ getStatValue(statistics, ['successCount', 'success', 'successful']) }}</p>

          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="rounded-full bg-yellow-100 p-3">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">警告数量</p>
            <p class="text-2xl font-semibold text-gray-900">{{ getStatValue(statistics, ['warningCount', 'warning', 'warnings']) }}</p>

          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="rounded-full bg-red-100 p-3">
            <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600">错误数量</p>
            <p class="text-2xl font-semibold text-gray-900">{{ getStatValue(statistics, ['errorCount', 'error', 'errors']) }}</p>

          </div>
        </div>
      </div>
    </div>

    <!-- 高级统计面板 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <!-- 模块分布统计 -->
      <div class="bg-white rounded-lg shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">模块分布统计</h3>
        </div>
        <div class="p-6">
          <div v-if="moduleStats.length > 0" class="space-y-3">
            <div v-for="stat in moduleStats" :key="stat.module" class="flex items-center justify-between">
              <div class="flex items-center">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mr-3"
                      :class="getModuleBadgeClass(stat.module)">
                  {{ getModuleName(stat.module) }}
                </span>
                <span class="text-sm text-gray-600">{{ stat.count }} 条</span>
              </div>
              <div class="w-24 bg-gray-200 rounded-full h-2">
                <div class="bg-blue-600 h-2 rounded-full" 
                     :style="{ width: ((stat.count / Math.max(...moduleStats.map(s => s.count))) * 100) + '%' }"></div>
              </div>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-8">
            暂无模块统计数据
          </div>
        </div>
      </div>

      <!-- 用户活跃度统计 -->
      <div class="bg-white rounded-lg shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">用户活跃度排行</h3>
        </div>
        <div class="p-6">
          <div v-if="userStats.length > 0" class="space-y-3">
            <div v-for="(stat, index) in userStats.slice(0, 5)" :key="stat.username" class="flex items-center justify-between">
              <div class="flex items-center">
                <span class="w-6 h-6 rounded-full bg-blue-100 text-blue-800 flex items-center justify-center text-xs font-medium mr-3">
                  {{ index + 1 }}
                </span>
                <span class="font-medium text-gray-900">{{ stat.username || '匿名用户' }}</span>
                <span class="ml-2 text-sm text-gray-600">{{ stat.count }} 次操作</span>
              </div>
              <span class="text-sm text-gray-500">{{ ((stat.count / totalLogs) * 100).toFixed(1) }}%</span>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-8">
            暂无用户统计数据
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">模块</label>
          <select 
            v-model="filters.module"
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          >
            <option value="">全部模块</option>
            <option value="USER">用户模块</option>
            <option value="CHAT">聊天模块</option>
            <option value="IMAGE">图片模块</option>
            <option value="ADMIN">管理模块</option>
            <option value="SYSTEM">系统模块</option>
            <option value="日志管理">日志管理</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
          <select 
            v-model="filters.status"
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          >
            <option value="">全部状态</option>
            <option value="SUCCESS">成功</option>
            <option value="WARNING">警告</option>
            <option value="ERROR">错误</option>
            <option value="INFO">信息</option>
            <option value="DEBUG">调试</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <input
            v-model="filters.username"
            type="text"
            placeholder="搜索用户名"
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">操作类型</label>
          <input
            v-model="filters.operation"
            type="text"
            placeholder="搜索操作类型"
            class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          />
        </div>

        <div class="flex items-end space-x-2">
          <button
            @click="loadLogs"
            :disabled="loading"
            class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {{ loading ? '查询中...' : '查询' }}
          </button>
          <button
            @click="resetFilters"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            重置
          </button>
        </div>
      </div>

      <!-- 时间范围筛选 -->
      <div class="mt-4 pt-4 border-t border-gray-200">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
            <input
              v-model="filters.startTime"
              type="datetime-local"
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
            <input
              v-model="filters.endTime"
              type="datetime-local"
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          <div class="flex items-end space-x-2">
            <button
              @click="loadLogs"
              :disabled="loading"
              class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
            >
              {{ loading ? '查询中...' : '查询' }}
            </button>
            <button
              @click="resetFilters"
              class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              重置
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 日志表格 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h2 class="text-lg font-medium text-gray-900">日志列表</h2>
        <div class="text-sm text-gray-600">
          共 {{ total }} 条记录
        </div>
      </div>
      
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">模块</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">用户</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">IP地址</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="loading">
              <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                <div class="flex items-center justify-center">
                  <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mr-2"></div>
                  加载中...
                </div>
              </td>
            </tr>
            <tr v-else-if="logs.length === 0">
              <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                暂无日志数据
              </td>
            </tr>
            <tr v-for="log in logs" :key="log.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ formatDate(log.createTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getModuleBadgeClass(log.module)">
                  {{ getModuleName(log.module) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 max-w-xs truncate">
                <div class="flex items-center space-x-2">
                  <span>{{ getOperationType(log.operation) }}</span>
                  <span v-if="generateLogSummary(log)" class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
                    {{ generateLogSummary(log) }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ log.username || '匿名用户' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                <div>
                  <div>{{ getIpAddressValue(log) || '-' }}</div>
                  <div class="text-xs text-gray-500">{{ getIpLocation(getIpAddressValue(log)) }}</div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(extractStatusFromLog(log))">
                  {{ getStatusText(extractStatusFromLog(log)) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="viewLogDetail(log)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  查看详情
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="logs.length > 0" class="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
        <div class="text-sm text-gray-700">
          显示第 {{ (currentPage - 1) * pageSize + 1 }} 到 {{ Math.min(currentPage * pageSize, total) }} 条，
          共 {{ total }} 条记录
        </div>
        <div class="flex space-x-2">
          <button
            @click="prevPage"
            :disabled="currentPage <= 1"
            class="px-3 py-1 rounded-md border text-sm disabled:opacity-50"
            :class="currentPage <= 1 ? 'border-gray-300 text-gray-400' : 'border-blue-600 text-blue-600 hover:bg-blue-50'"
          >
            上一页
          </button>
          <span class="px-3 py-1 text-sm text-gray-700">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </span>
          <button
            @click="nextPage"
            :disabled="currentPage >= totalPages"
            class="px-3 py-1 rounded-md border text-sm disabled:opacity-50"
            :class="currentPage >= totalPages ? 'border-gray-300 text-gray-400' : 'border-blue-600 text-blue-600 hover:bg-blue-50'"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 日志详情模态框 -->
    <div v-if="showDetailModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-medium text-gray-900">日志详情</h3>
            <button @click="closeDetailModal" class="text-gray-400 hover:text-gray-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
          
          <div v-if="selectedLog" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-700">时间</label>
                <p class="mt-1 text-sm text-gray-900">{{ formatDate(selectedLog.createTime) }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">模块</label>
                <p class="mt-1">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="getModuleBadgeClass(selectedLog.module)">
                    {{ getModuleName(selectedLog.module) }}
                  </span>
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">操作类型</label>
                <p class="mt-1 text-sm text-gray-900">{{ getOperationType(selectedLog.operation) }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">用户</label>
                <p class="mt-1 text-sm text-gray-900">{{ selectedLog.username || '-' }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">IP地址</label>
                <p class="mt-1 text-sm text-gray-900">{{ getIpAddressValue(selectedLog) || '-' }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">状态</label>
                <p class="mt-1">
                  <span :class="getStatusClass(extractStatusFromLog(selectedLog))">
                    {{ getStatusText(extractStatusFromLog(selectedLog)) }}
                  </span>
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">请求方法</label>
                <p class="mt-1 text-sm text-gray-900">{{ selectedLog.method || '-' }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">状态码</label>
                <p class="mt-1 text-sm text-gray-900">{{ selectedLog.statusCode || '-' }}</p>
              </div>
            </div>
            
            <div v-if="shouldShowField(selectedLog.uri, 'uri')">
              <label class="block text-sm font-medium text-gray-700">请求URI</label>
              <p class="mt-1 text-sm text-gray-900 break-all bg-gray-50 p-2 rounded">{{ selectedLog.uri }}</p>
            </div>
            
            <div v-if="shouldShowField(selectedLog.params, 'params')">
              <label class="block text-sm font-medium text-gray-700">请求参数</label>
              <pre class="mt-1 text-sm text-gray-900 bg-gray-50 p-3 rounded overflow-x-auto">{{ formatParams(selectedLog.params) }}</pre>
            </div>
            
            <div v-if="shouldShowField(getErrorMessageValue(selectedLog), 'errorMessage')">
              <label class="block text-sm font-medium text-gray-700">错误信息</label>
              <p class="mt-1 text-sm text-red-600 bg-red-50 p-3 rounded">{{ getErrorMessageValue(selectedLog) }}</p>
            </div>
            
            <div v-if="shouldShowField(selectedLog.userAgent, 'userAgent')">
              <label class="block text-sm font-medium text-gray-700">用户代理</label>
              <p class="mt-1 text-sm text-gray-900 bg-gray-50 p-2 rounded">{{ formatUserAgent(selectedLog.userAgent) }}</p>
            </div>

            <div v-if="shouldShowField(getResponseTimeValue(selectedLog), 'responseTime')">
              <label class="block text-sm font-medium text-gray-700">响应时间</label>
              <p class="mt-1 text-sm" :class="getResponseTimeClass(getResponseTimeValue(selectedLog))">
                {{ getResponseTimeValue(selectedLog) }}ms {{ getResponseTimeLevel(getResponseTimeValue(selectedLog)) }}
              </p>
            </div>

            <div class="pt-4 border-t border-gray-200">
              <button
                @click="copyFullLog(selectedLog)"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
              >
                复制完整日志信息
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { 
  getLogs, 
  getLogStatistics, 
  getModuleStats, 
  getUserStats 
} from '../services/log'

// 响应式数据
const loading = ref(false)
const logs = ref([])
const statistics = ref({})
const moduleStats = ref([])
const userStats = ref([])
const showDetailModal = ref(false)
const selectedLog = ref(null)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选条件
const filters = reactive({
  module: '',
  status: '',
  username: '',
  operation: '',
  startTime: '',
  endTime: ''
})

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const totalLogs = computed(() => statistics.value.totalLogs || 0)

// 方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const formatJson = (jsonStr) => {
  try {
    const obj = JSON.parse(jsonStr)
    return JSON.stringify(obj, null, 2)
  } catch {
    return jsonStr
  }
}

const formatParams = (params) => {
  if (!params) return '-'
  
  // 如果是查询字符串格式
  if (typeof params === 'string' && params.includes('=')) {
    try {
      const paramObj = {}
      const pairs = params.split('&')
      pairs.forEach(pair => {
        const [key, value] = pair.split('=')
        if (key && value !== undefined) {
          paramObj[decodeURIComponent(key)] = decodeURIComponent(value)
        }
      })
      return JSON.stringify(paramObj, null, 2)
    } catch {
      return params
    }
  }
  
  // 如果是JSON字符串
  try {
    const obj = JSON.parse(params)
    return Object.keys(obj).length > 0 ? JSON.stringify(obj, null, 2) : '{}'
  } catch {
    return params || '-'
  }
}

const shouldShowField = (fieldValue, fieldName) => {
  // 对于特定字段，空值时不显示
  const hideWhenEmpty = ['errorMessage', 'uri', 'method', 'statusCode', 'params']
  if (hideWhenEmpty.includes(fieldName)) {
    return fieldValue !== null && fieldValue !== undefined && fieldValue !== ''
  }
  return true
}

const getModuleName = (module) => {
  const moduleMap = {
    'USER': '用户模块',
    'CHAT': '聊天模块',
    'IMAGE': '图片模块',
    'ADMIN': '管理模块',
    'SYSTEM': '系统模块',
    '日志管理': '日志管理'
  }
  return moduleMap[module] || module || '未知模块'
}

const getModuleBadgeClass = (module) => {
  const classMap = {
    'USER': 'bg-blue-100 text-blue-800',
    'CHAT': 'bg-green-100 text-green-800',
    'IMAGE': 'bg-purple-100 text-purple-800',
    'ADMIN': 'bg-red-100 text-red-800',
    'SYSTEM': 'bg-gray-100 text-gray-800',
    '日志管理': 'bg-indigo-100 text-indigo-800'
  }
  return classMap[module] || 'bg-gray-100 text-gray-800'
}

const extractStatusFromLog = (log) => {
  // 按优先级检查不同的状态字段
  const statusFields = ['status', 'level', 'statusCode', 'state', 'result', 'responseCode']
  
  for (const field of statusFields) {
    if (log[field] !== undefined && log[field] !== null) {
      return log[field]
    }
  }
  
  // 如果所有字段都为空，尝试从其他信息推断
  if (log.statusCode) {
    // 根据HTTP状态码推断
    const code = parseInt(log.statusCode)
    if (code >= 200 && code < 300) return 'SUCCESS'
    if (code >= 400 && code < 500) return 'WARNING' 
    if (code >= 500) return 'ERROR'
  }
  
  return null
}

const getStatusText = (status) => {
  // 处理各种可能的状态值
  if (!status) {
    return '未知'
  }
  
  // 如果是数字类型，转换为对应的文本
  if (typeof status === 'number') {
    const numberMap = {
      200: '成功',
      201: '创建成功',
      400: '请求错误',
      401: '未授权',
      403: '禁止访问',
      404: '未找到',
      500: '服务器错误'
    }
    return numberMap[status] || `HTTP ${status}`
  }
  
  // 字符串状态映射
  const statusMap = {
    // 标准状态
    'SUCCESS': '成功',
    'WARNING': '警告',
    'ERROR': '错误',
    'INFO': '信息',
    'DEBUG': '调试',
    
    // 数据库状态
    '1': '成功',
    '0': '失败',
    
    // HTTP状态码文本
    'OK': '成功',
    'FAILED': '失败',
    
    // 中文状态
    '成功': '成功',
    '失败': '失败',
    '警告': '警告',
    '错误': '错误'
  }
  
  // 精确匹配
  if (statusMap[status]) {
    return statusMap[status]
  }
  
  // 模糊匹配（包含关键字）
  const fuzzyMatches = [
    { pattern: /success|成功|ok/i, result: '成功' },
    { pattern: /warn|警告/i, result: '警告' },
    { pattern: /error|失败|err/i, result: '错误' },
    { pattern: /info|信息/i, result: '信息' },
    { pattern: /debug|调试/i, result: '调试' }
  ]
  
  for (const match of fuzzyMatches) {
    if (match.pattern.test(status)) {
      return match.result
    }
  }
  
  return status.toString()
}

const getStatusClass = (status) => {
  const classMap = {
    'SUCCESS': 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800',
    'WARNING': 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800',
    'ERROR': 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800'
  }
  return classMap[status] || 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800'
}

const loadStatistics = async () => {
  try {
    const response = await getLogStatistics()
    if (response?.data) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
    showMessage('加载统计信息失败', 'error')
  }
}

const loadModuleStats = async () => {
  try {
    console.log('开始加载模块统计...')
    const response = await getModuleStats()
    console.log('模块统计接口响应:', response)
    if (response?.data) {
      moduleStats.value = Array.isArray(response.data) ? response.data : []
      console.log('模块统计数据:', moduleStats.value)
    }
  } catch (error) {
    console.error('加载模块统计失败:', error)
  }
}

const loadUserStats = async () => {
  try {
    console.log('开始加载用户统计...')
    const response = await getUserStats()
    console.log('用户统计接口响应:', response)
    if (response?.data) {
      userStats.value = Array.isArray(response.data) ? response.data : []
      console.log('用户统计数据:', userStats.value)
    }
  } catch (error) {
    console.error('加载用户统计失败:', error)
  }
}

const loadLogs = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 处理时间参数
    if (params.startTime) {
      params.startTime = new Date(params.startTime).toISOString()
    }
    if (params.endTime) {
      params.endTime = new Date(params.endTime).toISOString()
    }
    
    const response = await getLogs(params)
    if (response?.data) {
      logs.value = response.data.logs || []
      total.value = response.data.total || 0
    } else {
      logs.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载日志失败:', error)
    showMessage('加载日志失败', 'error')
    logs.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadRecentLogs = async () => {
  loading.value = true
  try {
    const response = await getRecentLogs()
    if (response?.data) {
      logs.value = response.data.logs || []
      total.value = response.data.total || 0
      currentPage.value = 1
    }
  } catch (error) {
    console.error('加载最近日志失败:', error)
    showMessage('加载最近日志失败', 'error')
  } finally {
    loading.value = false
  }
}


const resetFilters = () => {
  filters.module = ''
  filters.status = ''
  filters.username = ''
  filters.operation = ''
  filters.startTime = ''
  filters.endTime = ''
  currentPage.value = 1
  loadLogs()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadLogs()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadLogs()
  }
}

const viewLogDetail = (log) => {
  selectedLog.value = log
  console.log('查看日志详情:', log)
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedLog.value = null
}

const copyLogInfo = (log) => {
  const info = `${formatDate(log.createTime)} | ${getModuleName(log.module)} | ${log.operation} | ${log.username || '-'} | ${log.ipAddress || '-'}`;
  navigator.clipboard.writeText(info).then(() => {
    showMessage('已复制到剪贴板', 'success')
  }).catch(() => {
    showMessage('复制失败', 'error')
  })
}

const copyFullLog = (log) => {
  const fullInfo = `
时间: ${formatDate(log.createTime)}
模块: ${getModuleName(log.module)}
操作: ${log.operation}
用户: ${log.username || '-'}
IP地址: ${getIpAddressValue(log) || '-'}
状态: ${getStatusText(extractStatusFromLog(log))}
请求方法: ${log.method || '-'}
状态码: ${log.statusCode || '-'}
请求URI: ${log.uri || '-'}
请求参数: ${log.params || '-'}
错误信息: ${log.errorMessage || '-'}
响应时间: ${getResponseTimeValue(log) || '-'}ms
  `.trim();
  
  navigator.clipboard.writeText(fullInfo).then(() => {
    showMessage('完整日志信息已复制到剪贴板', 'success')
  }).catch(() => {
    showMessage('复制失败', 'error')
  })
}

const formatUserAgent = (userAgent) => {
  if (!userAgent) return '-'
  
  // 提取浏览器和操作系统信息
  const browserMatch = userAgent.match(/(Chrome|Firefox|Safari|Edge|Opera)\/[\d.]+/)
  const osMatch = userAgent.match(/\(([^)]+)\)/)
  
  let info = []
  if (browserMatch) {
    info.push(browserMatch[1])
  }
  if (osMatch) {
    const osInfo = osMatch[1]
    if (osInfo.includes('Windows')) info.push('Windows')
    else if (osInfo.includes('Mac')) info.push('macOS')
    else if (osInfo.includes('Linux')) info.push('Linux')
  }
  
  return info.length > 0 ? info.join(' on ') : userAgent
}

const getIpAddressValue = (log) => {
  return log.ipAddress || log.ip || null
}

const getErrorMessageValue = (log) => {
  return log.errorMessage || log.exception || null
}

const getIpLocation = (ip) => {
  if (!ip || ip === '0:0:0:0:0:0:0:1' || ip === '::1') {
    return '本地访问'
  }
  
  // 简单的IP分类
  if (ip.startsWith('192.168.') || ip.startsWith('10.') || ip.startsWith('172.')) {
    return '内网访问'
  }
  
  return '外网访问'
}

const getResponseTimeLevel = (responseTime) => {
  if (!responseTime) return ''
  
  if (responseTime < 50) return '🟢 极快'
  if (responseTime < 200) return '🔵 快速'
  if (responseTime < 500) return '🟡 一般'
  if (responseTime < 1000) return '🟠 较慢'
  return '🔴 很慢'
}

const getResponseTimeValue = (log) => {
  return log.responseTime || log.executionTime || null
}

const getResponseTimeClass = (responseTime) => {
  if (!responseTime) return ''
  
  if (responseTime < 50) return 'text-green-600'
  if (responseTime < 200) return 'text-blue-600'
  if (responseTime < 500) return 'text-yellow-600'
  if (responseTime < 1000) return 'text-orange-600'
  return 'text-red-600'
}

const getOperationType = (operation) => {
  if (!operation) return '-'
  
  const operationTypes = {
    '查询': '🔍 查询操作',
    '新增': '➕ 新增操作',
    '修改': '✏️ 修改操作',
    '删除': '🗑️ 删除操作',
    '登录': '🔑 登录操作',
    '登出': '🚪 登出操作'
  }
  
  for (const [key, value] of Object.entries(operationTypes)) {
    if (operation.includes(key)) {
      return value
    }
  }
  return operation
}

const generateLogSummary = (log) => {
  if (!log) return ''
  
  let summary = []
  
  // 添加模块信息
  if (log.module) {
    summary.push(getModuleName(log.module))
  }
  
  // 添加操作类型图标
  if (log.operation) {
    const icons = {
      '查询': '🔍',
      '新增': '➕',
      '修改': '✏️',
      '删除': '🗑️',
      '登录': '🔑',
      '登出': '🚪'
    }
    
    let icon = ''
    for (const [key, value] of Object.entries(icons)) {
      if (log.operation.includes(key)) {
        icon = value
        break
      }
    }
    summary.push(`${icon} ${log.operation}`)
  }
  
  // 添加关键参数信息
  if (log.params) {
    try {
      let paramsInfo = ''
      if (typeof log.params === 'string' && log.params.includes('=')) {
        // 查询参数
        const paramObj = {}
        const pairs = log.params.split('&')
        pairs.forEach(pair => {
          const [key, value] = pair.split('=')
          if (key && value !== undefined) {
            paramObj[decodeURIComponent(key)] = decodeURIComponent(value)
          }
        })
        if (paramObj.page || paramObj.size) {
          paramsInfo = `页码:${paramObj.page || 1}, 每页:${paramObj.size || 10}`
        }
      } else {
        // JSON参数
        const obj = JSON.parse(log.params)
        if (Object.keys(obj).length > 0) {
          paramsInfo = `${Object.keys(obj).length}个参数`
        }
      }
      if (paramsInfo) {
        summary.push(`(${paramsInfo})`)
      }
    } catch (e) {
      // 忽略解析错误
    }
  }
  
  return summary.filter(item => item).join(' ')
}

const getStatValue = (statObj, fieldNames) => {
  // 支持多个可能的字段名
  for (const fieldName of fieldNames) {
    if (statObj[fieldName] !== undefined) {
      return statObj[fieldName]
    }
  }
  return 0
}

// 在模板中使用时替换统计显示
// 总日志数: {{ getStatValue(statistics, ['totalLogs', 'total']) }}
// 成功请求数: {{ getStatValue(statistics, ['successCount', 'success', 'successful']) }}
// 警告数量: {{ getStatValue(statistics, ['warningCount', 'warning', 'warnings']) }}
// 错误数量: {{ getStatValue(statistics, ['errorCount', 'error', 'errors']) }}

// 生命周期
onMounted(() => {
  Promise.all([
    loadStatistics(),
    loadModuleStats(),
    loadUserStats(),
    loadLogs()
  ]).catch(error => {
    console.error('初始化数据加载失败:', error)
  })
})
</script>

<style scoped>
.log-management {
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 消息提示动画 */
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fade-out {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(100%);
  }
}

.animate-fade-in {
  animation: fade-in 0.3s ease-out forwards;
}

.animate-fade-out {
  animation: fade-out 0.3s ease-in forwards;
}

/* 表格悬停效果增强 */
.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
  transition: background-color 0.2s ease;
}

/* 按钮悬停效果 */
button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

/* 卡片悬停效果 */
.bg-white.rounded-lg.shadow:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

/* 进度条动画 */
.w-24.bg-gray-200.rounded-full.h-2 .bg-blue-600 {
  transition: width 0.5s ease-in-out;
}

/* 模态框入场动画 */
.fixed.inset-0.bg-black.bg-opacity-50.flex.items-center.justify-center.p-4.z-50 {
  animation: modal-fade-in 0.3s ease-out;
}

@keyframes modal-fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 详情模态框内容动画 */
.max-w-4xl.w-full.max-h-\[90vh\].overflow-y-auto {
  animation: slide-up 0.3s ease-out;
}

@keyframes slide-up {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 加载动画增强 */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .grid.grid-cols-1.md\:grid-cols-2.lg\:grid-cols-4.gap-6.mb-8 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  
  .grid.grid-cols-1.lg\:grid-cols-2.gap-6.mb-8 {
    grid-template-columns: 1fr;
  }
  
  .overflow-x-auto table {
    min-width: 800px;
  }
}
</style>