package com.example.demo.Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Contact;
import com.example.demo.ContactRepository;
import com.example.demo.EmailService;
import com.example.demo.MyOrder;
import com.example.demo.MyOrderRepo;
import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.helper.Message;
import com.razorpay.*;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private MyOrderRepo myOrderRepo;
	
	@Autowired
	private EmailService emailService;
	
	@ModelAttribute
	public void CommonData(Model model,Principal principal) {
		
		String userName=principal.getName();
		
		User user=userRepository.getUserByUserName(userName);
		
		model.addAttribute("user", user);
		
		System.out.println("USER"+user);
		
	
		
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
			return "normal/UserDashboard";
	}
	
	
	//open add from handler
	@RequestMapping("/add-contact")
	public String addContact(Model model) {
		
		model.addAttribute("contact", new Contact());
		
		return "normal/add_Contact";
		
	}
	
	@RequestMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact,
			Principal principal,HttpSession session) {
	
		try {
		String name=principal.getName();
		User user=userRepository.getUserByUserName(name);
		
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		userRepository.save(user);
		session.setAttribute("message", new Message("your contact is added !! Add more...", "success"));
		
		System.out.println("contact"+contact);
		}catch (Exception e) {
			session.setAttribute("message", new Message("Some went wrong !! Try again", "danger"));
			System.out.println();
		}
		return "normal/add_Contact";
	}

	
	@RequestMapping("/showclients/{page}")
	public String showClients(@PathVariable("page") Integer page,Model model,Principal principal) {
		//find Current User email
		String Username=principal.getName();
		User user=userRepository.getUserByUserName(Username);
		//find current uid
		int uid=user.getUID();
		
		//current page
		//contact Per page -5
		Pageable pageable=PageRequest.of(page, 4);
		
		//find user contacts
		Page<Contact> contacts=contactRepository.findContactsByUser(uid,pageable);
		//add contacts
		model.addAttribute("contacts", contacts);
		//add current page
		model.addAttribute("currentPage",page);
		//add total page
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/showClients";
		
	}
	
	//showing particular contact details
	
	@RequestMapping("{cid}/contact")
	public String ShowContacts(@PathVariable("cid") int cid,Model model,Principal principal) {
		
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		Optional<Contact> optionalContact=contactRepository.findById(cid);
		Contact contact=optionalContact.get();

		if(user.getUID()==contact.getUser().getUID()) {
			model.addAttribute("contact", contact);
			
			
		}
		
		System.out.println(cid);
		return "normal/contact_details";
		
		
	}
	
	
	//delete clients
	@RequestMapping("/delete/{cid}")
	public String delteClient(@PathVariable("cid") int cid,Principal principal,HttpSession session)
	{
		String usename=principal.getName();
		User user=userRepository.getUserByUserName(usename);
		
				
		Contact contact=contactRepository.findById(cid).get();
		
		
		
		
		  if(user.getUID()==contact.getUser().getUID()) {
		  
			  user.getContacts().remove(contact);
				
				userRepository.save(user);
		  session.setAttribute("message", new Message("User delete Successfully !!",
		  "success"));
		  
		  }
		 
		return "redirect:/user/showclients/0";
		
	}
	
	//open update form handler
	@PostMapping("/update-client/{cid}")
	public String updateForm(@PathVariable("cid") int cid,Model model) {
		
		Contact contact=contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		
		 return "normal/update_form";
	}
	
	//update user data
	
	@PostMapping("/Process-update")
	public String UpdateHandler(@ModelAttribute Contact contact,HttpSession session,Principal principal) {
		
		try {
		System.out.println(contact.getEmail());
		System.out.println(contact.getCID());
		
		String username=principal.getName();
		User user=userRepository.getUserByUserName(username);
		contact.setUser(user);
		contactRepository.save(contact);
		session.setAttribute("message", new Message("your Client is updated....", "success"));
		
		
		
		}catch (Exception e) {
				e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCID()+"/contact";
	}
	
	//user profile
	@RequestMapping("/profile")
	public String yourProfile() {
		
		return "normal/profile";
	}
	
	
	
	//create order for payment
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createorder(@RequestBody Map<String, Object> data,Principal principal)throws RazorpayException {
	
		int amt=Integer.parseInt(data.get("amount").toString());
		
	RazorpayClient client=new RazorpayClient("rzp_test_zA149j1booSoSd", "ofhsi86ozGTAIrrFXmPz7DgL");
	
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txt_12345");
		
		//creating new order
		
		Order order=client.Orders.create(ob);
		
		System.out.println(order);
	
		//save the order in database
		
		MyOrder myOrder=new MyOrder();
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setStatus("created");
		myOrder.setUser(userRepository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		myOrderRepo.save(myOrder);
		
		
		//Razorpay integration code 
		//https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/build-integration
		
		
		System.out.println(data);
		return order.toString();
	}
	
	

	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data,Principal principal)
	{
		
		User user=userRepository.getUserByUserName(principal.getName());
		String email=user.getEmail();
		MyOrder myorder=myOrderRepo.findByOrderId(data.get("order_id").toString());
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		myOrderRepo.save(myorder);
		boolean b=emailService.sendEmail("Payment Success", "your Payment is Successfully Received for Donation in UserAdminPortal", email);
		
		if(b) {
			System.out.println("mail send successfully");
		}
		else {
			System.out.println("mail not send");
		}
		
		
		return ResponseEntity.ok(Map.of("msg","update"));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}

