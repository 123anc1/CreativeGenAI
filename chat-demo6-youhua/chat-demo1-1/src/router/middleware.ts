import { RouteLocationNormalized, NavigationGuardNext } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { isAdminRole } from '@/shared/constants/userRoles';  // 导入角色判断函数

// 检查用户是否已登录
export const isAuthenticated = (_to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const token = localStorage.getItem('token');
  if (!token) {
    next('/login');
    return;
  }
  
  // 仅检查token是否存在，不解析JWT（如果后端使用非标准JWT格式）
  // 如果需要验证token有效性，应该通过API调用验证
  next();
};

// 检查用户是否具有管理员权限
export const isAdmin = (_to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  isAuthenticated(_to, _from, () => {
    const authStore = useAuthStore();
    authStore.loadUserInfo(); // 确保用户信息已加载
    
    const userInfoStr = localStorage.getItem('userInfo');
    if (!userInfoStr) {
      next('/login');
      return;
    }

    try {
      const parsedInfo = JSON.parse(userInfoStr);
      if (parsedInfo.role && isAdminRole(parsedInfo.role)) {
        next();
      } else {
        // 如果不是管理员，重定向到首页
        next('/');
      }
    } catch (e) {
      console.error('用户信息解析失败', e);
      next('/login');
    }
  });
};

