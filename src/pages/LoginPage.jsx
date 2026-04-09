import React from 'react';
import { Container } from 'react-bootstrap';
import LoginForm from '../components/auth/LoginForm';

const LoginPage = () => {
  return (
    <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
      <LoginForm />
    </Container>
  );
};

export default LoginPage;
