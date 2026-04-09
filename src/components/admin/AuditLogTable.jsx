import React, { useState, useEffect } from 'react';
import { Table, Form, Row, Col, Button, Badge } from 'react-bootstrap';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';
import LoadingSpinner from '../common/LoadingSpinner';

const ACTION_COLORS = {
  LOGIN: 'primary',
  LOGOUT: 'secondary',
  SIGNUP: 'success',
  PASSWORD_CHANGE: 'warning',
  PROFILE_UPDATE: 'info',
  USER_APPROVED: 'success',
  USER_REJECTED: 'danger',
  USER_DELETED: 'danger',
};

const AuditLogTable = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [actionFilter, setActionFilter] = useState('');
  const [userIdFilter, setUserIdFilter] = useState('');
  const pageSize = 20;

  useEffect(() => {
    fetchLogs();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      let url = `/admin/audit/logs?page=${page}&size=${pageSize}&sortBy=timestamp&sortDir=desc`;
      const response = await API.get(url);
      const data = response.data.data || response.data;

      if (data.content) {
        // Paginated response
        setLogs(data.content);
        setTotalPages(data.totalPages || 1);
      } else if (Array.isArray(data)) {
        setLogs(data);
        setTotalPages(1);
      }
    } catch (err) {
      toast.error('Failed to load audit logs');
    } finally {
      setLoading(false);
    }
  };

  const filterByAction = async () => {
    if (!actionFilter) { fetchLogs(); return; }
    setLoading(true);
    try {
      const response = await API.get(`/admin/audit/action/${actionFilter}`);
      const data = response.data.data || response.data || [];
      setLogs(Array.isArray(data) ? data : []);
      setTotalPages(1);
    } catch (err) {
      toast.error('Failed to filter by action');
    } finally {
      setLoading(false);
    }
  };

  const filterByUser = async () => {
    if (!userIdFilter) { fetchLogs(); return; }
    setLoading(true);
    try {
      const response = await API.get(`/admin/audit/user/${userIdFilter}`);
      const data = response.data.data || response.data || [];
      setLogs(Array.isArray(data) ? data : []);
      setTotalPages(1);
    } catch (err) {
      toast.error('Failed to filter by user');
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setActionFilter('');
    setUserIdFilter('');
    setPage(0);
    fetchLogs();
  };

  if (loading) return <LoadingSpinner message="Loading audit logs..." />;

  return (
    <>
      {/* Filters */}
      <Row className="mb-3 g-2">
        <Col md={3}>
          <Form.Select value={actionFilter} onChange={(e) => setActionFilter(e.target.value)}>
            <option value="">Filter by Action</option>
            {Object.keys(ACTION_COLORS).map(a => (
              <option key={a} value={a}>{a.replace(/_/g, ' ')}</option>
            ))}
          </Form.Select>
        </Col>
        <Col md={2}>
          <Button variant="outline-primary" className="w-100" onClick={filterByAction}>Apply</Button>
        </Col>
        <Col md={3}>
          <Form.Control
            placeholder="Filter by User ID"
            value={userIdFilter}
            onChange={(e) => setUserIdFilter(e.target.value)}
          />
        </Col>
        <Col md={2}>
          <Button variant="outline-primary" className="w-100" onClick={filterByUser}>Apply</Button>
        </Col>
        <Col md={2}>
          <Button variant="outline-secondary" className="w-100" onClick={clearFilters}>Clear</Button>
        </Col>
      </Row>

      <Table responsive hover size="sm" className="align-middle">
        <thead style={{ backgroundColor: '#1a365d', color: 'white' }}>
          <tr>
            <th>ID</th>
            <th>User ID</th>
            <th>Action</th>
            <th>Resource</th>
            <th>Details</th>
            <th>IP Address</th>
            <th>Timestamp</th>
          </tr>
        </thead>
        <tbody>
          {logs.length === 0 ? (
            <tr><td colSpan="7" className="text-center text-muted py-4">No audit logs found</td></tr>
          ) : (
            logs.map((log) => (
              <tr key={log.auditId}>
                <td>{log.auditId}</td>
                <td><code>{log.userId}</code></td>
                <td>
                  <Badge bg={ACTION_COLORS[log.action] || 'secondary'}>
                    {log.action?.replace(/_/g, ' ')}
                  </Badge>
                </td>
                <td className="text-truncate" style={{ maxWidth: '200px' }}>{log.resource}</td>
                <td className="text-truncate" style={{ maxWidth: '250px' }}>{log.details}</td>
                <td>{log.ipAddress}</td>
                <td>{log.timestamp ? new Date(log.timestamp).toLocaleString() : '-'}</td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="d-flex justify-content-center gap-2 mt-3">
          <Button variant="outline-primary" size="sm" disabled={page === 0} onClick={() => setPage(page - 1)}>
            Previous
          </Button>
          <span className="align-self-center text-muted">Page {page + 1} of {totalPages}</span>
          <Button variant="outline-primary" size="sm" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
            Next
          </Button>
        </div>
      )}
    </>
  );
};

export default AuditLogTable;
