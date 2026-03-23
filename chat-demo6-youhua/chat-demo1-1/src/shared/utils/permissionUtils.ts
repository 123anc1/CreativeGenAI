/**
 * 权限相关的工具函数
 * 提供用户权限检查和验证的相关工具
 */

import { ADMIN_ROLES } from '@/shared/constants/userRoles';

/**
 * 检查用户是否为管理员
 * @param role - 用户角色
 * @returns 是否为管理员
 */
export const checkIsAdmin = (role: string): boolean => {
  return ADMIN_ROLES.some(adminRole => 
    adminRole.toLowerCase() === role.toLowerCase()
  );
};

/**
 * 检查用户是否有特定权限
 * @param userRole - 用户角色
 * @param requiredRole - 需要的角色权限
 * @returns 是否有权限
 */
export const hasPermission = (userRole: string, requiredRole: string): boolean => {
  return userRole.toLowerCase() === requiredRole.toLowerCase();
};

/**
 * 获取用户权限等级
 * @param role - 用户角色
 * @returns 权限等级数字，数字越大权限越高
 */
export const getUserPrivilegeLevel = (role: string): number => {
  const normalizedRole = role.toLowerCase();
  
  switch (normalizedRole) {
    case 'admin':
    case 'administrator':
      return 3;
    case 'moderator':
      return 2;
    case 'user':
      return 1;
    default:
      return 0;
  }
};

/**
 * 检查是否具有足够权限
 * @param userRole - 用户角色
 * @param minRequiredLevel - 最低权限等级要求
 * @returns 是否有足够权限
 */
export const hasSufficientPrivileges = (role: string, minRequiredLevel: number): boolean => {
  return getUserPrivilegeLevel(role) >= minRequiredLevel;
};