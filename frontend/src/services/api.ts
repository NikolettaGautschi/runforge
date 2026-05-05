// src/services/apiV01.ts
import axios from 'axios';
import type { Member, TrainingPlan, DashboardStats } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v01',
  headers: { 'Content-Type': 'application/json' },
});

// --- Members ---
export const memberApi = {
  getAll: () => api.get<Member[]>('/members').then(r => r.data),
  getActive: () => api.get<Member[]>('/members/active').then(r => r.data),
  getById: (id: number) => api.get<Member>(`/members/${id}`).then(r => r.data),
  create: (data: Omit<Member, 'id' | 'joinedDate'>) =>
    api.post<Member>('/members', data).then(r => r.data),
  update: (id: number, data: Partial<Member>) =>
    api.put<Member>(`/members/${id}`, data).then(r => r.data),
  delete: (id: number) => api.delete(`/members/${id}`),
};

// --- Training Plans ---
export const planApi = {
  getAll: () => api.get<TrainingPlan[]>('/plans').then(r => r.data),
  getById: (id: number) => api.get<TrainingPlan>(`/plans/${id}`).then(r => r.data),
  create: (data: Omit<TrainingPlan, 'id' | 'createdDate'>) =>
    api.post<TrainingPlan>('/plans', data).then(r => r.data),
  update: (id: number, data: Partial<TrainingPlan>) =>
    api.put<TrainingPlan>(`/plans/${id}`, data).then(r => r.data),
  delete: (id: number) => api.delete(`/plans/${id}`),
};

// --- Statistics ---
export const statsApi = {
  getDashboard: () => api.get<DashboardStats>('/stats/dashboard').then(r => r.data),
};

export default api;
