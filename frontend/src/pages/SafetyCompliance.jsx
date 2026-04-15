import { useEffect, useState } from 'react';
import { getSafetyTrends, getInspectionsSummary } from '../services/api';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  BarChart, Bar, PieChart, Pie, Cell, Legend, AreaChart, Area,
} from 'recharts';
import { ShieldCheck, AlertTriangle, CheckCircle, XCircle, Loader2 } from 'lucide-react';

const COLORS = ['#10b981', '#f59e0b', '#ef4444', '#2563eb', '#8b5cf6'];

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
    trends: [
      { month: 'Jan', incidents: 5, nearMisses: 8, inspectionsPassed: 45, inspectionsFailed: 3 },
      { month: 'Feb', incidents: 3, nearMisses: 6, inspectionsPassed: 48, inspectionsFailed: 2 },
      { month: 'Mar', incidents: 7, nearMisses: 10, inspectionsPassed: 42, inspectionsFailed: 5 },
      { month: 'Apr', incidents: 2, nearMisses: 4, inspectionsPassed: 50, inspectionsFailed: 1 },
      { month: 'May', incidents: 4, nearMisses: 7, inspectionsPassed: 47, inspectionsFailed: 3 },
      { month: 'Jun', incidents: 1, nearMisses: 3, inspectionsPassed: 52, inspectionsFailed: 1 },
    ],
    inspections: {
      totalInspections: 312, passed: 284, failed: 15, pending: 13, complianceRate: 95,
      categories: [
        { name: 'Fire Safety', passed: 58, failed: 2 },
        { name: 'PPE Compliance', passed: 72, failed: 4 },
        { name: 'Scaffolding', passed: 45, failed: 5 },
        { name: 'Electrical', passed: 52, failed: 2 },
        { name: 'Fall Protection', passed: 57, failed: 2 },
      ],
    },
  },
  'P-1001': {
    trends: [
      { month: 'Jan', incidents: 2, nearMisses: 3, inspectionsPassed: 18, inspectionsFailed: 1 },
      { month: 'Feb', incidents: 1, nearMisses: 2, inspectionsPassed: 20, inspectionsFailed: 0 },
      { month: 'Mar', incidents: 3, nearMisses: 4, inspectionsPassed: 16, inspectionsFailed: 2 },
      { month: 'Apr', incidents: 0, nearMisses: 1, inspectionsPassed: 21, inspectionsFailed: 0 },
      { month: 'May', incidents: 2, nearMisses: 3, inspectionsPassed: 19, inspectionsFailed: 1 },
      { month: 'Jun', incidents: 0, nearMisses: 1, inspectionsPassed: 22, inspectionsFailed: 0 },
    ],
    inspections: {
      totalInspections: 120, passed: 116, failed: 4, pending: 0, complianceRate: 97,
      categories: [
        { name: 'Fire Safety', passed: 24, failed: 1 },
        { name: 'PPE Compliance', passed: 30, failed: 1 },
        { name: 'Scaffolding', passed: 18, failed: 1 },
        { name: 'Electrical', passed: 22, failed: 0 },
        { name: 'Fall Protection', passed: 22, failed: 1 },
      ],
    },
  },
  'P-1002': {
    trends: [
      { month: 'Jan', incidents: 1, nearMisses: 2, inspectionsPassed: 12, inspectionsFailed: 1 },
      { month: 'Feb', incidents: 1, nearMisses: 2, inspectionsPassed: 13, inspectionsFailed: 1 },
      { month: 'Mar', incidents: 2, nearMisses: 3, inspectionsPassed: 11, inspectionsFailed: 2 },
      { month: 'Apr', incidents: 1, nearMisses: 1, inspectionsPassed: 14, inspectionsFailed: 0 },
      { month: 'May', incidents: 1, nearMisses: 2, inspectionsPassed: 13, inspectionsFailed: 1 },
      { month: 'Jun', incidents: 0, nearMisses: 1, inspectionsPassed: 15, inspectionsFailed: 0 },
    ],
    inspections: {
      totalInspections: 82, passed: 78, failed: 5, pending: 0, complianceRate: 94,
      categories: [
        { name: 'Fire Safety', passed: 15, failed: 1 },
        { name: 'PPE Compliance', passed: 18, failed: 2 },
        { name: 'Scaffolding', passed: 12, failed: 1 },
        { name: 'Electrical', passed: 16, failed: 1 },
        { name: 'Fall Protection', passed: 17, failed: 0 },
      ],
    },
  },
  'P-1003': {
    trends: [
      { month: 'Jan', incidents: 1, nearMisses: 2, inspectionsPassed: 10, inspectionsFailed: 1 },
      { month: 'Feb', incidents: 0, nearMisses: 1, inspectionsPassed: 11, inspectionsFailed: 0 },
      { month: 'Mar', incidents: 1, nearMisses: 2, inspectionsPassed: 10, inspectionsFailed: 1 },
      { month: 'Apr', incidents: 1, nearMisses: 2, inspectionsPassed: 11, inspectionsFailed: 1 },
      { month: 'May', incidents: 0, nearMisses: 1, inspectionsPassed: 11, inspectionsFailed: 0 },
      { month: 'Jun', incidents: 1, nearMisses: 1, inspectionsPassed: 11, inspectionsFailed: 1 },
    ],
    inspections: {
      totalInspections: 70, passed: 64, failed: 4, pending: 2, complianceRate: 91,
      categories: [
        { name: 'Fire Safety', passed: 12, failed: 0 },
        { name: 'PPE Compliance', passed: 15, failed: 1 },
        { name: 'Scaffolding', passed: 10, failed: 2 },
        { name: 'Electrical', passed: 13, failed: 1 },
        { name: 'Fall Protection', passed: 14, failed: 0 },
      ],
    },
  },
  'P-1004': {
    trends: [
      { month: 'Jan', incidents: 1, nearMisses: 1, inspectionsPassed: 3, inspectionsFailed: 0 },
      { month: 'Feb', incidents: 1, nearMisses: 1, inspectionsPassed: 3, inspectionsFailed: 1 },
      { month: 'Mar', incidents: 1, nearMisses: 1, inspectionsPassed: 3, inspectionsFailed: 0 },
      { month: 'Apr', incidents: 0, nearMisses: 0, inspectionsPassed: 3, inspectionsFailed: 0 },
      { month: 'May', incidents: 1, nearMisses: 1, inspectionsPassed: 3, inspectionsFailed: 1 },
      { month: 'Jun', incidents: 0, nearMisses: 0, inspectionsPassed: 3, inspectionsFailed: 0 },
    ],
    inspections: {
      totalInspections: 22, passed: 18, failed: 2, pending: 2, complianceRate: 82,
      categories: [
        { name: 'Fire Safety', passed: 4, failed: 0 },
        { name: 'PPE Compliance', passed: 5, failed: 0 },
        { name: 'Scaffolding', passed: 3, failed: 1 },
        { name: 'Electrical', passed: 3, failed: 0 },
        { name: 'Fall Protection', passed: 3, failed: 1 },
      ],
    },
  },
  'P-1005': {
    trends: [
      { month: 'Jan', incidents: 0, nearMisses: 0, inspectionsPassed: 2, inspectionsFailed: 0 },
      { month: 'Feb', incidents: 0, nearMisses: 0, inspectionsPassed: 1, inspectionsFailed: 0 },
      { month: 'Mar', incidents: 0, nearMisses: 0, inspectionsPassed: 2, inspectionsFailed: 0 },
      { month: 'Apr', incidents: 0, nearMisses: 0, inspectionsPassed: 1, inspectionsFailed: 0 },
      { month: 'May', incidents: 0, nearMisses: 0, inspectionsPassed: 1, inspectionsFailed: 0 },
      { month: 'Jun', incidents: 0, nearMisses: 0, inspectionsPassed: 1, inspectionsFailed: 0 },
    ],
    inspections: {
      totalInspections: 18, passed: 8, failed: 0, pending: 10, complianceRate: 100,
      categories: [
        { name: 'Fire Safety', passed: 3, failed: 0 },
        { name: 'PPE Compliance', passed: 4, failed: 0 },
        { name: 'Scaffolding', passed: 2, failed: 0 },
        { name: 'Electrical', passed: 0, failed: 0 },
        { name: 'Fall Protection', passed: 1, failed: 0 },
      ],
    },
  },
};

