import { useEffect, useState } from 'react';
import { getResourceUtilization, getLaborAllocation } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, RadarChart, Radar, PolarGrid,
  PolarAngleAxis, PolarRadiusAxis, AreaChart, Area,
} from 'recharts';
import { Users, UserCheck, UserX, Briefcase, Loader2 } from 'lucide-react';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

const PROJECTS = [
  { id: 'ALL', name: 'All Projects' },
  { id: 'P-1001', name: 'West Park Towers' },
  { id: 'P-1002', name: 'Lakeview Residences' },
  { id: 'P-1003', name: 'Steel Bridge Crossing' },
  { id: 'P-1004', name: 'Residential Complex Alpha' },
  { id: 'P-1005', name: 'Water Treatment Plant' },
];

const FALLBACK_DATA = {
  'ALL': {
    utilization: {
      overallUtilization: 82, totalResources: 356, activeResources: 312, idleResources: 44,
      departments: [
        { name: 'Civil Engineering', utilization: 88, headcount: 85 },
        { name: 'Electrical', utilization: 76, headcount: 62 },
        { name: 'Plumbing', utilization: 92, headcount: 48 },
        { name: 'Structural', utilization: 84, headcount: 74 },
        { name: 'Safety', utilization: 79, headcount: 35 },
        { name: 'Management', utilization: 71, headcount: 52 },
      ],
    },
    labor: {
      allocation: [
        { project: 'West Park Towers', workers: 78, percentage: 25 },
        { project: 'Lakeview Residences', workers: 65, percentage: 21 },
        { project: 'Steel Bridge Crossing', workers: 52, percentage: 17 },
        { project: 'Residential Complex', workers: 48, percentage: 15 },
        { project: 'Water Treatment', workers: 38, percentage: 12 },
        { project: 'Unallocated', workers: 31, percentage: 10 },
      ],
      trends: [
        { month: 'Jan', allocated: 310, available: 340 },
        { month: 'Feb', allocated: 318, available: 345 },
        { month: 'Mar', allocated: 325, available: 350 },
        { month: 'Apr', allocated: 308, available: 352 },
        { month: 'May', allocated: 320, available: 356 },
        { month: 'Jun', allocated: 312, available: 356 },
      ],
    },
  },
  'P-1001': {
    utilization: {
      overallUtilization: 88, totalResources: 78, activeResources: 72, idleResources: 6,
      departments: [
        { name: 'Civil Engineering', utilization: 92, headcount: 22 },
        { name: 'Electrical', utilization: 85, headcount: 16 },
        { name: 'Plumbing', utilization: 90, headcount: 10 },
        { name: 'Structural', utilization: 88, headcount: 18 },
        { name: 'Safety', utilization: 82, headcount: 6 },
        { name: 'Management', utilization: 78, headcount: 6 },
      ],
    },
    labor: {
      allocation: [
        { project: 'West Park Towers', workers: 78, percentage: 100 },
      ],
      trends: [
        { month: 'Jan', allocated: 70, available: 78 },
        { month: 'Feb', allocated: 72, available: 78 },
        { month: 'Mar', allocated: 75, available: 78 },
        { month: 'Apr', allocated: 68, available: 78 },
        { month: 'May', allocated: 74, available: 78 },
        { month: 'Jun', allocated: 72, available: 78 },
      ],
    },
  },
  'P-1002': {
    utilization: {
      overallUtilization: 84, totalResources: 65, activeResources: 58, idleResources: 7,
      departments: [
        { name: 'Civil Engineering', utilization: 86, headcount: 18 },
        { name: 'Electrical', utilization: 80, headcount: 14 },
        { name: 'Plumbing', utilization: 91, headcount: 9 },
        { name: 'Structural', utilization: 82, headcount: 14 },
        { name: 'Safety', utilization: 78, headcount: 5 },
        { name: 'Management', utilization: 75, headcount: 5 },
      ],
    },
    labor: {
      allocation: [
        { project: 'Lakeview Residences', workers: 65, percentage: 100 },
      ],
      trends: [
        { month: 'Jan', allocated: 58, available: 65 },
        { month: 'Feb', allocated: 60, available: 65 },
        { month: 'Mar', allocated: 62, available: 65 },
        { month: 'Apr', allocated: 55, available: 65 },
        { month: 'May', allocated: 61, available: 65 },
        { month: 'Jun', allocated: 58, available: 65 },
      ],
    },
  },
  'P-1003': {
    utilization: {
      overallUtilization: 79, totalResources: 52, activeResources: 44, idleResources: 8,
      departments: [
        { name: 'Civil Engineering', utilization: 82, headcount: 14 },
        { name: 'Electrical', utilization: 68, headcount: 10 },
        { name: 'Plumbing', utilization: 88, headcount: 7 },
        { name: 'Structural', utilization: 80, headcount: 12 },
        { name: 'Safety', utilization: 76, headcount: 4 },
        { name: 'Management', utilization: 70, headcount: 5 },
      ],
    },
    labor: {
      allocation: [
        { project: 'Steel Bridge Crossing', workers: 52, percentage: 100 },
      ],
      trends: [
        { month: 'Jan', allocated: 44, available: 52 },
        { month: 'Feb', allocated: 46, available: 52 },
        { month: 'Mar', allocated: 48, available: 52 },
        { month: 'Apr', allocated: 42, available: 52 },
        { month: 'May', allocated: 47, available: 52 },
        { month: 'Jun', allocated: 44, available: 52 },
      ],
    },
  },
  'P-1004': {
    utilization: {
      overallUtilization: 76, totalResources: 48, activeResources: 40, idleResources: 8,
      departments: [
        { name: 'Civil Engineering', utilization: 80, headcount: 12 },
        { name: 'Electrical', utilization: 70, headcount: 9 },
        { name: 'Plumbing', utilization: 85, headcount: 6 },
        { name: 'Structural', utilization: 74, headcount: 11 },
        { name: 'Safety', utilization: 72, headcount: 5 },
        { name: 'Management', utilization: 68, headcount: 5 },
      ],
    },
    labor: {
      allocation: [
        { project: 'Residential Complex', workers: 48, percentage: 100 },
      ],
      trends: [
        { month: 'Jan', allocated: 38, available: 48 },
        { month: 'Feb', allocated: 40, available: 48 },
        { month: 'Mar', allocated: 42, available: 48 },
        { month: 'Apr', allocated: 36, available: 48 },
        { month: 'May', allocated: 41, available: 48 },
        { month: 'Jun', allocated: 40, available: 48 },
      ],
    },
  },
  'P-1005': {
    utilization: {
      overallUtilization: 72, totalResources: 38, activeResources: 30, idleResources: 8,
      departments: [
        { name: 'Civil Engineering', utilization: 78, headcount: 10 },
        { name: 'Electrical', utilization: 65, headcount: 7 },
        { name: 'Plumbing', utilization: 90, headcount: 6 },
        { name: 'Structural', utilization: 70, headcount: 8 },
        { name: 'Safety', utilization: 68, headcount: 4 },
        { name: 'Management', utilization: 60, headcount: 3 },
      ],
    },
    labor: {
      allocation: [
        { project: 'Water Treatment', workers: 38, percentage: 100 },
      ],
      trends: [
        { month: 'Jan', allocated: 28, available: 38 },
        { month: 'Feb', allocated: 30, available: 38 },
        { month: 'Mar', allocated: 32, available: 38 },
        { month: 'Apr', allocated: 27, available: 38 },
        { month: 'May', allocated: 31, available: 38 },
        { month: 'Jun', allocated: 30, available: 38 },
      ],
    },
  },
};

