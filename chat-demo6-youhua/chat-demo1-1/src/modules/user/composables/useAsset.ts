import { ref, type Ref } from 'vue';
import {  type Asset, type AssetState, type ImageSaveRequest } from '../types/canvas';

export function useAsset() {
  const assetState: AssetState = {
    showAssetsModal: ref<boolean>(false),
    loadingAssets: ref<boolean>(false),
    userAssets: ref<Asset[]>([]),
    selectedAssets: ref<Asset[]>([])
  };

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

  const openAssetsModal = async () => {
    assetState.showAssetsModal.value = true;
    assetState.selectedAssets.value = [];
    await loadUserAssets();
  };

  const closeAssetsModal = () => {
    assetState.showAssetsModal.value = false;
    assetState.selectedAssets.value = [];
  };

  const loadUserAssets = async () => {
    assetState.loadingAssets.value = true;
    try {
      const userInfo = getUserInfo();
      const userIdStr = userInfo?.id;
      
      if (!userIdStr) {
        assetState.userAssets.value = [];
        return;
      }

      const response = await fetch('/image/list', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          token: localStorage.getItem('token') || ''
        },
        body: `userid=${encodeURIComponent(userIdStr)}`
      });

      if (!response.ok) {
        throw new Error(`请求失败: ${response.status}`);
      }

      const contentType = response.headers.get('content-type');
      let result;
      
      if (contentType && contentType.includes('application/json')) {
        result = await response.json();
      } else {
        const textResponse = await response.text();
        if (!textResponse || textResponse.trim() === '') {
          assetState.userAssets.value = [];
          return;
        }
        try {
          result = JSON.parse(textResponse);
        } catch {
          assetState.userAssets.value = [];
          return;
        }
      }

      const imagesData = Array.isArray(result) ? result : (result.images || []);
      
      assetState.userAssets.value = imagesData.map((img: any) => ({
        id: img.id,
        userid: img.userid,
        url: img.imagedata,
        prompt: img.prompt || img.description || '无提示词',
        timestamp: img.updatedat ?? img.createdat
      }));
    } catch {
      assetState.userAssets.value = [];
    } finally {
      assetState.loadingAssets.value = false;
    }
  };

  const toggleAssetSelection = (asset: Asset) => {
    const index = assetState.selectedAssets.value.findIndex(a => a.id === asset.id);
    if (index > -1) {
      assetState.selectedAssets.value.splice(index, 1);
    } else {
      assetState.selectedAssets.value.push(asset);
    }
  };

  const saveImagesToServer = async (images: ImageSaveRequest[], sessionId: Ref<string | null>) => {
    if (!sessionId.value) {
      return [];
    }

    try {
      const userInfo = getUserInfo();
      const userId = userInfo?.id;
      
      const imageBase64Array: string[] = [];
      const imageUrlArray: string[] = [];
      
      images.forEach(image => {
        if (image.imageBase64) {
          imageBase64Array.push(image.imageBase64);
        }
        if (image.imageUrl) {
          imageUrlArray.push(image.imageUrl);
        }
      });
      
      const response = await fetch('/canvas/image', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          token: localStorage.getItem('token') || ''
        },
        body: JSON.stringify({
          uid: userId,
          sessionId: sessionId.value,
          imageBase64: imageBase64Array,
          imageUrl: imageUrlArray
        })
      });

      const result = await response.json();
      if (result.code === 1) {
        if (result.data && Array.isArray(result.data)) {
          return result.data.map((item: any) => item.imageUrl);
        } else if (result.data && result.data.imageBackUrl) {
          return result.data.imageBackUrl;
        } else {
          return [];
        }
      } else {
        return [];
      }
    } catch {
      return [];
    }
  };

  return {
    assetState,
    getUserInfo,
    openAssetsModal,
    closeAssetsModal,
    loadUserAssets,
    toggleAssetSelection,
    saveImagesToServer
  };
}
