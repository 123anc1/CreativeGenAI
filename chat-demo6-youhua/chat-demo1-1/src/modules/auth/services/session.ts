/**
 * 会话管理服务
 * 处理用户会话相关操作
 */

import { DeleteSessionRequestParams, DeleteSessionResponse, DeleteSessionResult } from '@/shared/types';

/**
 * 删除会话请求
 * @param userid - 用户ID
 * @param sessionid - 会话ID
 * @returns 会话删除结果
 */
export async function deleteSessionRequest({
  userid,
  sessionid,
}: DeleteSessionRequestParams): Promise<DeleteSessionResult> {
  const body: DeleteSessionRequestParams = { userid, sessionid };

  // 后端使用 @DeleteMapping("/delete")，使用 DELETE 方法并发送 JSON body
  const resp = await fetch('/session/delete', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      token: localStorage.getItem('token') || '',
    },
    body: JSON.stringify(body),
  });

  if (!resp.ok) {
    const txt = await resp.text().catch(() => '');
    const err = new Error(`deleteSession failed: ${resp.status} ${txt}`);
    (err as any).status = resp.status;
    throw err;
  }

  let data: DeleteSessionResponse | null = null;
  try {
    data = await resp.json();
  } catch (e) {
    try {
      const txt = await resp.text();
      data = txt ? { code: 200, raw: txt } : null;
    } catch (_) {
      data = null;
    }
  }

  // 提取会话ID和用户ID，优先使用响应数据，其次使用请求参数
  const sid =
    data?.sessionid || (data?.session && data.session.sessionid) || sessionid;
  const uid = data?.userid || (data?.session && data.session.userid) || userid;

  return { sessionid: sid, userid: uid, raw: data ?? undefined};
}

export default { deleteSessionRequest };