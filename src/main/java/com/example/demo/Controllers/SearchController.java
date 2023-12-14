package com.example.demo.Controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Contact;
import com.example.demo.ContactRepository;
import com.example.demo.User;
import com.example.demo.UserRepository;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//Search handler
	@GetMapping("/searching/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		System.out.println(query);
		
		User user=userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts=contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
		
	}
	
	
}
