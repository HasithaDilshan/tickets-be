package com.tikets.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tikets.tickets.model.TicketPool;

@Configuration
public class AppConfig {

    @Bean
    public TicketPool ticketPool() {
        return new TicketPool();
    }
}
