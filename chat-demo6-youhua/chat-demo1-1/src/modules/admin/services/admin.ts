/**
 * 管理员相关 API 模块
 *
 * @description 处理用户管理、系统配置等管理员权限的操作
 * @module services/admin
 * 
 * @example
 * ```typescript
 * import { getUsers, updateUser, deleteUser } from '@/services/admin'
 * 
 * const users = await getUsers()
 * await updateUser(1, { name: 'New Name' })
 * ```
 */

import request from '@/shared/utils/request'
import type { User } from '@/shared/types'

/**
 * 获取用户列表
 * 
 * @description 获取系统中所有用户的列表信息，需要管理员权限
 * @async
 * @returns {Promise<any>} 用户列表响应
 * 
 * @example
 * ```typescript
 * const response = await getUsers()
 * if (response.data) {
 *   console.log('用户列表:', response.data)
 * }
 * ```
 * 
 * @throws {Error} 请求失败时抛出错误
 */
export async function getUsers(): Promise<any> {
  return request.get('/user/list')
}

/**
 * 获取指定用户详情
 * 
 * @param userId - 用户 ID
 * @returns 用户信息响应
 * 
 * @example
 * ```typescript
 * const user = await getUserById(123)
 * ```
 */
export async function getUserById(userId: number): Promise<any> {
  return request.get(`/user/${userId}`)
}

/**
 * 更新用户信息
 * 
 * @param userId - 用户 ID
 * @param userData - 要更新的用户数据
 * @returns 更新后的用户信息
 * 
 * @example
 * ```typescript
 * await updateUser(123, { name: '新名字', age: 25 })
 * ```
 */
export async function updateUser(userId: number, userData: Partial<User>): Promise<any> {
  return request.put(`/user/${userId}`, userData)
}

/**
 * 删除用户
 * 
 * @param userId - 用户 ID
 * @returns 删除结果
 * 
 * @example
 * ```typescript
 * await deleteUser(123)
 * ```
 */
export async function deleteUser(userId: number): Promise<any> {
  return request.delete(`/user/${userId}`)
}

/**
 * 创建新用户
 * 
 * @param userData - 新用户数据（必须包含密码）
 * @returns 创建的用户信息
 * 
 * @example
 * ```typescript
 * const newUser = await createUser({
 *   username: 'john_doe',
 *   name: 'John Doe',
 *   email: 'john@example.com',
 *   password: 'securePassword123',
 *   role: 'user'
 * })
 * ```
 */
export async function createUser(userData: Omit<User, 'id' | 'active'> & { password: string }): Promise<any> {
  return request.post('/user/create', userData)
}

/**
 * 更新用户角色
 * 
 * @param userId - 用户 ID
 * @param newRole - 新角色（如 'admin', 'user', 'moderator'）
 * @returns 更新结果
 * 
 * @example
 * ```typescript
 * await updateUserRole(123, 'admin')
 * ```
 */
export async function updateUserRole(userId: number, newRole: string): Promise<any> {
  return request.put(`/user/role/${userId}`, {}, {
    params: { newRole }
  })
}

/**
 * 更新用户状态（启用/禁用）
 * 
 * @param userId - 用户 ID
 * @param active - 是否启用
 * @returns 更新结果
 * 
 * @example
 * ```typescript
 * await updateUserStatus(123, true)
 * ```
 */
export async function updateUserStatus(userId: number, active: boolean): Promise<any> {
  return request.put(`/user/status/${userId}`, {}, {
    params: { active }
  })
}

/**
 * 获取系统统计信息
 * 
 * @description 获取关键业务指标：用户数、文章数、评论数等
 * @async
 * @returns 系统统计数据
 * 
 * @example
 * ```typescript
 * const stats = await getSystemStats()
 * console.log(`总用户数: ${stats.data.totalUsers}`)
 * ```
 */
export async function getSystemStats(): Promise<any> {
  return request.get('/api/admin/stats')
}

