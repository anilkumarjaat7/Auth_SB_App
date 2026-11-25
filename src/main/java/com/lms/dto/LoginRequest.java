package com.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

	@Email
	@NotBlank
	private String email;
	
	
	@Size(min=5)
	private String password;
	
	
	


	public LoginRequest() {
		super();
	}


	public LoginRequest(@Email @NotBlank String email, @Size(min = 5) String password) {
		super();
		this.email = email;
		this.password = password;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
