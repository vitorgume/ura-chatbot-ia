package com.guminteligencia.ura_chatbot_ia;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UraChatbotIaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();

		System.setProperty("AWS_SQS_URL", dotenv.get("AWS_SQS_URL"));

		SpringApplication.run(UraChatbotIaApplication.class, args);
	}

}
