import { Imagepost, Imagepostparam } from '@/shared/types';
import request from '@/shared/utils/request';

// 定义后端返回的结果包装类型
interface ResultWrapper<T> {
  code: number;
  msg: string;
  data: T;
}

/**
 * 将后端Imagepost转换为前端Imagepost类型
 */
function convertBackendImagepost(backendPost: any): Imagepost {
  if (backendPost && typeof backendPost === 'object' && 'image_url' in backendPost) {
    return backendPost;
  }
  
  return {
    id: backendPost.id,
    userid: backendPost.userId,           
    imageId: backendPost.imageId,
    user_name: backendPost.name,      
    image_url: backendPost.imageUrl,
    imageData: backendPost.imageData,
    image_title: backendPost.title,    
    image_description: backendPost.caption,
    status: backendPost.status,
    published_at: backendPost.publishedAt ? new Date(backendPost.publishedAt) : undefined,
    viewcount: backendPost.viewcount,
    likeCount: backendPost.likeCount,
    commentCount: backendPost.commentCount,
    created_at: backendPost.createdAt ? new Date(backendPost.createdAt) : undefined,
    updated_at: backendPost.updatedAt ? new Date(backendPost.updatedAt) : undefined,
    imagesize: backendPost.imagesize,
    negativePrompt: backendPost.negativePrompt,
    steps: backendPost.steps,
    cfgScale: backendPost.cfgScale,
    modelName: backendPost.modelName,
    style: backendPost.style,
    width: backendPost.width,
    height: backendPost.height
  };
}

// API请求缓存
const apiCache = new Map<string, { data: any; timestamp: number }>();
const CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存

// 定义图像分享服务
class ImagepostService {
  /**
   * 生成缓存键
   */
  private generateCacheKey(endpoint: string, params: any): string {
    return `${endpoint}_${JSON.stringify(params)}`;
  }

  /**
   * 从缓存获取数据
   */
  private getFromCache(key: string) {
    const cached = apiCache.get(key);
    if (cached && (Date.now() - cached.timestamp) < CACHE_DURATION) {
      return cached.data;
    }
    apiCache.delete(key);
    return null;
  }

  /**
   * 缓存数据
   */
  private cacheData(key: string, data: any) {
    apiCache.set(key, {
      data,
      timestamp: Date.now()
    });
  }

