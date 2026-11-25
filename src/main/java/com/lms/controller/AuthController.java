package com.lms.controller;

import java.time.Duration;
import java.util.Map;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.LoginResult;
import com.lms.dto.LoginRequest;
import com.lms.dto.RegisterRequest;
import com.lms.dto.TokenRefreshResult;
import com.lms.dto.VerifyEmailRequest;
import com.lms.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	

	private AuthService authService;
	
	private final boolean secureCookie;
	
	
	


	public AuthController(AuthService authService, @Value("${app.cookie.secure:false}") boolean secureCookie) {
		super();
		this.authService = authService;
		this.secureCookie = secureCookie;
	}

	@PostMapping("/register")
	public ResponseEntity<?> rigster( @Valid @RequestBody RegisterRequest request) {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		LoginResult result = authService.login(request);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken()).toString())
				.body(result.getResponse());
	}
	
	
	@PostMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
		return ResponseEntity.ok(Map.of(
				"message", "Email verified successfully",
				"user", authService.verifyEmail(request.getEmail(), request.getOtp())));
	}
	
	@GetMapping("/getme")
	public ResponseEntity<?> getMe(Authentication authentication) {
		

		return ResponseEntity.ok(authService.getMe(authentication.getName()));
	}


	@GetMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		TokenRefreshResult result = authService.refreshToken(refreshToken);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken()).toString())
				.body(Map.of("message", "Access token refreshed successfully", "accessToken", result.getAccessToken()));
	}

	@GetMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		authService.logout(refreshToken);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
				.body(Map.of("message", "Logged out successfully"));
	}


	@GetMapping("/logout-all")
	public ResponseEntity<?> logoutAll(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		authService.logoutAll(refreshToken);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
				.body(Map.of("message", "Logged out from all devices successfully"));
	}
	
    
    private ResponseCookie  refreshCookie(String value) {
    	return ResponseCookie.from("refreshToken", value)
				.httpOnly(true)
				.secure(secureCookie)
				.sameSite("Strict")
				.path("/api/auth")
				.maxAge(Duration.ofDays(7))
				.build();
	}
    
    private ResponseCookie  clearRefreshCookie() {
    	return ResponseCookie.from("refreshToken", "")
				.httpOnly(true)
				.secure(secureCookie)
				.sameSite("Strict")
				.path("/api/auth")
				.maxAge(Duration.ZERO)
				.build();
	}

}
