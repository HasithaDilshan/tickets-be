package com.tikets.tickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootTest
class TicketsApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(TicketsApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner run(TicketingService ticketingService) {
	// 	return args -> {
	// 		ticketingService.startSystem();
	// 		Thread.sleep(30000); // Run for 30 seconds
	// 		ticketingService.stopSystem();
	// 	};
	// }

}
