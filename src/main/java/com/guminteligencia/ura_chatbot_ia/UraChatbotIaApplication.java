package com.guminteligencia.ura_chatbot_ia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UraChatbotIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(UraChatbotIaApplication.class, args);
	}

}
