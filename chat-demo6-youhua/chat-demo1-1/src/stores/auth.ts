import { defineStore } from 'pinia';
import { isAdminRole } from '@/shared/constants/userRoles'; 

interface AuthState {
  token: string | null;
  userInfo: {
    id: number;
    username: string;
    name: string;
    role: string;
    age?: number; 
    isActive?: boolean;  
  } | null;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    userInfo: null,
  }),

  getters: {
    isAuthenticated(): boolean {
      return !!this.token;
    },
    
    isAdmin(): boolean {
      return this.userInfo?.role ? isAdminRole(this.userInfo.role) : false;
    },
    
    currentUser: (state) => state.userInfo,
  },

  actions: {
    setAuthData(token: string, userInfo: AuthState['userInfo']) {
      this.token = token;
      this.userInfo = userInfo;
      
      localStorage.setItem('token', token);
      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
      }
    },

    clearAuthData() {
      this.token = null;
      this.userInfo = null;
      
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    },

    loadUserInfo() {
      const userInfoStr = localStorage.getItem('userInfo');
      if (userInfoStr) {
        try {
          const parsed = JSON.parse(userInfoStr);
          // 验证是否为对象且包含必要字段（如 username 或 id）
          if (parsed && typeof parsed === 'object' && 'id' in parsed && 'username' in parsed) {
            this.userInfo = parsed;
          } else {
            console.warn('Invalid user info structure in localStorage, clearing it.');
            localStorage.removeItem('userInfo');
          }
        } catch (e) {
          console.error('Failed to parse user info', e);
          localStorage.removeItem('userInfo'); // 防止下次继续尝试加载坏数据
        }
      }
    },
  },
});