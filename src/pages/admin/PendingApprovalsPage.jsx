import React from 'react';
import { Container, Card } from 'react-bootstrap';
import PendingApprovals from '../../components/admin/PendingApprovals';

const PendingApprovalsPage = () => {
  return (
    <Container className="py-4">
      <h4 className="fw-bold mb-4" style={{ color: '#1a365d' }}>Pending Approvals</h4>
      <Card className="shadow-sm border-0">
        <Card.Body>
          <PendingApprovals />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PendingApprovalsPage;
