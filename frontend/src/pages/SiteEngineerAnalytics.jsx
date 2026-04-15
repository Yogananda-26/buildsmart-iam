import { useEffect, useState } from 'react';
import {
  getSiteEngineerPerformance,
  getSiteProgressSummary,
  getSiteEngineerDailyLogs,
} from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, RadarChart, Radar, PolarGrid,
  PolarAngleAxis, PolarRadiusAxis, AreaChart, Area,
} from 'recharts';
import {
  HardHat, CheckCircle, AlertTriangle, ClipboardList, Star,
  Loader2, Search, Clock, Sun, CloudRain, Wind,
} from 'lucide-react';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

const PROJECTS = [
  { id: 'ALL', name: 'All Projects' },
  { id: 'P-1001', name: 'West Park Towers' },
  { id: 'P-1002', name: 'Lakeview Residences' },
  { id: 'P-1003', name: 'Steel Bridge Crossing' },
];

const ALL_ENGINEERS = [
  { engineerId: 'SE001', engineerName: 'Rajesh Kumar', assignedProject: 'West Park Towers', projectId: 'P-1001', taskCompletionRate: 92.5, avgHoursOnSite: 9.2, totalInspections: 28, issuesResolved: 45, issuesPending: 3, qualityScore: 88.4, performanceGrade: 'A' },
  { engineerId: 'SE002', engineerName: 'Anil Sharma', assignedProject: 'Lakeview Residences', projectId: 'P-1002', taskCompletionRate: 85.3, avgHoursOnSite: 8.5, totalInspections: 22, issuesResolved: 38, issuesPending: 7, qualityScore: 81.2, performanceGrade: 'B+' },
  { engineerId: 'SE003', engineerName: 'Priya Patel', assignedProject: 'Steel Bridge Crossing', projectId: 'P-1003', taskCompletionRate: 96.1, avgHoursOnSite: 9.8, totalInspections: 35, issuesResolved: 52, issuesPending: 1, qualityScore: 94.7, performanceGrade: 'A+' },
  { engineerId: 'SE004', engineerName: 'Vikram Singh', assignedProject: 'West Park Towers', projectId: 'P-1001', taskCompletionRate: 78.9, avgHoursOnSite: 7.8, totalInspections: 18, issuesResolved: 30, issuesPending: 8, qualityScore: 73.5, performanceGrade: 'B' },
  { engineerId: 'SE005', engineerName: 'Meena Reddy', assignedProject: 'Lakeview Residences', projectId: 'P-1002', taskCompletionRate: 88.7, avgHoursOnSite: 8.9, totalInspections: 25, issuesResolved: 41, issuesPending: 4, qualityScore: 85.1, performanceGrade: 'A-' },
  { engineerId: 'SE006', engineerName: 'Suresh Nair', assignedProject: 'Steel Bridge Crossing', projectId: 'P-1003', taskCompletionRate: 71.2, avgHoursOnSite: 7.2, totalInspections: 15, issuesResolved: 22, issuesPending: 12, qualityScore: 66.8, performanceGrade: 'C+' },
];

