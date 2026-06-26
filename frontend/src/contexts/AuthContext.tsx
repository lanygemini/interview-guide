import React, { createContext, useCallback, useEffect, useState } from 'react';
import { authApi } from '../api/auth';
import type { LoginRequest, RegisterRequest, UserDTO } from '../types/auth';

interface AuthContextType {
  /** 当前登录用户（null 表示未登录） */
  user: UserDTO | null;
  /** 是否正在加载用户信息 */
  loading: boolean;
  /** 登录 */
  login: (data: LoginRequest) => Promise<void>;
  /** 注册 */
  register: (data: RegisterRequest) => Promise<UserDTO>;
  /** 退出登录 */
  logout: () => void;
  /** 是否已登录 */
  isAuthenticated: boolean;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);

  // 启动时检查 token 并拉取用户信息
  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    if (!token) {
      setLoading(false);
      return;
    }

    authApi.getMe()
      .then((userData) => {
        setUser(userData);
        localStorage.setItem('auth_user', JSON.stringify(userData));
      })
      .catch(() => {
        // token 无效，清空
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const login = useCallback(async (data: LoginRequest) => {
    const response = await authApi.login(data);
    localStorage.setItem('auth_token', response.token);
    localStorage.setItem('auth_user', JSON.stringify(response.user));
    setUser(response.user);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    const userData = await authApi.register(data);
    return userData;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        isAuthenticated: user !== null,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