  /**
   * 分享图片
   */
  async upload(imagepost: any) {
    return fetch('/imagepost/image', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify({
        userId: imagepost.userid,
        imageId: imagepost.imageId,
        imageUrl: imagepost.image_url,
        caption: imagepost.image_description || imagepost.image_title,
        username: localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo') || '{}').name : '',
        title: imagepost.image_title,
        negativePrompt: imagepost.negativePrompt,
        steps: imagepost.steps,
        cfgScale: imagepost.cfgScale,
        modelName: imagepost.modelName,
        style: imagepost.style,
        width: imagepost.width,
        height: imagepost.height
      })
    });
  }

  /**
   * 获取图片列表
   */
  async getImagepostList(params: Imagepostparam, userId?: number) {
    // 生成缓存键
    const cacheKey = this.generateCacheKey('getImagepostList', { ...params, userId });
    
    // 尝试从缓存获取数据
    const cachedData = this.getFromCache(cacheKey);
    if (cachedData) {
      console.log('从缓存获取图片列表数据');
      return cachedData;
    }

    try {
      // 构建请求参数
      const requestParams: any = {
        page: params.page,
        pageSize: params.size
      };

      // 如果有userId，添加到参数中（新接口需要userId作为必填参数）
      if (userId) {
        requestParams.userId = userId;
      }

      // 如果有标题搜索，添加到参数中
      if (params.title) {
        requestParams.title = params.title;
      }

      // 如果有用户ID筛选，添加到参数中
      if (params.userid) {
        requestParams.userid = params.userid;
      }

      const response = await request.get('/imagepost/list', {
        params: requestParams
      });

      console.log('getImagepostList 响应:', response);
      
      // 检查后端返回的状态码
      if (response.code !== 1) {
        throw new Error(response.msg || '获取图片列表失败');
      }

      // 检查数据结构完整性
      if (!response.data) {
        throw new Error('服务器返回数据格式错误：缺少data字段');
      }

      if (!Array.isArray(response.data.rows)) {
        console.error('数据结构错误:', response.data);
        throw new Error('服务器返回数据格式错误：rows字段不是数组');
      }

      // 转换数据格式
      const convertedRecords = response.data.rows.map(convertBackendImagepost);
      console.log('转换后的记录:', convertedRecords);
      const responseData = {
        records: convertedRecords,
        total: response.data.total || 0
      };
      
      // 缓存数据
      this.cacheData(cacheKey, responseData);
      
      return responseData;
    } catch (error: any) {
      console.error('获取图片列表失败:', error);
      if (error.code === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        throw new Error('登录已过期，请重新登录');
      } else if (error.code === 403) {
        throw new Error('权限不足，无法访问图片列表');
      }
      throw new Error(`获取图片列表失败: ${error.msg || error.message}`);
    }
  }

  /**
   * 获取图片列表（管理员）
   */
  async getImagepostListAdmin(params: Imagepostparam) {
    try {
      const response = await request.get('/imagepost/listadmin', {
        params: {
          page: params.page,
          size: params.size,
          title: params.title,
          userid: params.userid
        }
      });

      console.log('getImagepostListAdmin 响应:', response);
      
      // 检查后端返回的状态码
      if (response.code !== 1) {
        throw new Error(response.msg || '获取图片列表失败');
      }

      // 检查数据结构完整性
      if (!response.data) {
        throw new Error('服务器返回数据格式错误：缺少data字段');
      }

      if (!Array.isArray(response.data.rows)) {
        console.error('数据结构错误:', response.data);
        throw new Error('服务器返回数据格式错误：rows字段不是数组');
      }

      // 转换数据格式
      const convertedRecords = response.data.rows.map(convertBackendImagepost);
      console.log('转换后的记录:', convertedRecords);
      return {
        records: convertedRecords,
        total: response.data.total || 0
      };
    } catch (error: any) {
      console.error('获取图片列表失败:', error);
      if (error.code === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        throw new Error('登录已过期，请重新登录');
      } else if (error.code === 403) {
        throw new Error('权限不足，无法访问图片列表');
      }
      throw new Error(`获取图片列表失败: ${error.msg || error.message}`);
    }
  }

  /**
   * 搜索图片（使用新的搜索API）
   */
  async search(keyword: string, page: number = 1, pageSize: number = 20) {
    try {
      const response = await request.get('/imagepost/search', {
        params: {
          keyword: keyword,
          page: page,
          pageSize: pageSize
        }
      });

      console.log('search 响应:', response);

      if (response.code !== 1) {
        throw new Error(response.msg || '搜索图片失败');
      }

      if (!response.data) {
        throw new Error('服务器返回数据格式错误：缺少data字段');
      }

      if (!Array.isArray(response.data.rows)) {
        console.error('数据结构错误:', response.data);
        throw new Error('服务器返回数据格式错误：rows字段不是数组');
      }

      const convertedRecords = response.data.rows.map(convertBackendImagepost);
      console.log('搜索转换后的记录:', convertedRecords);

      return {
        records: convertedRecords,
        total: response.data.total || 0
      };
    } catch (error: any) {
      console.error('搜索图片失败:', error);
      if (error.code === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        throw new Error('登录已过期，请重新登录');
      } else if (error.code === 403) {
        throw new Error('权限不足，无法搜索图片');
      }
      throw new Error(`搜索图片失败: ${error.msg || error.message}`);
    }
  }

  /**
   * 根据id获取图片
   */
  async getImagepostById(id: number) {
    const userInfo = localStorage.getItem('userInfo');
    let userId = 0;
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        userId = user.id || 0;
      } catch (e) {
        userId = 0;
      }
    }
    const response = await fetch(`/imagepost/list/${id}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`获取图片失败: ${response.status} ${response.statusText}`);
    }

    const result: ResultWrapper<any> = await response.json();
    
    if (result.code !== 1) {
      throw new Error(result.msg || '获取图片失败');
    }

    // 处理返回数据，可能包含 image 和 commentCount 字段
    const imageData = result.data.image || result.data;
    const commentCount = result.data.commentCount;
    
    const converted = convertBackendImagepost(imageData);
    if (commentCount !== undefined) {
      converted.commentCount = commentCount;
    }
    
    return converted;
  }

  /**
   * 根据用户id获取图片列表
   */
  async getUserImageposts(userId: number) {
    const response = await fetch(`/imagepost/list/user/${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`获取用户图片失败: ${response.status} ${response.statusText}`);
    }

    const result: any = await response.json();
    console.log('getUserImageposts 原始响应:', result);
    
    // 处理不同的响应格式
    let rawData: any[] = [];
    let total = 0;
    
    // 情况1: 后端直接返回数组
    if (Array.isArray(result)) {
      rawData = result;
      total = result.length;
      console.log('检测到数组格式数据');
    }
    // 情况2: 后端返回带data包装的对象
    else if (result && typeof result === 'object') {
      // 检查是否有data字段
      if (result.hasOwnProperty('data')) {
        const data = result.data;
        // data是数组
        if (Array.isArray(data)) {
          rawData = data;
          total = data.length;
          console.log('检测到data数组格式');
        }
        // data是分页对象
        else if (data && typeof data === 'object' && data.hasOwnProperty('records')) {
          rawData = Array.isArray(data.records) ? data.records : [];
          total = data.total || rawData.length;
          console.log('检测到分页数据格式');
        }
      }
      // 直接是分页对象
      else if (result.hasOwnProperty('records')) {
        rawData = Array.isArray(result.records) ? result.records : [];
        total = result.total || rawData.length;
        console.log('检测到直接分页格式');
      }
      // 其他对象格式，尝试转换为数组
      else {
        rawData = [result];
        total = 1;
        console.log('检测到单个对象格式');
      }
    }
    
    console.log('处理后的原始数据:', rawData);
    console.log('总数:', total);
    
    // 转换数据格式
    const convertedRecords = rawData.map(item => {
      // 如果已经是Imagepost格式，直接返回
      if (item && typeof item === 'object' && 'image_url' in item) {
        return item;
      }
      // 否则进行转换
      return convertBackendImagepost(item);
    });
    
    console.log('转换后的记录:', convertedRecords);
    
    return {
      records: convertedRecords,
      total: total
    };
  }

  /**
   * 修改图片
   */
  async updateImagepost(imagepost: Imagepost) {
    const response = await fetch('/imagepost/image', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify({
        id: imagepost.id,
        userId: imagepost.userid,
        imageId: imagepost.imageId,
        imageUrl: imagepost.image_url,
        caption: imagepost.image_description || imagepost.image_title,
        title: imagepost.image_title,
        negativePrompt: imagepost.negativePrompt,
        steps: imagepost.steps,
        cfgScale: imagepost.cfgScale,
        modelName: imagepost.modelName,
        style: imagepost.style,
        width: imagepost.width,
        height: imagepost.height
      })
    });

    if (!response.ok) {
      throw new Error(`修改图片失败: ${response.status} ${response.statusText}`);
    }

    return response.json();
  }

  /**
   * 删除图片
   */
  async deleteImagepost(imagepost: Imagepost) {
    const response = await fetch('/imagepost/delete', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify({
        id: imagepost.id,
        userId: imagepost.userid
      })
    });

    if (!response.ok) {
      throw new Error(`删除图片失败: ${response.status} ${response.statusText}`);
    }

    return response.json();
  }

  /**
   * 点赞图片
   */
  async addLike(postId: number, userId: number) {
    const response = await fetch(`/imagepost/like/${postId}?userId=${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`点赞失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '点赞失败');
    }
    return result;
  }

  /**
   * 取消点赞图片
   */
  async removeLike(postId: number, userId: number) {
    const response = await fetch(`/imagepost/like/${postId}?userId=${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`取消点赞失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '取消点赞失败');
    }
    return result;
  }

  /**
   * 检查是否点赞图片
   */
  async checkLike(postId: number, userId: number): Promise<boolean> {
    const response = await fetch(`/imagepost/like/check/${postId}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`检查点赞状态失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '检查点赞状态失败');
    }
    return result.data === true;
  }

  /**
   * 收藏图片
   */
  async addCollection(postId: number, userId: number) {
    const response = await fetch(`/imagepost/collection/${postId}?userId=${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`收藏失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '收藏失败');
    }
    return result;
  }

  /**
   * 取消收藏图片
   */
  async removeCollection(postId: number, userId: number) {
    const response = await fetch(`/imagepost/collection/${postId}?userId=${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`取消收藏失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '取消收藏失败');
    }
    return result;
  }

  /**
   * 检查是否收藏图片
   */
  async checkCollection(postId: number, userId: number): Promise<boolean> {
    const response = await fetch(`/imagepost/collection/check/${postId}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`检查收藏状态失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '检查收藏状态失败');
    }
    return result.data === true;
  }

  /**
   * 获取用户收藏的图片列表
   */
  async getCollectionByUserid(userId: number) {
    const response = await fetch(`/imagepost/collection/list?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (!response.ok) {
      throw new Error(`获取收藏列表失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code !== 1) {
      throw new Error(result.msg || '获取收藏列表失败');
    }

    // 转换数据格式
    const rawData = Array.isArray(result.data) ? result.data : [];
    const convertedRecords = rawData.map(convertBackendImagepost);

    return {
      success: true,
      data: convertedRecords,
      message: '获取收藏列表成功'
    };
  }
}

export default new ImagepostService();

// 导出便捷函数
export const fetchFavoritePosts = async (userId: number) => {
  const service = new ImagepostService();
  return service.getCollectionByUserid(userId);
};

export const toggleFavorite = async (postId: number, userId: number, isCollected: boolean) => {
  const service = new ImagepostService();
  if (isCollected) {
    return service.removeCollection(postId, userId);
  } else {
    return service.addCollection(postId, userId);
  }
};

export const checkLikeAndCollectionStatus = async (postId: number) => {
  const userInfo = localStorage.getItem('userInfo');
  let userId = 0;
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo);
      userId = user.id || 0;
    } catch (e) {
      userId = 0;
    }
  }

  if (userId === 0) {
    return { isLiked: false, isCollected: false };
  }

  const service = new ImagepostService();
  const [isLiked, isCollected] = await Promise.all([
    service.checkLike(postId, userId),
    service.checkCollection(postId, userId)
  ]);

  return { isLiked, isCollected };
};