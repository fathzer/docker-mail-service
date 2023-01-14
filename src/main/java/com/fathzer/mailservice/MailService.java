package com.fathzer.mailservice;

import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fathzer.mail.Mailer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private Mailer mailer;

	
	@Operation(description = "Sends a mail")
	@PostMapping(value="/v1/send", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> send(@Parameter(description = "The mail addresses of the recipients of the mail") @RequestParam("dest") List<String> dest,
			@Parameter(description = "The mail subject") @RequestParam("subject") String subject,
			@Parameter(description = "The content of the email") @RequestParam("body") String body) {
		final StringBuilder errors = new StringBuilder();
		if (dest==null || dest.isEmpty()) {
			errors.append("No dest specified");
		} else {
			for (String address : dest) {
				if (!EmailValidator.getInstance().isValid(address)) {
					if (errors.length()>0) {
						errors.append('\n');
					}
					errors.append(address+" is not a valid email");
				}
			}
		}
		if (body==null) {
			addError(errors, "body");
		}
		if (subject==null) {
			addError(errors, "subject");
		}
		if (errors.length()>0) {
			return ResponseEntity.badRequest().body(errors.toString());
		}
		try {
			mailer.sendMail(dest, subject, body);
			LOGGER.trace("Mail sent to {}", dest);
			return ResponseEntity.ok().body("Mail sent to "+dest+" using GMail");
		} catch (AuthenticationFailedException e) {
			LOGGER.warn("Authentication error", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication error");
		} catch (MessagingException e) {
			LOGGER.error("Unable to send mail", e);
			return ResponseEntity.status(500).body("An error occurred");
		}
	}

	public void addError(StringBuilder errors, String formParameterName) {
		if (errors.length()>0) {
			errors.append('\n');
		}
		errors.append("Missing "+formParameterName+" form parameter");
	}
}