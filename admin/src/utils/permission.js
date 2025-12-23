/**
 * 权限检查工具函数
 */

/**
 * 检查用户是否有指定角色
 * @param {Object} userInfo 用户信息
 * @param {Array|Number} roles 需要的角色ID数组或单个角色ID
 * @returns {Boolean} 是否有权限
 */
export function hasRole(userInfo, roles) {
  if (!userInfo) {
    return false
  }

  // 获取用户角色，支持单角色和多角色
  const userRoles = Array.isArray(userInfo.roles)
    ? userInfo.roles
    : (userInfo.role !== undefined && userInfo.role !== null
      ? [userInfo.role]
      : [])

  // 如果用户没有角色，拒绝访问
  if (userRoles.length === 0) {
    return false
  }

  // 检查用户角色是否在允许的角色列表中
  const requiredRoles = Array.isArray(roles) ? roles : [roles]
  return userRoles.some(role => requiredRoles.includes(role))
}

/**
 * 检查用户是否有指定权限
 * @param {Object} userInfo 用户信息
 * @param {Array|Number} permissions 需要的权限ID数组或单个权限ID
 * @returns {Boolean} 是否有权限
 */
export function hasPermission(userInfo, permissions) {
  if (!userInfo) {
    return false
  }

  // 如果没有指定权限要求，则允许访问
  if (!permissions) {
    return true
  }

  // 获取用户权限
  const userPermissions = Array.isArray(userInfo.permissionIds)
    ? userInfo.permissionIds
    : []

  // 如果用户没有权限，拒绝访问
  if (userPermissions.length === 0) {
    return false
  }

  // 检查用户权限是否在允许的权限列表中
  const requiredPermissions = Array.isArray(permissions) ? permissions : [permissions]
  return userPermissions.some(permission => requiredPermissions.includes(permission))
}

export default {
  hasRole,
  hasPermission
}
