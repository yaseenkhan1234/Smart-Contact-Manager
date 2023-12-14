package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long>{

	
	public MyOrder findByOrderId(String myorder);
}
