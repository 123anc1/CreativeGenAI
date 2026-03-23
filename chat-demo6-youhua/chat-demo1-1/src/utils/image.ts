// 图像处理工具文件

// 图像缓存类，实现 LRU 策略
class ImageCache {
  private cache = new Map<string, string>();
  private maxSize = 100; // 最大缓存数量
  
  get(key: string): string | undefined {
    const value = this.cache.get(key);
    if (value) {
      // 移动到最近使用
      this.cache.delete(key);
      this.cache.set(key, value);
    }
    return value;
  }
  
  set(key: string, value: string): void {
    // 如果已存在，先删除
    if (this.cache.has(key)) {
      this.cache.delete(key);
    }
    // 如果超过最大大小，删除最久未使用的
    if (this.cache.size >= this.maxSize) {
      const firstKey = this.cache.keys().next().value;
      if (firstKey) {
        this.cache.delete(firstKey);
      }
    }
    // 添加到缓存
    this.cache.set(key, value);
  }
  
  clear(): void {
    this.cache.clear();
  }
}

// 创建图像缓存实例
const imageCache = new ImageCache();

// 计算属性，处理返回的base64数据或URL
export const getImageSrc = (image: any): string => {
  if (!image) return '';
  
  // 生成缓存键
  const cacheKey = typeof image === 'object' ? JSON.stringify(image) : image;
  
  // 检查缓存中是否存在
  const cachedValue = imageCache.get(cacheKey);
  if (cachedValue) {
    return cachedValue;
  }
  
  // 定义可能的图片URL字段，现在包含了实际的字段名imagedata
  const possibleFields = ['imagedata', 'image_data', 'url', 'imageUrl', 'src', 'picture', 'image_url'];
  
  for (const field of possibleFields) {
    if (image[field]) {
      let imageUrl = image[field];
      
      // 如果是base64编码的图片数据
      if (typeof imageUrl === 'string' && imageUrl.startsWith('data:image')) {
        imageCache.set(cacheKey, imageUrl);
        return imageUrl;
      }
      
      // 如果是相对路径，补全协议和主机名
      if (typeof imageUrl === 'string') {
        // 检查是否是完整的URL
        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
          imageCache.set(cacheKey, imageUrl);
          return imageUrl;
        }
        
        // 检查是否是绝对路径
        if (imageUrl.startsWith('/')) {
          // 检查是否是当前域名下的路径
          const currentOrigin = window.location.origin;
          const fullUrl = currentOrigin + imageUrl;
          imageCache.set(cacheKey, fullUrl);
          return fullUrl;
        }
        
        // 如果是相对路径，尝试构造完整URL
        const currentOrigin = window.location.origin;
        const fullUrl = currentOrigin + '/' + imageUrl;
        imageCache.set(cacheKey, fullUrl);
        return fullUrl;
      }
    }
  }
  
  // 如果没有找到有效的图片字段
  const emptyResult = '';
  imageCache.set(cacheKey, emptyResult);
  return emptyResult;
};

// 将图片URL（base64或远程URL）转换为File对象的辅助函数
export const convertUrlToFile = async (url: string): Promise<File | null> => {
  try {
    // 如果是base64数据
    if (url.startsWith('data:image')) {
      const arr = url.split(',');
      const mime = arr[0].match(/:(.*?);/)![1];
      const bstr = atob(arr[1]);
      let n = bstr.length;
      const u8arr = new Uint8Array(n);
      
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      
      return new File([u8arr], `reference_image_${Date.now()}.png`, { type: mime });
    } 
    // 如果是远程URL，需要先获取图片数据
    else if (url.startsWith('http')) {
      try {
        // 尝试使用 cors 模式获取
        const response = await fetch(url, { 
          mode: 'cors',
          headers: {
            'Accept': 'image/*'
          }
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const blob = await response.blob();
        return new File([blob], `reference_image_${Date.now()}.png`, { type: blob.type });
      } catch (corsError) {
        // 如果跨域失败，尝试使用 no-cors 模式（会返回 opaque 响应，无法读取内容）
        // 或者使用图片加载方式
        console.warn('跨域获取图片失败，尝试使用图片加载方式:', corsError);
        
        // 通过 Image 和 Canvas 方式转换
        return await convertImageUrlToFileViaCanvas(url);
      }
    } 
    // 如果是本地路径，需要先获取图片数据
    else if (url.startsWith('/')) {
      // 补全为完整的URL再获取
      const fullUrl = window.location.origin + url;
      const response = await fetch(fullUrl);
      const blob = await response.blob();
      return new File([blob], `reference_image_${Date.now()}.png`, { type: blob.type });
    }
    // 其他情况，返回null
    else {
      console.warn('无法处理的图片URL格式:', url);
      return null;
    }
  } catch (error) {
    console.error('转换图片URL为File对象失败:', error);
    return null;
  }
};

// 通过 Canvas 将跨域图片转换为 File 对象
const convertImageUrlToFileViaCanvas = async (url: string): Promise<File | null> => {
  return new Promise((resolve) => {
    const img = new Image();
    img.crossOrigin = 'anonymous';
    
    img.onload = () => {
      try {
        const canvas = document.createElement('canvas');
        canvas.width = img.naturalWidth || img.width;
        canvas.height = img.naturalHeight || img.height;
        
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          console.warn('无法获取 canvas context');
          resolve(null);
          return;
        }
        
        ctx.drawImage(img, 0, 0);
        
        canvas.toBlob((blob) => {
          if (blob) {
            const file = new File([blob], `reference_image_${Date.now()}.png`, { type: 'image/png' });
            resolve(file);
          } else {
            console.warn('Canvas toBlob 返回 null');
            resolve(null);
          }
        }, 'image/png');
      } catch (error) {
        console.error('Canvas 转换图片失败:', error);
        resolve(null);
      }
    };
    
    img.onerror = () => {
      console.warn('图片加载失败，可能是跨域问题:', url);
      resolve(null);
    };
    
    img.src = url;
  });
};

// 下载图片
export const downloadImage = (image: any) => {
  if (!image.url) return;

  // 创建一个临时的a标签用于下载
  const a = document.createElement('a');
  
  a.href = image.url;
  const fileName = image.url.split('/').pop()?.split('?')[0] || `generated-image-${Date.now()}.png`;
  a.download = fileName;
  
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};

// 格式化日期
export const formatDate = (timestamp: string): string => {
  return new Date(timestamp).toLocaleString('zh-CN');
};
