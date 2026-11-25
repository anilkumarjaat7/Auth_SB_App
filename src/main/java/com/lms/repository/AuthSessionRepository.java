package com.lms.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.entity.AuthSession;
import com.lms.entity.User;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Integer> {

	Optional<AuthSession> findByRefreshTokenHashAndRevokedFalse(String refreshTokenHash);

	List<AuthSession> findByUserAndRevokedFalse(User user);
}

