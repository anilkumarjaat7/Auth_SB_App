package com.lms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	private final JavaMailSender mailSender;
    private final String fromEmail;
    
    public EmailService(JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String fromEmail) {
this.mailSender = mailSender;
this.fromEmail = fromEmail;
}
    
    public void sendOtp(String toEmail, String otp) {
    	SimpleMailMessage message = new SimpleMailMessage();
    	 message.setFrom(fromEmail);
         message.setTo(toEmail);
         message.setSubject("Email Verification OTP");
         message.setText("Your OTP is: " + otp + "\nThis OTP expires in 10 minutes.");
         mailSender.send(message);
    	
    }
    
    
    

}
