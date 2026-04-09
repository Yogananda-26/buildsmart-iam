import React from 'react';
import { Container } from 'react-bootstrap';
import ResetPasswordForm from '../components/auth/ResetPasswordForm';

const ResetPasswordPage = () => {
  return (
    <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
      <ResetPasswordForm />
    </Container>
  );
};

export default ResetPasswordPage;
