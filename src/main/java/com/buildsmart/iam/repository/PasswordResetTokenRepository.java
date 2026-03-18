package com.buildsmart.iam.repository;

import com.buildsmart.iam.entity.PasswordResetToken;
import com.buildsmart.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    List<PasswordResetToken> findByUser(User user);
    
    List<PasswordResetToken> findByUsedFalseAndExpiresAtBefore(LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredOrUsedTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user")
    void markTokensAsUsedByUser(@Param("user") User user);
    
    boolean existsByToken(String token);
}