export default function ResourceWorkforce() {
  const [utilization, setUtilization] = useState(null);
  const [labor, setLabor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedProject, setSelectedProject] = useState('ALL');

  const fetchData = () => {
    setLoading(true);
    Promise.all([
      getResourceUtilization(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getLaborAllocation(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
    ]).then(([utilRes, laborRes]) => {
      const fb = FALLBACK_DATA[selectedProject] || FALLBACK_DATA['ALL'];
      setUtilization(utilRes.data || fb.utilization);
      setLabor(laborRes.data || fb.labor);
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [selectedProject]);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading resource data...</p></div>;

  const radarData = utilization?.departments?.map((d) => ({
    subject: d.name,
    utilization: d.utilization,
    fullMark: 100,
  }));

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Resource & Workforce</h2>
        <p>Monitor resource utilization and labor allocation across projects</p>
      </div>

      {/* Project Selector */}
      <div className="filter-bar">
        <label>Project:</label>
        <select value={selectedProject} onChange={(e) => setSelectedProject(e.target.value)}>
          {PROJECTS.map((p) => (
            <option key={p.id} value={p.id}>{p.id === 'ALL' ? p.name : `${p.id} - ${p.name}`}</option>
          ))}
        </select>
      </div>

      <div className="stat-cards">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><Users size={24} /></div>
          <div className="stat-info"><span className="stat-value">{utilization?.totalResources}</span><span className="stat-label">Total Resources</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><UserCheck size={24} /></div>
          <div className="stat-info"><span className="stat-value">{utilization?.activeResources}</span><span className="stat-label">Active</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><UserX size={24} /></div>
          <div className="stat-info"><span className="stat-value">{utilization?.idleResources}</span><span className="stat-label">Idle</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f5f3ff', color: '#8b5cf6' }}><Briefcase size={24} /></div>
          <div className="stat-info"><span className="stat-value">{utilization?.overallUtilization}%</span><span className="stat-label">Overall Utilization</span></div>
        </div>
      </div>

      <div className="charts-grid">
        {/* Department Utilization */}
        <div className="chart-card chart-large">
          <h3>Department Utilization</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={utilization?.departments}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="name" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" domain={[0, 100]} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Bar dataKey="utilization" fill="#2563eb" radius={[6, 6, 0, 0]} name="Utilization %">
                {utilization?.departments?.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Radar Chart */}
        <div className="chart-card chart-small">
          <h3>Utilization Radar</h3>
          <ResponsiveContainer width="100%" height={320}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="#e2e8f0" />
              <PolarAngleAxis dataKey="subject" tick={{ fontSize: 11 }} stroke="#94a3b8" />
              <PolarRadiusAxis angle={30} domain={[0, 100]} stroke="#94a3b8" />
              <Radar name="Utilization" dataKey="utilization" stroke="#2563eb" fill="#2563eb" fillOpacity={0.2} />
              <Tooltip />
            </RadarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="charts-grid">
        {/* Labor Allocation Pie */}
        <div className="chart-card chart-small">
          <h3>Labor Allocation by Project</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart>
              <Pie data={labor?.allocation} cx="50%" cy="50%" innerRadius={55} outerRadius={95} paddingAngle={3} dataKey="workers" nameKey="project">
                {labor?.allocation?.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Workforce Trends */}
        <div className="chart-card chart-large">
          <h3>Workforce Trends</h3>
          <ResponsiveContainer width="100%" height={320}>
            <AreaChart data={labor?.trends}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="month" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Legend />
              <Area type="monotone" dataKey="available" stroke="#94a3b8" fill="#94a3b8" fillOpacity={0.1} name="Available" />
              <Area type="monotone" dataKey="allocated" stroke="#2563eb" fill="#2563eb" fillOpacity={0.15} name="Allocated" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Department Table */}
      <div className="table-card">
        <div className="table-header"><h3>Department Summary</h3></div>
        <table>
          <thead>
            <tr><th>Department</th><th>Headcount</th><th>Utilization</th><th>Status</th></tr>
          </thead>
          <tbody>
            {utilization?.departments?.map((d) => (
              <tr key={d.name}>
                <td><strong>{d.name}</strong></td>
                <td>{d.headcount}</td>
                <td>
                  <div className="progress-bar-wrapper">
                    <div className="progress-bar" style={{ width: `${d.utilization}%`, backgroundColor: d.utilization >= 85 ? '#10b981' : d.utilization >= 70 ? '#f59e0b' : '#ef4444' }} />
                    <span>{d.utilization}%</span>
                  </div>
                </td>
                <td>
                  <span className={`badge ${d.utilization >= 85 ? 'badge-success' : d.utilization >= 70 ? 'badge-warning' : 'badge-danger'}`}>
                    {d.utilization >= 85 ? 'Optimal' : d.utilization >= 70 ? 'Moderate' : 'Low'}
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
