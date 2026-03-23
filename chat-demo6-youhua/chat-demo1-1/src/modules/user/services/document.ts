export interface UploadDocumentToSessionParams {
  file: File;
  userId: string;
  sessionId: string;
}

/**
 * 上传文件到指定会话
 */
export async function uploadDocumentToSession(
  params: UploadDocumentToSessionParams,
): Promise<boolean> {
  const formData = new FormData();
  formData.append("files", params.file); // 修改参数名为'files'
  formData.append("userId", params.userId);
  formData.append("sessionId", params.sessionId);

  try {
    const response = await fetch("/word/session", {
      method: "POST",
      body: formData,
      headers: {
        // 注意：不能在这里设置 Content-Type，因为 multipart/form-data 的边界信息由浏览器自动设置
        token: localStorage.getItem("token") || "",
      },
    });

    if (!response.ok) {
      throw new Error(`上传失败: ${response.status} ${response.statusText}`);
    }

    // 解析响应结果，后端返回的是Result类型
    const result = await response.json();

    // 检查后端返回的成功状态
    if (result && result.code !== 1) {
      // 假设code为1表示成功
      throw new Error(result?.msg || "文件上传失败");
    }

    return true;
  } catch (error: any) {
    console.error("文件上传失败:", error);
    throw error;
  }
}

/**
 * 全局上传文件
 */
export async function uploadGlobalDocument(files: File[]): Promise<boolean> {
  const formData = new FormData();

  for (const file of files) {
    formData.append("files", file); // 参数名为'files'
  }

  // 获取用户ID
  const userId = getUserIdFromStorage();
  if (!userId) {
    throw new Error("用户未登录，无法上传文件");
  }
  formData.append("userId", userId);

  try {
    // 修改接口地址为新的后端接口
    const response = await fetch("/word/load", {
      method: "POST",
      body: formData,
      headers: {
        token: localStorage.getItem("token") || "",
      },
    });

    if (!response.ok) {
      throw new Error(
        `全局上传失败: ${response.status} ${response.statusText}`,
      );
    }

    const result = await response.json();

    if (result && result.code !== 1) {
      throw new Error(result?.msg || "全局文件上传失败");
    }

    return true;
  } catch (error: any) {
    console.error("全局文件上传失败:", error);
    throw error;
  }
}

// 从 token 中解析用户信息
function parseJwt(token: string | null): Record<string, any> | null {
  if (!token) return null;
  try {
    const payload = token.split(".")[1];
    const decoded = atob(payload);
    return JSON.parse(decoded);
  } catch (e) {
    console.error("解析 token 失败:", e);
    return null;
  }
}

// 获取用户ID
function getUserIdFromStorage(): string | null {
  const token = localStorage.getItem("token");
  const payload = parseJwt(token);

  // 优先从token中获取用户ID
  if (payload?.id) return payload.id;
  if (payload?.userId) return payload.userId;
  if (payload?.userid) return payload.userid;

  // 其次从userInfo中获取
  try {
    const userInfo = localStorage.getItem("userInfo");
    if (userInfo) {
      const user: any = JSON.parse(userInfo);
      return user?.id || null;
    }
  } catch {
    // ignore
  }
  return null;
}

/**
 * 获取会话中的文档列表
 */
export async function getSessionDocuments(sessionId: string): Promise<any[]> {
  try {
    const response = await fetch(
      `/word/list?sessionId=${encodeURIComponent(sessionId)}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: localStorage.getItem("token") || "",
        },
      },
    );

    if (!response.ok) {
      throw new Error(
        `获取文档列表失败: ${response.status} ${response.statusText}`,
      );
    }

    // 根据后端实际返回格式，直接返回响应数组
    const result = await response.json();

    // 添加调试日志以查看实际返回的数据
    console.log("getSessionDocuments 返回的数据:", result);

    // 根据你提供的后端代码，后端直接返回List<WorldSession>，不需要检查Result包装
    // 所以直接返回结果
    if (Array.isArray(result)) {
      return result;
    }

    // 如果是Result格式且code为1，则返回data
    if (
      result &&
      typeof result === "object" &&
      result.code === 1 &&
      result.data
    ) {
      return result.data;
    }

    // 如果是其他格式，可能是错误情况
    console.warn("Unexpected response format:", result);
    return Array.isArray(result) ? result : [];
  } catch (error: any) {
    console.error("获取文档列表失败:", error);
    throw error;
  }
}

/**
 * 删除会话中的文档
 */
export async function deleteSessionDocument(
  fileName: string,
  userId: string,
  sessionId: string,
): Promise<boolean> {
  try {
    const response = await fetch(
      `/word/delete_session?fileName=${encodeURIComponent(fileName)}&userId=${encodeURIComponent(userId)}&sessionId=${encodeURIComponent(sessionId)}`,
      {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          token: localStorage.getItem("token") || "",
        },
      },
    );

    if (!response.ok) {
      throw new Error(
        `删除文档失败: ${response.status} ${response.statusText}`,
      );
    }

    const result = await response.json();

    console.log("deleteSessionDocument 返回的数据:", result);

    // 检查后端是否成功返回
    if (result && result.code !== 1) {
      console.error("后端删除操作返回错误:", result);
      throw new Error(result?.msg || "删除文档失败");
    }

    return true;
  } catch (error: any) {
    console.error("删除文档失败:", error);
    throw error;
  }
}
