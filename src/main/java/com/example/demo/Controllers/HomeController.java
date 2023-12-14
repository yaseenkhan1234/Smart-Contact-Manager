package com.example.demo.Controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.helper.Message;

@Controller
public class HomeController {

	
	@Value("${spring.mail.username}")
	private String sender;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncorder;
	
	@RequestMapping("/")
	public String Home() {
		return "home";
	}
	
	//signup
	@RequestMapping("/SignUp")
	public String SignUp(Model model) {
		
		model.addAttribute("user",new User());
		
		return "signUp";
	}
	
	//handler for registering user
	@RequestMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,Model model,HttpSession session) {
		
		if(bindingResult.hasErrors()) {
			
			System.out.println(bindingResult);
			return "signUp";
		}
		
		
		try {
			
			 SimpleMailMessage mailMessage
             = new SimpleMailMessage();

         // Setting up necessary details
         mailMessage.setFrom(sender);
         mailMessage.setTo(user.getEmail());
         user.setMsgBody("Hi\t"+user.getName()+"this email through yaseen website");
         user.setSubject("Hi\t"+user.getName()+"\t"+"your registration is complete in www.UserAdminPortal.co.in");
         mailMessage.setText(user.getMsgBody());
         mailMessage.setSubject(user.getSubject());
			
			System.out.println("USER"+user);
			
			user.setRole("ROLE_USER");
			
			user.setEnabled(true);
			user.setPassword(passwordEncorder.encode(user.getPassword()));
			user.setImageUrl("hello");
			User result=userRepository.save(user);
			// Sending the mail
	         javaMailSender.send(mailMessage);
			model.addAttribute("user", new User());
			
			session.setAttribute("message",new Message("Successfully Registered !!","alert-success"));
			session.setAttribute("Email", new Message("Email is send to\t"+user.getEmail()+"\t successfully", "alert-success"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			model.addAttribute("user",user);
			
			session.setAttribute("message",new Message("Email Id already registered !!","alert-danger"));
			session.setAttribute("Email",new Message("Email Id not send !!","alert-danger"));

			return "signUp";
			
			
		}
		
		
		return "signUp";
	}

	
	//custom login page
	@RequestMapping("/SignIn")
	public String customLogin() {
		
		return "login";
	}
	
	
}
