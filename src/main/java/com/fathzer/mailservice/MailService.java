package com.fathzer.mailservice;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MailService {
	@Autowired
	private MailSettings mailSettings;
	private static final EmailValidator VALIDATOR = EmailValidator.getInstance();
	
	@Operation(description = "Sends a mail")
	@PostMapping(value="/v1/send")
	public ResponseEntity<Collection<String>> send(@Parameter(description = "The mail addresses of the recipients of the mail") @RequestParam("dest") List<String> dest,
			@Parameter(description = "The mail subject") @RequestParam("subject") String subject,
			@Parameter(description = "The content of the email") @RequestParam("body") String body) {
//FIXME body clearly can't be in request param (it could be long)
		dest = dest.stream().map(String::trim).collect(Collectors.toList());
		final List<String> errors = new LinkedList<>();
		if (dest==null || dest.isEmpty()) {
			errors.add("No dest specified");
		} else {
			dest.stream().map(this::getError).filter(Objects::nonNull).forEach(errors::add);
		}
		if (!errors.isEmpty()) {
			return ResponseEntity.badRequest().body(errors);
		}
		try {
			mailSettings.getMailer().sendMail(dest, subject, body);
			log.trace("Mail sent to {}", dest);
			return ResponseEntity.ok().body(Collections.singleton("Mail sent to "+dest));
		} catch (AuthenticationFailedException e) {
			// Warning error return should be a 500 error (and not an authentication error) because client has nothing to do with authentication on smtp server
			log.error("Authentication error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singleton("Authentication error on smtp server. Configuration is probably wrong."));
		} catch (MessagingException e) {
			log.error("Unable to send mail", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singleton("An error occurred"));
		}
	}
	
	private String getError(String dest) {
		if (!mailSettings.isAuthorized(dest)) {
			return dest+" is not an authorized email address";
		} else if (!VALIDATOR.isValid(dest)) {
			return dest+" is not a valid email address";
		} else {
			return null;
		}
	}

	public void addError(StringBuilder errors, String formParameterName) {
		if (errors.length()>0) {
			errors.append('\n');
		}
		errors.append("Missing "+formParameterName+" form parameter");
	}
}