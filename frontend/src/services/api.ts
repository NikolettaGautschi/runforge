/**
 * src/services/api.ts — HTTP client for RunForge backend API
 *
 * Provides async API methods for all CRUD operations on members, training plans,
 * and statistics. Uses Axios with baseURL pointing to the Spring Boot backend.
 *
 * All methods return Promises resolving to typed response data.
 * Errors are thrown and must be handled by the calling component.
 */

// src/services/apiV01.ts
import axios from 'axios';
import type { Member, RunningPlan, DashboardStats } from '../types';

/**
 * Axios instance configured for RunForge backend.
 * - Base URL: http://localhost:8080/api
 * - Content-Type: application/json
 * - All requests automatically include JSON serialization
 */
const api = axios.create({
  baseURL: 'http://localhost:8080/api/v01',
  headers: { 'Content-Type': 'application/json' },
});

/**
 * memberApi — API methods for member management (CRUD).
 *
 * Endpoints:
 * - GET /api/members — fetch all members
 * - GET /api/members/active — fetch active members only
 * - GET /api/members/{id} — fetch single member
 * - POST /api/members — create member
 * - PUT /api/members/{id} — update member
 * - DELETE /api/members/{id} — soft-delete member
 */
export const memberApi = {
  /**
   * Fetch all members (including inactive).
   * @returns Promise resolving to array of Member objects
   */
  getAll:  () =>
                 api.get('/members').then(r => {
                   const d = r.data;
                   return Array.isArray(d) ? d : (d && d.content) ? d.content : [];
                 }),

  /**
   * Fetch active members only (where active = true).
   * @returns Promise resolving to array of active Member objects
   */
  getActive: () => api.get<Member[]>('/members/active').then(r => r.data),

  /**
   * Fetch a single member by ID.
   * @param id - Member ID
   * @returns Promise resolving to Member object
   */
  getById: (id: number) => api.get<Member>(`/members/${id}`).then(r => r.data),

  /**
   * Create a new member.
   * @param data - Member data excluding id and joinedDate (auto-generated)
   * @returns Promise resolving to created Member with ID
   */
  create: (data: Omit<Member, 'id' | 'joinedDate'>) =>
    api.post<Member>('/members', data).then(r => r.data),

  /**
   * Update an existing member.
   * @param id - Member ID
   * @param data - Partial member data to merge
   * @returns Promise resolving to updated Member
   */
  update: (id: number, data: Partial<Member>) =>
    api.put<Member>(`/members/${id}`, data).then(r => r.data),

  /**
   * Soft-delete a member (sets active = false).
   * @param id - Member ID
   * @returns Promise resolving when delete succeeds
   */
  delete: (id: number) => api.delete(`/members/${id}`),
};

/**
 * planApi — API methods for training plan management (CRUD).
 *
 * Endpoints:
 * - GET /api/plans — fetch all plans
 * - GET /api/plans/{id} — fetch single plan
 * - POST /api/plans — create plan
 * - PUT /api/plans/{id} — update plan
 * - DELETE /api/plans/{id} — delete plan
 */
export const planApi = {
  /**
   * Fetch all training plans.
   * @returns Promise resolving to array of RunningPlan objects
   */
  getAll: () => api.get<RunningPlan[]>('/plans').then(r => r.data),

  /**
   * Fetch a single plan by ID.
   * @param id - Plan ID
   * @returns Promise resolving to RunningPlan object
   */
  getById: (id: number) => api.get<RunningPlan>(`/plans/${id}`).then(r => r.data),

  /**
   * Create a new training plan.
   * @param data - Plan data excluding id and createdDate (auto-generated)
   * @returns Promise resolving to created RunningPlan with ID
   */
  create: (data: Omit<RunningPlan, 'id' | 'createdDate'>) =>
    api.post<RunningPlan>('/plans', data).then(r => r.data),

  /**
   * Update an existing training plan.
   * @param id - Plan ID
   * @param data - Partial plan data to merge
   * @returns Promise resolving to updated RunningPlan
   */
  update: (id: number, data: Partial<RunningPlan>) =>
    api.put<RunningPlan>(`/plans/${id}`, data).then(r => r.data),

  /**
   * Delete a training plan (hard delete).
   * @param id - Plan ID
   * @returns Promise resolving when delete succeeds
   */
  delete: (id: number) => api.delete(`/plans/${id}`),
};

/**
 * statsApi — API methods for dashboard statistics.
 *
 * Endpoints:
 * - GET /api/stats/dashboard — fetch aggregated statistics
 */
export const statsApi = {
  /**
   * Fetch aggregated dashboard statistics.
   * Returns counts of members/plans/assignments and member distribution by level.
   * @returns Promise resolving to DashboardStats object
   */
  getDashboard: () => api.get<DashboardStats>('/stats/dashboard').then(r => r.data),
};

/**
 * trainingSetApi — API methods for training set management (individual workout sessions).
 *
 * Endpoints:
 * - GET /api/plans/{planId}/sets — fetch all sets for a plan
 * - GET /api/plans/{planId}/sets/type/{type} — fetch sets by type
 * - GET /api/sets/{id} — fetch single set
 * - POST /api/plans/{planId}/sets — create set
 * - PUT /api/sets/{id} — update set
 * - DELETE /api/sets/{id} — delete set
 * - POST /api/plans/{planId}/sets/reorder — reorder sets
 */
export const IntervalApi = {
  /**
   * Fetch all training sets for a plan, ordered by sequence.
   * @param planId - Training plan ID
   * @returns Promise resolving to array of Interval objects
   */
  getByPlan: (planId: number) =>
    api.get<any[] >(`/plans/${planId}/sets`).then(r => r.data),

  /**
   * Fetch a single training set by ID.
   * @param id - Training set ID
   * @returns Promise resolving to Interval object
   */
  getById: (id: number) =>
    api.get<any>(`/sets/${id}`).then(r => r.data),

  /**
   * Create a new training set in a plan.
   * Automatically assigns order if not provided.
   * @param planId - Training plan ID
   * @param data - Training set data (type, unit, quantity required)
   * @returns Promise resolving to created Interval with ID
   */
  create: (planId: number, data: any) =>
    api.post<any>(`/plans/${planId}/sets`, data).then(r => r.data),

  /**
   * Update an existing training set.
   * @param id - Training set ID
   * @param data - Partial training set data to merge
   * @returns Promise resolving to updated Interval
   */
  update: (id: number, data: Partial<any>) =>
    api.put<any>(`/sets/${id}`, data).then(r => r.data),

  /**
   * Delete a training set.
   * @param id - Training set ID
   * @returns Promise resolving when delete succeeds
   */
  delete: (id: number) =>
    api.delete(`/sets/${id}`),

  /**
   * Reorder training sets within a plan.
   * @param planId - Training plan ID
   * @param setIds - Array of set IDs in desired order
   * @returns Promise resolving when reorder succeeds
   */
  reorder: (planId: number, setIds: number[]) =>
    api.post(`/plans/${planId}/sets/reorder`, { setIds }),
};

export default api;
