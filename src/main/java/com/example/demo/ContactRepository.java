package com.example.demo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

	@Query("select c from Contact c where c.user.UID= :userId")
	//current page
	//contact Per page -5
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pePageble);
	
	
	@Query("select c from Contact c where c.Name= :Name and c.user= :user")
	public List<Contact> findByNameContainingAndUser(@Param("Name") String name,@Param("user") User user);
	
	
	
}
