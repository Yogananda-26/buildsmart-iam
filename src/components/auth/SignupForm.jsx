import React, { useState } from 'react';
import { Form, Button, Alert, Card, Row, Col } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';

const ROLES = [
  { value: 'PROJECT_MANAGER', label: 'Project Manager' },
  { value: 'SITE_ENGINEER', label: 'Site Engineer' },
  { value: 'SAFETY_OFFICER', label: 'Safety Officer' },
  { value: 'VENDOR', label: 'Vendor' },
  { value: 'FINANCE_OFFICER', label: 'Finance Officer' },
];

const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
const PHONE_REGEX = /^\d{10}$/;

const SignupForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    role: 'PROJECT_MANAGER',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
    setValidationErrors({ ...validationErrors, [e.target.name]: '' });
  };

  const validate = () => {
    const errors = {};
    if (!formData.name || formData.name.length < 2 || formData.name.length > 100) {
      errors.name = 'Name must be 2-100 characters';
    }
    if (!formData.email) {
      errors.email = 'Email is required';
    }
    if (!PHONE_REGEX.test(formData.phone)) {
      errors.phone = 'Phone must be exactly 10 digits';
    }
    if (!PASSWORD_REGEX.test(formData.password)) {
      errors.password = 'Password must be at least 8 characters with uppercase, lowercase, digit, and special character';
    }
    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setError('');

    try {
      const { confirmPassword, ...signupData } = formData;
      const response = await API.post('/api/auth/signup', signupData);
      setSuccess(true);
      toast.success(response.data.message || 'Registration successful!');
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || 'Registration failed';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <Card className="shadow" style={{ maxWidth: '500px', width: '100%' }}>
        <Card.Body className="p-4 text-center">
          <div className="mb-3">
            <span style={{ fontSize: '4rem' }}>✅</span>
          </div>
          <h4 className="fw-bold" style={{ color: '#1a365d' }}>Registration Successful!</h4>
          <p className="text-muted">
            Your account is pending admin approval. You will be able to login once an administrator approves your account.
          </p>
          <Button
            onClick={() => navigate('/login')}
            style={{ backgroundColor: '#1a365d', border: 'none' }}
          >
            Go to Login
          </Button>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className="shadow" style={{ maxWidth: '550px', width: '100%' }}>
      <Card.Body className="p-4">
        <div className="text-center mb-4">
          <h2 className="fw-bold">
            <span style={{ color: '#dd6b20' }}>Build</span>
            <span style={{ color: '#1a365d' }}>Smart</span>
          </h2>
          <p className="text-muted">Create your account</p>
        </div>

        {error && <Alert variant="danger">{error}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Full Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter full name"
              isInvalid={!!validationErrors.name}
              required
            />
            <Form.Control.Feedback type="invalid">{validationErrors.name}</Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter email"
              isInvalid={!!validationErrors.email}
              required
            />
            <Form.Control.Feedback type="invalid">{validationErrors.email}</Form.Control.Feedback>
          </Form.Group>

          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Phone</Form.Label>
                <Form.Control
                  type="text"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  placeholder="10-digit number"
                  isInvalid={!!validationErrors.phone}
                  required
                />
                <Form.Control.Feedback type="invalid">{validationErrors.phone}</Form.Control.Feedback>
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Role</Form.Label>
                <Form.Select name="role" value={formData.role} onChange={handleChange}>
                  {ROLES.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>

          <Form.Group className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Min 8 chars, uppercase, lowercase, digit, special"
              isInvalid={!!validationErrors.password}
              required
            />
            <Form.Control.Feedback type="invalid">{validationErrors.password}</Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Confirm Password</Form.Label>
            <Form.Control
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Re-enter password"
              isInvalid={!!validationErrors.confirmPassword}
              required
            />
            <Form.Control.Feedback type="invalid">{validationErrors.confirmPassword}</Form.Control.Feedback>
          </Form.Group>

          <Button
            type="submit"
            className="w-100 mb-3"
            style={{ backgroundColor: '#1a365d', border: 'none' }}
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Sign Up'}
          </Button>
        </Form>

        <div className="text-center">
          <span className="text-muted">Already have an account? </span>
          <Link to="/login" className="text-decoration-none" style={{ color: '#1a365d' }}>
            Sign In
          </Link>
        </div>
      </Card.Body>
    </Card>
  );
};

export default SignupForm;
