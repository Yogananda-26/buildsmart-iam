import React from 'react';
import { Container } from 'react-bootstrap';
import ForgotPasswordForm from '../components/auth/ForgotPasswordForm';

const ForgotPasswordPage = () => {
  return (
    <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
      <ForgotPasswordForm />
    </Container>
  );
};

export default ForgotPasswordPage;