const ALL_LOGS = [
  { logId: 'DL001', engineerId: 'SE001', engineerName: 'Rajesh Kumar', projectId: 'P-1001', projectName: 'West Park Towers', date: '2026-04-09', hoursOnSite: 9.5, tasksCompleted: 8, tasksAssigned: 10, issuesReported: 1, weatherCondition: 'Clear', remarks: 'Foundation pour completed for Block C' },
  { logId: 'DL002', engineerId: 'SE002', engineerName: 'Anil Sharma', projectId: 'P-1002', projectName: 'Lakeview Residences', date: '2026-04-09', hoursOnSite: 8.0, tasksCompleted: 6, tasksAssigned: 8, issuesReported: 2, weatherCondition: 'Partly Cloudy', remarks: 'Rebar inspection pending for Level 3' },
  { logId: 'DL003', engineerId: 'SE003', engineerName: 'Priya Patel', projectId: 'P-1003', projectName: 'Steel Bridge Crossing', date: '2026-04-09', hoursOnSite: 10.0, tasksCompleted: 12, tasksAssigned: 12, issuesReported: 0, weatherCondition: 'Clear', remarks: 'Girder placement ahead of schedule' },
  { logId: 'DL004', engineerId: 'SE004', engineerName: 'Vikram Singh', projectId: 'P-1001', projectName: 'West Park Towers', date: '2026-04-09', hoursOnSite: 7.5, tasksCompleted: 5, tasksAssigned: 8, issuesReported: 3, weatherCondition: 'Rainy', remarks: 'Electrical conduit delayed due to rain' },
  { logId: 'DL005', engineerId: 'SE001', engineerName: 'Rajesh Kumar', projectId: 'P-1001', projectName: 'West Park Towers', date: '2026-04-08', hoursOnSite: 9.0, tasksCompleted: 9, tasksAssigned: 10, issuesReported: 0, weatherCondition: 'Clear', remarks: 'Column reinforcement completed' },
  { logId: 'DL006', engineerId: 'SE003', engineerName: 'Priya Patel', projectId: 'P-1003', projectName: 'Steel Bridge Crossing', date: '2026-04-08', hoursOnSite: 9.5, tasksCompleted: 11, tasksAssigned: 11, issuesReported: 1, weatherCondition: 'Windy', remarks: 'Safety net installed on north side' },
  { logId: 'DL007', engineerId: 'SE002', engineerName: 'Anil Sharma', projectId: 'P-1002', projectName: 'Lakeview Residences', date: '2026-04-08', hoursOnSite: 8.5, tasksCompleted: 7, tasksAssigned: 9, issuesReported: 1, weatherCondition: 'Clear', remarks: 'Plumbing rough-in for floors 4-6' },
  { logId: 'DL008', engineerId: 'SE005', engineerName: 'Meena Reddy', projectId: 'P-1002', projectName: 'Lakeview Residences', date: '2026-04-09', hoursOnSite: 9.0, tasksCompleted: 8, tasksAssigned: 9, issuesReported: 1, weatherCondition: 'Clear', remarks: 'Concrete curing check for Level 2 slab' },
];

