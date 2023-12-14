package com.example.demo.Controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.EmailService;
import com.example.demo.User;
import com.example.demo.UserRepository;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder b;
	
	Random random=new Random(1000);

	//email id from handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String SendOTP(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println(email);
		//generating otp for 4 digit 
		
		int otp=random.nextInt(9999);
		System.out.println(otp);
		String subject="OTP from www.UserAdminPortal.com";
		String message="  OTP = "+otp+" ";
		String to=email;
		
       boolean flag=emailService.sendEmail(subject,message,to);
       if(flag) {
    	   session.setAttribute("myotp", otp);
    	   session.setAttribute("email", email);
    	   return "verify_otp";
       }else {
    	   session.setAttribute("message", "check your email id !!");
    	   return "forgot_email_form";
    	  
       }
       
       //System.out.println("flag value="+flag);	
		//write code for send otp to email
		
		
	}
	//verify otp
	
	@PostMapping("/verify-otp")
	public String VerifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		
		System.out.println("myOtp"+myOtp);
		System.out.println("otp"+otp);
		
		if(myOtp==otp) {
			
			//get user by email
			User user=userRepository.getUserByUserName(email);
			
			if(user==null) {
				
				session.setAttribute("message", "User does not exist with this email Please Register !!");
				return "forgot_email_form";
				
			}else {
				//password change module
				return "password_change_form";
				
			}
			
		}else {
			
			session.setAttribute("message","You have entered wrong otp !!");
			return "verify_otp";
			
		}
		
		
		
	}
	
	
	//change password module
	@PostMapping("/password-change")
	public String password_Change(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String userEmail=(String)session.getAttribute("email");
		
		User user=userRepository.getUserByUserName(userEmail);
		
		user.setPassword(b.encode(newpassword));
		userRepository.save(user);
		
		return "redirect:/SignIn?change=password change successfully..";
		
	}
	
	
	
	
	
	
	
	
	
}
