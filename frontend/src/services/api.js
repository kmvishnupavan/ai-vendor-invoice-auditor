import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    let friendlyMessage = 'An unexpected error occurred while communicating with the server.';
    if (error.response) {
      if (error.response.data && error.response.data.message) {
        friendlyMessage = error.response.data.message;
      } else if (error.response.status === 404) {
        friendlyMessage = 'Requested resource was not found on the server.';
      } else if (error.response.status === 503) {
        friendlyMessage = 'AI extraction service is temporarily unavailable. Please try again.';
      }
    } else if (error.request) {
      friendlyMessage = 'Cannot connect to the backend server. Please ensure Spring Boot is running on port 8080.';
    }
    const enhancedError = new Error(friendlyMessage);
    enhancedError.originalError = error;
    enhancedError.status = error.response?.status;
    enhancedError.data = error.response?.data;
    return Promise.reject(enhancedError);
  }
);

export const api = {
  submitAudit: async (auditData) => {
    const response = await apiClient.post('/api/audits', auditData);
    return response.data;
  },

  fetchSummary: async () => {
    const response = await apiClient.get('/api/audits/summary');
    return response.data;
  },

  fetchAuditHistory: async () => {
    const response = await apiClient.get('/api/audits');
    return response.data;
  },

  fetchAuditById: async (logId) => {
    const response = await apiClient.get(`/api/audits/${encodeURIComponent(logId)}`);
    return response.data;
  },

  fetchAllPurchaseOrders: async () => {
    const response = await apiClient.get('/api/purchase-orders');
    return response.data;
  },

  fetchPurchaseOrderByPoId: async (poId) => {
    const response = await apiClient.get(`/api/purchase-orders/${encodeURIComponent(poId)}`);
    return response.data;
  },

  fetchSystemStatus: async () => {
    const response = await apiClient.get('/api/system/status');
    return response.data;
  },
};

export default api;
