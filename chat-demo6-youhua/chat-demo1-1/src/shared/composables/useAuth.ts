/**
 * 认证管理 Hook
 * 
 * 功能: 统一的用户认证管理，包括登录、登出、权限检查
 * 与Pinia store结合使用
 * 
 * @example
 * ```typescript
 * const { user, isAdmin, login, logout, canAccess } = useAuth()
 * 
 * const handleLogin = async (credentials) => {
 *   await login(credentials)
 *   router.push('/')
 * }
 * ```
 */

import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { isAdminRole } from '@/shared/constants/userRoles'
import { useRouter } from 'vue-router'

/**
 * 使用认证 Hook
 * @returns 用户信息、权限相关的计算属性和操作方法
 */
export const useAuth = () => {
  const authStore = useAuthStore()
  const router = useRouter()

  /**
   * 当前用户信息
   */
  const user = computed(() => authStore.userInfo)

  /**
   * 是否已认证
   */
  const isAuthenticated = computed(() => authStore.isAuthenticated)

  /**
   * 是否为管理员
   */
  const isAdmin = computed(() => authStore.isAdmin)

  /**
   * 用户ID
   */
  const userId = computed(() => authStore.userInfo?.id)

  /**
   * 用户名
   */
  const username = computed(() => authStore.userInfo?.username)

  /**
   * 用户角色
   */
  const userRole = computed(() => authStore.userInfo?.role)

  /**
   * 获取用户头像（基于名字生成）
   */
  const getAvatarGradient = computed(() => {
    if (!user.value?.name) {
      return 'linear-gradient(135deg, #10B981 0%, #059669 100%)'
    }
    
    const colors = [
      'linear-gradient(135deg, #10B981 0%, #059669 100%)',
      'linear-gradient(135deg, #34D399 0%, #10B981 100%)',
      'linear-gradient(135deg, #059669 0%, #047857 100%)',
      'linear-gradient(135deg, #34D399 0%, #059669 100%)',
    ]
    
    const index = user.value.name.charCodeAt(0) % colors.length
    return colors[index]
  })

  /**
   * 获取用户首字母
   */
  const getInitial = (): string => {
    if (!user.value?.name) return 'U'
    return user.value.name.charAt(0).toUpperCase()
  }

  /**
   * 检查是否有特定权限
   * @param role - 所需角色
   * @returns 是否有权限
   */
  const hasRole = (role: string | string[]): boolean => {
    if (!userRole.value) return false
    
    if (Array.isArray(role)) {
      return role.some(r => isAdminRole(r) ? isAdmin.value : userRole.value === r)
    }
    
    return isAdminRole(role) ? isAdmin.value : userRole.value === role
  }

  /**
   * 检查是否拥有资源权限
   * @param userId - 资源所有者ID
   * @returns 是否可以访问
   */
  const canAccessResource = (resourceUserId: number): boolean => {
    // 管理员可以访问所有资源
    if (isAdmin.value) return true
    
    // 用户只能访问自己的资源
    return userId.value === resourceUserId
  }

  /**
   * 登出
   */
  const logout = () => {
    authStore.clearAuthData()
    router.push('/login')
  }

  /**
   * 检查认证状态（供路由守卫使用）
   * @returns 是否已认证
   */
  const checkAuth = (): boolean => {
    if (!isAuthenticated.value) {
      router.push('/login')
      return false
    }
    return true
  }

  /**
   * 检查管理员权限（供路由守卫使用）
   * @returns 是否为管理员
   */
  const checkAdmin = (): boolean => {
    if (!isAuthenticated.value || !isAdmin.value) {
      router.push('/login')
      return false
    }
    return true
  }

  return {
    user,
    isAuthenticated,
    isAdmin,
    userId,
    username,
    userRole,
    getAvatarGradient,
    getInitial,
    hasRole,
    canAccessResource,
    logout,
    checkAuth,
    checkAdmin
  }
}

export default useAuth
