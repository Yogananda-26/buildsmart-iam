import { useEffect, useState } from 'react';
import { getVendorPerformance, getVendorCompliance, getVendorSpend } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, RadarChart, Radar, PolarGrid,
  PolarAngleAxis, PolarRadiusAxis,
} from 'recharts';
import { Truck, CheckCircle, AlertTriangle, XCircle, Star, Loader2, Search } from 'lucide-react';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

const PROJECTS = [
  { id: 'ALL', name: 'All Projects' },
  { id: 'P-1001', name: 'West Park Towers' },
  { id: 'P-1002', name: 'Lakeview Residences' },
  { id: 'P-1003', name: 'Steel Bridge Crossing' },
  { id: 'P-1004', name: 'Residential Complex Alpha' },
  { id: 'P-1005', name: 'Water Treatment Plant' },
];

const ALL_VENDORS = [
  { vendorId: 'VND001', vendorName: 'Titan Steel Suppliers', projectId: 'P-1001', projectName: 'West Park Towers', onTimeDeliveryRate: 94.5, qualityScore: 88.2, costVariance: -2.3, activeContracts: 3, overallRating: 'A' },
  { vendorId: 'VND002', vendorName: 'QuickMix Concrete', projectId: 'P-1001', projectName: 'West Park Towers', onTimeDeliveryRate: 87.3, qualityScore: 82.5, costVariance: 1.8, activeContracts: 2, overallRating: 'B+' },
  { vendorId: 'VND003', vendorName: 'ElectraPower Solutions', projectId: 'P-1002', projectName: 'Lakeview Residences', onTimeDeliveryRate: 96.1, qualityScore: 91.7, costVariance: -0.5, activeContracts: 4, overallRating: 'A+' },
  { vendorId: 'VND004', vendorName: 'SafeGuard Equipment', projectId: 'P-1002', projectName: 'Lakeview Residences', onTimeDeliveryRate: 78.8, qualityScore: 74.3, costVariance: 5.2, activeContracts: 1, overallRating: 'B' },
  { vendorId: 'VND005', vendorName: 'GreenScape Landscaping', projectId: 'P-1003', projectName: 'Steel Bridge Crossing', onTimeDeliveryRate: 65.2, qualityScore: 58.9, costVariance: 12.4, activeContracts: 0, overallRating: 'C' },
  { vendorId: 'VND006', vendorName: 'PrimePlumb Services', projectId: 'P-1003', projectName: 'Steel Bridge Crossing', onTimeDeliveryRate: 91.0, qualityScore: 85.6, costVariance: -1.1, activeContracts: 2, overallRating: 'A-' },
  { vendorId: 'VND007', vendorName: 'AeroLift Crane Hire', projectId: 'P-1004', projectName: 'Residential Complex Alpha', onTimeDeliveryRate: 93.7, qualityScore: 90.1, costVariance: -3.4, activeContracts: 3, overallRating: 'A' },
  { vendorId: 'VND008', vendorName: 'ClearView Glass Works', projectId: 'P-1005', projectName: 'Water Treatment Plant', onTimeDeliveryRate: 72.4, qualityScore: 69.8, costVariance: 8.7, activeContracts: 1, overallRating: 'C+' },
];

const ALL_SPEND = [
  { category: 'Materials', budgeted: 850000, actual: 812000, variance: 38000, vendorCount: 3, projectId: 'P-1001' },
  { category: 'Electrical', budgeted: 420000, actual: 445000, variance: -25000, vendorCount: 1, projectId: 'P-1002' },
  { category: 'Safety Equipment', budgeted: 180000, actual: 165000, variance: 15000, vendorCount: 1, projectId: 'P-1002' },
  { category: 'Heavy Equipment', budgeted: 620000, actual: 598000, variance: 22000, vendorCount: 1, projectId: 'P-1003' },
  { category: 'Plumbing', budgeted: 290000, actual: 305000, variance: -15000, vendorCount: 1, projectId: 'P-1003' },
  { category: 'Landscaping', budgeted: 150000, actual: 172000, variance: -22000, vendorCount: 1, projectId: 'P-1004' },
  { category: 'Glass & Facade', budgeted: 310000, actual: 335000, variance: -25000, vendorCount: 1, projectId: 'P-1005' },
  { category: 'Concrete', budgeted: 480000, actual: 460000, variance: 20000, vendorCount: 2, projectId: 'P-1001' },
];

