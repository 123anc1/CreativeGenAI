import request from '@/shared/utils/request';

// 获取基础模型列表
export const getBaseModels = () => {
  return request.get('/model/basemodels');
};

// 选择基础模型
export const selectBaseModel = (id: string) => {
  return request.get(`/model/basemodels/${id}`);
};

// 获取风格模型列表
export const getStyleModels = () => {
  return request.get('/model/stylemodels');
};

// 选择风格模型
export const selectStyleModel = (id: string) => {
  return request.get(`/model/stylemodels/${id}`);
};

// 添加基础模型
export const addBaseModel = (data: any) => {
  return request.post('/model/basemodels', data);
};

// 添加风格模型
export const addStyleModel = (data: any) => {
  return request.post('/model/stylemodels', data);
};

// 更新基础模型
export const updateBaseModel = (data: any) => {
  return request.put('/model/basemodels', data);
};

// 更新风格模型 (后端代码中没有此API，暂时注释)
// export const updateStyleModel = (data: any) => {
//   return request.put('/model/stylemodels', data);
// };