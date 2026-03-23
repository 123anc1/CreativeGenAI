Sidebar.vue
<template>
  <div class="flex flex-col h-full">
    <!-- Logo区域 -->
    <div class="flex items-center px-4 py-6 border-b border-gray-200">
      <div class="flex-shrink-0">
        <div class="h-10 w-10 rounded-full bg-gradient-to-r from-blue-500 to-emerald-500 flex items-center justify-center">
          <span class="text-white font-bold text-lg">A</span>
        </div>
      </div>
      <div class="ml-3">
        <h1 class="text-xl font-bold text-gray-900">管理员</h1>
        <p class="text-xs text-gray-500">控制台</p>
      </div>
    </div>

    <!-- 导航菜单 -->
    <nav class="mt-6 flex-1 px-2 space-y-1">
      <router-link
        v-for="item in navigationItems"
        :key="item.name"
        :to="item.href"
        :class="[
          item.current
            ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
            : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900',
          'group flex items-center px-4 py-3 text-sm font-medium rounded-md'
        ]"
      >
        <component
          :is="item.icon"
          :class="[
            item.current ? 'text-blue-600' : 'text-gray-500 group-hover:text-gray-600',
            'mr-3 flex-shrink-0 h-6 w-6'
          ]"
          aria-hidden="true"
        />
        <span class="truncate">{{ item.name }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

// 定义导航项
const route = useRoute();
const navigationItems = ref([
  { name: '仪表盘',
    href: '/admin/dashboard',
    icon: {
      name: 'dashboard-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
      `
    },
    current: route.path === '/admin/dashboard'
  },
  {
    name: '用户管理',
    href: '/admin/users',  // 改为相对路径
    icon: {
      name: 'users-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
      `
    },
    current: route.path === '/admin/users'
  },
  {
    name: '模型管理',
    href: '/admin/models',  // 改为相对路径
    icon: {
      name: 'model-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
      `
    },
    current: route.path === '/admin/models'
  },
  {
    name: '内容管理',
    href: '/admin/content',
    icon: {
      name: 'content-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      `
    },
    current: route.path === '/admin/content'
  },
  {
    name: '日志管理',
    href: '/admin/logs',
    icon: {
      name: 'logs-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
        </svg>
      `
    },
    current: route.path === '/admin/logs'
  },
  // 暂时注释掉未实现的功能，避免误导用户
  /*
  {
    name: '内容管理',
    href: '#content',
    icon: {
      name: 'content-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      `
    },
    current: false
  },
  {
    name: '系统设置',
    href: '#settings',
    icon: {
      name: 'settings-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      `
    },
    current: false
  },
  {
    name: '数据统计',
    href: '#stats',
    icon: {
      name: 'stats-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2-2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
      `
    },
    current: false
  },
  {
    name: '日志审计',
    href: '#logs',
    icon: {
      name: 'logs-icon',
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      `
    },
    current: false
  }
  */
]);

// 更新当前路由高亮
const updateCurrentRoute = () => {
  navigationItems.value.forEach(item => {
    // 处理不同类型的路由路径匹配
    if (item.href.startsWith('/admin/')) {
      // 对于管理员子路由，检查是否匹配当前路径
      item.current = route.path === item.href;
    } else {
      // 其他路径的匹配
      item.current = item.href === route.path;
    }
  });
};

// 组件挂载时初始化
onMounted(() => {
  updateCurrentRoute();
});

// 监听路由变化，实时更新高亮状态
watch(
  () => route.path,
  () => {
    updateCurrentRoute();
  }
);
</script>

<style scoped>
a {
  transition: all 0.2s ease;
}
</style>