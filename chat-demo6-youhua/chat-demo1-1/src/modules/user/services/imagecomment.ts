/**
 * 图片评论服务
 */

export interface ImageComment {
  id?: number;
  postId: number;
  userId?: number;
  userName?: string;
  content: string;
  parentId?: number | null;
  replyUserId?: number | null;
  replyUserName?: string | null;
  likeCount?: number;
  status?: number;
  createdAt?: string;
  updatedAt?: string;
  replies?: ImageComment[];
  replyCount?: number;
}

export interface CommentParam {
  postId: number;
  page?: number;
  pageSize?: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  page?: number;
  pageSize?: number;
}


class ImageCommentService {
  /**
   * POST表单请求包装器
   */
  private async postForm(path: string, params: URLSearchParams) {
    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded',
      token: localStorage.getItem('token') || ''
    };

    try {
      console.log('[ImageCommentService] POST', path, params.toString(), headers);
      const response = await fetch(path, {
        method: 'POST',
        headers,
        body: params.toString()
      });

      const text = await response.text();
      console.log('[ImageCommentService] Response', path, response.status, text);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${text}`);
      }

      try {
        return JSON.parse(text);
      } catch (e) {
        // 不是 JSON，直接返回原始文本
        return text;
      }
    } catch (err) {
      console.error('[ImageCommentService] 请求出错', path, err);
      throw err;
    }
  }
  /**
   * 发布新评论
   */
  async addComment(comment: ImageComment) {
    const params = new URLSearchParams();
    const userInfo = localStorage.getItem('userInfo');
    let userId: string | null = localStorage.getItem('userId');
    try {
      if (userInfo) userId = JSON.parse(userInfo).id;
    } catch {
      // ignore
    }

    if (userId) params.append('userid', String(userId));
    params.append('postid', String(comment.postId));
    params.append('content', comment.content);
    if (comment.parentId !== undefined && comment.parentId !== null) {
      params.append('parentid', String(comment.parentId));
    }

    const json = await this.postForm('/imagepost/comment/add', params);

    if (json && typeof json === 'object' && 'code' in json) {
      if (json.code !== 1) throw new Error(json.msg || '发布评论失败');
      return json.data;
    }

    return json;
  }

  /**
   * 回复评论
   */
  async replyComment(comment: ImageComment) {
    const params = new URLSearchParams();
    const userInfo = localStorage.getItem('userInfo');
    let userId: string | null = localStorage.getItem('userId');
    try {
      if (userInfo) userId = JSON.parse(userInfo).id;
    } catch {
      // ignore
    }

    if (userId) params.append('userid', String(userId));
    params.append('postid', String(comment.postId));
    params.append('content', comment.content);
    params.append('parentid', String(comment.parentId));

    const json = await this.postForm('/imagepost/comment/reply', params);
    if (json && typeof json === 'object' && 'code' in json) {
      if (json.code !== 1) throw new Error(json.msg || '回复评论失败');
      return json.data;
    }

    return json;
  }

  /**
   * 获取评论列表（分页）
   */
  async getComments(postId: number, page: number = 1, pageSize: number = 10) {
    const params = new URLSearchParams();
    params.append('postid', String(postId));
    params.append('page', String(page));
    params.append('pagesize', String(pageSize));

    const json = await this.postForm('/imagepost/comment/list', params);

    if (json && typeof json === 'object') {
      if ('code' in json) {
        if (json.code !== 1) throw new Error(json.msg || '获取评论列表失败');
        return json.data as PageResult<ImageComment>;
      }

      // 处理后端直接返回 { total, rows: [...] } 的情况
      if ('rows' in json && Array.isArray((json as any).rows)) {
        return {
          records: (json as any).rows as ImageComment[],
          total: (json as any).total || ((json as any).rows.length || 0),
          page: page,
          pageSize: pageSize
        } as PageResult<ImageComment>;
      }

      // 直接返回 PageResult（records 字段）
      return json as PageResult<ImageComment>;
    }

    throw new Error('服务器返回格式异常');
  }

  /**
   * 获取评论回复列表
   */
  async getReplies(parentId: number) {
    const params = new URLSearchParams();
    params.append('parentid', String(parentId));

    const json = await this.postForm('/imagepost/comment/replies', params);
    if (json && typeof json === 'object') {
      if ('code' in json) {
        if (json.code !== 1) throw new Error(json.msg || '获取回复列表失败');
        return json.data as ImageComment[];
      }
      return json as ImageComment[];
    }

    throw new Error('服务器返回格式异常');
  }

  /**
   * 删除评论
   */
  async deleteComment(commentId: number) {
    const params = new URLSearchParams();
    const userInfo = localStorage.getItem('userInfo');
    let userId: string | null = localStorage.getItem('userId');
    try {
      if (userInfo) userId = JSON.parse(userInfo).id;
    } catch {
      // ignore
    }

    params.append('commentid', String(commentId));
    if (userId) params.append('userid', String(userId));

    const json = await this.postForm('/imagepost/comment/delete', params);
    if (json && typeof json === 'object' && 'code' in json) {
      if (json.code !== 1) throw new Error(json.msg || '删除评论失败');
      return json.data;
    }

    return json;
  }

  /**
   * 点赞评论相关方法
   */
  async likeComment(commentId: number) {
    const params = new URLSearchParams();
    params.append('commentid', String(commentId));

    const json = await this.postForm('/imagepost/comment/like', params);
    if (json && typeof json === 'object' && 'code' in json) {
      if (json.code !== 1) throw new Error(json.msg || '点赞失败');
      return json.data;
    }

    return json;
  }

  /**
   * 取消点赞评论
   */
  async unlikeComment(commentId: number) {
    const params = new URLSearchParams();
    params.append('commentid', String(commentId));

    const json = await this.postForm('/imagepost/comment/unlike', params);
    if (json && typeof json === 'object' && 'code' in json) {
      if (json.code !== 1) throw new Error(json.msg || '取消点赞失败');
      return json.data;
    }

    return json;
  }
}

export default new ImageCommentService();
