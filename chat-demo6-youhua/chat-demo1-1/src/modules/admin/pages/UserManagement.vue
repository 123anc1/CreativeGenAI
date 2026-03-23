UserManagement.vue
<template>
  <div class="bg-white shadow overflow-hidden sm:rounded-lg">
    <div class="px-4 py-5 border-b border-gray-200 sm:px-6">
      <h3 class="text-lg leading-6 font-medium text-gray-900">
        用户管理
      </h3>
      <p class="mt-1 text-sm text-gray-500">
        管理平台上的所有用户账户
      </p>
    </div>
    <div class="px-4 py-5 sm:px-6">
      <!-- 搜索和筛选工具栏 -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
        <div class="flex flex-col sm:flex-row gap-3 flex-1">
          <div class="relative rounded-md shadow-sm flex-1">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              type="text"
              v-model="searchQuery"
              class="focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 pr-12 py-2 sm:text-sm border-gray-300 rounded-md"
              placeholder="搜索用户..."
            />
          </div>
          <select
            v-model="filterRole"
            class="block w-40 pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
          >
            <option value="">所有角色</option>
            <option value="ADMIN">管理员</option>
            <option value="USER">普通用户</option>
          </select>
        </div>
        
        <button
          @click="addNewUser"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          :disabled="loading"
        >
          <svg v-if="!loading" class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          <svg v-else class="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          添加用户
        </button>
      </div>

      <!-- 加载提示 -->
      <div v-if="loading" class="flex justify-center items-center py-8">
        <svg class="animate-spin h-8 w-8 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span class="ml-2 text-gray-600">正在加载用户数据...</span>
      </div>

      <!-- 用户表格 -->
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                用户
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                用户名
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                年龄
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                角色
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                状态
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                注册时间
              </th>
              <th scope="col" class="relative px-6 py-3">
                <span class="sr-only">操作</span>
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in paginatedUsers" :key="user.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10">
                    <div class="h-10 w-10 rounded-full bg-gradient-to-r from-blue-400 to-emerald-400 flex items-center justify-center">
                      <span class="text-white font-medium">{{ user.name.charAt(0).toUpperCase() }}</span>
                    </div>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ user.name }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ user.username }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ user.age || '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ user.role }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span 
                  :class="[
                    user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                    'px-2 inline-flex text-xs leading-5 font-semibold rounded-full'
                  ]"
                >
                  {{ user.active ? '激活' : '禁用' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(user.createdAt || '') }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button 
                  @click="editUser(user)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  编辑
                </button>
                <button 
                  @click="toggleUserStatus(user)"
                  class="text-gray-600 hover:text-gray-900 mr-3"
                >
                  {{ user.active ? '禁用' : '启用' }}
                </button>
                <button 
                  @click="deleteUser(user)"
                  class="text-red-600 hover:text-red-900"
                >
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="!loading" class="mt-6 flex items-center justify-between">
        <div class="text-sm text-gray-700">
          显示 <span class="font-medium">{{ startIndex + 1 }}</span> 到 
          <span class="font-medium">{{ Math.min(endIndex, filteredUsers.length) }}</span> 条，
          共 <span class="font-medium">{{ filteredUsers.length }}</span> 条记录
        </div>
        <div class="flex space-x-1">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="relative inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            上一页
          </button>
          
          <!-- 页码按钮 -->
          <button
            v-for="page in totalPages"
            :key="page"
            @click="currentPage = page"
            :class="[
              'relative inline-flex items-center px-3 py-2 border text-sm font-medium rounded-md',
              currentPage === page 
                ? 'bg-blue-50 border-blue-500 text-blue-600' 
                : 'border-gray-300 text-gray-700 bg-white hover:bg-gray-50'
            ]"
          >
            {{ page }}
          </button>
          
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="relative inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- 用户编辑模态框 -->
  <div v-if="showEditModal" class="fixed z-10 inset-0 overflow-y-auto">
    <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity" aria-hidden="true">
        <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
      </div>

      <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

      <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
        <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <div class="sm:flex sm:items-start">
            <div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg leading-6 font-medium text-gray-900">
                  {{ editingUser ? '编辑用户' : '添加用户' }}
                </h3>
                <button
                  @click="cancelEdit"
                  class="text-gray-400 hover:text-gray-500 focus:outline-none"
                  :disabled="saving"
                >
                  <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <div class="mt-2">
                <form class="space-y-4">
                  <div>
                    <label for="userName" class="block text-sm font-medium text-gray-700">姓名 *</label>
                    <input
                      type="text"
                      v-model="currentUser.name"
                      class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      required
                    />
                  </div>
                  <div>
                    <label for="userUsername" class="block text-sm font-medium text-gray-700">用户名 *</label>
                    <input
                      type="text"
                      v-model="currentUser.username"
                      class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      required
                      :readonly="editingUser"
                    />
                    <p v-if="editingUser" class="mt-1 text-xs text-gray-500">用户名不可修改</p>
                  </div>
                  <div>
                    <label for="userAge" class="block text-sm font-medium text-gray-700">年龄</label>
                    <input
                      type="number"
                      v-model.number="currentUser.age"
                      min="1"
                      max="150"
                      class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    />
                  </div>
                  <div v-if="!editingUser">
                    <label for="userPassword" class="block text-sm font-medium text-gray-700">密码 *</label>
                    <input
                      type="password"
                      v-model="currentUser.password"
                      class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      required
                    />
                    <p class="mt-1 text-xs text-gray-500">至少6位字符</p>
                  </div>
                  <div>
                    <label for="userRole" class="block text-sm font-medium text-gray-700">角色</label>
                    <select
                      v-model="currentUser.role"
                      class="mt-1 block w-full bg-white border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    >
                      <option value="USER">普通用户</option>
                      <option value="ADMIN">管理员</option>
                    </select>
                  </div>
                  <div v-if="editingUser" class="flex items-center">
                    <input
                      id="userActive"
                      type="checkbox"
                      v-model="currentUser.active"
                      class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                    <label for="userActive" class="ml-2 block text-sm text-gray-900">
                      激活账户
                    </label>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
          <button
            type="button"
            @click="saveUser"
            class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
            :disabled="saving"
          >
            <span v-if="!saving">保存</span>
            <span v-else class="flex items-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              保存中...
            </span>
          </button>
          <button
            type="button"
            @click="cancelEdit"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            :disabled="saving"
          >
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { 
  getUsers as apiGetUsers, 
  createUser as apiCreateUser, 
  deleteUser as apiDeleteUser,
  updateUserStatus as apiUpdateUserStatus,
  updateUserRole as apiUpdateUserRole
} from '@/modules/admin/services/admin';

