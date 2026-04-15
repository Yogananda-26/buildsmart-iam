import { useEffect, useState } from 'react';
import { getReportHistory, exportReport } from '../services/api';
import { FileText, Download, Filter, Loader2, Search, ChevronLeft, ChevronRight } from 'lucide-react';

const SCOPES = ['PROJECT', 'SAFETY', 'FINANCE', 'RESOURCE', 'VENDOR', 'SITE_ENGINEER'];

export default function ReportHistory() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [scope, setScope] = useState('PROJECT');
  const [searchTerm, setSearchTerm] = useState('');
  const [exporting, setExporting] = useState('');
  const [page, setPage] = useState(1);
  const perPage = 8;

  useEffect(() => {
    setLoading(true);
    getReportHistory(scope)
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : [res.data];
        setReports(data);
      })
      .catch(() => {
        // Fallback data
        setReports([
          { reportId: 'BSRA001', scope: 'PROJECT', generatedDate: '2024-01-15T10:30:00Z', metrics: 'Project health and milestone report', format: 'PDF' },
          { reportId: 'BSRA002', scope: 'PROJECT', generatedDate: '2024-01-14T09:15:00Z', metrics: 'Weekly progress summary', format: 'PDF' },
          { reportId: 'BSRA003', scope: 'SAFETY', generatedDate: '2024-01-13T14:00:00Z', metrics: 'Safety compliance audit', format: 'EXCEL' },
          { reportId: 'BSRA004', scope: 'FINANCE', generatedDate: '2024-01-12T11:45:00Z', metrics: 'Monthly budget variance analysis', format: 'PDF' },
          { reportId: 'BSRA005', scope: 'RESOURCE', generatedDate: '2024-01-11T08:20:00Z', metrics: 'Workforce allocation report', format: 'PDF' },
          { reportId: 'BSRA006', scope: 'PROJECT', generatedDate: '2024-01-10T16:30:00Z', metrics: 'Quarter review dashboard', format: 'EXCEL' },
          { reportId: 'BSRA007', scope: 'SAFETY', generatedDate: '2024-01-09T13:00:00Z', metrics: 'Incident trend analysis', format: 'PDF' },
          { reportId: 'BSRA008', scope: 'FINANCE', generatedDate: '2024-01-08T10:00:00Z', metrics: 'Cash flow projection', format: 'PDF' },
          { reportId: 'BSRA009', scope: 'RESOURCE', generatedDate: '2024-01-07T09:30:00Z', metrics: 'Equipment utilization report', format: 'EXCEL' },
          { reportId: 'BSRA010', scope: 'PROJECT', generatedDate: '2024-01-06T15:00:00Z', metrics: 'Risk assessment overview', format: 'PDF' },
        ]);
      })
      .finally(() => { setLoading(false); setPage(1); });
  }, [scope]);

  const handleExport = async (reportId) => {
    setExporting(reportId);
    try {
      await exportReport(reportId);
      alert(`Report ${reportId} exported successfully!`);
    } catch {
      alert('Export failed. Please try again.');
    } finally {
      setExporting('');
    }
  };

  const filtered = reports.filter((r) =>
    r.reportId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    r.metrics?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filtered.length / perPage);
  const paginated = filtered.slice((page - 1) * perPage, page * perPage);

  const formatDate = (d) => {
    try { return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }); }
    catch { return d; }
  };

  const scopeColor = (s) => {
    const map = { PROJECT: 'badge-info', SAFETY: 'badge-success', FINANCE: 'badge-warning', RESOURCE: 'badge-purple' };
    return map[s] || 'badge-info';
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Report History</h2>
        <p>Browse and export previously generated reports</p>
      </div>

      <div className="stat-cards" style={{ gridTemplateColumns: 'repeat(4, 1fr)' }}>
        {SCOPES.map((s) => (
          <div
            key={s}
            className={`stat-card clickable ${scope === s ? 'active' : ''}`}
            onClick={() => setScope(s)}
            style={{ cursor: 'pointer', border: scope === s ? '2px solid #2563eb' : undefined }}
          >
            <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}><FileText size={20} /></div>
            <div className="stat-info">
              <span className="stat-value">{s}</span>
              <span className="stat-label">Scope</span>
            </div>
          </div>
        ))}
      </div>

      <div className="table-card">
        <div className="table-header">
          <h3>Reports — {scope}</h3>
          <div className="search-box">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search reports..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        {loading ? (
          <div className="loading-spinner" style={{ padding: '3rem' }}><Loader2 className="spin" size={30} /></div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Report ID</th>
                  <th>Scope</th>
                  <th>Description</th>
                  <th>Generated Date</th>
                  <th>Format</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((r) => (
                  <tr key={r.reportId}>
                    <td><strong>{r.reportId}</strong></td>
                    <td><span className={`badge ${scopeColor(r.scope)}`}>{r.scope}</span></td>
                    <td>{r.metrics}</td>
                    <td>{formatDate(r.generatedDate)}</td>
                    <td><span className="badge badge-outline">{r.format || 'PDF'}</span></td>
                    <td>
                      <button
                        className="btn btn-outline btn-sm"
                        onClick={() => handleExport(r.reportId)}
                        disabled={exporting === r.reportId}
                      >
                        {exporting === r.reportId ? <Loader2 size={14} className="spin" /> : <Download size={14} />}
                        <span style={{ marginLeft: '4px' }}>Export</span>
                      </button>
                    </td>
                  </tr>
                ))}
                {paginated.length === 0 && (
                  <tr><td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>No reports found</td></tr>
                )}
              </tbody>
            </table>

            {totalPages > 1 && (
              <div className="pagination">
                <button disabled={page <= 1} onClick={() => setPage(page - 1)}><ChevronLeft size={16} /></button>
                <span>Page {page} of {totalPages}</span>
                <button disabled={page >= totalPages} onClick={() => setPage(page + 1)}><ChevronRight size={16} /></button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
