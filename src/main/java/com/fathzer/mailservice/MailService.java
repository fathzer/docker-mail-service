package com.fathzer.mailservice;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/hello")
public class MailService {

	@GET
	@Path("/{dest}")
	public Response getMsg(@PathParam("dest") String dest) {
		String user = System.getenv("user");
		String pwd = System.getenv("pwd");
		String output = "Sending mail using GMail from "+user+":"+pwd;
		GoogleMailer mailer = new GoogleMailer(user, pwd);
		try {
			mailer.sendMail(new String[]{dest}, "This is a test", "This is the test's body");
			return Response.status(200).entity(output).build();
		} catch (AuthenticationFailedException e) {
			return Response.status(403).entity("Authentication error").build();
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.status(500).entity(output).build();
		}
	}
}