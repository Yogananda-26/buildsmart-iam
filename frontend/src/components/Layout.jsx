import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Activity,
  ShieldCheck,
  DollarSign,
  Users,
  FileText,
  FilePlus,
  LogOut,
  BarChart3,
  Menu,
  X,
  Truck,
  HardHat,
  UsersRound,
} from 'lucide-react';
import { useState } from 'react';

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/project-health', icon: Activity, label: 'Project Health' },
  { to: '/safety-compliance', icon: ShieldCheck, label: 'Safety & Compliance' },
  { to: '/financial-budget', icon: DollarSign, label: 'Financial & Budget' },
  { to: '/resource-workforce', icon: Users, label: 'Resource & Workforce' },
  { to: '/vendor-analytics', icon: Truck, label: 'Vendor Analytics' },
  { to: '/site-engineer', icon: HardHat, label: 'Site Engineer' },
  { to: '/user-analytics', icon: UsersRound, label: 'User Analytics' },
  { to: '/report-history', icon: FileText, label: 'Report History' },
  { to: '/generate-report', icon: FilePlus, label: 'Generate Report' },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-layout">
      {/* Mobile overlay */}
      {sidebarOpen && (
        <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)} />
      )}

      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <BarChart3 size={28} />
          <div>
            <h1>BuildSmart</h1>
            <span className="sidebar-subtitle">Report & Analytics</span>
          </div>
          <button className="sidebar-close" onClick={() => setSidebarOpen(false)}>
            <X size={20} />
          </button>
        </div>

        <nav className="sidebar-nav">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `nav-link ${isActive ? 'active' : ''}`
              }
              onClick={() => setSidebarOpen(false)}
            >
              <Icon size={20} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="user-info">
            <div className="user-avatar">
              {user?.name?.charAt(0) || 'U'}
            </div>
            <div className="user-details">
              <span className="user-name">{user?.name || 'User'}</span>
              <span className="user-role">{user?.role || 'Admin'}</span>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout} title="Logout">
            <LogOut size={18} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <div className="main-content">
        <header className="topbar">
          <button className="menu-toggle" onClick={() => setSidebarOpen(true)}>
            <Menu size={24} />
          </button>
          <div className="topbar-right">
            <span className="welcome-text">
              Welcome back, <strong>{user?.name?.split(' ')[0] || 'User'}</strong>
            </span>
          </div>
        </header>

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
