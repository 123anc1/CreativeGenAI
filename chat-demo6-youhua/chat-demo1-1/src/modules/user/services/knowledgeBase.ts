export interface WorldGlobal {
  id: number;
  md5: string;
  filename: string;
  size: number;
  userid: number;
}

/**
 * 获取知识库文档列表
 */
export async function getKnowledgeBaseList(userId: string): Promise<WorldGlobal[]> {
  try {
    console.log('开始请求知识库列表，用户ID:', userId);
    
    const response = await fetch(`/knowledgeBase/list?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    console.log('响应状态:', response.status, response.statusText);
    console.log('响应头:', response.headers);

    if (!response.ok) {
      throw new Error(`获取文档列表失败: ${response.status} ${response.statusText}`);
    }

    // 检查响应是否为JSON格式
    const contentType = response.headers.get('content-type');
    console.log('Content-Type:', contentType);
    
    if (!contentType || !contentType.includes('application/json')) {
      const text = await response.text();
      console.log('非JSON响应内容:', text);
      
      // 尝试检查是否是错误页面
      if (text.startsWith('<!DOCTYPE') || text.startsWith('<html>')) {
        throw new Error('服务器返回了错误页面，请检查后端接口是否正常');
      }
      
      throw new Error('服务器返回了意外的响应格式');
    }

    const result: WorldGlobal[] = await response.json();
    console.log('成功获取到知识库列表:', result);
    return result;
  } catch (error: any) {
    console.error('获取知识库列表失败:', error);
    throw error;
  }
}

/**
 * 删除知识库文档
 */
export async function deleteKnowledgeBaseDocument(fileName: string, userId: number): Promise<boolean> {
  try {
    console.log('开始删除文档:', fileName, userId);
    
    const response = await fetch(`/knowledgeBase/delete?fileName=${encodeURIComponent(fileName)}&userId=${userId}`, { 
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    console.log('删除文档响应状态:', response.status, response.statusText);

    if (!response.ok) {
      throw new Error(`删除文档失败: ${response.status} ${response.statusText}`);
    }

    return true;
  } catch (error: any) {
    console.error('删除知识库文档失败:', error);
    throw error;
  }
}