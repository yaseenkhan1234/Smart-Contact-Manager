

 console.log("this is script file")
 const search = () => {
	  console.log("searching ....")

	 
		let query=$("#search-input").val();
		console.log(query)
		if(query == ""){
			$(".search-result").hide();
			
		}else{
		console.log(query);
		
		let url=`http://localhost:8282/searching/${query}`;
		fetch(url)
		.then((response) => {
			
			return response.json();
			
		})
		.then((data) => {
			console.log(data);
			let text=`<div class='list-group'>`;
			
			data.forEach((contact) => {
				text+=`<a href='#' class='list-group-item list-group-action'> ${contact.Name}</a>`;
			});
			
			text+=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
			
			
		});
		
		}
		
		 
		 
};

const paymentStart=()=>{
	 console.log("paymentStart.....");
	 var amount= $("#payment_field").val();
	 console.log(amount);
	 
	 if(amount=="" || amount==null){
		 swal("failed !!", "amount is required !!", "error");
		 return;
	 }
	 
	 $.ajax({
	 		
			url:"/user/create_order",
			data:JSON.stringify({amount:amount,info:'order_request'}),
			contentType:"application/json",
			type:"POST",
			dataType:"json",
			success: function (response) {
				
				//invoked where success
				console.log(response);
				if(response.status=="created"){
						
						let options={
						key:'rzp_test_zA149j1booSoSd',
						amount:response.amount,
						currency:'INR',
						name:'Smart Contact Manager',
						description:'Donation',
						order_id:response.id,
						
						handler:function(response){
						
									console.log(response.razorpay_payment_id);
									console.log(response.razorpay_order_id);
									console.log(response.razorpay_signature);
								
									console.log("payment successfull !!");
									
									updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid");
									
									swal("Good job!", "congrates !! Payment success !!", "success");
						},
						
						prefill: {
						
								name: "",
								email: "",
								contact: "",
							
						},
						
						notes: {
								
								address: "www.UserAdmin.Portal.com",
						
						},
						
						theme: {
						
							color: "#3399cc",
							
						},
									
						
				};
			
			
			let rzp=new Razorpay(options);
			
			rzp.open();
			
			
			}	
			},
			error: function (error){
				//invoked when error
				console.log(error);
				alert("something went wrong !!");
				swal("failed", "Oops payment failed !!", "error");
			},
			
	 })
	 
	
};


function updatePaymentOnServer(payment_id,order_id,status){
	
	$.ajax({
	 		
			url:"/user/update_order",
			data:JSON.stringify({
			payment_id: payment_id,
			order_id:order_id,
			status:status
			}),
			contentType:"application/json",
			type:"POST",
			dataType:"json",
			
			success:function(response){
				
				swal("Greate job!", "congrates !! Payment success !!", "success");
				swal("Greate job!", "Please Check your Email !!", "success");
			},
			error:function(error){
				
				swal("failed !!", "your payment is successfull , but we did not capture", "error");
				

			},
	
	});
}

