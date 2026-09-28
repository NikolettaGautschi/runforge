import './App.css'
import { Show, SignInButton, SignUpButton, UserButton } from '@clerk/react'
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Users, ClipboardList, BarChart2, Footprints } from 'lucide-react';
import MembersPage from './pages/MembersPage';
import PlansPage from './pages/PlansPage';
import DashboardPage from './pages/DashboardPage';


const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, staleTime: 30_000 } },
});
export default function App() {
  return (
    <>
      <header>
        <Show when="signed-out">
             <div className="logo">
               <Footprints size={28} strokeWidth={1.5} />
               <span>runforge</span>
             </div>
          <SignInButton><button className="btn">Anmelden</button></SignInButton>
          <SignUpButton><button className="btn">Registrieren</button></SignUpButton>
        </Show>
        <Show when="signed-in">
          <UserButton />
          <QueryClientProvider client={queryClient}>
            <BrowserRouter>
              <div className="app">
                <nav className="sidebar">
                  <div className="logo">
                    <Footprints size={28} strokeWidth={1.5} />
                    <span>runforge</span>
                  </div>
                  <NavLink to="/members" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <Users size={18} /> Mitglieder
                  </NavLink>
                  <NavLink to="/plans" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <ClipboardList size={18} /> Trainingspläne
                  </NavLink>
                  <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                    <BarChart2 size={18} /> Dashboard
                  </NavLink>
                </nav>
                <main className="content">
                  <Routes>
                    <Route path="/" element={<DashboardPage />} />
                    <Route path="/members" element={<MembersPage />} />
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

