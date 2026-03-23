import request from '@/shared/utils/request'

// 获取日志列表
export const getLogs = (params: any) => {
  return request.get('/sys/log/list', { params })
}

// 获取日志统计信息
export const getLogStatistics = () => {
  return request.get('/sys/log/statistics')
}

// 获取模块统计
export const getModuleStats = () => {
  return request.get('/sys/log/stats/module')
}

// 获取用户统计
export const getUserStats = () => {
  return request.get('/sys/log/stats/user')
}

// 根据ID获取日志详情
export const getLogById = (id: number) => {
  return request.get(`/sys/log/${id}`)
}