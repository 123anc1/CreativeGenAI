/**
 * Tailwind CSS 样式常量
 * 
 * 统一管理项目中重复使用的样式类名组合
 * 便于维护和修改统一的设计风格
 * 
 * @example
 * ```vue
 * <div :class="tailwindClasses.cardBase">
 *   <button :class="tailwindClasses.buttonPrimary">
 *     确定
 *   </button>
 * </div>
 * ```
 */

/**
 * 卡片样式
 */
export const cardClasses = {
  /** 基础卡片样式 */
  base: 'bg-white dark:bg-gray-800 rounded-lg shadow-sm hover:shadow-md transition-shadow',
  /** 带边框的卡片 */
  bordered: 'border border-gray-200 dark:border-gray-700',
  /** 网格卡片 */
  grid: 'rounded-lg shadow-sm hover:shadow-md transition-shadow overflow-hidden',
}

/**
 * 按钮样式
 */
export const buttonClasses = {
  /** 主按钮 */
  primary: 'px-4 py-2 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg transition-colors',
  /** 次按钮 */
  secondary: 'px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-900 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-lg transition-colors',
  /** 危险按钮 */
  danger: 'px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors',
  /** 禁用状态 */
  disabled: 'px-4 py-2 bg-gray-300 text-gray-500 rounded-lg cursor-not-allowed opacity-50',
  /** 大尺寸 */
  lg: 'px-6 py-3 text-lg',
  /** 小尺寸 */
  sm: 'px-2 py-1 text-sm',
}

/**
 * 输入框样式
 */
export const inputClasses = {
  /** 基础样式 */
  base: 'w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 dark:bg-gray-700 dark:text-white',
  /** 错误状态 */
  error: 'border-red-500 focus:ring-red-500',
  /** 成功状态 */
  success: 'border-green-500 focus:ring-green-500',
}

/**
 * 用户菜单样式（复用频繁）
 */
export const dropdownClasses = {
  /** 菜单容器 */
  menu: 'bg-white dark:bg-gray-800 border border-green-100 dark:border-emerald-900 rounded-xl shadow-2xl shadow-green-200/30 dark:shadow-emerald-900/30 backdrop-blur-sm z-50 overflow-visible',
  /** 菜单项基础样式 */
  item: 'flex items-center w-full px-4 py-3 text-sm transition-all duration-300 rounded-lg group relative overflow-visible hover:shadow-md cursor-pointer',
  /** 菜单项悬停样式 */
  itemHover: 'hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50 dark:hover:from-emerald-900/30 dark:hover:to-green-900/30 text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white hover:ring-1 hover:ring-green-200 dark:hover:ring-emerald-800',
}

/**
 * 头像梯度颜色
 */
export const avatarGradients = [
  'linear-gradient(135deg, #10B981 0%, #059669 100%)',
  'linear-gradient(135deg, #34D399 0%, #10B981 100%)',
  'linear-gradient(135deg, #059669 0%, #047857 100%)',
  'linear-gradient(135deg, #34D399 0%, #059669 100%)',
]

/**
 * 文本样式
 */
export const textClasses = {
  /** 主标题 */
  title: 'text-xl font-bold text-gray-900 dark:text-white',
  /** 小标题 */
  subtitle: 'text-lg font-semibold text-gray-800 dark:text-gray-200',
  /** 标签 */
  label: 'text-sm font-medium text-gray-700 dark:text-gray-300',
  /** 帮助文字 */
  helper: 'text-xs text-gray-500 dark:text-gray-400',
}

/**
 * 其他常用样式
 */
export const commonClasses = {
  /** 分隔线 */
  divider: 'border-t border-gray-200 dark:border-gray-700',
  /** 加载动画 */
  spinner: 'animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-emerald-500',
  /** 骨架屏 */
  skeleton: 'animate-pulse bg-gray-200 dark:bg-gray-700 rounded',
  /** 焦点环 */
  focusRing: 'focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500',
  /** 高亮背景 */
  highlight: 'bg-yellow-50 dark:bg-yellow-900/20 border-l-4 border-yellow-400',
}

export default {
  cardClasses,
  buttonClasses,
  inputClasses,
  dropdownClasses,
  avatarGradients,
  textClasses,
  commonClasses,
}
