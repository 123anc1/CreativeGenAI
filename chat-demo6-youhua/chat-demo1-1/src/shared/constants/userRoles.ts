/**
 * 用户角色常量定义
 * 定义系统中所有的用户角色类型
 */

export enum USER_ROLES {
  ADMIN = 'ADMIN',
  USER = 'USER',
  MODERATOR = 'MODERATOR',
  GUEST = 'GUEST'
}

/**
 * 管理员角色标识符
 */
export const ADMIN_ROLES = [
  USER_ROLES.ADMIN,
  'admin',
  'administrator',
  'ADMINISTRATOR'
];

/**
 * 检查是否为管理员角色
 * @param role - 用户角色
 * @returns 是否为管理员
 */
export const isAdminRole = (role: string): boolean => {
  return ADMIN_ROLES.includes(role);
};