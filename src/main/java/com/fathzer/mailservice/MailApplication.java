package com.fathzer.mailservice;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fathzer.mail.Encryption;
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
		String host = System.getenv("HOST");
		if (host==null) {
			host = GMAIL_HOST_NAME;
		}
		final MailerBuilder mailerBuilder = new MailerBuilder(host);
		String user = System.getenv("USER");
		if (user!=null) {
			mailerBuilder.withAuthentication(user, System.getenv("PWD"));
		}
		String encryptionStr = System.getenv("ENCRYPTION");
		if (encryptionStr!=null) {
			mailerBuilder.withEncryption(Encryption.valueOf(encryptionStr.toUpperCase()));
		}
		String portString = System.getenv("PORT");
		if (portString!=null) {
			mailerBuilder.withPort(Integer.parseInt(portString));
		}
		final String from = System.getenv("FROM");
		if (from!=null) {
			mailerBuilder.withFrom(from);
		}
		final Mailer mailer = mailerBuilder.build();
		LoggerFactory.getLogger(MailApplication.class).info("Services will use a SMTP connection of {} user to {}:{} with {} encryption",mailerBuilder.getUser()==null?"no":mailerBuilder.getUser(), host,mailerBuilder.getPort(),Encryption.NONE.equals(mailerBuilder.getEncryption()) ? "no" : mailerBuilder.getEncryption());
		return mailer;
	}
}
