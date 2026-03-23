import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
      "vue": "vue/dist/vue.esm-bundler.js"
    },
  },
  server: {
    host: '0.0.0.0', 
    // 确保服务器可以从外部访问
    // Vite 开发服务器默认已提供 SPA 回退（history API fallback），
    // 所以移除非标准的 `historyApiFallback` 配置以避免混淆。
    // 生产环境请在你实际使用的静态服务器（例如 Nginx/Apache/Express）上配置
    // 将未知路径重写到 `/index.html`，以支持 `createWebHistory()` 路由。
    proxy: {
      "/api/login": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),  // 移除/api前缀，将请求发送到后端的/login
      },
      "/api/register": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),  // 移除/api前缀，将请求发送到后端的/register
      },
      "/user": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        // 仅代理API请求，不代理前端路由
        filter: function (pathname, req) {
          // 只代理真正的用户API请求，不代理前端路由
          const userApiPaths = [
            '/user/login',
            '/user/register',
            '/user/logout',
            '/user/profile',
            '/user/change-password'
          ];
          
          return userApiPaths.some(apiPath => pathname.startsWith(apiPath));
        }
      },
      "/session": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/ai": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        // 禁用代理时的压缩请求头，避免后端返回 gzip 后代理缓冲导致无法流式传输
        configure: (proxy) => {
          proxy.on("proxyReq", (proxyReq, req, res) => {
            try {
              proxyReq.setHeader("accept-encoding", "identity");
            } catch (e) {
              // ignore
            }
          });
        },
      },
      "/multimodal": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        // 禁用代理时的压缩请求头，避免后端返回 gzip 后代理缓冲导致无法流式传输
        configure: (proxy) => {
          proxy.on("proxyReq", (proxyReq, req, res) => {
            try {
              proxyReq.setHeader("accept-encoding", "identity");
            } catch (e) {
              // ignore
            }
          });
        },
      },
      
      "/word": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      
      "/knowledgeBase": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      
      "/image/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/imagepost/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/model/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/sys/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/admin/": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/canvas": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});