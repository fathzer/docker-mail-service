package com.fathzer.mail;

import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DefaultMailer implements Mailer {
	private static final String SMTP_PORT = "465";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private String host;
	private String user;
	private String password;
	private Address sender;
	private Address[] replyTo;
	private boolean debug;

	public DefaultMailer(String host, String userLogin, String userPwd) {
		this.host = host;
		this.user = userLogin;
		this.password = userPwd;
		this.debug = false;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setSender(String sender) {
		this.sender = toAddress(sender);
	}
	
	public void setReplyTo(List<String> replyTo) {
		this.replyTo = replyTo==null?null:toAddresses(replyTo);
	}

	@Override
	public void sendMail(List<String> recipients, String subject, String message) throws MessagingException {
		final Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", Boolean.toString(debug));
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtps.ssl.checkserveridentity","true");

		final Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		session.setDebug(debug);

		final Message msg = new MimeMessage(session);
		msg.setFrom(sender!=null?sender:toAddress(user));
		if (replyTo!=null) {
			msg.setReplyTo(replyTo);
		}
		msg.setRecipients(Message.RecipientType.TO, toAddresses(recipients));
		msg.setSubject(subject);
		msg.setContent(message, "text/plain; charset=UTF-8");

		// Setting the Subject and Content Type
		Transport.send(msg);
	}
	
	private Address toAddress(String addr) {
		try {
			return new InternetAddress(addr);
		} catch (AddressException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Address[] toAddresses(List<String> addr) {
		return addr.stream().map(this::toAddress).toArray(Address[]::new);
	}

}
