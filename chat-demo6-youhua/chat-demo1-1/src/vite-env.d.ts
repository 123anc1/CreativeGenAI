/// <reference types="vite/client" />

// Vue 组件类型声明
declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 为 import.meta.env 添加类型声明
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  // 在这里添加其他环境变量
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}