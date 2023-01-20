package com.fathzer.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fathzer.mail.Encryption;
import com.fathzer.mail.EMailAddress;
import com.fathzer.mail.Mailer;
import com.fathzer.mail.MailerBuilder;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class MailApplication {
	private static final String GMAIL_HOST_NAME = "smtp.gmail.com";
	
	public static void main(String[] args) {
        SpringApplication.run(MailApplication.class, args);
	}
	
	@Bean
	public MailSettings getMailer() {
		String host = System.getenv("HOST");
		if (host==null) {
			host = GMAIL_HOST_NAME;
		}
		final MailerBuilder mailerBuilder = new MailerBuilder(host);
		String user = System.getenv("HOST_USER");
		if (user!=null) {
			mailerBuilder.withAuthentication(user, System.getenv("HOST_PWD"));
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
			mailerBuilder.withDefaultSender(new EMailAddress(from));
		}
		final Mailer mailer = mailerBuilder.build();
		log.info("Services will use a SMTP connection of {} user to {}:{} with {} encryption",mailerBuilder.getUser()==null?"no":mailerBuilder.getUser(), host,mailerBuilder.getPort(),Encryption.NONE.equals(mailerBuilder.getEncryption()) ? "no" : mailerBuilder.getEncryption());
		
		final String authorizedStr = System.getenv("AUTHORIZED_DEST");
		final AddressValidator authorizedDest;
		if (authorizedStr==null) {
			authorizedDest = new AddressValidator();
		} else {
			authorizedDest = new AddressValidator(authorizedStr);
			log.info("Only the following recipients are authorized: {}",authorizedDest.getAuthorized());
		}
		return new MailSettings(mailer,authorizedDest);
	}
}
