import React, { useState } from 'react';
import { Form, Button, Alert, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';

const ForgotPasswordForm = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await API.post('/api/auth/forgot-password', { email });
      setSuccess(true);
      toast.success(response.data.message || 'Password reset link sent to your email');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to send reset link';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="shadow" style={{ maxWidth: '450px', width: '100%' }}>
      <Card.Body className="p-4">
        <div className="text-center mb-4">
          <h2 className="fw-bold">
            <span style={{ color: '#dd6b20' }}>Build</span>
            <span style={{ color: '#1a365d' }}>Smart</span>
          </h2>
          <p className="text-muted">Reset your password</p>
        </div>

        {error && <Alert variant="danger">{error}</Alert>}

        {success ? (
          <Alert variant="success">
            <p className="mb-0">A password reset link has been sent to <strong>{email}</strong>.</p>
            <p className="mb-0 mt-2">Please check your email and follow the instructions.</p>
          </Alert>
        ) : (
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Email Address</Form.Label>
              <Form.Control
                type="email"
                value={email}
                onChange={(e) => { setEmail(e.target.value); setError(''); }}
                placeholder="Enter your registered email"
                required
              />
            </Form.Group>

            <Button
              type="submit"
              className="w-100 mb-3"
              style={{ backgroundColor: '#1a365d', border: 'none' }}
              disabled={loading}
            >
              {loading ? 'Sending...' : 'Send Reset Link'}
            </Button>
          </Form>
        )}

        <div className="text-center mt-3">
          <Link to="/login" className="text-decoration-none" style={{ color: '#1a365d' }}>
            Back to Login
          </Link>
        </div>
      </Card.Body>
    </Card>
  );
};

export default ForgotPasswordForm;