/**
 * 获取系统日志
 * 
 * @description 分页获取系统操作日志
 * @param page - 页码，默认 1
 * @param limit - 每页记录数，默认 10
 * @returns 日志列表分页结果
 * 
 * @example
 * ```typescript
 * const logs = await getSystemLogs(1, 20)
 * ```
 */
export async function getSystemLogs(page: number = 1, limit: number = 10): Promise<any> {
  return request.get('/api/admin/logs', {
    params: { page, limit }
  })
}

/**
 * 更新系统配置
 * 
 * @description 更新各种系统参数配置
 * @param configData - 配置数据对象
 * @returns 更新结果
 * 
 * @example
 * ```typescript
 * await updateSystemConfig({
 *   siteName: '新站点名称',
 *   maintenanceMode: false
 * })
 * ```
 */
export async function updateSystemConfig(configData: any): Promise<any> {
  return request.put('/api/admin/config', configData)
}

/**
 * 获取系统概览统计数据
 * 
 * @description 获取系统概览统计数据，包括用户数、活跃用户数等
 * @async
 * @returns 系统概览统计数据
 * 
 * @example
 * ```typescript
 * const overview = await getStatisticsOverview()
 * console.log('系统概览:', overview.data)
 * ```
 */
export async function getStatisticsOverview(): Promise<any> {
  return request.get('/admin/statistics/overview')
}

/**
 * 获取用户统计数据
 * 
 * @description 获取用户相关的统计数据
 * @async
 * @returns 用户统计数据
 * 
 * @example
 * ```typescript
 * const userStats = await getUserStatistics()
 * console.log('用户统计:', userStats.data)
 * ```
 */
export async function getUserStatistics(): Promise<any> {
  return request.get('/admin/statistics/user')
}

/**
 * 获取社区统计数据
 * 
 * @description 获取社区相关的统计数据
 * @async
 * @returns 社区统计数据
 * 
 * @example
 * ```typescript
 * const communityStats = await getCommunityStatistics()
 * console.log('社区统计:', communityStats.data)
 * ```
 */
export async function getCommunityStatistics(): Promise<any> {
  return request.get('/admin/statistics/community')
}

/**
 * 获取文档统计数据
 * 
 * @description 获取文档相关的统计数据
 * @async
 * @returns 文档统计数据
 * 
 * @example
 * ```typescript
 * const documentStats = await getDocumentStatistics()
 * console.log('文档统计:', documentStats.data)
 * ```
 */
export async function getDocumentStatistics(): Promise<any> {
  return request.get('/admin/statistics/document')
}

/**
 * 获取图片生成统计数据
 * 
 * @description 获取图片生成相关的统计数据
 * @async
 * @returns 图片生成统计数据
 * 
 * @example
 * ```typescript
 * const imageStats = await getImageGenerationStatistics()
 * console.log('图片生成统计:', imageStats.data)
 * ```
 */
export async function getImageGenerationStatistics(): Promise<any> {
  return request.get('/admin/statistics/image-generation')
}

/**
 * 获取多模态交互统计数据
 * 
 * @description 获取多模态交互相关的统计数据
 * @async
 * @returns 多模态交互统计数据
 * 
 * @example
 * ```typescript
 * const multimodalStats = await getMultiModalStatistics()
 * console.log('多模态交互统计:', multimodalStats.data)
 * ```
 */
export async function getMultiModalStatistics(): Promise<any> {
  return request.get('/admin/statistics/multimodal')
}

/**
 * 获取最近7天的统计数据
 * 
 * @description 获取最近7天的统计数据，用于图表展示
 * @async
 * @returns 最近7天的统计数据
 * 
 * @example
 * ```typescript
 * const recentStats = await getRecent7DaysStatistics()
 * console.log('最近7天统计:', recentStats.data)
 * ```
 */
export async function getRecent7DaysStatistics(): Promise<any> {
  return request.get('/admin/statistics/recent-7days')
}