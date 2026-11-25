package com.lms.dto;

public class AuthResponse {

	private final String message;
	private final UserResponse user;
	private final String accessToken;
	
	
	
	
	public AuthResponse(String message, UserResponse user, String accessToken) {
		super();
		this.message = message;
		this.user = user;
		this.accessToken = accessToken;
	}
	public String getMessage() {
		return message;
	}
	public UserResponse getUser() {
		return user;
	}
	public String getAccessToken() {
		return accessToken;
	}
	
	
	
	
}
