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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fathzer.mail.Mailer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MailService {
	@Autowired
	private MailSettings mailSettings;
	private static final EmailValidator VALIDATOR = EmailValidator.getInstance();
	
	@Operation(description = "Sends an email")
	@PostMapping(value="/v1/send", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE})
	public ResponseEntity<Collection<String>> send(@Parameter(description = "The mail addresses of the recipients of the mail") @RequestParam("dest") List<String> dest,
			@Parameter(description = "The mail subject") @RequestParam("subject") String subject,
			@Schema(description = "The content of the email.\n\nPlease note that the 'Content-Type' header is used to determine the encoding of the email.  \nOnly text/html and text/plain are accepted",
					example = "This is a cool email") @RequestBody String body,
			@Parameter(hidden=true) @RequestHeader(name = "Content-Type", required=false, defaultValue = Mailer.TEXT_UTF8) String contentType) {
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
			mailSettings.mailer().sendMail(dest, subject, body, contentType);
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
		if (!mailSettings.destValidator().test(dest)) {
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