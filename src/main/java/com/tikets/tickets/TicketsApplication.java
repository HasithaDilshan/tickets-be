package com.tikets.tickets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tikets.tickets.Listeners.ConsoleLogListener;
import com.tikets.tickets.Listeners.WebsocketLogListener;
import com.tikets.tickets.service.LogService;

@SpringBootApplication
public class TicketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketsApplication.class, args);
	}

	@Autowired
	public void configureLogService(LogService logService,
			ConsoleLogListener consoleLogListener,
			WebsocketLogListener webSocketLogListener) {
		logService.addListener(consoleLogListener);
		logService.addListener(webSocketLogListener);
	}

}
