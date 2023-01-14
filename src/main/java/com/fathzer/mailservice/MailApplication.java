package com.fathzer.mailservice;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fathzer.mail.GoogleMailer;
import com.fathzer.mail.Mailer;

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
		if (user==null || pwd==null) {
			throw new IllegalStateException("User or pwd environment variable is not defined");
		} else if (!EmailValidator.getInstance().isValid(user)) {
			throw new IllegalStateException(user+" is not a valid mail address");
		} else {
			return new GoogleMailer(user, pwd);
		}
	}
}
