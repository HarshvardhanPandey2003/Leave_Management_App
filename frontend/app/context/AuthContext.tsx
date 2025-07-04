"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useMemo,
  useCallback,
  ReactNode,
} from "react";
import {jwtDecode} from "jwt-decode";

interface JWTPayload {
  sub: string;
  name: string;
  email: string;
  role: string;
  exp: number;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (token: string) => void;
  logout: () => void;
  checkAuth: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const useProvideAuth = (): AuthContextType => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const decodeToken = (token: string): User | null => {
    try {
      const decoded = jwtDecode<JWTPayload>(token);
      return {
        id: decoded.sub,
        name: decoded.name,
        email: decoded.email,
        role: decoded.role,
      };
    } catch (error) {
      console.error("Invalid JWT token:", error);
      return null;
    }
  };

  // Use useCallback to stabilize the function reference
  const checkAuth = useCallback(() => {
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      const decodedUser = decodeToken(storedToken);
      if (decodedUser) {
        setUser(decodedUser);
        setToken(storedToken);
      } else {
        localStorage.removeItem("token");
        setUser(null);
        setToken(null);
      }
    } else {
      setUser(null);
      setToken(null);
    }
    setLoading(false);
  }, []); // No dependencies needed as it only uses localStorage and setState

  // Use useCallback to stabilize the function reference
  const login = useCallback((newToken: string) => {
    localStorage.setItem("token", newToken);
    const decodedUser = decodeToken(newToken);
    setUser(decodedUser);
    setToken(newToken);
  }, []);

  // Use useCallback to stabilize the function reference
  const logout = useCallback(() => {
    localStorage.removeItem("token");
    setUser(null);
    setToken(null);
  }, []);

  // Fixed: Added checkAuth to dependency array
  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  // Fixed: Added login, logout, and checkAuth to dependency array
  return useMemo(
    () => ({
      user,
      token,
      loading,
      login,
      logout,
      checkAuth,
    }),
    [user, token, loading, login, logout, checkAuth]
  );
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const auth = useProvideAuth();

  if (auth.loading) {
    return (
      <div
        style={{
          minHeight: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        Loading...
      </div>
    );
  }

  return (
    <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context)
    throw new Error("useAuth must be used within an AuthProvider");
  return context;
};
