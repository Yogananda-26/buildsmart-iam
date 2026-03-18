package com.buildsmart.iam.repository;

import com.buildsmart.iam.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUserId(String userId, Pageable pageable);
    
    List<AuditLog> findByUserId(String userId);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);
    
    List<AuditLog> findByActionAndTimestampBetween(String action, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.action = :action ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserIdAndAction(@Param("userId") String userId, @Param("action") String action);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action AND a.timestamp >= :since")
    long countByActionSince(@Param("action") String action, @Param("since") LocalDateTime since);
}
