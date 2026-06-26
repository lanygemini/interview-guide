import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

declare module 'axios' {
  interface AxiosRequestConfig {
    skipResultTransform?: boolean;
  }
}

/**
 * 后端统一响应结构
 */
export interface Result<T = unknown> {
  code: number;
  message: string;
  data: T;
}

const SUCCESS_CODE = 200;
const RESULT_BLOB_PARSE_LIMIT = 64 * 1024;

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

const instance: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
});

// ========== 请求拦截器：注入 Authorization ==========
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ========== 响应拦截器 ==========
function isRecord(value: unknown): value is Record<string, unknown> {
  return value !== null && typeof value === 'object';
}

export function isResult(value: unknown): value is Result {
  return isRecord(value)
    && typeof value.code === 'number'
    && typeof value.message === 'string';
}

export function getResultError(value: unknown): Error | null {
  if (!isResult(value) || value.code === SUCCESS_CODE) {
    return null;
  }
  return new Error(value.message || '请求失败');
}

function parseResultText(text: string): Result | null {
  const trimmed = text.trim();
  if (!trimmed.startsWith('{')) {
    return null;
  }

  try {
    const value = JSON.parse(trimmed) as unknown;
    return isResult(value) ? value : null;
  } catch {
    return null;
  }
}

function shouldTryParseBlob(blob: Blob): boolean {
  const type = blob.type.toLowerCase();
  return type.includes('json')
    || type.startsWith('text/')
    || blob.size <= RESULT_BLOB_PARSE_LIMIT;
}

export async function parseResultPayload(payload: unknown): Promise<Result | null> {
  if (isResult(payload)) {
    return payload;
  }

  if (payload instanceof Blob && shouldTryParseBlob(payload)) {
    return parseResultText(await payload.text());
  }

  if (typeof payload === 'string') {
    return parseResultText(payload);
  }

  return null;
}

export async function resolveBlobDownload(blob: Blob): Promise<Blob> {
  const result = await parseResultPayload(blob);
  if (!result) {
    return blob;
  }

  if (result.code !== SUCCESS_CODE) {
    throw new Error(result.message || '文件下载失败');
  }

  throw new Error(result.message && result.message !== 'success'
    ? result.message
    : '文件下载失败：服务端未返回文件内容');
}

async function getErrorFromResponseData(data: unknown): Promise<Error | null> {
  const result = await parseResultPayload(data);
  if (!result) {
    return null;
  }

  return new Error(result.message || '请求失败');
}

/** 判断当前 URL 是否为登录/注册接口 */
function isAuthEndpoint(url?: string): boolean {
  if (!url) return false;
  return url.endsWith('/api/auth/login') || url.endsWith('/api/auth/register');
}

instance.interceptors.response.use(
  (response) => {
    if (response.config.skipResultTransform) {
      return response;
    }

    const result = response.data as Result;

    // 检查是否是 Result 格式
    if (isResult(result)) {
      if (result.code === SUCCESS_CODE) {
        // 成功：返回 data
        response.data = result.data;
        return response;
      }
      // 失败：直接抛出 message
      return Promise.reject(new Error(result.message || '请求失败'));
    }
    
    // 非 Result 格式，直接返回
    return response;
  },
  async (error) => {
    // 有响应的情况：后端返回了结果（即使是错误）
    if (error.response) {
      const { data, config } = error.response;

      // 401 未授权 → 清 token 跳登录（排除登录/注册接口的失败场景）
      if (error.response.status === 401 && !isAuthEndpoint(config?.url)) {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
        window.location.href = '/login';
        return Promise.reject(new Error('登录已失效，请重新登录'));
      }

      // 尝试解析 Result 格式
      const responseError = await getErrorFromResponseData(data);
      if (responseError) {
        return Promise.reject(responseError);
      }
      // 响应格式不对
      return Promise.reject(new Error('请求失败，请重试'));
    }

    // 没有响应的情况：真正的网络错误或连接被重置
    const config = error.config;
    const isUpload = config && (
      config.url?.includes('/upload') ||
      config.headers?.['Content-Type']?.toString().includes('multipart')
    );

    if (isUpload) {
      return Promise.reject(new Error('上传失败，可能是网络超时或连接中断，请重试'));
    }

    return Promise.reject(new Error('网络连接失败，请检查网络'));
  }
);

export const request = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.get(url, config).then(res => res.data);
  },

  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.post(url, data, config).then(res => res.data);
  },

  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.put(url, data, config).then(res => res.data);
  },

  patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.patch(url, data, config).then(res => res.data);
  },

  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.delete(url, config).then(res => res.data);
  },

  /**
   * 文件上传
   */
  upload<T>(url: string, formData: FormData, config?: AxiosRequestConfig): Promise<T> {
    return instance.post(url, formData, {
      timeout: 300000,
      headers: { 'Content-Type': 'multipart/form-data' },
      ...config,
    }).then(res => res.data);
  },

  /**
   * 文件下载
   */
  async download(url: string, config?: AxiosRequestConfig): Promise<Blob> {
    const response = await instance.get<Blob>(url, {
      ...config,
      responseType: 'blob',
      skipResultTransform: true,
    });
    return resolveBlobDownload(response.data);
  },

  /**
   * 获取原始实例
   */
  getInstance(): AxiosInstance {
    return instance;
  },
};

/**
 * 获取错误信息
 */
export function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }
  return '未知错误';
}

export default request;
