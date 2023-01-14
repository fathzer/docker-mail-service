package com.fathzer.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.commons.validator.routines.EmailValidator;

public class MailerBuilder {
	private String host;
	private String user;
	private String pwd;
	private String from;
	private Encryption encryption;
	private int port;
	
	public MailerBuilder(String host) {
		this.host = host;
		this.withEncryption(Encryption.SSL);
	}
	
	public MailerBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public MailerBuilder withEncryption(Encryption encryption) {
		this.encryption = encryption;
		this.port = encryption.getDefaultPort();
		return this;
	}
	
	public MailerBuilder withAuthentication(String user, String pwd) {
		this.user = user;
		this.pwd = pwd;
		if (from==null) {
			from = user;
		}
		return this;
	}

	public MailerBuilder withFrom(String from) {
		this.from = from;
		return this;
	}

	public MailerBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public Mailer build() {
		if (host==null) {
			throw new IllegalStateException("Host is null");
		}
		if (from==null) {
			throw new IllegalStateException("From adress is null");
		}
		if (!EmailValidator.getInstance().isValid(from)) {
			throw new IllegalStateException(from+" is not a valid mail address");
		}
		if (port<=0) {
			throw new IllegalStateException("Port is <= 0");
		}
		if (user!=null && pwd==null) {
			throw new IllegalStateException("Password is null");
		}
		
		final Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		encryption.apply(props);
		final Authenticator auth;
		if (user!=null) {
			props.put("mail.smtp.auth", "true"); //enable authentication
			auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pwd);
				}
			};
		} else {
			auth = null;
		}
		System.out.println(props);
		final Session session = Session.getDefaultInstance(props, auth);
		return new DefaultMailer(session, from);
	}
}
