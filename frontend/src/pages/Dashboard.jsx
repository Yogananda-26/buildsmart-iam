import { useEffect, useState } from 'react';
import { getDashboardSummary } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, LineChart, Line, AreaChart, Area,
} from 'recharts';
import {
  FileText, AlertTriangle, DollarSign, Users,
  TrendingUp, ShieldCheck, Activity, Loader2,
} from 'lucide-react';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getDashboardSummary()
      .then((res) => setData(res.data))
      .catch(() => setError('Failed to load dashboard data'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading dashboard...</p></div>;
  if (error) return <div className="error-state">{error}</div>;

  const statCards = [
    { label: 'Total Reports', value: data?.totalReports ?? 24, icon: FileText, color: '#2563eb', bg: '#eff6ff' },
    { label: 'Active Projects', value: data?.activeProjects ?? 12, icon: Activity, color: '#10b981', bg: '#ecfdf5' },
    { label: 'Safety Score', value: `${data?.safetyScore ?? 94}%`, icon: ShieldCheck, color: '#f59e0b', bg: '#fffbeb' },
    { label: 'Budget Utilization', value: `${data?.budgetUtilization ?? 78}%`, icon: DollarSign, color: '#ef4444', bg: '#fef2f2' },
    { label: 'Workforce', value: data?.totalWorkforce ?? 356, icon: Users, color: '#8b5cf6', bg: '#f5f3ff' },
    { label: 'Overdue Tasks', value: data?.overdueTasks ?? 7, icon: AlertTriangle, color: '#f97316', bg: '#fff7ed' },
  ];

  const monthlyData = data?.monthlyTrends || [
    { month: 'Jan', reports: 18, incidents: 3, budget: 82 },
    { month: 'Feb', reports: 22, incidents: 2, budget: 79 },
    { month: 'Mar', reports: 15, incidents: 5, budget: 85 },
    { month: 'Apr', reports: 28, incidents: 1, budget: 76 },
    { month: 'May', reports: 32, incidents: 4, budget: 88 },
    { month: 'Jun', reports: 24, incidents: 2, budget: 91 },
  ];

  const scopeData = data?.reportsByScope || [
    { name: 'Project', value: 35 },
    { name: 'Safety', value: 25 },
    { name: 'Finance', value: 22 },
    { name: 'Resource', value: 18 },
  ];

  const recentReports = data?.recentReports || [
    { id: 'BSRA001', scope: 'PROJECT', date: '2024-01-15', status: 'Completed' },
    { id: 'BSRA002', scope: 'SAFETY', date: '2024-01-14', status: 'Completed' },
    { id: 'BSRA003', scope: 'FINANCE', date: '2024-01-13', status: 'Pending' },
    { id: 'BSRA004', scope: 'RESOURCE', date: '2024-01-12', status: 'Completed' },
    { id: 'BSRA005', scope: 'PROJECT', date: '2024-01-11', status: 'In Progress' },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Dashboard Overview</h2>
        <p>Real-time insights across all BuildSmart modules</p>
      </div>

      {/* Stat Cards */}
      <div className="stat-cards">
        {statCards.map((card) => (
          <div className="stat-card" key={card.label}>
            <div className="stat-icon" style={{ backgroundColor: card.bg, color: card.color }}>
              <card.icon size={24} />
            </div>
            <div className="stat-info">
              <span className="stat-value">{card.value}</span>
              <span className="stat-label">{card.label}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="charts-grid">
        <div className="chart-card chart-large">
          <h3>Monthly Trends</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="month" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip
                contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }}
              />
              <Area type="monotone" dataKey="reports" stroke="#2563eb" fill="#2563eb" fillOpacity={0.1} name="Reports" />
              <Area type="monotone" dataKey="budget" stroke="#10b981" fill="#10b981" fillOpacity={0.1} name="Budget %" />
              <Area type="monotone" dataKey="incidents" stroke="#ef4444" fill="#ef4444" fillOpacity={0.1} name="Incidents" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card chart-small">
          <h3>Reports by Scope</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={scopeData}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={100}
                paddingAngle={4}
                dataKey="value"
              >
                {scopeData.map((_, idx) => (
                  <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent Reports Table */}
      <div className="table-card">
        <div className="table-header">
          <h3>Recent Reports</h3>
          <a href="/report-history" className="btn btn-outline btn-sm">View All</a>
        </div>
        <table>
          <thead>
            <tr>
              <th>Report ID</th>
              <th>Scope</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {recentReports.map((r) => (
              <tr key={r.id}>
                <td><strong>{r.id}</strong></td>
                <td><span className={`badge badge-${r.scope.toLowerCase()}`}>{r.scope}</span></td>
                <td>{r.date}</td>
                <td>
                  <span className={`badge ${r.status === 'Completed' ? 'badge-success' : r.status === 'Pending' ? 'badge-warning' : 'badge-info'}`}>
                    {r.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
