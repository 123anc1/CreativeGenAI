// API 服务文件，处理所有网络请求

// 基础请求函数
async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const defaultOptions: RequestInit = {
    headers: {
      token: localStorage.getItem('token') || '',
      'Content-Type': 'application/json'
    },
    ...options
  };

  try {
    const response = await fetch(url, defaultOptions);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}, message: ${response.statusText}`);
    }
    
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return await response.json();
    } else {
      return await response.text() as unknown as T;
    }
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// 模型相关 API
export const modelApi = {
  // 获取基础模型列表
  getBaseModels: async () => {
    return request<any>('/model/basemodels', {
      method: 'GET'
    });
  },
  
  // 获取风格模型列表
  getStyleModels: async () => {
    return request<any>('/model/stylemodels', {
      method: 'GET'
    });
  },
  
  // 获取基础模型参数
  getBaseModelParams: async (id: string) => {
    return request<any>(`/model/basemodels/${id}`, {
      method: 'GET'
    });
  },
  
  // 获取风格模型参数
  getStyleModelParams: async (id: string) => {
    return request<any>(`/model/stylemodels/${id}`, {
      method: 'GET'
    });
  }
};

// 图像相关 API
export const imageApi = {
  // 获取用户图像列表
  getUserImages: async (userId: string) => {
    return request<any[]>('/image/list', {
      method: 'POST',
      headers: {
        token: localStorage.getItem('token') || '',
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: `userid=${userId}`
    });
  },
  
  // 异步生成图像
  generateImage: async (formData: FormData) => {
    return request<any>('/image/async_text_to_image', {
      method: 'POST',
      headers: {
        token: localStorage.getItem('token') || ''
        // 注意：FormData 不需要设置 Content-Type
      },
      body: formData
    });
  },
  
  // 获取任务状态
  getTaskStatus: async (taskId: string) => {
    return request<any>(`/image/task_status/${taskId}`, {
      method: 'GET'
    });
  }
};

// 轮询服务类
export class PollingService {
  private timer: number | null = null;
  private pollCount = 0;
  private maxPollCount = 3600; // 最大轮询次数 (2小时)
  private startTime = 0;
  private baseInterval = 2000; // 基础轮询间隔
  private maxInterval = 30000; // 最大轮询间隔
  private interval = this.baseInterval;
  
  // 开始轮询
  start<T>(
    taskId: string,
    fetchFn: (taskId: string) => Promise<T>,
    onUpdate: (data: T) => boolean, // 返回 true 表示停止轮询
    onError: (error: any) => void = console.error
  ): void {
    // 停止之前的轮询
    this.stop();
    
    // 重置计数器
    this.pollCount = 0;
    this.startTime = Date.now();
    this.interval = this.baseInterval;
    
    const poll = async () => {
      // 增加轮询计数
      this.pollCount++;
      
      console.log(`第${this.pollCount}次轮询任务状态，任务ID: ${taskId}`);
      
      // 检查是否超过最大轮询次数
      if (this.pollCount > this.maxPollCount) {
        console.warn('轮询次数超限，自动停止');
        this.stop();
        return;
      }
      
      // 检查是否超过2小时
      const elapsedTime = Date.now() - this.startTime;
      if (elapsedTime > 2 * 60 * 60 * 1000) { // 2小时
        console.warn('轮询时间超限，自动停止');
        this.stop();
        return;
      }

      try {
        const result = await fetchFn(taskId);
        const shouldStop = onUpdate(result);
        
        if (shouldStop) {
          this.stop();
        } else {
          // 指数退避：如果任务仍在进行中，增加轮询间隔
          this.interval = Math.min(this.interval * 1.5, this.maxInterval);
          this.timer = window.setTimeout(poll, this.interval);
        }
      } catch (error) {
        onError(error);
        // 网络错误时，使用基础间隔重试
        this.interval = this.baseInterval;
        this.timer = window.setTimeout(poll, this.interval);
      }
    };
    
    // 开始轮询
    this.timer = window.setTimeout(poll, this.interval);
  }
  
  // 停止轮询
  stop(): void {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
  }
  
  // 检查是否正在轮询
  isPolling(): boolean {
    return this.timer !== null;
  }
}
