/**
 * src/pages/DashboardPage.tsx — Dashboard page with statistics and charts
 *
 * Displays aggregated statistics for the running group:
 * - Four stat cards: total/active members, plans count, active assignments
 * - Pie chart: member distribution by running level
 * - Bar chart: comparison of active vs completed assignments
 *
 * Uses TanStack Query to fetch statistics from /api/stats/dashboard.
 * Charts rendered with Recharts library and custom color scheme.
 */

import { useQuery } from '@tanstack/react-query';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, BarChart, Bar, XAxis, YAxis } from 'recharts';
import { statsApi } from '../services/api';
import { LEVEL_LABELS, LEVEL_COLORS, type RunningLevel } from '../types';

/**
 * DashboardPage component — Renders dashboard statistics and charts.
 *
 * @returns JSX element or loading/error state
 */
export default function DashboardPage() {
  /**
   * Fetch dashboard statistics using TanStack Query.
   * - queryKey: ['stats'] — cache key for invalidation
   * - queryFn: statsApi.getDashboard — async function to fetch data
   * - staleTime: 30s (set in App.tsx defaultOptions)
   */
  const { data: stats, isLoading, error } = useQuery({
    queryKey: ['stats'],
    queryFn: statsApi.getDashboard,
  });

  // Loading state
  if (isLoading) return <div className="loading">Lade Dashboard...</div>;

  // Error state (backend might be down)
  if (error) return <div className="loading">Fehler beim Laden — läuft das Backend? (Port 8080)</div>;

  // No data
  if (!stats) return null;

  /**
   * Transform membersByLevel distribution into chart-ready format.
   * Maps level enum (BEGINNER, etc.) to German labels and colors.
   */
  const levelData = stats.membersByLevel
    ? Object.entries(stats.membersByLevel).map(([key, value]) => ({
        name: LEVEL_LABELS[key as RunningLevel] ?? key,
        value,
        color: LEVEL_COLORS[key as RunningLevel] ?? '#888',
      }))
    : [];

  /**
   * Prepare data for assignment status bar chart.
   * Shows active (incomplete) vs completed assignments side-by-side.
   */
  const assignmentData = [
    { name: 'Aktiv', value: stats.activeAssignments, color: '#3b82f6' },
    { name: 'Abgeschlossen', value: stats.completedAssignments, color: '#10b981' },
  ];

  return (
    <div>
      {/* Page Header */}
      <div className="page-header">
        <div>
          <div className="page-title">Dashboard</div>
          <div className="page-subtitle">Übersicht der runforge</div>
        </div>
      </div>

      {/* Statistics Cards Grid */}
      <div className="stats-grid">
        {/* Total Members Card */}
        <div className="stat-card">
          <div className="stat-label">Mitglieder gesamt</div>
          <div className="stat-value blue">{stats.totalMembers}</div>
        </div>

        {/* Active Members Card */}
        <div className="stat-card">
          <div className="stat-label">Aktive Mitglieder</div>
          <div className="stat-value green">{stats.activeMembers}</div>
        </div>

        {/* Training Plans Card */}
        <div className="stat-card">
          <div className="stat-label">Trainingspläne</div>
          <div className="stat-value purple">{stats.totalPlans}</div>
        </div>

        {/* Active Assignments Card */}
        <div className="stat-card">
          <div className="stat-label">Aktive Zuweisungen</div>
          <div className="stat-value amber">{stats.activeAssignments}</div>
        </div>
      </div>

      {/* Charts Grid */}
      <div className="charts-grid">
        {/* Pie Chart: Members by Level */}
        <div className="card">
          <div className="card-title">Mitglieder nach Level</div>
          {levelData.length > 0 ? (
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie data={levelData} cx="50%" cy="50%" innerRadius={60} outerRadius={90} paddingAngle={4} dataKey="value">
                  {levelData.map((entry, i) => <Cell key={i} fill={entry.color} />)}
                </Pie>
                <Tooltip
                  contentStyle={{ background: '#1e2535', border: '1px solid #2a3349', borderRadius: 8, fontSize: 13 }}
                  formatter={(val, name) => [val, name]}
                />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="empty">Keine Daten</div>
          )}
          {/* Legend with color indicators */}
          <div style={{ display: 'flex', gap: 16, justifyContent: 'center', marginTop: 8 }}>
            {levelData.map(d => (
              <span key={d.name} style={{ fontSize: 12, color: '#8b98b1', display: 'flex', alignItems: 'center', gap: 6 }}>
                <span style={{ width: 10, height: 10, borderRadius: '50%', background: d.color, display: 'inline-block' }} />
                {d.name} ({d.value})
              </span>
            ))}
          </div>
        </div>

        {/* Bar Chart: Assignment Status */}
        <div className="card">
          <div className="card-title">Trainingszuweisungen</div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={assignmentData} barSize={48}>
              <XAxis dataKey="name" tick={{ fill: '#8b98b1', fontSize: 13 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#8b98b1', fontSize: 12 }} axisLine={false} tickLine={false} />
              <Tooltip
                contentStyle={{ background: '#1e2535', border: '1px solid #2a3349', borderRadius: 8, fontSize: 13 }}
              />
              <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                {/* Color each bar segment */}
                {assignmentData.map((entry, i) => <Cell key={i} fill={entry.color} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
