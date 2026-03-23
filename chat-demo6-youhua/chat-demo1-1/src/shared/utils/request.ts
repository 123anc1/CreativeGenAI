/**
 * HTTP 请求工具模块
 *
 * @description 基于 axios 的 HTTP 请求封装
 * 包含请求拦截、响应处理、错误捕获等功能
 * 统一的认证令牌管理
 * 
 * @module utils/request
 * 
 * @example
 * ```typescript
 * import request from '@/utils/request'
 * 
 * // GET 请求
 * const data = await request.get('/api/users')
 * 
 * // POST 请求
 * const result = await request.post('/api/users', { name: 'John' })
 * 
 * // 带参数的请求
 * const filtered = await request.get('/api/items', { 
 *   params: { page: 1, size: 10 } 
 * })
 * ```
 */

import axios from 'axios'

// 直接使用 any 类型代替具体的 Axios 类型
type AxiosRequestConfigType = any
type AxiosResponseType<> = any

/**
 * 创建 axios 实例
 * 
 * @description
 * - baseURL: API 根路径，由环境变量控制
 * - timeout: 请求超时时间（10秒）
 * - withCredentials: 跨域请求时携带凭证
 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '', // API根路径，如果环境变量没设置则使用根路径
  timeout: 10000, // 请求超时时间
  withCredentials: false // 跨域时不带凭证
})

/**
 * 请求拦截器
 * 
 * @description
 * - 从 localStorage 中获取认证 token
 * - 将 token 添加到请求头中
 * - 兼容不同的后端实现（Authorization Bearer 或 token 字段）
 */
request.interceptors.request.use(
  (config: AxiosRequestConfigType) => {
    // 从 localStorage 中获取 token
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      // 根据后端API要求设置认证头，兼容不同的后端实现
      // 如果后端使用token字段而非标准的Authorization Bearer格式，则使用token字段
      config.headers['token'] = token
    }
    return config
  },
  (error: any) => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 
 * @description
 * - 处理成功响应（状态码 200、201）
 * - 处理错误响应（包括认证过期）
 * - 401 错误时清除认证信息并重定向至登录
 * - 将响应数据包装为统一的 ApiResponse 格式
 */
request.interceptors.response.use(
  (response: AxiosResponseType) => {
    // 根据后端约定的结构处理响应
    if (response.status === 200 || response.status === 201) {
      // 如果响应数据已经是 ApiResponse 格式，直接返回
      if (response.data && typeof response.data === 'object' && 'code' in response.data) {
        return response.data;
      }
      // 否则包装成 ApiResponse 格式
      return {
        code: 200,
        msg: 'success',
        data: response.data,
        timestamp: Date.now()
      };
    }
    return Promise.reject(response);
  },
  (error: any) => {
    // 处理特定的错误响应
    if (error.response?.status === 401) {
      console.warn('[Auth Error] Token expired or invalid')
      // 清除认证信息并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      // 使用 router 进行导航，避免 window.location 直接跳转
      // 这样可以保持 Vue 应用的路由状态
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      console.warn('[Permission Error] Access denied')
    } else if (error.response?.status >= 500) {
      console.error('[Server Error]', error.response.status, error.response.data)
    } else if (error.code === 'ECONNABORTED') {
      console.error('[Timeout Error] Request timeout')
    } else if (!error.response) {
      console.error('[Network Error] No response received')
    }

    // 将错误也包装成 ApiResponse 格式
    return Promise.reject({
      code: error.response?.status || 500,
      msg: error.response?.data?.message || error.message || 'Request failed',
      data: null,
      timestamp: Date.now()
    });
  }
)

// 导出带有泛型支持的 request 实例
export default request as typeof request & {
  get<T = any>(url: string, config?: any): Promise<T>;
  post<T = any>(url: string, data?: any, config?: any): Promise<T>;
  put<T = any>(url: string, data?: any, config?: any): Promise<T>;
  delete<T = any>(url: string, config?: any): Promise<T>;
};