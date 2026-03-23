/**
 * 图像处理工具函数
 * 用于处理各种格式的图像数据，包括URL、base64、字节数组等
 */

/**
 * 处理图像对象，返回可用于img标签的src属性
 * @param image 图像对象，可能包含各种字段
 * @returns 处理后的图像URL字符串
 */
export function processImageSource(image: any): string {
  if (!image) return '';

  // 如果是字符串，直接返回
  if (typeof image === 'string') {
    return image;
  }

  // 检查常见的图像字段
  const possibleFields = ['image_url', 'imageUrl', 'url', 'src', 'picture', 'previewImage'];
  
  for (const field of possibleFields) {
    if (image[field]) {
      return image[field];
    }
  }

  return '';
}