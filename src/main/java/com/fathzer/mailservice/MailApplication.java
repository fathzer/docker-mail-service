package com.fathzer.mailservice;

import java.util.Collections;

import org.apache.commons.validator.routines.EmailValidator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailApplication extends ResourceConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailApplication.class);

	public MailApplication() {
		super(Collections.singleton(MailService.class));
		String user = System.getenv("user");
		String pwd = System.getenv("pwd");
		String error = null;
		if (user==null || pwd==null) {
			error = "User or pwd environment variable is not defined";
		} else if (!EmailValidator.getInstance().isValid(user)) {
			error = user+" is not a valid mail address";
		}
		if (error!=null) {
			LOGGER.error(error);
			System.exit(1);
		}
		GoogleMailer mailer = new GoogleMailer(user, pwd);
		this.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(mailer).to(GoogleMailer.class);
			}
		});
	}
}
