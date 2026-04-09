import React, { useState, useEffect } from 'react';
import { Table, Badge, Button, Alert } from 'react-bootstrap';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';
import LoadingSpinner from '../common/LoadingSpinner';
import ConfirmModal from '../common/ConfirmModal';

const ROLE_COLORS = {
  ADMIN: 'danger',
  PROJECT_MANAGER: 'primary',
  SITE_ENGINEER: 'success',
  SAFETY_OFFICER: 'warning',
  VENDOR: 'info',
  FINANCE_OFFICER: 'secondary',
};

const formatRole = (role) => role ? role.replace(/_/g, ' ') : '';

const PendingApprovals = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [modalAction, setModalAction] = useState(null); // 'approve' or 'reject'
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    fetchPendingUsers();
  }, []);

  const fetchPendingUsers = async () => {
    try {
      const response = await API.get('/admin/pending-users');
      setUsers(response.data.data || response.data || []);
    } catch (err) {
      toast.error('Failed to load pending users');
    } finally {
      setLoading(false);
    }
  };

  const openModal = (user, action) => {
    setSelectedUser(user);
    setModalAction(action);
    setShowModal(true);
  };

  const handleConfirm = async () => {
    if (!selectedUser || !modalAction) return;
    try {
      const endpoint = modalAction === 'approve'
        ? `/admin/approve/${selectedUser.userId}`
        : `/admin/reject/${selectedUser.userId}`;
      await API.post(endpoint);
      toast.success(`User ${modalAction === 'approve' ? 'approved' : 'rejected'} successfully`);
      setUsers(users.filter(u => u.userId !== selectedUser.userId));
    } catch (err) {
      toast.error(err.response?.data?.message || `Failed to ${modalAction} user`);
    } finally {
      setShowModal(false);
      setSelectedUser(null);
      setModalAction(null);
    }
  };

  if (loading) return <LoadingSpinner message="Loading pending approvals..." />;

  return (
    <>
      {users.length === 0 ? (
        <Alert variant="info">No pending approvals at this time.</Alert>
      ) : (
        <Table responsive hover className="align-middle">
          <thead style={{ backgroundColor: '#1a365d', color: 'white' }}>
            <tr>
              <th>User ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Requested Role</th>
              <th>Registered</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.userId}>
                <td><code>{user.userId}</code></td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.phone}</td>
                <td><Badge bg={ROLE_COLORS[user.role]}>{formatRole(user.role)}</Badge></td>
                <td>{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-'}</td>
                <td>
                  <Button
                    variant="success"
                    size="sm"
                    className="me-1"
                    onClick={() => openModal(user, 'approve')}
                  >
                    Approve
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => openModal(user, 'reject')}
                  >
                    Reject
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      <ConfirmModal
        show={showModal}
        onHide={() => { setShowModal(false); setSelectedUser(null); }}
        onConfirm={handleConfirm}
        title={modalAction === 'approve' ? 'Approve User' : 'Reject User'}
        message={`Are you sure you want to ${modalAction} user "${selectedUser?.name}" (${selectedUser?.email})?`}
        confirmText={modalAction === 'approve' ? 'Approve' : 'Reject'}
        variant={modalAction === 'approve' ? 'success' : 'danger'}
      />
    </>
  );
};

export default PendingApprovals;
