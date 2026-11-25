package com.lms.dto;

public class TokenRefreshResult {

	private final String accessToken;
	private final String refreshToken;

	public TokenRefreshResult(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
