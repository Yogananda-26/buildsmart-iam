import React from 'react';
import { Container, Card } from 'react-bootstrap';
import AuditLogTable from '../../components/admin/AuditLogTable';

const AuditLogsPage = () => {
  return (
    <Container className="py-4">
      <h4 className="fw-bold mb-4" style={{ color: '#1a365d' }}>Audit Logs</h4>
      <Card className="shadow-sm border-0">
        <Card.Body>
          <AuditLogTable />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AuditLogsPage;
