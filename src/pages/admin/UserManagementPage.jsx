import React from 'react';
import { Container, Card } from 'react-bootstrap';
import UserTable from '../../components/admin/UserTable';

const UserManagementPage = () => {
  return (
    <Container className="py-4">
      <h4 className="fw-bold mb-4" style={{ color: '#1a365d' }}>User Management</h4>
      <Card className="shadow-sm border-0">
        <Card.Body>
          <UserTable />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default UserManagementPage;
