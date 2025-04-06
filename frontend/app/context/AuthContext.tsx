// app/context/AuthContext.tsx
"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useMemo,
  ReactNode,
} from "react";
import {jwtDecode} from "jwt-decode";

// Define an interface for the JWT payload.
// Adjust the fields based on what your JWT token contains.
interface JWTPayload {
  sub: string; // user id
  name: string;
  email: string;
  role: string;
  exp: number;
  // add other claims if needed
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

  // Helper function to decode the token and extract user info.
  const decodeToken = (token: string): User | null => {
    try {
      const decoded = jwtDecode<JWTPayload>(token);
      // Map the JWT payload fields to the User interface
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

  // Check for a token in localStorage and update state accordingly.
  const checkAuth = () => {
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
  };

  // Called after a successful login to store the token.
  const login = (newToken: string) => {
    localStorage.setItem("token", newToken);
    const decodedUser = decodeToken(newToken);
    setUser(decodedUser);
    setToken(newToken);
  };

  // Clears the token and user data upon logout.
  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    setToken(null);
  };

  useEffect(() => {
    checkAuth();
  }, []);

  return useMemo(
    () => ({
      user,
      token,
      loading,
      login,
      logout,
      checkAuth,
    }),
    [user, token, loading]
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