// 导入User类型
type User = {
  id: number;
  username: string;
  password?: string;
  name: string;
  age?: number;
  role: string;
  active: boolean;
  createdAt?: string;
};

// 用户数据
const users = ref<User[]>([]);
const searchQuery = ref('');
const filterRole = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(10);
const showEditModal = ref(false);
const editingUser = ref(false);
const loading = ref(false);
const saving = ref(false);
const currentUser = ref<User>({
  id: 0,
  username: '',
  name: '',
  age: undefined,
  role: 'USER',
  active: true
});

// 计算属性：过滤后的用户列表
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = 
      user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.username.toLowerCase().includes(searchQuery.value.toLowerCase());
    
    const matchesRole = !filterRole.value || user.role === filterRole.value;
    
    return matchesSearch && matchesRole;
  });
});

// 计算属性：分页后的用户列表
const paginatedUsers = computed(() => {
  return filteredUsers.value.slice(startIndex.value, endIndex.value);
});

// 计算属性：分页相关
const totalPages = computed(() => Math.ceil(filteredUsers.value.length / itemsPerPage.value));
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value);
const endIndex = computed(() => Math.min(startIndex.value + itemsPerPage.value, filteredUsers.value.length));

// 分页方法
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// 用户操作方法
const editUser = (user: User) => {
  currentUser.value = { ...user };
  // 清空密码字段，避免传输密码
  currentUser.value.password = '';
  editingUser.value = true;
  showEditModal.value = true;
};

const deleteUser = async (user: User) => {
  if (confirm(`确定要删除用户 "${user.name}" 吗？`)) {
    try {
      loading.value = true;
      const response = await apiDeleteUser(user.id);
      
      // 检查删除操作是否成功
      if (response && (response.data || response.status === 200)) {
        // 从本地数组中移除用户
        const index = users.value.findIndex(u => u.id === user.id);
        if (index !== -1) {
          users.value.splice(index, 1);
        }
        alert('用户删除成功');
      } else {
        throw new Error('删除操作未成功');
      }
    } catch (error: any) {
      console.error('删除用户失败:', error);
      if (error.message.includes('Unexpected token')) {
        alert('API响应格式错误，请检查后端服务是否正常运行');
      } else if (error.message.includes('404')) {
        alert('用户不存在或API端点错误');
      } else if (error.message.includes('403')) {
        alert('没有权限删除该用户');
      } else {
        alert(`删除用户失败: ${error.message}`);
      }
    } finally {
      loading.value = false;
    }
  }
};

const toggleUserStatus = async (user: User) => {
  try {
    loading.value = true;
    const newStatus = !user.active;
    const response = await apiUpdateUserStatus(user.id, newStatus);
    
    // 验证响应结果
    if (response && (response.data || response.status === 200)) {
      // 更新本地状态
      user.active = newStatus;
      
      // 重要修复：重新加载用户数据以确保与服务器同步
      await loadUsers();
      
      alert(newStatus ? '用户激活成功' : '用户禁用成功');
    } else {
      throw new Error('状态更新未成功');
    }
  } catch (error: any) {
    console.error('更新用户状态失败:', error);
    if (error.message.includes('Unexpected token')) {
      alert('API响应格式错误，请检查后端服务是否正常运行');
    } else if (error.message.includes('404')) {
      alert('用户不存在');
    } else if (error.message.includes('403')) {
      alert('没有权限更新该用户状态');
    } else {
      alert(`更新用户状态失败: ${error.message}`);
    }
  } finally {
    loading.value = false;
  }
};

