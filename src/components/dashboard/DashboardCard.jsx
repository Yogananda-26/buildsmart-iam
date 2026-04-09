import React from 'react';
import { Card } from 'react-bootstrap';

const DashboardCard = ({ title, value, icon, color = '#1a365d' }) => {
  return (
    <Card className="shadow-sm h-100 border-0">
      <Card.Body className="d-flex align-items-center">
        <div
          className="rounded-circle d-flex align-items-center justify-content-center me-3"
          style={{ width: '50px', height: '50px', backgroundColor: color, color: 'white', fontSize: '1.5rem' }}
        >
          {icon}
        </div>
        <div>
          <h6 className="text-muted mb-1">{title}</h6>
          <h4 className="mb-0 fw-bold">{value}</h4>
        </div>
      </Card.Body>
    </Card>
  );
};

export default DashboardCard;
