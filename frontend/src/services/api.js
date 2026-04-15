import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401/403 responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// === Dashboard ===
export const getDashboardSummary = () => api.get('/reports/dashboard-summary');

// === Project Health ===
export const getProjectHealth = (projectId) => api.get(`/reports/project/${projectId}/health`);
export const getProjectSummary = () => api.get('/reports/project/summary');

// === Safety ===
export const getSafetyTrends = () => api.get('/reports/safety/trends');
export const getInspectionsSummary = () => api.get('/reports/safety/inspections-summary');

// === Finance ===
export const getBudgetVariance = (projectId) => api.get(`/reports/finance/budget-variance/${projectId}`);
export const getCashFlow = () => api.get('/reports/finance/cash-flow');

// === Resources ===
export const getResourceUtilization = () => api.get('/reports/resources/utilization');
export const getLaborAllocation = () => api.get('/reports/resources/labor-allocation');

// === Vendor ===
export const getVendorPerformance = () => api.get('/reports/vendor/performance');
export const getVendorPerformanceById = (vendorId) => api.get(`/reports/vendor/performance/${vendorId}`);
export const getVendorCompliance = () => api.get('/reports/vendor/compliance');
export const getVendorSpend = () => api.get('/reports/vendor/spend');

// === Site Engineer ===
export const getSiteEngineerPerformance = () => api.get('/reports/site-engineer/performance');
export const getSiteEngineerPerformanceById = (id) => api.get(`/reports/site-engineer/performance/${id}`);
export const getSiteProgressSummary = () => api.get('/reports/site-engineer/summary');
export const getSiteEngineerDailyLogs = () => api.get('/reports/site-engineer/daily-logs');
export const getSiteEngineerDailyLogsByEngineer = (id) => api.get(`/reports/site-engineer/daily-logs/${id}`);

// === User Analytics (IAM) ===
export const getUserAnalytics = () => api.get('/reports/users/analytics');
export const getAllUsers = () => api.get('/reports/users/all');

// === Reports ===
export const generateReport = (data) => api.post('/reports/generate', data);
export const getReport = (id) => api.get(`/reports/${id}`);
export const getReportHistory = (scope) => api.get(`/reports/history/${scope}`);
export const exportReport = (reportId) => api.post(`/reports/export/${reportId}`);

export default api;
