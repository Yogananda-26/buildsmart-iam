import React from 'react';
import { Navbar, Nav, NavDropdown, Container, Badge } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ROLE_COLORS = {
  ADMIN: 'danger',
  PROJECT_MANAGER: 'primary',
  SITE_ENGINEER: 'success',
  SAFETY_OFFICER: 'warning',
  VENDOR: 'info',
  FINANCE_OFFICER: 'secondary',
};

const formatRole = (role) => {
  if (!role) return '';
  return role.replace(/_/g, ' ');
};

const AppNavbar = () => {
  const { user, isAuthenticated, hasRole, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (!isAuthenticated) return null;

  return (
    <Navbar expand="lg" className="shadow-sm" style={{ backgroundColor: '#1a365d' }} variant="dark" sticky="top">
      <Container>
        <Navbar.Brand as={Link} to="/dashboard" className="fw-bold">
          <span style={{ color: '#dd6b20' }}>Build</span>Smart
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="main-navbar" />
        <Navbar.Collapse id="main-navbar">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/dashboard">Dashboard</Nav.Link>
            <Nav.Link as={Link} to="/profile">Profile</Nav.Link>
            {hasRole('ADMIN') && (
              <NavDropdown title="Admin" id="admin-dropdown">
                <NavDropdown.Item as={Link} to="/admin/users">User Management</NavDropdown.Item>
                <NavDropdown.Item as={Link} to="/admin/pending">Pending Approvals</NavDropdown.Item>
                <NavDropdown.Item as={Link} to="/admin/audit">Audit Logs</NavDropdown.Item>
              </NavDropdown>
            )}
          </Nav>
          <Nav>
            <Nav.Item className="d-flex align-items-center me-3">
              <Badge bg={ROLE_COLORS[user?.role] || 'secondary'} className="me-2">
                {formatRole(user?.role)}
              </Badge>
              <span className="text-light">{user?.name}</span>
            </Nav.Item>
            <Nav.Link onClick={handleLogout} className="text-light">
              Logout
            </Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default AppNavbar;