export default function SafetyCompliance() {
  const [trends, setTrends] = useState(null);
  const [inspections, setInspections] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedProject, setSelectedProject] = useState('ALL');

  const fetchData = () => {
    setLoading(true);
    Promise.all([
      getSafetyTrends(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getInspectionsSummary(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
    ]).then(([trendsRes, inspRes]) => {
      const fb = FALLBACK_DATA[selectedProject] || FALLBACK_DATA['ALL'];
      setTrends(trendsRes.data || fb.trends);
      setInspections(inspRes.data || fb.inspections);
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [selectedProject]);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading safety data...</p></div>;

  const statusPie = [
    { name: 'Passed', value: inspections?.passed || 0 },
    { name: 'Failed', value: inspections?.failed || 0 },
    { name: 'Pending', value: inspections?.pending || 0 },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Safety & Compliance</h2>
        <p>Track safety incidents, inspections, and compliance metrics</p>
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
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><ShieldCheck size={24} /></div>
          <div className="stat-info"><span className="stat-value">{inspections?.totalInspections}</span><span className="stat-label">Total Inspections</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><CheckCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{inspections?.complianceRate}%</span><span className="stat-label">Compliance Rate</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}><AlertTriangle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{inspections?.failed}</span><span className="stat-label">Failed Inspections</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><XCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{trends?.reduce?.((sum, t) => sum + t.incidents, 0) || 22}</span><span className="stat-label">Total Incidents (YTD)</span></div>
        </div>
      </div>

      <div className="charts-grid">
        <div className="chart-card chart-large">
          <h3>Safety Trends</h3>
          <ResponsiveContainer width="100%" height={320}>
            <AreaChart data={trends}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="month" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Area type="monotone" dataKey="incidents" stroke="#ef4444" fill="#ef4444" fillOpacity={0.1} name="Incidents" />
              <Area type="monotone" dataKey="nearMisses" stroke="#f59e0b" fill="#f59e0b" fillOpacity={0.1} name="Near Misses" />
              <Area type="monotone" dataKey="inspectionsPassed" stroke="#10b981" fill="#10b981" fillOpacity={0.1} name="Inspections Passed" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card chart-small">
          <h3>Inspection Results</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart>
              <Pie data={statusPie} cx="50%" cy="50%" innerRadius={55} outerRadius={90} paddingAngle={4} dataKey="value">
                {statusPie.map((_, i) => <Cell key={i} fill={COLORS[i]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Categories Table */}
      <div className="table-card">
        <div className="table-header"><h3>Inspection Categories</h3></div>
        <table>
          <thead>
            <tr>
              <th>Category</th>
              <th>Passed</th>
              <th>Failed</th>
              <th>Pass Rate</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {inspections?.categories?.map((cat) => {
              const rate = Math.round((cat.passed / (cat.passed + cat.failed)) * 100);
              return (
                <tr key={cat.name}>
                  <td><strong>{cat.name}</strong></td>
                  <td>{cat.passed}</td>
                  <td>{cat.failed}</td>
                  <td>
                    <div className="progress-bar-wrapper">
                      <div className="progress-bar" style={{ width: `${rate}%`, backgroundColor: rate >= 90 ? '#10b981' : '#f59e0b' }} />
                      <span>{rate}%</span>
                    </div>
                  </td>
                  <td>
                    <span className={`badge ${rate >= 90 ? 'badge-success' : 'badge-warning'}`}>
                      {rate >= 90 ? 'Compliant' : 'Needs Attention'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
