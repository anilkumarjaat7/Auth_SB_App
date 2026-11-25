package com.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.entity.EmailOtp;
import com.lms.entity.User;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Integer> {

	Optional<EmailOtp> findByEmailAndOtpHash(String email, String otpHash);

	void deleteByUser(User user);
}

