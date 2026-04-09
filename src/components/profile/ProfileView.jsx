import React, { useState, useEffect } from 'react';
import { Card, Form, Button, Row, Col, Badge, Alert } from 'react-bootstrap';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';
import LoadingSpinner from '../common/LoadingSpinner';

const STATUS_COLORS = {
  ACTIVE: 'success',
  INACTIVE: 'secondary',
  SUSPENDED: 'danger',
  PENDING_VERIFICATION: 'warning',
};

const ROLE_COLORS = {
  ADMIN: 'danger',
  PROJECT_MANAGER: 'primary',
  SITE_ENGINEER: 'success',
  SAFETY_OFFICER: 'warning',
  VENDOR: 'info',
  FINANCE_OFFICER: 'secondary',
};

const formatRole = (role) => role ? role.replace(/_/g, ' ') : '';
const formatStatus = (status) => status ? status.replace(/_/g, ' ') : '';

const ProfileView = () => {
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({ name: '', phone: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await API.get('/users/profile');
      const user = response.data.data || response.data;
      setProfile(user);
      setFormData({ name: user.name, phone: user.phone });
    } catch (err) {
      setError('Failed to load profile');
      toast.error('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const response = await API.put('/users/profile', formData);
      const updated = response.data.data || response.data;
      setProfile(updated);
      setEditing(false);
      toast.success('Profile updated successfully');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to update profile';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading profile..." />;
  if (error) return <Alert variant="danger">{error}</Alert>;
  if (!profile) return null;

  return (
    <Card className="shadow-sm border-0">
      <Card.Header className="bg-white border-bottom">
        <div className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0 fw-bold" style={{ color: '#1a365d' }}>My Profile</h5>
          {!editing && (
            <Button variant="outline-primary" size="sm" onClick={() => setEditing(true)}>
              Edit Profile
            </Button>
          )}
        </div>
      </Card.Header>
      <Card.Body className="p-4">
        {editing ? (
          <Form onSubmit={handleSave}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Name</Form.Label>
                  <Form.Control
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Phone</Form.Label>
                  <Form.Control
                    type="text"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
            <div className="d-flex gap-2">
              <Button type="submit" style={{ backgroundColor: '#1a365d', border: 'none' }} disabled={saving}>
                {saving ? 'Saving...' : 'Save Changes'}
              </Button>
              <Button variant="secondary" onClick={() => { setEditing(false); setFormData({ name: profile.name, phone: profile.phone }); }}>
                Cancel
              </Button>
            </div>
          </Form>
        ) : (
          <Row>
            <Col md={6} className="mb-3">
              <small className="text-muted">User ID</small>
              <p className="fw-bold mb-0">{profile.userId}</p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Name</small>
              <p className="fw-bold mb-0">{profile.name}</p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Email</small>
              <p className="fw-bold mb-0">{profile.email}</p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Phone</small>
              <p className="fw-bold mb-0">{profile.phone}</p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Role</small>
              <p className="mb-0"><Badge bg={ROLE_COLORS[profile.role]}>{formatRole(profile.role)}</Badge></p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Status</small>
              <p className="mb-0"><Badge bg={STATUS_COLORS[profile.status]}>{formatStatus(profile.status)}</Badge></p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Created</small>
              <p className="fw-bold mb-0">{profile.createdAt ? new Date(profile.createdAt).toLocaleString() : '-'}</p>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">Last Updated</small>
              <p className="fw-bold mb-0">{profile.updatedAt ? new Date(profile.updatedAt).toLocaleString() : '-'}</p>
            </Col>
          </Row>
        )}
      </Card.Body>
    </Card>
  );
};

export default ProfileView;
