import React from 'react';
import { Spinner } from 'react-bootstrap';

const LoadingSpinner = ({ message = 'Loading...' }) => {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center py-5">
      <Spinner animation="border" role="status" style={{ color: '#1a365d' }} />
      <span className="mt-2 text-muted">{message}</span>
    </div>
  );
};

export default LoadingSpinner;
