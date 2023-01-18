package com.fathzer.mail;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

public class MailerBuilder {
	private String host;
	private String user;
	private String pwd;
	private MailAddress from;
	private Encryption encryption;
	private int port;
	
	public MailerBuilder(String host) {
		this.withHost(host);
		this.withEncryption(Encryption.SSL);
	}
	
	public MailerBuilder withHost(String host) {
		if (host==null) {
			throw new IllegalStateException("Host is null");
		}
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
			try {
				from = new MailAddress(user);
			} catch (IllegalArgumentException e) {
				// Ok, user is not a mail address 
			}
		}
		return this;
	}

	public MailerBuilder withFrom(MailAddress from) {
		this.from = from;
		return this;
	}

	public MailerBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public Mailer build() {
		if (from==null) {
			throw new IllegalStateException("From adress is null");
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
		final Session session = Session.getDefaultInstance(props, auth);
		return new DefaultMailer(session, from);
	}

	public String getHost() {
		return host;
	}

	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}

	public MailAddress getFrom() {
		return from;
	}

	public Encryption getEncryption() {
		return encryption;
	}

	public int getPort() {
		return port;
	}
}
