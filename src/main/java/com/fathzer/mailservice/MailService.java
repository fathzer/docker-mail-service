package com.fathzer.mailservice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fathzer.mail.EMailAddress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fathzer.mail.EMail;
import com.fathzer.mail.MimeType;
import com.fathzer.mail.Recipients;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MailService {
	@Autowired
	private MailSettings mailSettings;
	
	@Operation(description = "Sends an email")
	@ApiResponse(responseCode = "200", description="The resquest was successfully processed")
	@ApiResponse(responseCode = "400", description="The arguments passed to this endpoint are wrong")
	@ApiResponse(responseCode = "500", description="Something went wrong during sending the mail. It's probably due to a server misconfiguration")
	@PostMapping(value="/v1/mails", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Reply> send(@Schema(allOf = EMailParams.class) @RequestBody EMailParams body) throws IOException {
		final Reply reply = new Reply();
		final Recipients recipients = getRecipients(reply, body);
		if (body.getContent()==null || body.getContent().trim().isBlank()) {
			reply.addMessage("Email content is missing");
		}
		if (!reply.getMessages().isEmpty()) {
			return ResponseEntity.badRequest().body(reply);
		}
		mailSettings.mailer().send(new EMail(recipients, body.getSubject(), body.getContent()).withMimeType(new MimeType(body.getEncoding())));
		log.trace("Mail sent");
		reply.addMessage("Mail sent");
		return ResponseEntity.ok().body(reply);
	}
	
	private Recipients getRecipients(Reply reply, EMailParams body) {
		final Recipients result = new Recipients();
		final List<EMailAddress> to = toAddresses(reply, body.getTo());
		final List<EMailAddress> cc = toAddresses(reply, body.getCc());
		final List<EMailAddress> bcc = toAddresses(reply, body.getBcc());
		if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
			reply.addMessage("No recipient is specified");
		} else {
			result.setTo(to);
			result.setCc(cc);
			result.setBcc(bcc);
		}
		return result;
	}
	
	private List<EMailAddress> toAddresses(Reply reply, List<String> addresses) {
		return addresses==null ? Collections.emptyList() : addresses.stream().map(addr -> toMailAddress(reply, addr.trim())).toList();
	}

	private EMailAddress toMailAddress(Reply reply, String address) {
		if (!mailSettings.destValidator().test(address)) {
			reply.addMessage(address+" is not an authorized email address");
			return null;
		}
		try {
			return new EMailAddress(address);
		} catch (IllegalArgumentException e) {
			// Nothing to do, specified user is wrong
			reply.addMessage(address+" is not a valid email address");
			return null;
		}
	}

	// This method prevents 500 errors when input JSon is wrong.
	@ExceptionHandler(JsonProcessingException.class)
	private ResponseEntity<Object> handleJsonException(JsonProcessingException ex) {
		final Reply reply = new Reply();
		log.debug("Unable to decode request", ex);
		reply.addMessage("Body request seems not to be a valid JSON: "+ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
    }


	@ExceptionHandler(IOException.class)
	private ResponseEntity<Object> handleIOException(IOException ex) {
		final Reply reply = new Reply();
		log.error("Unable to send mail", ex);
		reply.addMessage("An error occurred: "+ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(reply);
    }
}