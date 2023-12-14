package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.helper.Message;

@Service
public class EmailService {
	
	@Value("${spring.mail.username}")
	private String sender;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	boolean flag=false;

	public boolean sendEmail(String subject,String message,String to) {
		
		try {
			
			 SimpleMailMessage mailMessage
            = new SimpleMailMessage();

			 
        // Setting up necessary details
        mailMessage.setFrom(sender);
		mailMessage.setTo(to);
		mailMessage.setText(message);
		
		mailMessage.setSubject(subject);
		
		
			
			
			// Sending the mail
	         javaMailSender.send(mailMessage);
			System.out.println("otp send successfully ....");
	         flag=true;
	         
		} catch (Exception e) {
			
			e.printStackTrace();			
			
		}
		
		return flag;

		
		
	}
	
	
}
