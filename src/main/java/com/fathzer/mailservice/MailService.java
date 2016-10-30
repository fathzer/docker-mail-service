package com.fathzer.mailservice;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/send")
public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

	@Inject
	private GoogleMailer mailer;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response send(@QueryParam("dest") List<String> dest, @FormParam("subject") String subject, @FormParam("body") String body) {
		StringBuilder errors = new StringBuilder();
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
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(errors.toString()).build();
		}
		try {
			mailer.sendMail(dest, subject, body);
			LOGGER.trace("Mail sent to {}", dest);
			return Response.status(HttpURLConnection.HTTP_OK).entity("Mail sent to "+dest+" using GMail").build();
		} catch (AuthenticationFailedException e) {
			LOGGER.warn("Authentication error", e);
			return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity("Authentication error").build();
		} catch (MessagingException e) {
			LOGGER.error("Unable to send mail", e);
			return Response.status(500).entity("An error occurred").build();
		}
	}

	public void addError(StringBuilder errors, String formParameterName) {
		if (errors.length()>0) {
			errors.append('\n');
		}
		errors.append("Missing "+formParameterName+" form parameter");
	}
}