export default function VendorAnalytics() {
  const [vendors, setVendors] = useState(null);
  const [compliance, setCompliance] = useState(null);
  const [spend, setSpend] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedProject, setSelectedProject] = useState('ALL');

  const fetchData = () => {
    setLoading(true);
    Promise.all([
      getVendorPerformance(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getVendorCompliance(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
      getVendorSpend(selectedProject !== 'ALL' ? selectedProject : undefined).catch(() => ({ data: null })),
    ]).then(([vendorRes, compRes, spendRes]) => {
      const filteredVendorsFallback = selectedProject === 'ALL'
        ? ALL_VENDORS
        : ALL_VENDORS.filter((v) => v.projectId === selectedProject);

      const filteredSpendFallback = selectedProject === 'ALL'
        ? ALL_SPEND
        : ALL_SPEND.filter((s) => s.projectId === selectedProject);

      setVendors(vendorRes.data || filteredVendorsFallback);

      const vList = vendorRes.data || filteredVendorsFallback;
      const compliant = vList.filter((v) => v.overallRating?.startsWith('A')).length;
      const nonCompliant = vList.filter((v) => v.overallRating?.startsWith('C')).length;
      const pending = vList.length - compliant - nonCompliant;

      setCompliance(compRes.data || {
        totalVendors: vList.length,
        compliantVendors: compliant,
        nonCompliantVendors: nonCompliant,
        pendingReview: pending,
        complianceRate: vList.length > 0 ? ((compliant / vList.length) * 100) : 0,
        contractsExpiringSoon: Math.max(1, Math.floor(vList.length / 4)),
      });

      setSpend(spendRes.data || filteredSpendFallback);
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [selectedProject]);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading vendor data...</p></div>;

  const filteredVendors = vendors?.filter((v) =>
    v.vendorName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    v.vendorId?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const compliancePie = compliance ? [
    { name: 'Compliant', value: compliance.compliantVendors },
    { name: 'Non-Compliant', value: compliance.nonCompliantVendors },
    { name: 'Pending Review', value: compliance.pendingReview },
  ] : [];

  const radarData = filteredVendors?.slice(0, 6).map((v) => ({
    vendor: v.vendorId,
    'On-Time Delivery': v.onTimeDeliveryRate,
    'Quality Score': v.qualityScore,
  }));

  const ratingColor = (rating) => {
    if (rating?.startsWith('A')) return 'badge-success';
    if (rating?.startsWith('B')) return 'badge-warning';
    return 'badge-danger';
  };

  const formatCurrency = (val) => `$${(val / 1000).toFixed(0)}K`;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Vendor Analytics</h2>
        <p>Monitor vendor performance, compliance, and spend analysis</p>
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
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><Truck size={24} /></div>
          <div className="stat-info"><span className="stat-value">{compliance?.totalVendors || 0}</span><span className="stat-label">Total Vendors</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><CheckCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{compliance?.compliantVendors || 0}</span><span className="stat-label">Compliant</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><XCircle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{compliance?.nonCompliantVendors || 0}</span><span className="stat-label">Non-Compliant</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}><AlertTriangle size={24} /></div>
          <div className="stat-info"><span className="stat-value">{compliance?.contractsExpiringSoon || 0}</span><span className="stat-label">Contracts Expiring</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f5f3ff', color: '#8b5cf6' }}><Star size={24} /></div>
          <div className="stat-info"><span className="stat-value">{compliance?.complianceRate?.toFixed(1) || 0}%</span><span className="stat-label">Compliance Rate</span></div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="charts-grid">
        {/* Spend by Category */}
        <div className="chart-card chart-large">
          <h3>Vendor Spend: Budget vs Actual</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={spend} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis type="number" stroke="#94a3b8" tickFormatter={(v) => formatCurrency(v)} />
              <YAxis dataKey="category" type="category" stroke="#94a3b8" width={120} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} formatter={(v) => formatCurrency(v)} />
              <Legend />
              <Bar dataKey="budgeted" fill="#2563eb" name="Budgeted" radius={[0, 4, 4, 0]} />
              <Bar dataKey="actual" fill="#f59e0b" name="Actual" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Compliance Pie */}
        <div className="chart-card chart-small">
          <h3>Vendor Compliance</h3>
          <ResponsiveContainer width="100%" height={320}>
            <PieChart>
              <Pie data={compliancePie} cx="50%" cy="50%" innerRadius={55} outerRadius={95} paddingAngle={4} dataKey="value">
                {compliancePie.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Performance Chart Row */}
      <div className="charts-grid">
        {/* Delivery & Quality Bar */}
        <div className="chart-card chart-large">
          <h3>Vendor Performance Scores</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={filteredVendors}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="vendorId" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" domain={[0, 100]} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} />
              <Legend />
              <Bar dataKey="onTimeDeliveryRate" fill="#2563eb" name="On-Time Delivery %" radius={[4, 4, 0, 0]} />
              <Bar dataKey="qualityScore" fill="#10b981" name="Quality Score" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Radar Chart */}
        <div className="chart-card chart-small">
          <h3>Performance Radar (Top 6)</h3>
          <ResponsiveContainer width="100%" height={320}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="#e2e8f0" />
              <PolarAngleAxis dataKey="vendor" tick={{ fontSize: 11 }} stroke="#94a3b8" />
              <PolarRadiusAxis angle={30} domain={[0, 100]} stroke="#94a3b8" />
              <Radar name="On-Time Delivery" dataKey="On-Time Delivery" stroke="#2563eb" fill="#2563eb" fillOpacity={0.15} />
              <Radar name="Quality Score" dataKey="Quality Score" stroke="#10b981" fill="#10b981" fillOpacity={0.15} />
              <Tooltip />
              <Legend />
            </RadarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Vendor Table */}
      <div className="table-card">
        <div className="table-header">
          <h3>All Vendors</h3>
          <div className="search-box">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search vendors..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
        <table>
          <thead>
            <tr>
              <th>Vendor ID</th>
              <th>Name</th>
              <th>Project</th>
              <th>On-Time Delivery</th>
              <th>Quality Score</th>
              <th>Cost Variance</th>
              <th>Active Contracts</th>
              <th>Rating</th>
            </tr>
          </thead>
          <tbody>
            {filteredVendors?.map((v) => (
              <tr key={v.vendorId}>
                <td><strong>{v.vendorId}</strong></td>
                <td>{v.vendorName}</td>
                <td>{v.projectName || '-'}</td>
                <td>
                  <div className="progress-bar-wrapper">
                    <div className="progress-bar" style={{ width: `${v.onTimeDeliveryRate}%`, backgroundColor: v.onTimeDeliveryRate >= 90 ? '#10b981' : v.onTimeDeliveryRate >= 75 ? '#f59e0b' : '#ef4444' }} />
                    <span>{v.onTimeDeliveryRate}%</span>
                  </div>
                </td>
                <td>
                  <div className="progress-bar-wrapper">
                    <div className="progress-bar" style={{ width: `${v.qualityScore}%`, backgroundColor: v.qualityScore >= 80 ? '#10b981' : v.qualityScore >= 60 ? '#f59e0b' : '#ef4444' }} />
                    <span>{v.qualityScore}</span>
                  </div>
                </td>
                <td style={{ color: v.costVariance <= 0 ? '#10b981' : '#ef4444' }}>
                  {v.costVariance > 0 ? '+' : ''}{v.costVariance}%
                </td>
                <td>{v.activeContracts}</td>
                <td><span className={`badge ${ratingColor(v.overallRating)}`}>{v.overallRating}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Spend Breakdown Table */}
      <div className="table-card">
        <div className="table-header"><h3>Spend by Category</h3></div>
        <table>
          <thead>
            <tr>
              <th>Category</th>
              <th>Budgeted</th>
              <th>Actual</th>
              <th>Variance</th>
              <th>Vendors</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {spend?.map((s) => (
              <tr key={s.category}>
                <td><strong>{s.category}</strong></td>
                <td>{formatCurrency(s.budgeted)}</td>
                <td>{formatCurrency(s.actual)}</td>
                <td style={{ color: s.variance >= 0 ? '#10b981' : '#ef4444' }}>
                  {s.variance >= 0 ? '+' : ''}{formatCurrency(s.variance)}
                </td>
                <td>{s.vendorCount}</td>
                <td>
                  <span className={`badge ${s.variance >= 0 ? 'badge-success' : 'badge-danger'}`}>
                    {s.variance >= 0 ? 'Under Budget' : 'Over Budget'}
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
