import axios from "axios";
export const API_URL = import.meta.env.VITE_API_URL || "http://localhost:5000/api";
const api = axios.create({ baseURL: API_URL, headers: {"Content-Type":"application/json"} });
api.interceptors.request.use(config => {
  const token = localStorage.getItem("garejx_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use(r=>r, err=>{
  if (err.response?.status===401) { localStorage.removeItem("garejx_token"); localStorage.removeItem("garejx_user"); window.location.href="/"; }
  return Promise.reject(err);
});
export default api;
