import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

export function useSession() {
  const router = useRouter();
  const route = useRoute();
  
  const sessionId = ref<string | null>(null);
  const sessionName = ref<string>('');

  const getUserInfo = () => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      try {
        return JSON.parse(userInfo);
      } catch {
        return null;
      }
    }
    return null;
  };

  const createSession = async () => {
    try {
      const userInfo = getUserInfo();
      const userId = userInfo?.id;
      
      if (!userId) {
        sessionId.value = `session-${Date.now()}`;
        sessionName.value = '未命名项目';
        router.push(`/canvas/${sessionId.value}`);
        return;
      }
      
      const response = await fetch('/canvas/session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          token: localStorage.getItem('token') || ''
        },
        body: JSON.stringify({ uid: userId, text: '未命名项目' })
      });

      const result = await response.json();
      if (result.code === 1) {
        sessionId.value = result.data.sessionId;
        sessionName.value = result.data.text;
        router.push(`/canvas/${sessionId.value}`);
      } else {
        sessionId.value = `session-${Date.now()}`;
        sessionName.value = '未命名项目';
        router.push(`/canvas/${sessionId.value}`);
      }
    } catch {
      sessionId.value = `session-${Date.now()}`;
      sessionName.value = '未命名项目';
      router.push(`/canvas/${sessionId.value}`);
    }
  };

  const loadSessionImages = async (sid: string, addLayerFn: (url: string, width: number, height: number, id: string | number | null) => void) => {
    try {
      const url = `/canvas/session/list/image?sessionId=${sid}`;
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          token: localStorage.getItem('token') || ''
        }
      });

      const result = await response.json();
      if (result.code === 1) {
        const imageDataList = result.data || [];
        
        for (const imageData of imageDataList) {
          const imageUrl = imageData.imageUrl;
          const imageId = imageData.id;
          const cleanUrl = imageUrl.replace(/[`]/g, '').trim();
          if (cleanUrl) {
            await new Promise<void>((resolve) => {
              const img = new Image();
              img.onload = () => {
                addLayerFn(cleanUrl, img.width, img.height, imageId);
                resolve();
              };
              img.onerror = () => {
                resolve();
              };
              img.src = cleanUrl;
            });
          }
        }
      }
    } catch {}
  };

  const loadSession = async (sid: string, addLayerFn: (url: string, width: number, height: number, id: string | number | null) => void) => {
    try {
      await loadSessionImages(sid, addLayerFn);
      const userInfo = getUserInfo();
      const userId = userInfo?.id;
      if (userId) {
        const response = await fetch(`/canvas/session/list?userId=${userId}`, {
          method: 'GET',
          headers: {
            token: localStorage.getItem('token') || ''
          }
        });
        const result = await response.json();
        if (result.code === 1) {
          const sessions = result.data || [];
          const session = sessions.find((s: any) => s.sessionId === sid);
          sessionName.value = session?.text || '未命名项目';
        } else {
          sessionName.value = '未命名项目';
        }
      } else {
        sessionName.value = '未命名项目';
      }
    } catch {
      sessionName.value = '未命名项目';
    }
  };

  const initSession = async (addLayerFn: (url: string, width: number, height: number, id: string | number | null) => void) => {
    const routeSessionId = route.params.sessionId as string;
    if (routeSessionId) {
      sessionId.value = routeSessionId;
      await loadSession(routeSessionId, addLayerFn);
    } else {
      await createSession();
    }
  };

  return {
    sessionId,
    sessionName,
    getUserInfo,
    createSession,
    loadSessionImages,
    loadSession,
    initSession
  };
}
