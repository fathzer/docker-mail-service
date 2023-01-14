package com.fathzer.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fathzer.mail.Mailer;
import com.fathzer.mail.MailerBuilder;

@SpringBootApplication
public class MailApplication {
	private static final String GMAIL_HOST_NAME = "smtp.gmail.com";
	
	public static void main(String[] args) {
        SpringApplication.run(MailApplication.class, args);
	}
	
	@Bean
	public Mailer getMailer() {
		String user = System.getenv("USER");
		String pwd = System.getenv("PWD");
		return new MailerBuilder(GMAIL_HOST_NAME).withAuthentication(user, pwd).build();
	}
}
