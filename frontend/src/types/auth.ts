/** 登录响应 */
export interface LoginResponse {
  token: string;
  user: UserDTO;
}

/** 用户信息 */
export interface UserDTO {
  id: number;
  username: string;
  nickname: string;
}

/** 注册请求 */
export interface RegisterRequest {
  username: string;
  password: string;
  nickname?: string;
}

/** 登录请求 */
export interface LoginRequest {
  username: string;
  password: string;
}
