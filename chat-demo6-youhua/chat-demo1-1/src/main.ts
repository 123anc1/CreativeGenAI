/**
 * 应用程序入口文件
 * 
 * 项目结构说明:
 * - components/: 可复用的UI组件
 * - pages/: 页面级组件，按功能模块分组
 *   - admin/: 管理员页面
 *   - user/: 普通用户页面
 *   - auth/: 认证相关页面
 * - services/: API服务层
 * - stores/: Pinia状态管理
 * - router/: 路由配置
 * - utils/: 工具函数
 * - constants/: 常量定义
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from "./router/index";

const pinia = createPinia();
const app = createApp(App);

app.use(pinia); // 使用Pinia状态管理
app.use(router); // 使用Vue Router管理路由

app.mount('#app');