import React from 'react';
import { Container } from 'react-bootstrap';
import SignupForm from '../components/auth/SignupForm';

const SignupPage = () => {
  return (
    <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
      <SignupForm />
    </Container>
  );
};

export default SignupPage;
