package com.lms.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lms.dto.AuthResponse;
import com.lms.dto.LoginRequest;
import com.lms.dto.LoginResult;
import com.lms.dto.RegisterRequest;
import com.lms.dto.TokenRefreshResult;
import com.lms.dto.UserResponse;
import com.lms.entity.AuthSession;
import com.lms.entity.EmailOtp;
import com.lms.entity.User;
import com.lms.repository.AuthSessionRepository;
import com.lms.repository.EmailOtpRepository;
import com.lms.repository.UserRepository;
import com.lms.securitry.JwtService;

import io.jsonwebtoken.JwtException;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JwtService jwtService;

   @Autowired
	private EmailOtpRepository otpRepo;

	@Autowired
	private AuthSessionRepository sessionRepo;

   
   @Transactional
	public Map<String , Object> register(RegisterRequest request) {

		if (userRepo.findByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setVerfied(false);

		 userRepo.save(user);


		
		String otp = createOtp();
		EmailOtp emailOtp = new EmailOtp();
		emailOtp.setEmail(user.getEmail());
		emailOtp.setUser(user);
		emailOtp.setOtpHash(hash(otp));
		emailOtp.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
		otpRepo.save(emailOtp);
		
		emailService.sendOtp(user.getEmail(), otp);
		
		
		
		return Map.of("message", "OTP sent to email", "user", response(user));
	}

   
   @Transactional
	public UserResponse verifyEmail(String email, String otp) {

			EmailOtp emailOtp = otpRepo.findByEmailAndOtpHash(email, hash(otp))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP"));

		if (emailOtp.getExpiresAt().isBefore(Instant.now())) {
			otpRepo.delete(emailOtp);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired");
		}

		User user = emailOtp.getUser();
		user.setVerfied(true);
		userRepo.save(user);
		otpRepo.deleteByUser(user);
		return response(user);
	}

    @Transactional
	public LoginResult login(LoginRequest request) {
		
		User user = userRepo.findByEmail(request.getEmail())
		.orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
		
		if(! passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");	
		}
		
		if (!user.isVerfied()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email is not verified");

		}



      String refreshToken = jwtService.refreshToken(user);

	  AuthSession session = new AuthSession();
		session.setUser(user);
		session.setRefreshTokenHash(hash(refreshToken));
		session.setRevoked(false);
		sessionRepo.save(session);
		
		
	   String accessToken = jwtService.accessToken(user);
		
		AuthResponse response = new AuthResponse("user login Succfully", response(user),accessToken);

		return new LoginResult(response, refreshToken);
	}
	
	
	public UserResponse getMe(String email) {
		
		User user = userRepo.findByEmail(email)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
		
		return response(user);
	}
	
	
	@Transactional
	public TokenRefreshResult refreshToken(String refreshToken) {
		AuthSession session = getSession(refreshToken);
		User user = session.getUser();
		
		
		String newRefreshToken = jwtService.refreshToken(user);
		session.setRefreshTokenHash(hash(newRefreshToken));
		sessionRepo.save(session);

		return new TokenRefreshResult(jwtService.accessToken(user), newRefreshToken);
		
	}
	
	
	@Transactional
	public void logout(String refreshToken) {
	AuthSession session = getSession(refreshToken);
	session.setRevoked(true);
	sessionRepo.save(session);
	}
	
	
	@Transactional
	public void logoutAll(String refreshToken) {
	AuthSession currentSession = getSession(refreshToken);
	List<AuthSession> sessions = sessionRepo.findByUserAndRevokedFalse(currentSession.getUser());
	for (AuthSession session : sessions) {
		session.setRevoked(true);
	}
	sessionRepo.saveAll(sessions);
}
	
	
	private UserResponse response(User u) {
		return new UserResponse(u.getUsername(), u.getEmail(),u.getPhone(), u.isVerfied());
	}
	
	
	private AuthSession getSession(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
		}

		String email;
		try {
			email = jwtService.getEmail(refreshToken);
		} catch (JwtException | IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}

		AuthSession session = sessionRepo.findByRefreshTokenHashAndRevokedFalse(hash(refreshToken))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
		if (!session.getUser().getEmail().equals(email)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}
		return session;
	}
	
	private String createOtp() {
		return String.format("%06d", new SecureRandom().nextInt(1_000_000));
		}
	
	
	private String hash(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}

}
