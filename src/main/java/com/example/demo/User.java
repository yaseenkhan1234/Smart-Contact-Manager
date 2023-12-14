package com.example.demo;

import java.util.ArrayList;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


import lombok.Data;

@Entity
@Table(name = "User")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int UID;
	@NotBlank(message = "User Name can not be empty !!")
	@Size(min = 3,max = 30,message = "User Name must be between 3-30 characters !!")
	private String name;
	@Column(unique = true)
	@NotBlank(message = "Email can not be empty !!")
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "please Enter Valid Email !")
	private String email;
	@NotBlank(message = "password can not be empty !")
	private String password;
	private String role;
	private boolean enabled;
	private String imageUrl;
	@Column(length = 500)
	@NotBlank(message = "please write somethink about you")
	@Size(min = 3,max = 500,message = "about must be between 3-500 characters !!")
	private String about;
	
	@Transient
	private String MsgBody;
	@Transient
	private String Subject;
	
	
	
	//one user can have store more than one contacts at a time
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "user",orphanRemoval = true)
	private List<Contact> contacts=new ArrayList<>();
	
	
	
	
	
}
