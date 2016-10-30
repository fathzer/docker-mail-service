package com.fathzer.mailservice;

import java.util.List;

import javax.inject.Inject;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/mail")
public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

	@Inject
	private GoogleMailer mailer;

	@POST
	public Response getMsg(@QueryParam("dest") List<String> dest) {
		String output = "Sending mail using GMail to "+dest;
		try {
			mailer.sendMail(dest, "This is a test", "This is the test's body");
			LOGGER.trace("Mail sent to {}", dest);
			return Response.status(200).entity(output).build();
		} catch (AuthenticationFailedException e) {
			LOGGER.warn("Authentication error", e);
			return Response.status(403).entity("Authentication error").build();
		} catch (MessagingException e) {
			LOGGER.error("Unable to send mail", e);
			return Response.status(500).entity(output).build();
		}
	}
}