export default function SiteEngineerAnalytics() {
  const [engineers, setEngineers] = useState(null);
  const [summary, setSummary] = useState(null);
  const [logs, setLogs] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('performance');
  const [selectedProject, setSelectedProject] = useState('ALL');

  const fetchData = () => {
    setLoading(true);
    Promise.all([
      getSiteEngineerPerformance(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getSiteProgressSummary(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getSiteEngineerDailyLogs(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
    ]).then(([engRes, sumRes, logRes]) => {
      const filteredEng = selectedProject === 'ALL'
        ? ALL_ENGINEERS
        : ALL_ENGINEERS.filter((e) => e.projectId === selectedProject);

      const filteredLogs = selectedProject === 'ALL'
        ? ALL_LOGS
        : ALL_LOGS.filter((l) => l.projectId === selectedProject);

      setEngineers(engRes.data || filteredEng);

      const engList = engRes.data || filteredEng;
      const avgTaskRate = engList.length > 0 ? engList.reduce((s, e) => s + e.taskCompletionRate, 0) / engList.length : 0;
      const avgQuality = engList.length > 0 ? engList.reduce((s, e) => s + e.qualityScore, 0) / engList.length : 0;
      const totalOpen = engList.reduce((s, e) => s + e.issuesPending, 0);
      const totalResolved = engList.reduce((s, e) => s + e.issuesResolved, 0);
      const totalInsp = engList.reduce((s, e) => s + e.totalInspections, 0);

      setSummary(sumRes.data || {
        totalSiteEngineers: engList.length,
        activeSites: selectedProject === 'ALL' ? 3 : 1,
        avgTaskCompletionRate: avgTaskRate,
        avgQualityScore: avgQuality,
        totalIssuesOpen: totalOpen,
        totalIssuesResolved: totalResolved,
        inspectionsThisMonth: totalInsp,
        siteEfficiencyIndex: (avgTaskRate + avgQuality) / 2 * 0.85,
      });

      setLogs(logRes.data || filteredLogs);
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [selectedProject]);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading site engineer data...</p></div>;

  const filteredEngineers = engineers?.filter((e) =>
    e.engineerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    e.engineerId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    e.assignedProject?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const gradeColor = (grade) => {
    if (grade?.startsWith('A')) return 'badge-success';
    if (grade?.startsWith('B')) return 'badge-warning';
    return 'badge-danger';
  };

  const weatherIcon = (w) => {
    if (w?.toLowerCase().includes('rain')) return <CloudRain size={14} />;
    if (w?.toLowerCase().includes('wind')) return <Wind size={14} />;
    return <Sun size={14} />;
  };

  const radarData = filteredEngineers?.map((e) => ({
    engineer: e.engineerId,
    'Task Completion': e.taskCompletionRate,
    'Quality Score': e.qualityScore,
  }));

  const issuesPie = [
    { name: 'Resolved', value: summary?.totalIssuesResolved || 0 },
    { name: 'Open', value: summary?.totalIssuesOpen || 0 },
  ];

  const projectAllocation = engineers?.reduce((acc, e) => {
    const existing = acc.find((a) => a.project === e.assignedProject);
    if (existing) existing.count++;
    else acc.push({ project: e.assignedProject, count: 1 });
    return acc;
  }, []) || [];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Site Engineer Analytics</h2>
        <p>Monitor site engineer performance, daily logs, and site progress</p>
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

      {/* Stat Cards */}
      <div className="stat-cards">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><HardHat size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.totalSiteEngineers || 0}</span><span className="stat-label">Site Engineers</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><CheckCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.avgTaskCompletionRate?.toFixed(1)}%</span><span className="stat-label">Avg Task Completion</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f5f3ff', color: '#8b5cf6' }}><Star size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.avgQualityScore?.toFixed(1)}</span><span className="stat-label">Avg Quality Score</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}><AlertTriangle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.totalIssuesOpen || 0}</span><span className="stat-label">Open Issues</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><ClipboardList size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.inspectionsThisMonth || 0}</span><span className="stat-label">Inspections (Month)</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><Clock size={24} /></div>
          <div className="stat-info"><span className="stat-value">{summary?.siteEfficiencyIndex?.toFixed(1)}%</span><span className="stat-label">Efficiency Index</span></div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="charts-grid">
        <div className="chart-card chart-large">
          <h3>Engineer Performance Comparison</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={filteredEngineers}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="engineerId" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" domain={[0, 100]} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Legend />
              <Bar dataKey="taskCompletionRate" fill="#2563eb" name="Task Completion %" radius={[4, 4, 0, 0]} />
              <Bar dataKey="qualityScore" fill="#10b981" name="Quality Score" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card chart-small">
          <h3>Issues Overview</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart>
              <Pie data={issuesPie} cx="50%" cy="50%" innerRadius={55} outerRadius={95} paddingAngle={4} dataKey="value">
                <Cell fill="#10b981" />
                <Cell fill="#ef4444" />
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="charts-grid">
        <div className="chart-card chart-small">
          <h3>Engineers per Project</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie data={projectAllocation} cx="50%" cy="50%" innerRadius={50} outerRadius={90} paddingAngle={4} dataKey="count" nameKey="project">
                {projectAllocation.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card chart-large">
          <h3>Performance Radar</h3>
          <ResponsiveContainer width="100%" height={300}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="#e2e8f0" />
              <PolarAngleAxis dataKey="engineer" tick={{ fontSize: 11 }} stroke="#94a3b8" />
              <PolarRadiusAxis angle={30} domain={[0, 100]} stroke="#94a3b8" />
              <Radar name="Task Completion" dataKey="Task Completion" stroke="#2563eb" fill="#2563eb" fillOpacity={0.15} />
              <Radar name="Quality Score" dataKey="Quality Score" stroke="#10b981" fill="#10b981" fillOpacity={0.15} />
              <Tooltip />
              <Legend />
            </RadarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Tab Toggle */}
      <div className="login-mode-toggle" style={{ marginBottom: '1.5rem', maxWidth: '400px' }}>
        <button className={`mode-btn ${activeTab === 'performance' ? 'active' : ''}`} onClick={() => setActiveTab('performance')}>
          Performance
        </button>
        <button className={`mode-btn ${activeTab === 'daily-logs' ? 'active' : ''}`} onClick={() => setActiveTab('daily-logs')}>
          Daily Logs
        </button>
      </div>

      {activeTab === 'performance' ? (
        /* Performance Table */
        <div className="table-card">
          <div className="table-header">
            <h3>Engineer Performance</h3>
            <div className="search-box">
              <Search size={16} />
              <input
                type="text"
                placeholder="Search engineers..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Assigned Project</th>
                <th>Task Completion</th>
                <th>Avg Hours/Day</th>
                <th>Inspections</th>
                <th>Issues (Resolved/Open)</th>
                <th>Quality</th>
                <th>Grade</th>
              </tr>
            </thead>
            <tbody>
              {filteredEngineers?.map((e) => (
                <tr key={e.engineerId}>
                  <td><strong>{e.engineerId}</strong></td>
                  <td>{e.engineerName}</td>
                  <td>{e.assignedProject}</td>
                  <td>
                    <div className="progress-bar-wrapper">
                      <div className="progress-bar" style={{
                        width: `${e.taskCompletionRate}%`,
                        backgroundColor: e.taskCompletionRate >= 90 ? '#10b981' : e.taskCompletionRate >= 75 ? '#f59e0b' : '#ef4444'
                      }} />
                      <span>{e.taskCompletionRate}%</span>
                    </div>
                  </td>
                  <td>{e.avgHoursOnSite}h</td>
                  <td>{e.totalInspections}</td>
                  <td>
                    <span style={{ color: '#10b981' }}>{e.issuesResolved}</span>
                    {' / '}
                    <span style={{ color: '#ef4444' }}>{e.issuesPending}</span>
                  </td>
                  <td>
                    <div className="progress-bar-wrapper">
                      <div className="progress-bar" style={{
                        width: `${e.qualityScore}%`,
                        backgroundColor: e.qualityScore >= 85 ? '#10b981' : e.qualityScore >= 70 ? '#f59e0b' : '#ef4444'
                      }} />
                      <span>{e.qualityScore}</span>
                    </div>
                  </td>
                  <td><span className={`badge ${gradeColor(e.performanceGrade)}`}>{e.performanceGrade}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        /* Daily Logs Table */
        <div className="table-card">
          <div className="table-header">
            <h3>Daily Site Logs</h3>
          </div>
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Engineer</th>
                <th>Project</th>
                <th>Hours</th>
                <th>Tasks (Done/Total)</th>
                <th>Issues</th>
                <th>Weather</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody>
              {logs?.map((log) => (
                <tr key={log.logId}>
                  <td><strong>{log.date}</strong></td>
                  <td>{log.engineerName}</td>
                  <td>{log.projectName}</td>
                  <td>{log.hoursOnSite}h</td>
                  <td>
                    <span style={{ color: log.tasksCompleted === log.tasksAssigned ? '#10b981' : '#f59e0b' }}>
                      {log.tasksCompleted}/{log.tasksAssigned}
                    </span>
                  </td>
                  <td>
                    {log.issuesReported > 0 ? (
                      <span className="badge badge-danger">{log.issuesReported}</span>
                    ) : (
                      <span className="badge badge-success">0</span>
                    )}
                  </td>
                  <td>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      {weatherIcon(log.weatherCondition)}
                      {log.weatherCondition}
                    </span>
                  </td>
                  <td style={{ maxWidth: '250px', fontSize: '0.8rem', color: '#64748b' }}>{log.remarks}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
