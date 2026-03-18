package com.buildsmart.iam.repository;

import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    List<User> findByRole(Role role);
    
    List<User> findByStatus(UserStatus status);
    
    List<User> findByRoleAndStatus(Role role, UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% OR u.email LIKE %:email%")
    List<User> findByNameOrEmailContaining(@Param("name") String name, @Param("email") String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);
}
