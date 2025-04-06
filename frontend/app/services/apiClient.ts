// services/apiClient.ts
import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // your backend base URL
});

// Add a request interceptor to inject the JWT token
apiClient.interceptors.request.use(
  (config) => {
    // Retrieve token from localStorage or from an AuthContext if available
    const token = localStorage.getItem("token");
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiClient;