const addNewUser = () => {
  currentUser.value = {
    id: 0,
    username: '',
    name: '',
    age: undefined,
    role: 'USER',
    active: true
  };
  editingUser.value = false;
  showEditModal.value = true;
};

const saveUser = async () => {
  if (!currentUser.value.name || !currentUser.value.username) {
    alert('请填写必填字段（姓名和用户名）');
    return;
  }

  if (!editingUser.value && (!currentUser.value.password || currentUser.value.password.length < 6)) {
    alert('创建用户时必须设置至少6位密码');
    return;
  }

  saving.value = true;
  
  try {
    if (editingUser.value) {
      // 更新现有用户 - 后端只支持更新角色，所以这里只调用角色更新接口
      if (currentUser.value.role !== users.value.find(u => u.id === currentUser.value.id)?.role) {
        await apiUpdateUserRole(currentUser.value.id, currentUser.value.role);
      }
      
      // 更新本地用户数据
      const index = users.value.findIndex(u => u.id === currentUser.value.id);
      if (index !== -1) {
        users.value[index] = { ...users.value[index], ...currentUser.value };
      }
    } else {
      // 添加新用户
      if (!currentUser.value.password) {
        throw new Error('密码不能为空');
      }
      
      const userData = {
        username: currentUser.value.username,
        password: currentUser.value.password,
        name: currentUser.value.name,
        age: currentUser.value.age,
        role: currentUser.value.role
      };
      
      const response = await apiCreateUser(userData);
      
      // 验证创建响应并添加新用户到列表
      if (response && response.data) {
        const newUser: User = {
          id: (response.data as any).id || 0,
          username: (response.data as any).username || '',
          name: (response.data as any).name || '',
          age: (response.data as any).age,
          role: (response.data as any).role || 'USER',
          active: (response.data as any).active !== undefined ? (response.data as any).active : true,
          createdAt: new Date().toISOString()
        };
        users.value.push(newUser);
      } else {
        throw new Error('用户创建成功但响应数据无效');
      }
    }
    
    showEditModal.value = false;
    alert(editingUser.value ? '用户更新成功' : '用户创建成功');
  } catch (error: any) {
    console.error('保存用户失败:', error);
    if (error.message.includes('Unexpected token')) {
      alert('API响应格式错误，请检查后端服务是否正常运行');
    } else if (error.message.includes('409')) {
      alert('用户名已存在，请使用其他用户名');
    } else if (error.message.includes('400')) {
      alert('请求参数无效，请检查输入的数据');
    } else if (error.message.includes('403')) {
      alert('没有权限执行此操作');
    } else {
      alert(`保存用户失败: ${error.message}`);
    }
  } finally {
    saving.value = false;
  }
};

const cancelEdit = () => {
  showEditModal.value = false;
};

// 格式化日期
const formatDate = (dateString: string) => {
  const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short', day: 'numeric' };
  return new Date(dateString).toLocaleDateString('zh-CN', options);
};

// 加载用户数据
const loadUsers = async () => {
  try {
    console.log('开始加载用户数据...');
    loading.value = true;
    const response = await apiGetUsers();
    
    console.log('API响应:', response);
    
    // 检查响应是否包含预期的数据结构
    if (response && response.data) {
      // 验证数据是否为数组
      if (Array.isArray(response.data)) {
        users.value = response.data.map((user: any) => {
          // 确保每个用户对象有适当的属性
          return {
            id: user.id,
            username: user.username,
            name: user.name,
            age: user.age,
            role: user.role || 'USER',
            active: user.isActive !== undefined ? user.isActive : (user.active !== undefined ? user.active : true),
            createdAt: user.createdAt || user.created_at || user.create_time || new Date().toISOString()
          };
        });
        console.log(`成功加载 ${response.data.length} 个用户`);
      } else {
        console.error('API返回的数据不是数组:', response.data);
        throw new Error('API返回的数据格式不正确');
      }
    } else {
      console.error('API响应格式不正确:', response);
      throw new Error('API响应格式不正确');
    }
  } catch (error: any) {
    console.error('加载用户数据失败:', error);
    if (error.message.includes('Unexpected token')) {
      alert('API响应格式错误，请检查后端服务是否正常运行');
    } else if (error.message.includes('404')) {
      alert('API端点不存在，请检查后端API是否已正确部署');
    } else if (error.message.includes('401') || error.message.includes('403')) {
      alert('未授权访问，请检查您的管理员权限');
    } else if (error.message.includes('Network Error')) {
      alert('网络连接失败，请检查网络连接和服务器状态');
    } else {
      alert(`加载用户数据失败: ${error.message}`);
    }
  } finally {
    loading.value = false;
  }
};

// 监听路由变化，确保每次进入页面都重新加载数据
const route = useRoute();

watch(
  () => route.path,
  (newPath: string) => {
    if (newPath === '/admin/users') {
      console.log('路由变化到用户管理页面，重新加载数据');
      loadUsers();
    }
  }
);

onMounted(() => {
  console.log('UserManagement组件挂载');
  loadUsers();
});
</script>
<style scoped>
/* 保持原样式不变 */
</style>