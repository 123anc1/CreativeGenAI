import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Imagepost, Imagepostparam } from '@/shared/types';
import imagepostService from '@/modules/user/services/imagepost';

export function useImageData() {
  const router = useRouter();

  const SEARCH_KEY = 'image_sharing_search_key';

  const getSavedSearch = (): string => {
    try {
      return sessionStorage.getItem(SEARCH_KEY) || '';
    } catch {
      return '';
    }
  };

  const saveSearch = (keyword: string) => {
    try {
      if (keyword && keyword.trim()) {
        sessionStorage.setItem(SEARCH_KEY, keyword.trim());
      } else {
        sessionStorage.removeItem(SEARCH_KEY);
      }
    } catch {}
  };

  // 数据响应式变量
  const posts = ref<Imagepost[]>([]);
  const loading = ref(false);
  const currentPage = ref(1);
  const searchTitle = ref(getSavedSearch());
  const pageSize = 20; // 每页显示数量
  const error = ref<string | null>(null);
  const allLoaded = ref(false);
  
  // 调试状态追踪
  const loadCount = ref(0);
  const lastLoadTime = ref<number | null>(null);
  
  /**
   * 获取图片列表数据
   * @param page 页码，默认为1
   * @param isLoadMore 是否为加载更多，默认为false
   */
  const fetchPosts = async (page: number = 1, isLoadMore: boolean = false) => {
    if (loading.value) return;

    loading.value = true;
    loadCount.value++;
    lastLoadTime.value = Date.now();

    // 清除之前的错误
    if (!isLoadMore) {
      error.value = null;
    }

    try {
      // 再次检查 token
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('未登录或登录已过期');
      }

      // 获取用户ID
      let userId: number | undefined;
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        try {
          const user = JSON.parse(userInfo);
          userId = user.id || undefined;
        } catch (e) {
          userId = undefined;
        }
      }

      let response;

      // 如果有搜索关键字，使用新的搜索API
      if (searchTitle.value && searchTitle.value.trim()) {
        response = await imagepostService.search(searchTitle.value.trim(), page, pageSize);
      } else {
        // 否则使用普通的列表API
        const params: Imagepostparam = {
          page: page,
          size: pageSize,
          title: undefined
        };
        response = await imagepostService.getImagepostList(params, userId);
      }

      // 检查响应是否有效
      if (!response) {
        throw new Error('获取响应失败');
      }

      const newPosts = response.records || [];

      if (isLoadMore) {
        if (newPosts.length === 0) {
          allLoaded.value = true;
        } else {
          // 最简单的数据追加
          posts.value = posts.value.concat(newPosts);
          currentPage.value = page;
        }
      } else {
        posts.value = newPosts;
        currentPage.value = page;
        allLoaded.value = newPosts.length < pageSize;
      }

    } catch (err) {
      console.error('获取图片列表失败:', err);
      error.value = err instanceof Error ? err.message : '未知错误';

      // 确保即使出错也有明确的状态
      if (!isLoadMore) {
        posts.value = [];
      }

      // 如果是认证错误，重定向到登录页面
      const errorMessage = err instanceof Error ? err.message : '';
      if (errorMessage.includes('未登录') || errorMessage.includes('登录已过期') || errorMessage.includes('401')) {
        setTimeout(() => {
          router.push('/login');
        }, 1000);
      }
    } finally {
      loading.value = false;
    }
  };

  const resetAndFetch = (resetSearch: boolean = false) => {
    error.value = null;
    allLoaded.value = false;
    loadCount.value = 0;
    loading.value = false;
    if (resetSearch) {
      searchTitle.value = '';
    }
    fetchPosts(1);
  };

  const resetSearchState = () => {
    searchTitle.value = '';
    resetAndFetch(false);
  };

  const handleSearch = (keyword?: string) => {
    if (keyword !== undefined) {
      searchTitle.value = keyword;
      saveSearch(keyword);
    }
    resetAndFetch(false);
  };

  const retryLoad = () => {
    resetAndFetch(false);
  };

  const loadMore = () => {
    if (!loading.value && !allLoaded.value) {
      fetchPosts(currentPage.value + 1, true);
    }
  };

  const checkAuthAndLoad = () => {
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    resetAndFetch(false);
  };

  return {
    posts,
    loading,
    currentPage,
    searchTitle,
    error,
    allLoaded,
    loadCount,
    lastLoadTime,
    fetchPosts,
    resetAndFetch,
    resetSearchState,
    handleSearch,
    retryLoad,
    loadMore,
    checkAuthAndLoad
  };
}