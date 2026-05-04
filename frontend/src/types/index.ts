// src/types/index.ts

export type RunningLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export interface Member {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  level: RunningLevel;
  joinedDate: string;
  active: boolean;
}

export interface TrainingPlan {
  id: number;
  name: string;
  description: string;
  level: RunningLevel;
  durationWeeks: number;
  weeklyKm: number;
  createdDate: string;
}

export interface PlanAssignment {
  id: number;
  member: Member;
  trainingPlan: TrainingPlan;
  startDate: string;
  endDate: string;
  completed: boolean;
}

export interface DashboardStats {
  totalMembers: number;
  activeMembers: number;
  totalPlans: number;
  activeAssignments: number;
  completedAssignments: number;
  membersByLevel: Record<RunningLevel, number>;
}

export const LEVEL_LABELS: Record<RunningLevel, string> = {
  BEGINNER: 'Einsteiger',
  INTERMEDIATE: 'Fortgeschritten',
  ADVANCED: 'Experte',
};

export const LEVEL_COLORS: Record<RunningLevel, string> = {
  BEGINNER: '#10b981',
  INTERMEDIATE: '#3b82f6',
  ADVANCED: '#8b5cf6',
};
