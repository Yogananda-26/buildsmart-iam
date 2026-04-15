import { useState } from 'react';
import { generateReport } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { FilePlus, CheckCircle, Loader2, AlertTriangle } from 'lucide-react';

const SCOPES = ['PROJECT', 'SAFETY', 'FINANCE', 'RESOURCE', 'VENDOR', 'SITE_ENGINEER'];

export default function GenerateReport() {
  const [scope, setScope] = useState('PROJECT');
  const [metrics, setMetrics] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    setLoading(true);

    try {
      const res = await generateReport({ scope, metrics });
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate report. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const scopeDescriptions = {
    PROJECT: 'Generate reports on project progress, milestones, health scores, and schedule performance.',
    SAFETY: 'Generate safety compliance reports including incidents, inspections, and compliance trends.',
    FINANCE: 'Generate financial reports covering budget variance, cash flow, and cost analysis.',
    RESOURCE: 'Generate workforce and resource reports including utilization and labor allocation.',
    VENDOR: 'Generate vendor analytics reports covering performance, compliance, and spend analysis.',
    SITE_ENGINEER: 'Generate site engineer reports covering daily logs, task completion, inspections, and quality scores.',
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Generate Report</h2>
        <p>Create a new analytics report for any module</p>
      </div>

      <div className="generate-layout">
        <div className="generate-form-card">
          <form onSubmit={handleSubmit}>
            {/* Scope Selection */}
            <div className="form-group">
              <label>Report Scope</label>
              <div className="scope-grid">
                {SCOPES.map((s) => (
                  <div
                    key={s}
                    className={`scope-option ${scope === s ? 'selected' : ''}`}
                    onClick={() => setScope(s)}
                  >
                    <FilePlus size={20} />
                    <span>{s}</span>
                  </div>
                ))}
              </div>
              <p className="scope-desc">{scopeDescriptions[scope]}</p>
            </div>

            {/* Metrics */}
            <div className="form-group">
              <label htmlFor="metrics">Report Description / Metrics</label>
              <textarea
                id="metrics"
                rows={4}
                value={metrics}
                onChange={(e) => setMetrics(e.target.value)}
                placeholder="Describe the metrics and data points you want in this report..."
                required
              />
            </div>

            {error && (
              <div className="form-error">
                <AlertTriangle size={16} />
                {error}
              </div>
            )}

            <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
              {loading ? (
                <>
                  <Loader2 size={18} className="spin" />
                  Generating Report...
                </>
              ) : (
                <>
                  <FilePlus size={18} />
                  Generate Report
                </>
              )}
            </button>
          </form>
        </div>

        {/* Result */}
        {result && (
          <div className="generate-result-card">
            <div className="result-header">
              <CheckCircle size={40} color="#10b981" />
              <h3>Report Generated Successfully!</h3>
            </div>
            <div className="result-details">
              <div className="result-row">
                <span className="result-label">Report ID</span>
                <span className="result-value"><strong>{result.reportId}</strong></span>
              </div>
              <div className="result-row">
                <span className="result-label">Scope</span>
                <span className={`badge badge-${result.scope?.toLowerCase() || 'info'}`}>{result.scope}</span>
              </div>
              <div className="result-row">
                <span className="result-label">Generated Date</span>
                <span className="result-value">{new Date(result.generatedDate).toLocaleString()}</span>
              </div>
              <div className="result-row">
                <span className="result-label">Metrics</span>
                <span className="result-value">{result.metrics}</span>
              </div>
            </div>
            <div className="result-actions">
              <button className="btn btn-primary" onClick={() => navigate('/report-history')}>
                View History
              </button>
              <button className="btn btn-outline" onClick={() => { setResult(null); setMetrics(''); }}>
                Generate Another
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
