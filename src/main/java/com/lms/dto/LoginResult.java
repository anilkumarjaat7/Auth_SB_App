package com.lms.dto;

public class LoginResult {

	private final AuthResponse response;
	private final String refreshToken;

	public LoginResult(AuthResponse response, String refreshToken) {
		this.response = response;
		this.refreshToken = refreshToken;
	}

	public AuthResponse getResponse() {
		return response;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
