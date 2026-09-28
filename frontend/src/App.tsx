/**
 * src/App.tsx — Main application component
 *
 * Provides the root layout and routing structure for RunForge.
 * Integrates Clerk authentication for user sign-in/sign-up and
 * TanStack Query for server state management.
 *
 * Layout:
 * - Header: Logo with Clerk auth UI (sign-in/sign-up/user profile)
 * - Main app (when signed in): Sidebar nav + main content with routes
 * - Routes:
 *   - / (Dashboard) — aggregated statistics with charts
 *   - /members (Members) — CRUD table for members
 *   - /plans (Plans) — CRUD grid for training plans
 */

import './App.css'
import { Show, SignInButton, SignUpButton, UserButton } from '@clerk/react'
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Users, ClipboardList, BarChart2, Footprints } from 'lucide-react';
import MembersPage from './pages/MembersPage';
import PlansPage from './pages/PlansPage';
import DashboardPage from './pages/DashboardPage';

/**
 * Query client configuration for TanStack Query.
 * - retry: 1 — retry failed requests once
 * - staleTime: 30_000 — consider data fresh for 30 seconds
 */
const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, staleTime: 30_000 } },
});

/**
 * App component — Main React component rendering the entire application.
 *
 * @returns JSX element with conditional rendering based on Clerk auth state
 */
export default function App() {
  return (
    <>
      <header>
        {/* Show sign-in/sign-up when user is NOT authenticated */}
        <Show when="signed-out">
             <div className="logo">
               <Footprints size={28} strokeWidth={1.5} />
               <span>runforge</span>
             </div>
          <SignInButton><button className="btn">Anmelden</button></SignInButton>
          <SignUpButton><button className="btn">Registrieren</button></SignUpButton>
        </Show>

        {/* Show main app when user IS authenticated */}
        <Show when="signed-in">
          <UserButton />
          {/* Wrap entire app in QueryClientProvider for TanStack Query cache */}
          <QueryClientProvider client={queryClient}>
            <BrowserRouter>
              <div className="app">
                {/* Sidebar Navigation */}
                <nav className="sidebar">
                  <div className="logo">
                    <Footprints size={28} strokeWidth={1.5} />
                    <span>runforge</span>
                  </div>

                  {/* NavLink: Members page */}
                  <NavLink to="/members" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <Users size={18} /> Mitglieder
                  </NavLink>

                  {/* NavLink: Training Plans page */}
                  <NavLink to="/plans" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <ClipboardList size={18} /> Trainingspläne
                  </NavLink>

                  {/* NavLink: Dashboard page */}
                  <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <BarChart2 size={18} /> Dashboard
                  </NavLink>
                </nav>

                {/* Main Content Area */}
                <main className="content">
                  <Routes>
                    {/* Dashboard: Statistics and charts */}
                    <Route path="/" element={<DashboardPage />} />
                    {/* Members: CRUD table for running group members */}
                    <Route path="/members" element={<MembersPage />} />
                    {/* Plans: CRUD grid for training plans */}
                    <Route path="/plans" element={<PlansPage />} />
                  </Routes>
                </main>
              </div>
            </BrowserRouter>
          </QueryClientProvider>
        </Show>
      </header>
    </>
  )
}

