import React, { useState, useEffect } from 'react';
import { Table, Badge, Button, Form, InputGroup } from 'react-bootstrap';
import { toast } from 'react-toastify';
import API from '../../api/axiosInstance';
import LoadingSpinner from '../common/LoadingSpinner';
import ConfirmModal from '../common/ConfirmModal';
import UserEditModal from './UserEditModal';

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

const UserTable = () => {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // Delete modal
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);

  // Edit modal
  const [showEditModal, setShowEditModal] = useState(false);
  const [userToEdit, setUserToEdit] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  useEffect(() => {
    let filtered = users;
    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(u =>
        u.name?.toLowerCase().includes(term) ||
        u.email?.toLowerCase().includes(term) ||
        u.userId?.toLowerCase().includes(term)
      );
    }
    if (roleFilter) {
      filtered = filtered.filter(u => u.role === roleFilter);
    }
    if (statusFilter) {
      filtered = filtered.filter(u => u.status === statusFilter);
    }
    setFilteredUsers(filtered);
  }, [users, searchTerm, roleFilter, statusFilter]);

  const fetchUsers = async () => {
    try {
      const response = await API.get('/admin/users');
      setUsers(response.data.data || response.data || []);
    } catch (err) {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!userToDelete) return;
    try {
      await API.delete(`/admin/users/${userToDelete.userId}`);
      toast.success('User deleted successfully');
      setUsers(users.filter(u => u.userId !== userToDelete.userId));
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete user');
    } finally {
      setShowDeleteModal(false);
      setUserToDelete(null);
    }
  };

  const handleEditSave = async (updatedData) => {
    try {
      const response = await API.put(`/admin/users/${userToEdit.userId}`, updatedData);
      const updated = response.data.data || response.data;
      setUsers(users.map(u => u.userId === userToEdit.userId ? updated : u));
      toast.success('User updated successfully');
      setShowEditModal(false);
      setUserToEdit(null);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update user');
    }
  };

  if (loading) return <LoadingSpinner message="Loading users..." />;

  return (
    <>
      {/* Filters */}
      <div className="d-flex flex-wrap gap-2 mb-3">
        <InputGroup style={{ maxWidth: '300px' }}>
          <Form.Control
            placeholder="Search by name, email, or ID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </InputGroup>
        <Form.Select style={{ maxWidth: '200px' }} value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
          <option value="">All Roles</option>
          {Object.keys(ROLE_COLORS).map(r => (
            <option key={r} value={r}>{formatRole(r)}</option>
          ))}
        </Form.Select>
        <Form.Select style={{ maxWidth: '200px' }} value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="">All Statuses</option>
          {Object.keys(STATUS_COLORS).map(s => (
            <option key={s} value={s}>{formatStatus(s)}</option>
          ))}
        </Form.Select>
      </div>

      <Table responsive hover className="align-middle">
        <thead style={{ backgroundColor: '#1a365d', color: 'white' }}>
          <tr>
            <th>User ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredUsers.length === 0 ? (
            <tr><td colSpan="7" className="text-center text-muted py-4">No users found</td></tr>
          ) : (
            filteredUsers.map((user) => (
              <tr key={user.userId}>
                <td><code>{user.userId}</code></td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.phone}</td>
                <td><Badge bg={ROLE_COLORS[user.role]}>{formatRole(user.role)}</Badge></td>
                <td><Badge bg={STATUS_COLORS[user.status]}>{formatStatus(user.status)}</Badge></td>
                <td>
                  <Button
                    variant="outline-primary"
                    size="sm"
                    className="me-1"
                    onClick={() => { setUserToEdit(user); setShowEditModal(true); }}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => { setUserToDelete(user); setShowDeleteModal(true); }}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      <ConfirmModal
        show={showDeleteModal}
        onHide={() => { setShowDeleteModal(false); setUserToDelete(null); }}
        onConfirm={handleDelete}
        title="Delete User"
        message={`Are you sure you want to delete user "${userToDelete?.name}" (${userToDelete?.userId})? This action cannot be undone.`}
        confirmText="Delete"
        variant="danger"
      />

      <UserEditModal
        show={showEditModal}
        onHide={() => { setShowEditModal(false); setUserToEdit(null); }}
        user={userToEdit}
        onSave={handleEditSave}
      />
    </>
  );
};

export default UserTable;
