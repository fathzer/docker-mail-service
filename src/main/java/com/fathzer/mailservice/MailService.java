package com.fathzer.mailservice;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fathzer.mail.MailAddress;
import com.fathzer.mail.MimeType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MailService {
	@Autowired
	private MailSettings mailSettings;
	
	@Operation(description = "Sends an email")
	@PostMapping(value="/v1/mails", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE})
	public ResponseEntity<Collection<String>> send(@Parameter(description = "The mail addresses of the recipients of the mail") @RequestParam("dest") List<String> dest,
			@Parameter(description = "The mail subject") @RequestParam("subject") String subject,
			@Schema(description = "The content of the email.\n\nPlease note that the 'Content-Type' header is used to determine the encoding of the email.  \nOnly text/html and text/plain are accepted",
					example = "This is a cool email") @RequestBody String body,
			@Parameter(hidden=true) @RequestHeader(name = "Content-Type") String contentType) {
		dest = dest.stream().map(String::trim).collect(Collectors.toList());
		final List<String> errors = new LinkedList<>();
		final List<MailAddress> recipients;
		if (dest==null || dest.isEmpty()) {
			errors.add("No dest specified");
			recipients = null;
		} else {
			recipients = dest.stream().map(addr -> this.toMailAddress(errors,addr)).collect(Collectors.toList());
		}
		if (!errors.isEmpty()) {
			return ResponseEntity.badRequest().body(errors);
		}
		try {
			mailSettings.mailer().sendMail(recipients, subject, body, new MimeType(contentType));
			log.trace("Mail sent to {}", dest);
			return ResponseEntity.ok().body(Collections.singleton("Mail sent to "+dest));
		} catch (IOException e) {
			log.error("Unable to send mail", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singleton("An error occurred"));
		}
	}
	
	private MailAddress toMailAddress(List<String> errors, String dest) {
		if (!mailSettings.destValidator().test(dest)) {
			errors.add(dest+" is not an authorized email address");
			return null;
		}
		try {
			return new MailAddress(dest);
		} catch (IllegalArgumentException e) {
			// Nothing to do, specified user is wrong
			errors.add(dest+" is not a valid email address");
			return null;
		}
	}
}