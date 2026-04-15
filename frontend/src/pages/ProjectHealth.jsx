import { useEffect, useState } from 'react';
import { getProjectSummary, getProjectHealth } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  RadialBarChart, RadialBar, Legend, PieChart, Pie, Cell,
} from 'recharts';
import { Activity, CheckCircle, AlertTriangle, Clock, Loader2, Search } from 'lucide-react';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

export default function ProjectHealth() {
  const [projects, setProjects] = useState(null);
  const [selectedProject, setSelectedProject] = useState(null);
  const [healthData, setHealthData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [healthLoading, setHealthLoading] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    getProjectSummary()
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : [res.data];
        setProjects(data);
      })
      .catch(() => {
        // fallback dummy data
        setProjects([
          { projectId: 'PRJ001', projectName: 'Highway Bridge Construction', status: 'ON_TRACK', completionPercentage: 72, startDate: '2024-01-01', endDate: '2024-12-31', totalMilestones: 12, completedMilestones: 8 },
          { projectId: 'PRJ002', projectName: 'Commercial Tower Phase 2', status: 'AT_RISK', completionPercentage: 45, startDate: '2024-02-15', endDate: '2024-11-30', totalMilestones: 10, completedMilestones: 4 },
          { projectId: 'PRJ003', projectName: 'Metro Station Renovation', status: 'ON_TRACK', completionPercentage: 89, startDate: '2023-06-01', endDate: '2024-06-30', totalMilestones: 8, completedMilestones: 7 },
          { projectId: 'PRJ004', projectName: 'Residential Complex Alpha', status: 'DELAYED', completionPercentage: 32, startDate: '2024-03-01', endDate: '2025-03-01', totalMilestones: 15, completedMilestones: 4 },
          { projectId: 'PRJ005', projectName: 'Water Treatment Plant', status: 'ON_TRACK', completionPercentage: 61, startDate: '2024-01-15', endDate: '2024-10-15', totalMilestones: 9, completedMilestones: 5 },
        ]);
      })
      .finally(() => setLoading(false));
  }, []);

  const viewHealth = async (projectId) => {
    setSelectedProject(projectId);
    setHealthLoading(true);
    try {
      const res = await getProjectHealth(projectId);
      setHealthData(res.data);
    } catch {
      setHealthData({
        projectId,
        healthScore: 78,
        scheduleVariance: -3.5,
        costVariance: 2.1,
        riskLevel: 'MEDIUM',
        milestoneProgress: 67,
        qualityIndex: 85,
      });
    } finally {
      setHealthLoading(false);
    }
  };

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading projects...</p></div>;

  const filteredProjects = projects?.filter((p) =>
    p.projectName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.projectId?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const statusColor = (s) =>
    s === 'ON_TRACK' ? 'badge-success' : s === 'AT_RISK' ? 'badge-warning' : 'badge-danger';

  const completionData = filteredProjects?.map((p) => ({
    name: p.projectId,
    completion: p.completionPercentage,
  }));

  const statusCounts = filteredProjects?.reduce((acc, p) => {
    acc[p.status] = (acc[p.status] || 0) + 1;
    return acc;
  }, {});

  const statusPieData = statusCounts ? Object.entries(statusCounts).map(([name, value]) => ({ name, value })) : [];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Project Health</h2>
        <p>Monitor project progress, milestones, and health scores</p>
      </div>

      {/* Summary Cards */}
      <div className="stat-cards">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><CheckCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{statusCounts?.ON_TRACK || 0}</span><span className="stat-label">On Track</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}><AlertTriangle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{statusCounts?.AT_RISK || 0}</span><span className="stat-label">At Risk</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><Clock size={24} /></div>
          <div className="stat-info"><span className="stat-value">{statusCounts?.DELAYED || 0}</span><span className="stat-label">Delayed</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><Activity size={24} /></div>
          <div className="stat-info"><span className="stat-value">{filteredProjects?.length || 0}</span><span className="stat-label">Total Projects</span></div>
        </div>
      </div>

      {/* Charts */}
      <div className="charts-grid">
        <div className="chart-card chart-large">
          <h3>Project Completion</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={completionData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="name" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" domain={[0, 100]} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Bar dataKey="completion" fill="#2563eb" radius={[6, 6, 0, 0]} name="Completion %" />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="chart-card chart-small">
          <h3>Status Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie data={statusPieData} cx="50%" cy="50%" innerRadius={55} outerRadius={90} paddingAngle={4} dataKey="value">
                {statusPieData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Projects Table */}
      <div className="table-card">
        <div className="table-header">
          <h3>All Projects</h3>
          <div className="search-box">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search projects..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
        <table>
          <thead>
            <tr>
              <th>Project ID</th>
              <th>Name</th>
              <th>Status</th>
              <th>Completion</th>
              <th>Milestones</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredProjects?.map((p) => (
              <tr key={p.projectId}>
                <td><strong>{p.projectId}</strong></td>
                <td>{p.projectName}</td>
                <td><span className={`badge ${statusColor(p.status)}`}>{p.status?.replace('_', ' ')}</span></td>
                <td>
                  <div className="progress-bar-wrapper">
                    <div className="progress-bar" style={{ width: `${p.completionPercentage}%` }} />
                    <span>{p.completionPercentage}%</span>
                  </div>
                </td>
                <td>{p.completedMilestones}/{p.totalMilestones}</td>
                <td>
                  <button className="btn btn-outline btn-sm" onClick={() => viewHealth(p.projectId)}>
                    View Health
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Health Detail Modal */}
      {selectedProject && (
        <div className="modal-overlay" onClick={() => setSelectedProject(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Health Report — {selectedProject}</h3>
            {healthLoading ? (
              <div className="loading-spinner"><Loader2 className="spin" size={30} /></div>
            ) : healthData ? (
              <div className="health-grid">
                <div className="health-item">
                  <span className="health-label">Health Score</span>
                  <span className="health-value" style={{ color: healthData.healthScore >= 70 ? '#10b981' : '#ef4444' }}>
                    {healthData.healthScore}%
                  </span>
                </div>
                <div className="health-item">
                  <span className="health-label">Schedule Variance</span>
                  <span className="health-value">{healthData.scheduleVariance > 0 ? '+' : ''}{healthData.scheduleVariance}%</span>
                </div>
                <div className="health-item">
                  <span className="health-label">Cost Variance</span>
                  <span className="health-value">{healthData.costVariance > 0 ? '+' : ''}{healthData.costVariance}%</span>
                </div>
                <div className="health-item">
                  <span className="health-label">Risk Level</span>
                  <span className={`badge ${healthData.riskLevel === 'LOW' ? 'badge-success' : healthData.riskLevel === 'MEDIUM' ? 'badge-warning' : 'badge-danger'}`}>
                    {healthData.riskLevel}
                  </span>
                </div>
                <div className="health-item">
                  <span className="health-label">Milestone Progress</span>
                  <span className="health-value">{healthData.milestoneProgress}%</span>
                </div>
                <div className="health-item">
                  <span className="health-label">Quality Index</span>
                  <span className="health-value">{healthData.qualityIndex}%</span>
                </div>
              </div>
            ) : null}
            <button className="btn btn-primary" style={{ marginTop: '1.5rem' }} onClick={() => setSelectedProject(null)}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
