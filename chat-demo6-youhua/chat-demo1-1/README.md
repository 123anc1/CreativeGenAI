# 项目结构规范

## 目录结构

```
src/
├── modules/            # 业务模块
│   ├── auth/           # 认证模块
│   │   ├── components/ # 认证相关组件
│   │   ├── pages/      # 认证相关页面
│   │   └── services/   # 认证相关服务
│   ├── user/           # 用户模块
│   │   ├── components/ # 用户相关组件
│   │   ├── pages/      # 用户相关页面
│   │   └── services/   # 用户相关服务
│   └── admin/          # 管理员模块
│       ├── components/ # 管理员相关组件
│       ├── pages/      # 管理员相关页面
│       └── services/   # 管理员相关服务
├── shared/             # 共享资源
│   ├── components/     # 通用组件
│   ├── composables/    # 通用组合式函数
│   ├── constants/      # 常量定义
│   ├── utils/          # 工具函数
│   └── types/          # 类型定义
├── stores/             # 状态管理
├── router/             # 路由配置
├── App.vue             # 应用根组件
└── main.ts             # 应用入口
```

## 命名规范

### 组件命名
- 组件名称使用 PascalCase 格式
- 页面组件放在对应的模块 pages 目录下
- 可复用组件放在对应的模块 components 目录或 shared/components 目录下

### 文件命名
- 组件文件：使用 PascalCase 格式，如 `LoginForm.vue`
- 服务文件：使用 camelCase 格式，如 `userService.ts`
- 工具文件：使用 camelCase 格式，如 `dateUtils.ts`
- 类型文件：使用 PascalCase 格式，如 `UserType.ts`

### 变量命名
- 常量：使用 UPPER_SNAKE_CASE 格式，如 `MAX_LENGTH`
- 变量：使用 camelCase 格式，如 `userName`
- 函数：使用 camelCase 格式，如 `getUserInfo`
- 类：使用 PascalCase 格式，如 `UserService`

## 代码规范

### Vue 组件
- 使用 Composition API
- 组件逻辑清晰，职责单一
- 避免在组件中直接处理业务逻辑，应通过 services 层处理

### 服务层
- 按业务领域组织服务
- 服务方法应返回 Promise
- 统一处理错误和异常

### 类型定义
- 使用 TypeScript 类型定义
- 为接口、请求参数、响应数据等添加类型
- 类型定义放在 shared/types 目录下

## 约定大于配置

- 遵循目录结构约定，无需额外配置
- 遵循命名规范，提高代码可读性
- 遵循代码规范，确保代码质量
- 遵循业务逻辑分层，提高代码可维护性