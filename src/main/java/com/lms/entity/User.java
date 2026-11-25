package com.lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length = 100)
	private String username;
	
	@Column(nullable = false,unique = true, length = 150)
	private String email;

	private String phone;

	@Column(nullable = false, length = 100)
	private String password;
	
		 @Column(nullable = false)
	private boolean verfied = false;

		 public User(Integer id, String username, String email, String phone, String password, boolean verfied) {
			super();
			this.id = id;
			this.username = username;
			this.email = email;
			this.phone = phone;
			this.password = password;
			this.verfied = verfied;
		 }

		 public User() {
			super();
		 }

		 public Integer getId() {
			 return id;
		 }

		 public void setId(Integer id) {
			 this.id = id;
		 }

		 public String getUsername() {
			 return username;
		 }

		 public void setUsername(String username) {
			 this.username = username;
		 }

		 public String getEmail() {
			 return email;
		 }

		 public void setEmail(String email) {
			 this.email = email;
		 }

		 public String getPhone() {
			 return phone;
		 }

		 public void setPhone(String phone) {
			 this.phone = phone;
		 }

		 public String getPassword() {
			 return password;
		 }

		 public void setPassword(String password) {
			 this.password = password;
		 }

		 public boolean isVerfied() {
			 return verfied;
		 }

		 public void setVerfied(boolean verfied) {
			 this.verfied = verfied;
		 }
	 
		 
}
