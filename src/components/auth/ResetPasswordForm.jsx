import React, { useState, useEffect } from 'react';
import { Form, Button, Alert, Card } from 'react-bootstrap';
import { Link, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';

const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

const ResetPasswordForm = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const [formData, setFormData] = useState({ token, newPassword: '', confirmPassword: '' });
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const validateToken = async () => {
      if (!token) {
        setTokenValid(false);
        setValidating(false);
        return;
      }
      try {
        await API.get(`/api/auth/validate-reset-token/${token}`);
        setTokenValid(true);
      } catch {
        setTokenValid(false);
        setError('Invalid or expired reset token');
      } finally {
        setValidating(false);
      }
    };
    validateToken();
  }, [token]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!PASSWORD_REGEX.test(formData.newPassword)) {
      setError('Password must be at least 8 characters with uppercase, lowercase, digit, and special character');
      return;
    }
    if (formData.newPassword !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await API.post('/api/auth/reset-password', formData);
      setSuccess(true);
      toast.success(response.data.message || 'Password reset successful!');
    } catch (err) {
      const msg = err.response?.data?.message || 'Password reset failed';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  if (validating) {
    return (
      <Card className="shadow" style={{ maxWidth: '450px', width: '100%' }}>
        <Card.Body className="p-4 text-center">
          <p>Validating token...</p>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className="shadow" style={{ maxWidth: '450px', width: '100%' }}>
      <Card.Body className="p-4">
        <div className="text-center mb-4">
          <h2 className="fw-bold">
            <span style={{ color: '#dd6b20' }}>Build</span>
            <span style={{ color: '#1a365d' }}>Smart</span>
          </h2>
          <p className="text-muted">Set your new password</p>
        </div>

        {error && <Alert variant="danger">{error}</Alert>}

        {!tokenValid && !success ? (
          <div className="text-center">
            <Alert variant="danger">Invalid or expired reset token.</Alert>
            <Link to="/forgot-password" className="text-decoration-none" style={{ color: '#1a365d' }}>
              Request a new reset link
            </Link>
          </div>
        ) : success ? (
          <div className="text-center">
            <Alert variant="success">Password reset successful! You can now login with your new password.</Alert>
            <Link to="/login" className="btn" style={{ backgroundColor: '#1a365d', color: 'white' }}>
              Go to Login
            </Link>
          </div>
        ) : (
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>New Password</Form.Label>
              <Form.Control
                type="password"
                name="newPassword"
                value={formData.newPassword}
                onChange={handleChange}
                placeholder="Min 8 chars, uppercase, lowercase, digit, special"
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Confirm Password</Form.Label>
              <Form.Control
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Re-enter new password"
                required
              />
            </Form.Group>

            <Button
              type="submit"
              className="w-100"
              style={{ backgroundColor: '#1a365d', border: 'none' }}
              disabled={loading}
            >
              {loading ? 'Resetting...' : 'Reset Password'}
            </Button>
          </Form>
        )}
      </Card.Body>
    </Card>
  );
};

export default ResetPasswordForm;
