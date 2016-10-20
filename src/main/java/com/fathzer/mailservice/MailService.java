package com.fathzer.mailservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/hello")
public class MailService {

	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String env) {
		String output = "Jersey say : " + env + " is "+System.getenv(env);
		return Response.status(200).entity(output).build();
	}
}