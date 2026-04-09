import React from 'react';
import { Container } from 'react-bootstrap';
import ProfileView from '../components/profile/ProfileView';

const ProfilePage = () => {
  return (
    <Container className="py-4">
      <h4 className="fw-bold mb-4" style={{ color: '#1a365d' }}>Profile</h4>
      <ProfileView />
    </Container>
  );
};

export default ProfilePage;
