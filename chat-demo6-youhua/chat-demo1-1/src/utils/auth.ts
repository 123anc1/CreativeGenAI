// 从 token 中解析用户信息
export function parseJwt(token: string | null): Record<string, any> | null {
  if (!token) return null
  try {
    const payload = token.split('.')[1]
    const decoded = atob(payload)
    return JSON.parse(decoded)
  } catch (e) {
    console.error('解析 token 失败:', e)
    return null
  }
}

// 获取用户ID
export function getUserId(): string {
  const token = localStorage.getItem('token')
  const payload = parseJwt(token)

  // 优先从token中获取用户ID
  if (payload?.id) return payload.id
  if (payload?.userId) return payload.userId
  if (payload?.userid) return payload.userid

  // 其次从userInfo中获取
  try {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const user: any = JSON.parse(userInfo)
      return user?.id || ''
    }
  } catch {
    // ignore
  }
  return ''
}
