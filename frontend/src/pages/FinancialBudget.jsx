import { useEffect, useState } from 'react';
import { getBudgetVariance, getCashFlow } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  LineChart, Line, Legend, ComposedChart, Area,
} from 'recharts';
import { DollarSign, TrendingUp, TrendingDown, AlertTriangle, Loader2 } from 'lucide-react';

const PROJECTS = [
  { id: 'P-1001', name: 'West Park Towers' },
  { id: 'P-1002', name: 'Lakeview Residences' },
  { id: 'P-1003', name: 'Steel Bridge Crossing' },
  { id: 'P-1004', name: 'Residential Complex Alpha' },
  { id: 'P-1005', name: 'Water Treatment Plant' },
];

export default function FinancialBudget() {
  const [budgetData, setBudgetData] = useState(null);
  const [cashFlow, setCashFlow] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedProject, setSelectedProject] = useState('P-1001');

  const fetchData = async () => {
    setLoading(true);
    const [budgetRes, cashRes] = await Promise.all([
      getBudgetVariance(selectedProject).catch(() => ({ data: null })),
      getCashFlow().catch(() => ({ data: null })),
    ]);

    setBudgetData(budgetRes.data || {
      projectId: selectedProject,
      totalBudget: 2500000,
      spent: 1875000,
      remaining: 625000,
      variance: -3.5,
      categories: [
        { category: 'Labor', budgeted: 800000, actual: 845000, variance: -5.6 },
        { category: 'Materials', budgeted: 650000, actual: 612000, variance: 5.8 },
        { category: 'Equipment', budgeted: 450000, actual: 468000, variance: -4.0 },
        { category: 'Subcontract', budgeted: 350000, actual: 332000, variance: 5.1 },
        { category: 'Overhead', budgeted: 250000, actual: 238000, variance: 4.8 },
      ],
    });

    setCashFlow(cashRes.data || [
      { month: 'Jan', inflow: 420000, outflow: 380000 },
      { month: 'Feb', inflow: 380000, outflow: 410000 },
      { month: 'Mar', inflow: 520000, outflow: 450000 },
      { month: 'Apr', inflow: 460000, outflow: 475000 },
      { month: 'May', inflow: 510000, outflow: 390000 },
      { month: 'Jun', inflow: 480000, outflow: 420000 },
    ]);

    setLoading(false);
  };

  useEffect(() => {
    fetchData();
  }, [selectedProject]);

  if (loading) return <div className="loading-spinner"><Loader2 className="spin" size={40} /><p>Loading financial data...</p></div>;

  const utilizationPct = budgetData ? Math.round((budgetData.spent / budgetData.totalBudget) * 100) : 0;
  const formatCurrency = (val) => `$${(val / 1000).toFixed(0)}K`;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Financial & Budget</h2>
        <p>Track budget utilization, variance analysis, and cash flow</p>
      </div>

      <div className="stat-cards">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><DollarSign size={24} /></div>
          <div className="stat-info"><span className="stat-value">{formatCurrency(budgetData?.totalBudget || 0)}</span><span className="stat-label">Total Budget</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#ef4444' }}><TrendingDown size={24} /></div>
          <div className="stat-info"><span className="stat-value">{formatCurrency(budgetData?.spent || 0)}</span><span className="stat-label">Total Spent</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#10b981' }}><TrendingUp size={24} /></div>
          <div className="stat-info"><span className="stat-value">{formatCurrency(budgetData?.remaining || 0)}</span><span className="stat-label">Remaining</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#f59e0b' }}><AlertTriangle size={24} /></div>
          <div className="stat-info">
            <span className="stat-value" style={{ color: budgetData?.variance < 0 ? '#ef4444' : '#10b981' }}>
              {budgetData?.variance > 0 ? '+' : ''}{budgetData?.variance}%
            </span>
            <span className="stat-label">Budget Variance</span>
          </div>
        </div>
      </div>

      {/* Project Selector */}
      <div className="filter-bar">
        <label>Project:</label>
        <select value={selectedProject} onChange={(e) => setSelectedProject(e.target.value)}>
          {PROJECTS.map((p) => (
            <option key={p.id} value={p.id}>{p.id} - {p.name}</option>
          ))}
        </select>
      </div>

      {/* Budget Utilization Bar */}
      <div className="chart-card" style={{ marginBottom: '1.5rem' }}>
        <h3>Budget Utilization — {utilizationPct}%</h3>
        <div className="budget-progress">
          <div className="budget-bar" style={{
            width: `${utilizationPct}%`,
            backgroundColor: utilizationPct > 90 ? '#ef4444' : utilizationPct > 75 ? '#f59e0b' : '#10b981',
          }} />
        </div>
        <div className="budget-labels">
          <span>Spent: {formatCurrency(budgetData?.spent || 0)}</span>
          <span>Budget: {formatCurrency(budgetData?.totalBudget || 0)}</span>
        </div>
      </div>

      <div className="charts-grid">
        {/* Variance Chart */}
        <div className="chart-card chart-large">
          <h3>Budget vs Actual by Category</h3>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={budgetData?.categories} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis type="number" stroke="#94a3b8" tickFormatter={(v) => `$${(v / 1000).toFixed(0)}K`} />
              <YAxis dataKey="category" type="category" stroke="#94a3b8" width={100} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} formatter={(v) => formatCurrency(v)} />
              <Legend />
              <Bar dataKey="budgeted" fill="#2563eb" name="Budgeted" radius={[0, 4, 4, 0]} />
              <Bar dataKey="actual" fill="#f59e0b" name="Actual" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Cash Flow */}
        <div className="chart-card chart-small">
          <h3>Cash Flow</h3>
          <ResponsiveContainer width="100%" height={320}>
            <ComposedChart data={cashFlow}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="month" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" tickFormatter={(v) => `${(v / 1000).toFixed(0)}K`} />
              <Tooltip contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0' }} formatter={(v) => formatCurrency(v)} />
              <Legend />
              <Area type="monotone" dataKey="inflow" fill="#10b981" fillOpacity={0.1} stroke="#10b981" name="Inflow" />
              <Line type="monotone" dataKey="outflow" stroke="#ef4444" name="Outflow" strokeWidth={2} />
            </ComposedChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Categories Table */}
      <div className="table-card">
        <div className="table-header"><h3>Category Breakdown</h3></div>
        <table>
          <thead>
            <tr><th>Category</th><th>Budgeted</th><th>Actual</th><th>Variance</th><th>Status</th></tr>
          </thead>
          <tbody>
            {budgetData?.categories?.map((c) => (
              <tr key={c.category}>
                <td><strong>{c.category}</strong></td>
                <td>{formatCurrency(c.budgeted)}</td>
                <td>{formatCurrency(c.actual)}</td>
                <td style={{ color: c.variance < 0 ? '#ef4444' : '#10b981' }}>
                  {c.variance > 0 ? '+' : ''}{c.variance}%
                </td>
                <td>
                  <span className={`badge ${c.variance >= 0 ? 'badge-success' : Math.abs(c.variance) > 5 ? 'badge-danger' : 'badge-warning'}`}>
                    {c.variance >= 0 ? 'Under Budget' : Math.abs(c.variance) > 5 ? 'Over Budget' : 'Slight Over'}
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
