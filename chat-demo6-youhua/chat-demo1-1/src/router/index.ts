import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';

// 使用懒加载优化页面加载速度
const Chat = () => import('../modules/user/pages/Chat.vue');
const Login = () => import('../modules/auth/pages/Login.vue');
const Register = () => import('../modules/auth/pages/Register.vue');
const KnowledgeBase = () => import('../modules/user/pages/KnowledgeBase.vue');
const ImageGeneration = () => import('../modules/user/pages/ImageGeneration.vue');
const Profile = () => import('../modules/user/pages/Profile.vue');
const ImageSharing = () => import('../modules/user/pages/ImageSharing.vue');
const MyPosts = () => import('../modules/user/pages/MyPosts.vue');
const Favorites = () => import('../modules/user/pages/Favorites.vue');
// 导入管理员页面组件
const AdminLayout = () => import('../modules/admin/pages/AdminLayout.vue');
const Dashboard = () => import('../modules/admin/pages/Dashboard.vue');
const UserManagement = () => import('../modules/admin/pages/UserManagement.vue');
const ModelManagement = () => import('../modules/admin/pages/ModelManagement.vue');
const ContentManagement = () => import('../modules/admin/pages/ContentManagement.vue');
const LogManagement = () => import('../modules/admin/pages/LogManagement.vue');
const Canvas = () => import('../modules/user/pages/Canvas.vue');
const CanvasProject = () => import('../modules/user/pages/CanvasProject.vue');

// 导入路由中间件
import { isAuthenticated, isAdmin } from './middleware';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'ImageSharing',
    component: ImageSharing,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
  {
    path: '/knowledge-base',
    name: 'KnowledgeBase',
    component: KnowledgeBase,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/image-generation',
    name: 'ImageGeneration',
    component: ImageGeneration,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/user/image-sharing',
    name: 'ImageSharingRedirect',
    redirect: '/',
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/my-posts',
    name: 'MyPosts',
    component: MyPosts,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/canvas',
    name: 'Canvas',
    component: CanvasProject,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/canvas/create',
    name: 'CanvasCreate',
    component: Canvas,
    beforeEnter: [isAuthenticated],
  },
  {
    path: '/canvas/:sessionId',
    name: 'CanvasSession',
    component: Canvas,
    beforeEnter: [isAuthenticated],
  },
  // 管理员路由
  {
    path: '/admin',
    component: AdminLayout,
    beforeEnter: [isAuthenticated, isAdmin],
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: UserManagement,
      },
      {
        path: 'models',
        name: 'ModelManagement',
        component: ModelManagement,
      },
      {
        path: 'content',
        name: 'ContentManagement',
        component: ContentManagement,
      },
      {
        path: 'logs',
        name: 'LogManagement',
        component: LogManagement,
      }
    ]
  },
  {
    path: '/chat.html',
    redirect: '/chat',
  },
  {
    path: '/login.html',
    redirect: '/login',
  },
  {
    path: '/register.html',
    redirect: '/register',
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;