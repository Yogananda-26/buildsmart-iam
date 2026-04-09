import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { jwtDecode } from 'jwt-decode';
import API from '../api/axiosInstance';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  const decodeToken = useCallback((jwt) => {
    try {
      const decoded = jwtDecode(jwt);
      // Check if token is expired
      if (decoded.exp * 1000 < Date.now()) {
        return null;
      }
      return {
        userId: decoded.userId,
        email: decoded.sub,
        role: decoded.role,
        name: decoded.name,
      };
    } catch {
      return null;
    }
  }, []);

  useEffect(() => {
    if (token) {
      const decoded = decodeToken(token);
      if (decoded) {
        setUser(decoded);
      } else {
        // Token expired or invalid
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
      }
    }
    setLoading(false);
  }, [token, decodeToken]);

  const login = (authResponse) => {
    const { token: jwt } = authResponse;
    localStorage.setItem('token', jwt);
    setToken(jwt);
    const decoded = decodeToken(jwt);
    setUser(decoded);
    localStorage.setItem('user', JSON.stringify(decoded));
  };

  const logout = async () => {
    try {
      await API.post('/api/auth/logout');
    } catch (err) {
      // Even if logout API fails, clear local state
      console.error('Logout API error:', err);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      setToken(null);
      setUser(null);
    }
  };

  const isAuthenticated = !!user && !!token;

  const hasRole = (role) => {
    return user?.role === role;
  };

  const value = {
    user,
    token,
    login,
    logout,
    isAuthenticated,
    hasRole,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
