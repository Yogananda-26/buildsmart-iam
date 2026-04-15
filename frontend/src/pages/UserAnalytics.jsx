import { useEffect, useState } from 'react';
import { getUserAnalytics } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, RadarChart, Radar, PolarGrid,
  PolarAngleAxis, PolarRadiusAxis,
} from 'recharts';
import {
  Users, UserCheck, UserX, UserMinus, Loader2, Search, Shield,
} from 'lucide-react';

const COLORS = ['#10b981', '#f59e0b', '#ef4444', '#2563eb', '#8b5cf6', '#06b6d4'];
const STATUS_COLORS = { ACTIVE: '#10b981', INACTIVE: '#f59e0b', SUSPENDED: '#ef4444' };

export default function UserAnalytics() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    getUserAnalytics()
      .then((res) => {
        setAnalytics(res.data);
      })
      .catch((error) => {
        console.error('Failed to fetch user analytics:', error);
        setAnalytics({
          totalUsers: 0,
          activeUsers: 0,
          inactiveUsers: 0,
          suspendedUsers: 0,
          usersByRole: {},
          statusByRole: {},
          users: [],
        });
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading user analytics...</p></div>;

  const statusPie = [
    { name: 'Active', value: analytics?.activeUsers || 0 },
    { name: 'Inactive', value: analytics?.inactiveUsers || 0 },
    { name: 'Suspended', value: analytics?.suspendedUsers || 0 },
  ];

  const roleData = analytics?.usersByRole
    ? Object.entries(analytics.usersByRole).map(([role, count]) => ({
        role: role.replace('_', ' '),
        count,
      }))
    : [];

  const stackedData = analytics?.statusByRole
    ? Object.entries(analytics.statusByRole).map(([role, statuses]) => ({
        role: role.replace('_', ' '),
        Active: statuses.ACTIVE || 0,
        Inactive: statuses.INACTIVE || 0,
        Suspended: statuses.SUSPENDED || 0,
      }))
    : [];

  const radarData = analytics?.statusByRole
    ? Object.entries(analytics.statusByRole).map(([role, statuses]) => ({
        role: role.replace('_', ' '),
        Active: statuses.ACTIVE || 0,
        Total: (statuses.ACTIVE || 0) + (statuses.INACTIVE || 0) + (statuses.SUSPENDED || 0),
      }))
    : [];

  const filteredUsers = (analytics?.users || []).filter((u) => {
    const matchesSearch =
      u.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.userId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.role?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || u.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const statusBadge = (status) => {
    if (status === 'ACTIVE') return 'badge-success';
    if (status === 'INACTIVE') return 'badge-warning';
    if (status === 'SUSPENDED') return 'badge-danger';
    return 'badge-info';
  };

  const roleBadge = (role) => {
    if (role === 'ADMIN') return 'badge-danger';
    if (role === 'PROJECT_MANAGER') return 'badge-info';
    if (role === 'SITE_ENGINEER') return 'badge-success';
    if (role === 'WORKER') return 'badge-purple';
    if (role === 'VENDOR') return 'badge-warning';
    return 'badge-info';
  };

  const activeRate = analytics?.totalUsers > 0
    ? ((analytics.activeUsers / analytics.totalUsers) * 100).toFixed(1)
    : 0;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>User Analytics</h2>
        <p>IAM user status overview — Active, Inactive & Suspended users</p>
      </div>

      {/* Stat Cards */}
      <div className="stat-cards">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}>
            <Users size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-value">{analytics?.totalUsers || 0}</span>
            <span className="stat-label">Total Users</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}>
            <UserCheck size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-value">{analytics?.activeUsers || 0}</span>
            <span className="stat-label">Active Users</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}>
            <UserMinus size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-value">{analytics?.inactiveUsers || 0}</span>
            <span className="stat-label">Inactive Users</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}>
            <UserX size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-value">{analytics?.suspendedUsers || 0}</span>
            <span className="stat-label">Suspended Users</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f5f3ff', color: '#8b5cf6' }}>
            <Shield size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-value">{activeRate}%</span>
            <span className="stat-label">Active Rate</span>
          </div>
        </div>
      </div>

      {/* Charts Row 1 */}
      <div className="charts-grid">
        {/* Status Distribution Pie */}
        <div className="chart-card chart-small">
          <h3>User Status Distribution</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart>
              <Pie
                data={statusPie}
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={95}
                paddingAngle={4}
                dataKey="value"
              >
                {statusPie.map((entry, i) => (
                  <Cell key={i} fill={Object.values(STATUS_COLORS)[i]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Users by Role Bar */}
        <div className="chart-card chart-large">
          <h3>Users by Role</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={roleData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="role" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" allowDecimals={false} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Bar dataKey="count" fill="#2563eb" radius={[6, 6, 0, 0]} name="Users">
                {roleData.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2 */}
      <div className="charts-grid">
        {/* Stacked Status by Role */}
        <div className="chart-card chart-large">
          <h3>Status Breakdown by Role</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={stackedData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="role" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" allowDecimals={false} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Legend />
              <Bar dataKey="Active" stackId="status" fill="#10b981" name="Active" />
              <Bar dataKey="Inactive" stackId="status" fill="#f59e0b" name="Inactive" />
              <Bar dataKey="Suspended" stackId="status" fill="#ef4444" name="Suspended" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Radar */}
        <div className="chart-card chart-small">
          <h3>Active vs Total by Role</h3>
          <ResponsiveContainer width="100%" height={320}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="#e2e8f0" />
              <PolarAngleAxis dataKey="role" tick={{ fontSize: 11 }} stroke="#94a3b8" />
              <PolarRadiusAxis stroke="#94a3b8" />
              <Radar name="Active" dataKey="Active" stroke="#10b981" fill="#10b981" fillOpacity={0.3} />
              <Radar name="Total" dataKey="Total" stroke="#2563eb" fill="#2563eb" fillOpacity={0.1} />
              <Tooltip />
              <Legend />
            </RadarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="filter-bar" style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
        <label>Status:</label>
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="ALL">All Statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
          <option value="SUSPENDED">Suspended</option>
        </select>
      </div>

      {/* Users Table */}
      <div className="table-card">
        <div className="table-header">
          <h3>
            All Users
            {statusFilter !== 'ALL' && (
              <span className={`badge ${statusBadge(statusFilter)}`} style={{ marginLeft: '0.75rem', fontSize: '0.75rem' }}>
                {statusFilter}
              </span>
            )}
          </h3>
          <div className="search-box">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search users..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
        <table>
          <thead>
            <tr>
              <th>User ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                  No users found matching your criteria
                </td>
              </tr>
            ) : (
              filteredUsers.map((u) => (
                <tr key={u.userId}>
                  <td><strong>{u.userId}</strong></td>
                  <td>{u.name}</td>
                  <td style={{ color: '#64748b', fontSize: '0.875rem' }}>{u.email}</td>
                  <td>
                    <span className={`badge ${roleBadge(u.role)}`}>
                      {u.role?.replace('_', ' ')}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${statusBadge(u.status)}`}>
                      {u.status}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <div style={{ padding: '0.75rem 1.5rem', color: '#94a3b8', fontSize: '0.8rem', borderTop: '1px solid #e2e8f0' }}>
          Showing {filteredUsers.length} of {analytics?.totalUsers || 0} users
        </div>
      </div>
    </div>
  );
}
