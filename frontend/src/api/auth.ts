import request from './request';
import type { LoginRequest, LoginResponse, RegisterRequest, UserDTO } from '../types/auth';

export const authApi = {
  /** 注册 */
  register(data: RegisterRequest): Promise<UserDTO> {
    return request.post<UserDTO>('/api/auth/register', data);
  },

  /** 登录 */
  login(data: LoginRequest): Promise<LoginResponse> {
    return request.post<LoginResponse>('/api/auth/login', data);
  },

  /** 获取当前用户信息 */
  getMe(): Promise<UserDTO> {
    return request.get<UserDTO>('/api/auth/me');
  },
};
