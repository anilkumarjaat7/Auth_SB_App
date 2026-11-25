package com.lms.dto;

public class UserResponse {

	private final String username;
	private final String email;
	private final String phone;
	private final Boolean verfied;
	
	
	




	public UserResponse(String username, String email, String phone, Boolean verfied) {
		super();
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.verfied = verfied;
	}



	public String getUsername() {
		return username;
	}



	public String getEmail() {
		return email;
	}



	public String getPhone() {
		return phone;
	}



	public Boolean getVerfied() {
		return verfied;
	}
	
	
	
	
	
	
	
	

	
	
	
	
}
