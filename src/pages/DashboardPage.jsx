import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import DashboardCard from '../components/dashboard/DashboardCard';
import API from '../api/axiosInstance';

const ROLE_DESCRIPTIONS = {
  ADMIN: 'Full system access. Manage users, view audit logs, and oversee all operations.',
  PROJECT_MANAGER: 'Manage construction projects, assign tasks, and track progress.',
  SITE_ENGINEER: 'Oversee on-site operations, report progress, and manage safety compliance.',
  SAFETY_OFFICER: 'Monitor safety protocols, conduct inspections, and manage incident reports.',
  VENDOR: 'Manage supply chain, submit invoices, and track deliveries.',
  FINANCE_OFFICER: 'Manage budgets, process payments, and generate financial reports.',
};

const DashboardPage = () => {
  const { user, hasRole } = useAuth();
  const [pendingCount, setPendingCount] = useState(0);

  useEffect(() => {
    if (hasRole('ADMIN')) {
      fetchPendingCount();
    }
  }, [hasRole]);

  const fetchPendingCount = async () => {
    try {
      const response = await API.get('/admin/pending-users');
      const data = response.data.data || response.data || [];
      setPendingCount(Array.isArray(data) ? data.length : 0);
    } catch {
      // silently fail
    }
  };

  return (
    <Container className="py-4">
      {/* Welcome Card */}
      <Card className="shadow-sm border-0 mb-4" style={{ background: 'linear-gradient(135deg, #1a365d 0%, #2d4a7a 100%)' }}>
        <Card.Body className="p-4 text-white">
          <h3 className="fw-bold">Welcome back, {user?.name}! 👋</h3>
          <p className="mb-0 opacity-75">
            {ROLE_DESCRIPTIONS[user?.role] || 'Welcome to BuildSmart.'}
          </p>
        </Card.Body>
      </Card>

      {/* Stats Row (Admin only) */}
      {hasRole('ADMIN') && (
        <Row className="g-3 mb-4">
          <Col md={4}>
            <DashboardCard title="Pending Approvals" value={pendingCount} icon="👥" color="#dd6b20" />
          </Col>
          <Col md={4}>
            <DashboardCard title="Your Role" value={user?.role?.replace(/_/g, ' ')} icon="🔑" color="#1a365d" />
          </Col>
          <Col md={4}>
            <DashboardCard title="User ID" value={user?.userId} icon="🆔" color="#2d8a4e" />
          </Col>
        </Row>
      )}

      {/* Non-admin stats */}
      {!hasRole('ADMIN') && (
        <Row className="g-3 mb-4">
          <Col md={6}>
            <DashboardCard title="Your Role" value={user?.role?.replace(/_/g, ' ')} icon="🔑" color="#1a365d" />
          </Col>
          <Col md={6}>
            <DashboardCard title="User ID" value={user?.userId} icon="🆔" color="#2d8a4e" />
          </Col>
        </Row>
      )}

      {/* Quick Info */}
      <Card className="shadow-sm border-0">
        <Card.Body>
          <h5 className="fw-bold" style={{ color: '#1a365d' }}>Quick Info</h5>
          <p className="text-muted mb-1"><strong>Email:</strong> {user?.email}</p>
          <p className="text-muted mb-0"><strong>Role:</strong> {user?.role?.replace(/_/g, ' ')}</p>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default DashboardPage;
